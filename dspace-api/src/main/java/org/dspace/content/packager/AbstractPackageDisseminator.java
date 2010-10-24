/**
 * AbstractPackageDisseminator.java
 *
 * Version: $Revision$
 *
 * Date: $Date$
 *
 * Copyright (c) 2010, DuraSpace.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of DuraSpace nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.content.packager;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.content.crosswalk.CrosswalkException;
import org.dspace.core.Constants;
import org.dspace.core.Context;

/**
 * An abstract implementation of a DSpace Package Disseminator, which
 * implements a few helper/utility methods that most (all?) PackageDisseminators
 * may find useful.
 * <P>
 * First, implements recursive functionality in the disseminateAll()
 * method of the PackageIngester interface.  This method is setup to
 * recursively call disseminate() method.
 * <P>
 * All Package disseminators should either extend this abstract class
 * or implement <code>PackageDisseminator</code> to better suit their needs.
 *
 * @author Tim Donohue
 * @see PackageDisseminator
 */
public abstract class AbstractPackageDisseminator
        implements PackageDisseminator
{
    /** log4j category */
    private static Logger log = Logger.getLogger(AbstractPackageDisseminator.class);

    /**  List of all successfully disseminated package files */
    private List<File> packageFileList = new ArrayList<File>();

    /**
     * Recursively export one or more DSpace Objects as a series of packages.
     * This method will export the given DSpace Object as well as all referenced
     * DSpaceObjects (e.g. child objects) into a series of packages. The
     * initial object is exported to the location specified by the OutputStream.
     * All other packages are exported to the same directory location.
     * <p>
     * Package is any serialized representation of the item, at the discretion
     * of the implementing class.  It does not have to include content bitstreams.
     * <br>
     * Use the <code>params</code> parameter list to adjust the way the
     * package is made, e.g. including a "<code>metadataOnly</code>"
     * parameter might make the package a bare manifest in XML
     * instead of a Zip file including manifest and contents.
     * <br>
     * Throws an exception of the initial object is not acceptable or there is
     * a failure creating the package.
     *
     * @param context  DSpace context.
     * @param dso  initial DSpace object
     * @param params Properties-style list of options specific to this packager
     * @param pkgFile File where initial package should be written. All other
     *          packages will be written to the same directory as this File.
     * @throws PackageValidationException if package cannot be created or there is
     *  a fatal error in creating it.
     */
    public List<File> disseminateAll(Context context, DSpaceObject dso,
                     PackageParameters params, File pkgFile)
        throws PackageException, CrosswalkException,
               AuthorizeException, SQLException, IOException
    {
        //If unset, make sure the Parameters specifies this is a recursive dissemination
        if(!params.recursiveModeEnabled())
        {
            params.setRecursiveModeEnabled(true);
        }

        //try to disseminate the first object using provided PackageDisseminator
        disseminate(context, dso, params, pkgFile);

        //add to list of successfully disseminated packages
        addToPackageList(pkgFile);

        //We can only recursively disseminate non-Items
        //(NOTE: Items have no children, as Bitstreams/Bundles are created from Item packages)
        if(dso.getType()!=Constants.ITEM)
        {
            //Determine where first file package was disseminated to, as all
            //others will be written to same directory
            String pkgDirectory = pkgFile.getCanonicalFile().getParent();
            if(!pkgDirectory.endsWith(File.separator))
            {
                pkgDirectory += File.separator;
            }
            String fileExtension = PackageUtils.getFileExtension(pkgFile.getName());

            //recursively disseminate content, based on object type
            switch (dso.getType())
            {
                case Constants.COLLECTION :
                    //Also find all Items in this Collection and disseminate
                    Collection collection = (Collection) dso;
                    ItemIterator iterator = collection.getAllItems();
                    while(iterator.hasNext())
                    {
                        Item item = iterator.next();

                        //disseminate all items (recursively!)
                        String childFileName = pkgDirectory + PackageUtils.getPackageName(item, fileExtension);
                        disseminateAll(context, item, params, new File(childFileName));
                    }

                    break;
                case Constants.COMMUNITY :
                    //Also find all SubCommunities in this Community and disseminate
                    Community community = (Community) dso;
                    Community[] subcommunities = community.getSubcommunities();
                    for(int i=0; i<subcommunities.length; i++)
                    {
                        //disseminate all sub-communities (recursively!)
                        String childFileName = pkgDirectory + PackageUtils.getPackageName(subcommunities[i], fileExtension);
                        disseminateAll(context, subcommunities[i], params, new File(childFileName));
                    }

                    //Also find all Collections in this Community and disseminate
                    Collection[] collections = community.getCollections();
                    for(int i=0; i<collections.length; i++)
                    {
                        //disseminate all collections (recursively!)
                        String childFileName = pkgDirectory + PackageUtils.getPackageName(collections[i], fileExtension);
                        disseminateAll(context, collections[i], params, new File(childFileName));
                    }

                    break;
                case Constants.SITE :
                    //Also find all top-level Communities and disseminate
                    Community[] topCommunities = Community.findAllTop(context);
                    for(int i=0; i<topCommunities.length; i++)
                    {
                        //disseminate all top-level communities (recursively!)
                        String childFileName = pkgDirectory + PackageUtils.getPackageName(topCommunities[i], fileExtension);
                        disseminateAll(context, topCommunities[i], params, new File(childFileName));
                    }

                    break;
            }//end switch
        }//end if not an Item

        //return list of all successfully disseminated packages
        return getPackageList();
    }

    /**
     * Add File to list of successfully disseminated package files
     * @param file File
     */
    protected void addToPackageList(File f)
    {
        //add to list of successfully disseminated packages
        packageFileList.add(f);
    }

    /**
     * Return List of all package Files which have been disseminated
     * this instance of the Disseminator.
     * <P>
     * This list can be useful in reporting back to the user what content has
     * been disseminated as packages.  It's used by disseminateAll() to report
     * what packages were created.
     *
     * @return List of Files which correspond to the disseminated packages
     */
    protected List<File> getPackageList()
    {
        return packageFileList;
    }
}