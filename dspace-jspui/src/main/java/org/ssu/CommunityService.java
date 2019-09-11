package org.ssu;

import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;
import org.springframework.stereotype.Service;
import org.ssu.entity.response.CommunityResponse;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommunityService {
    private final transient org.dspace.content.service.CommunityService communityService
            = ContentServiceFactory.getInstance().getCommunityService();
    AuthorizeService authorizeService
            = AuthorizeServiceFactory.getInstance().getAuthorizeService();

    public CommunityResponse build(Context context) throws SQLException {
        Map<String, List<Collection>> colMap;

        // This will map communityIDs to arrays of sub-communities
        Map<String, List<Community>> commMap;

        colMap = new HashMap<>();
        commMap = new HashMap<>();

//        log.info(LogManager.getHeader(context, "view_community_list", ""));

        List<Community> communities = communityService.findAllTop(context);

        for (Community c : communities) {
            build(c, colMap, commMap);
        }

        // can they admin communities?
        if (authorizeService.isAdmin(context)) {
            // set a variable to create an edit button
//            request.setAttribute("admin_button", Boolean.TRUE);
        }

//        request.setAttribute("communities", communities);
//        request.setAttribute("collections.map", colMap);
//        request.setAttribute("subcommunities.map", commMp);

        System.out.println("=====================================");
        System.out.println("=====================================");
        System.out.println(communities);
        System.out.println("=====================================");
        System.out.println(colMap);
        System.out.println("=====================================");
        System.out.println(commMap);
        System.out.println("=====================================");
        System.out.println("=====================================");
        System.out.println("=====================================");

        return new CommunityResponse.Builder()
                .withColMap(colMap)
                .withCommMap(commMap)
                .withCommunities(communities)
                .build();
    }

    private void build(Community c, Map<String, List<Collection>> colMap, Map<String, List<Community>> commMap)
            throws SQLException {

        String comID = c.getID().toString();

        // Find collections in community
        List<Collection> colls = c.getCollections();
        colMap.put(comID, colls);

        // Find subcommunties in community
        List<Community> comms = c.getSubcommunities();

        // Get all subcommunities for each communities if they have some
        if (comms.size() > 0) {
            commMap.put(comID, comms);
            for (Community sub : comms) {
                build(sub, colMap, commMap);
            }
        }
    }
}
