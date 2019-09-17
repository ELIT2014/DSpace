package org.ssu;

import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.browse.BrowseEngine;
import org.dspace.browse.BrowseException;
import org.dspace.browse.BrowseInfo;
import org.dspace.browse.BrowserScope;
import org.dspace.content.*;
import org.dspace.content.Collection;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.springframework.stereotype.Service;
import org.ssu.entity.response.CommunityResponse;
import org.ssu.entity.response.ItemResponse;
import org.ssu.localization.AuthorsCache;
import org.ssu.localization.TypeLocalization;
import org.ssu.statistics.EssuirStatistics;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    @Resource
    private EssuirStatistics essuirStatistics;

    @Resource
    private TypeLocalization typeLocalization;

    @Resource
    private AuthorsCache authorsCache;


    private final transient org.dspace.content.service.CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();
    private final transient AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
    transient private final ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    public CommunityResponse build(Context context) throws SQLException {
        Map<String, List<Community>> subCommunities;
        subCommunities = new HashMap<>();
        List<Community> communities = communityService.findAllTop(context)
                .stream()
                .sorted(Comparator.comparing(Community::getName))
                .collect(Collectors.toList());
        for (Community community : communities) {
            build(community, subCommunities, context);
        }
        return new CommunityResponse.Builder()
                .withCommMap(subCommunities)
                .withIsAdmin(authorizeService.isAdmin(context))
                .withCommunities(communities)
                .build();
    }

    private void build(Community community, Map<String, List<Community>> commMap, Context context) throws SQLException {
        if(authorizeService.authorizeActionBoolean(context, community, Constants.READ)) {
            String comID = community.getID().toString();
            List<Community> communities = community.getSubcommunities()
                    .stream()
                    .sorted(Comparator.comparing(Community::getName))
                    .collect(Collectors.toList());

            for(Collection collection : community.getCollections()) {
                if(!authorizeService.authorizeActionBoolean(context, collection, Constants.READ)) {
                    community.removeCollection(collection);
                }
            }

            if (communities.size() > 0) {
                commMap.put(comID, communities);
                for (Community sub : communities) {
                    build(sub, commMap, context);
                }
            }
        }
    }

    public List<ItemResponse> getItems(Context context, BrowserScope browserScope) throws BrowseException {
        Locale locale = context.getCurrentLocale();

        Function<Item, Integer> extractIssuedYearForItem = (item) -> {
            List<MetadataValue> metadataArray = itemService.getMetadata(item, MetadataSchema.DC_SCHEMA, "date", "issued", Item.ANY);
            DCDate dd = new DCDate(metadataArray.get(0).getValue());
            return dd.getYear();
        };

        Function<Item, String> extractAuthorListForItem = (item) ->
            itemService.getMetadata(item, MetadataSchema.DC_SCHEMA, "contributor", "*", Item.ANY)
                    .stream()
                    .map(MetadataValue::getValue)
                    .map(author -> authorsCache.getAuthorLocalization(author))
                    .map(author -> String.format("%s, %s", author.getSurname(locale), author.getInitials(locale)))
                    .distinct()
                    .collect(Collectors.joining("; "));

        Function<Item, String> getItemType = (item) ->
                itemService.getMetadata(item, MetadataSchema.DC_SCHEMA, "type", "*", Item.ANY)
                .stream()
                .findFirst()
                .map(MetadataValue::getValue)
                .map(type -> typeLocalization.getTypeLocalized(type, locale))
                .orElse("Unknown");

        BrowseEngine browseEngine = new BrowseEngine(context);
        return browseEngine.browse(browserScope).getBrowseItemResults()
                .stream()
                .map(item -> new ItemResponse.Builder()
                .withTitle(item.getName())
                        .withYear(extractIssuedYearForItem.apply(item))
                        .withHandle(item.getHandle())
                        .withAuthors(extractAuthorListForItem.apply(item))
                        .withType(getItemType.apply(item))
                        .withViews(essuirStatistics.getViewsForItem(item.getLegacyId()))
                        .withDownloads(essuirStatistics.getDownloadsForItem(item.getLegacyId()))
                        .build())
                .collect(Collectors.toList());
    }
}
