package org.ssu.controller;

import org.apache.log4j.Logger;
import org.dspace.app.webui.discovery.DiscoverUtility;
import org.dspace.app.webui.discovery.DiscoverySearchRequestProcessor;
import org.dspace.app.webui.search.SearchProcessorException;
import org.dspace.app.webui.search.SearchRequestProcessor;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CommunityService;
import org.dspace.core.Context;
import org.dspace.core.PluginConfigurationError;
import org.dspace.core.factory.CoreServiceFactory;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.SearchUtils;
import org.dspace.discovery.configuration.DiscoveryConfiguration;
import org.dspace.discovery.configuration.DiscoverySearchFilterFacet;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.handle.service.HandleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class SearchController {
    private transient SearchRequestProcessor internalLogic;
    private HandleService handleService = HandleServiceFactory.getInstance().getHandleService();
    private CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();
    private static final Logger log = Logger.getLogger(SearchController.class);

    @PostConstruct
    private void init() {
        try
        {
            internalLogic = (SearchRequestProcessor) CoreServiceFactory.getInstance().getPluginService()
                    .getSinglePlugin(SearchRequestProcessor.class);
        }
        catch (PluginConfigurationError e)
        {
            log.warn(
                    "SimpleSearchServlet not properly configurated, please configure the SearchRequestProcessor plugin",
                    e);
        }
        if (internalLogic == null)
        {   // Discovery is the default search provider since DSpace 4.0
            internalLogic = new DiscoverySearchRequestProcessor();
        }
    }

    @RequestMapping(value = "/123456789/{itemId}/simple-search")
    public ModelAndView simpleSearchInCommunity(ModelAndView model, HttpServletRequest request, HttpServletResponse response, @PathVariable("itemId") String itemId) throws ServletException, IOException, SQLException, AuthorizeException, SearchProcessorException, SearchServiceException {
        System.out.println("in search query");
        Context dspaceContext = UIUtil.obtainContext(request);
        DSpaceObject scope;
        try
        {
            scope = DiscoverUtility.getSearchScope(dspaceContext, request);
        }
        catch (IllegalStateException e)
        {
            throw new SearchProcessorException(e.getMessage(), e);
        }
        catch (SQLException e)
        {
            throw new SearchProcessorException(e.getMessage(), e);
        }

        DiscoveryConfiguration discoveryConfiguration = SearchUtils.getDiscoveryConfiguration();
        DiscoverQuery queryArgs = DiscoverUtility.getDiscoverQuery(dspaceContext, request, scope, true);
        queryArgs.setSpellCheck(discoveryConfiguration.isSpellCheckEnabled());
        DiscoverResult qResults = SearchUtils.getSearchService().search(dspaceContext, scope, queryArgs);

        List<Community> resultsListComm = new ArrayList<Community>();
        List<Collection> resultsListColl = new ArrayList<Collection>();
        List<Item> resultsListItem = new ArrayList<Item>();

        for (DSpaceObject dso : qResults.getDspaceObjects())
        {
            if (dso instanceof Item)
            {
                resultsListItem.add((Item) dso);
            }
            else if (dso instanceof Collection)
            {
                resultsListColl.add((Collection) dso);

            }
            else if (dso instanceof Community)
            {
                resultsListComm.add((Community) dso);
            }
        }

        // Pass in some page qualities
        // total number of pages
        long pageTotal = 1 + ((qResults.getTotalSearchResults() - 1) / qResults
                .getMaxResults());

        // current page being displayed
        long pageCurrent = 1 + (qResults.getStart() / qResults
                .getMaxResults());

        // pageLast = min(pageCurrent+3,pageTotal)
        long pageLast = ((pageCurrent + 3) > pageTotal) ? pageTotal
                : (pageCurrent + 3);

        // pageFirst = max(1,pageCurrent-3)
        long pageFirst = ((pageCurrent - 3) > 1) ? (pageCurrent - 3) : 1;
        List<String> appliedFilterQueries = new ArrayList<String>();
        List<String[]> appliedFilters = DiscoverUtility.getFilters(request);
        for (String[] filter : appliedFilters)
        {
            appliedFilterQueries.add(filter[0] + "::" + filter[1] + "::"
                    + filter[2]);
        }
        List<DiscoverySearchFilterFacet> availableFacet = discoveryConfiguration.getSidebarFacets();
        request.setAttribute("facetsConfig",
                availableFacet != null ? availableFacet
                        : new ArrayList<DiscoverySearchFilterFacet>());
        request.setAttribute("queryresults", qResults);
        request.setAttribute("appliedFilters", appliedFilters);
        request.setAttribute("queryArgs", queryArgs);
        request.setAttribute("appliedFilterQueries", appliedFilterQueries);
        request.setAttribute("scope", scope);
        model.addObject("scope", scope);
        model.addObject("searchScope", scope!=null ? scope.getHandle() : "");
        model.addObject("scopes", getScopes(scope, dspaceContext));

        model.setViewName("search");
        return model;
    }

    private List<DSpaceObject> getScopes(DSpaceObject scope, Context context) throws SearchProcessorException {
        List<DSpaceObject> scopes = new ArrayList<DSpaceObject>();
        if (scope == null)
        {
            List<Community> topCommunities;
            try
            {
                topCommunities = communityService.findAllTop(context);
            }
            catch (SQLException e)
            {
                throw new SearchProcessorException(e.getMessage(), e);
            }
            for (Community com : topCommunities)
            {
                scopes.add(com);
            }
        }
        else
        {
            try
            {
                DSpaceObject pDso = ContentServiceFactory.getInstance().getDSpaceObjectService(scope)
                        .getParentObject(context, scope);
                while (pDso != null)
                {
                    // add to the available scopes in reverse order
                    scopes.add(0, pDso);
                    pDso = ContentServiceFactory.getInstance().getDSpaceObjectService(pDso)
                            .getParentObject(context, pDso);
                }
                scopes.add(scope);
                if (scope instanceof Community)
                {
                    List<Community> comms = ((Community) scope).getSubcommunities();
                    for (Community com : comms)
                    {
                        scopes.add(com);
                    }
                    List<Collection> colls = ((Community) scope).getCollections();
                    for (Collection col : colls)
                    {
                        scopes.add(col);
                    }
                }
            }
            catch (SQLException e)
            {
                throw new SearchProcessorException(e.getMessage(), e);
            }
        }
        return scopes;
    }
}
