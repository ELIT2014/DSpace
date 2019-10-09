package org.ssu.controller;

import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.browse.ItemCountException;
import org.dspace.browse.ItemCounter;
import org.dspace.content.*;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.factory.CoreServiceFactory;
import org.dspace.core.service.PluginService;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.handle.service.HandleService;
import org.dspace.plugin.CommunityHomeProcessor;
import org.dspace.plugin.PluginException;
import org.dspace.statistics.util.LocationUtils;
import org.jvnet.jaxb2_commons.xml.bind.model.MList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.ssu.entity.response.BitstreamResponse;
import org.ssu.entity.response.CountedCommunityResponse;
import org.ssu.entity.response.CountryStatisticsResponse;
import org.ssu.service.ItemService;
import org.ssu.service.statistics.EssuirStatistics;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class HandleController {
    private HandleService handleService = HandleServiceFactory.getInstance().getHandleService();
    private AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
    private org.dspace.content.service.ItemService dspaceItemService = ContentServiceFactory.getInstance().getItemService();
    @Resource
    private ItemService itemService;

    @Resource
    private EssuirStatistics essuirStatistics;

    @RequestMapping(value = "/123456789/{itemId}/browse")
    public ModelAndView browseInCommunity(ModelAndView model, HttpServletRequest request) {
        System.out.println("in browse page");

        return model;
    }

    @RequestMapping(value = "/123456789/{itemId}/simple-search")
    public ModelAndView simpleSearchInCommunity(ModelAndView model, HttpServletRequest request) {
        System.out.println("in search query");

        return model;
    }

    @RequestMapping(value = "/123456789/{itemId}")
    public ModelAndView entrypoint(HttpServletRequest request, HttpServletResponse response,  @PathVariable("itemId") String itemId, ModelAndView model) throws SQLException, ItemCountException, PluginException, AuthorizeException {
        Context dspaceContext = UIUtil.obtainContext(request);
        DSpaceObject dSpaceObject = handleService.resolveToObject(dspaceContext, "123456789/" + itemId);
        Locale locale = dspaceContext.getCurrentLocale();
        if(authorizeService.authorizeActionBoolean(dspaceContext, dSpaceObject, Constants.READ)) {
            if (dSpaceObject.getType() == Constants.ITEM) {
                return displayItem(request, model, (Item) dSpaceObject, locale);
            }
            if (dSpaceObject.getType() == Constants.COMMUNITY) {
                return displayCommunity(request, response, model, (Community) dSpaceObject, locale);
            }
            System.out.println(dSpaceObject.getType());
        }
        return null;
    }



    private ModelAndView displayCommunity(HttpServletRequest request, HttpServletResponse response, ModelAndView model, Community community, Locale locale) throws SQLException, ItemCountException, PluginException, AuthorizeException {
        Context dspaceContext = UIUtil.obtainContext(request);
        ItemCounter ic = new ItemCounter(dspaceContext);

        List<CountedCommunityResponse> subCommunities = community.getSubcommunities()
                .stream()
                .map(subCommunity -> {
                    try {
                        return new CountedCommunityResponse.Builder()
                                .withTitle(subCommunity.getName())
                                .withHandle(subCommunity.getHandle())
                                .withItemCount(ic.getCount(subCommunity))
                                .build();
                    } catch (ItemCountException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<CountedCommunityResponse> collections = community.getCollections()
                .stream()
                .map(subCommunity -> {
                    try {
                        return new CountedCommunityResponse.Builder()
                                .withTitle(subCommunity.getName())
                                .withHandle(subCommunity.getHandle())
                                .withItemCount(ic.getCount(subCommunity))
                                .build();
                    } catch (ItemCountException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        PluginService pluginService = CoreServiceFactory.getInstance().getPluginService();
        CommunityHomeProcessor[] chp = (CommunityHomeProcessor[]) pluginService.getPluginSequence(CommunityHomeProcessor.class);
        for (int i = 0; i < chp.length; i++)
        {
            chp[i].process(dspaceContext, request, response, community);
        }

        model.setViewName("community-display");
        request.setAttribute("community", community);
        model.addObject("title", community.getName());
        model.addObject("handle", community.getHandle());
        model.addObject("subCommunities", subCommunities);
        model.addObject("collections", collections);
        model.addObject("itemCount", ic.getCount(community));
        return model;
    }

    @RequestMapping("/item-download/{itemId}/{bitstreamId}")
    public ModelAndView downloadBitstream(HttpServletRequest request, @PathVariable("itemId") UUID itemId, @PathVariable("bitstreamId") UUID bitstreamId) throws SQLException {
        Context dspaceContext = UIUtil.obtainContext(request);
        Function<Bitstream, String> getLinkForBitstream = (bitstream) -> {
            try {
                Item item = dspaceItemService.find(dspaceContext, itemId);
                essuirStatistics.updateItemDownloads(request, item.getLegacyId());
                return String.format("%s/bitstream/%s/%s/%s", request.getContextPath(), item.getHandle(), bitstream.getSequenceID(), UIUtil.encodeBitstreamName(bitstream.getName(), Constants.DEFAULT_ENCODING));
            } catch (UnsupportedEncodingException | SQLException e) {
                e.printStackTrace();
            }
            return bitstream.getHandle();
        };

        Bitstream bitstream = ContentServiceFactory.getInstance().getBitstreamService().find(dspaceContext, bitstreamId);
        return new ModelAndView("redirect:" + getLinkForBitstream.apply(bitstream));
    }

    private ModelAndView displayItem(HttpServletRequest request, ModelAndView model, Item item, Locale locale) throws SQLException {
        Context dspaceContext = UIUtil.obtainContext(request);

        essuirStatistics.updateItemViews(request, item.getLegacyId());
        List<CountryStatisticsResponse> itemViewsByCountry = essuirStatistics.getItemViewsByCountry(item.getLegacyId())
                .entrySet()
                .stream()
                .map(country -> new CountryStatisticsResponse.Builder()
                        .withCountryCode(country.getKey())
                        .withCountryName(LocationUtils.getCountryName("--".equals(country.getKey()) ? "" : country.getKey(), locale))
                        .withCount(country.getValue())
                        .build()
                )
                .sorted(Comparator.comparing(CountryStatisticsResponse::getCountryName))
                .collect(Collectors.toList());

        List<CountryStatisticsResponse> itemDownloadsByCountry = essuirStatistics.getItemDownloadsByCountry(item.getLegacyId())
                .entrySet()
                .stream()
                .map(country -> new CountryStatisticsResponse.Builder()
                        .withCountryCode(country.getKey())
                        .withCountryName(LocationUtils.getCountryName("--".equals(country.getKey()) ? "" : country.getKey(), locale))
                        .withCount(country.getValue())
                        .build()
                )
                .sorted(Comparator.comparing(CountryStatisticsResponse::getCountryName))
                .collect(Collectors.toList());

        List<String> authors = itemService.extractAuthorListForItem(item).stream()
                .map(author -> String.format("%s, %s", author.getSurname(locale), author.getInitials(locale)))
                .collect(Collectors.toList());

        Function<Bitstream, String> getBitstreamFormat = (bitstream) -> {
            try {
                return bitstream.getFormatDescription(dspaceContext);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "";
        };



        List<Bundle> bundles = dspaceItemService.getBundles(item, "ORIGINAL");
        List<BitstreamResponse> bitstreams = bundles.stream()
                .flatMap(bundle -> bundle.getBitstreams().stream())
                .map(bitstream -> new BitstreamResponse.Builder()
                        .withDownloadCount(itemDownloadsByCountry.stream().mapToInt(CountryStatisticsResponse::getCount).sum())
                        .withFormat(getBitstreamFormat.apply(bitstream))
                        .withFilename(bitstream.getName())
                        .withHandle(bitstream.getHandle())
                        .withLink(String.format("%s/handle/item-download/%s/%s", request.getContextPath(), item.getID(), bitstream.getID()))
                        .withSize(UIUtil.formatFileSize(bitstream.getSizeBytes()))
                        .build())
                .collect(Collectors.toList());
        model.addObject("title", item.getName());
        model.addObject("titleAlternative", itemService.getAlternativeTitleForItem(item));
        model.addObject("owningCollections", item.getCollections());
        model.addObject("type", itemService.getItemTypeLocalized(item, locale));
        model.addObject("authors", authors);
        model.addObject("keywords", itemService.getKeywordsForItem(item));
        model.addObject("year", itemService.extractIssuedYearForItem(item));
        model.addObject("uri", itemService.getURIForItem(item));
        model.addObject("publisher", itemService.getPublisherForItem(item));
        model.addObject("citation", itemService.getCitationForItem(item));
        model.addObject("abstracts", itemService.getAbstractsForItem(item));
        model.addObject("views", itemViewsByCountry);
        model.addObject("downloads", itemDownloadsByCountry);
        model.addObject("bundles", bitstreams);
        model.addObject("handle", item.getHandle());
        model.addObject("itemId", item.getID());
        model.addObject("canEdit", dspaceItemService.canEdit(dspaceContext, item));

        model.setViewName("item-display");
        return model;
    }
}
