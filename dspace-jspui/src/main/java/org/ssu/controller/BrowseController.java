package org.ssu.controller;

import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.browse.BrowseException;
import org.dspace.browse.BrowseInfo;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.handle.service.HandleService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.sort.SortException;
import org.dspace.sort.SortOption;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.ssu.entity.response.ItemResponse;
import org.ssu.service.BrowseContext;
import org.ssu.service.CommunityService;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("")
public class BrowseController {
    @Resource
    private CommunityService communityService;

    private HandleService handleService = HandleServiceFactory.getInstance().getHandleService();
    private AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
    private org.dspace.content.service.ItemService dspaceItemService = ContentServiceFactory.getInstance().getItemService();

    private List<String> createPaginationLinksList(Integer currentPage, Integer resultsPerPage, Integer totalPages, String currentPageURL) {
        List<String> links = new ArrayList<>();
        String linkFormat = "<li><a href=\"%s&offset=%d\" class = \"%s\">%d</a></li>";
        String separator = "<li><span>...</span></li>";

        Predicate<Integer> needToDisplayLinkToFirstPage = (current) -> current - 2 > 1;
        Predicate<Integer> needToDisplayGapAfterFirstPage = (current) -> current - 2 > 2;
        BiPredicate<Integer, Integer> needToDisplayGapBeforeLastPage = (current, total) -> current + 2 < total - 1;
        BiPredicate<Integer, Integer> needToDisplayLinkToLastPage = (current, total) -> current + 2 < total;
        BiFunction<Integer, String, String> createLink = (pageNumber, style) -> String.format(linkFormat, currentPageURL, (pageNumber - 1) * resultsPerPage, style, pageNumber);

        if (needToDisplayLinkToFirstPage.test(currentPage)) links.add(createLink.apply(1, ""));
        if (needToDisplayGapAfterFirstPage.test(currentPage)) links.add(separator);

        for (int cur = Math.max(1, currentPage - 2); cur <= Math.min(totalPages, currentPage + 2); cur++) {
            links.add(createLink.apply(cur, (cur == currentPage) ? "current" : ""));
        }

        if (needToDisplayGapBeforeLastPage.test(currentPage, totalPages)) links.add(separator);
        if (needToDisplayLinkToLastPage.test(currentPage, totalPages)) links.add(createLink.apply(totalPages, ""));
        return links;
    }

    private Integer getResultsPerPage(String resultsPerPageValueFromRequest) {
        return Optional.ofNullable(resultsPerPageValueFromRequest)
                .map(Integer::valueOf)
                .orElse(DSpaceServicesFactory.getInstance().getConfigurationService().getIntProperty("webui.collectionhome.perpage", 20));
    }

    private ModelAndView fillModelWithData(ModelAndView model, List<ItemResponse> items, BrowseInfo browseInfo, HttpServletRequest request, Boolean isExtendedTable) throws SortException {
        Integer perPage = getResultsPerPage(request.getParameter("rpp"));


        String currentPageURL = (request.getRequestURL().toString() + "?" + request.getQueryString())
                .replaceAll("[?&]offset=\\d+", "")
//                .replaceAll("[?&]starts_with=[^&]*", "")
                .replaceAll("[?&]year=\\d+", "");
        int currentPage = browseInfo.getOffset() / perPage + 1;
        int totalPages = (int) Math.ceil(Double.valueOf(browseInfo.getTotal()) / perPage);

        model.addObject("items", items);
        model.addObject("type", request.getParameter("type"));
        model.addObject("startIndex", browseInfo.getStart());
        model.addObject("finishIndex", browseInfo.getFinish());
        model.addObject("totalItems", browseInfo.getTotal());
        model.addObject("sortedBy", browseInfo.getSortOption());
        model.addObject("sortOrder", request.getParameter("sortOrder"));
        model.addObject("rpp", perPage);
        model.addObject("selectedYear", Optional.ofNullable(request.getParameter("starts_with")).map(String::valueOf).orElse(""));
        model.addObject("sortOptions", SortOption.getSortOptions().stream().filter(SortOption::isVisible).collect(Collectors.toSet()));
        model.addObject("prevPageUrl", String.format("%s&offset=%d", currentPageURL, (currentPage - 2) * perPage));
        model.addObject("prevPageDisabled", browseInfo.hasPrevPage() ? "" : "disabled");
        model.addObject("nextPageUrl", String.format("%s&offset=%d", currentPageURL, currentPage * perPage));
        model.addObject("nextPageDisabled", browseInfo.hasNextPage() ? "" : "disabled");
        model.addObject("links", createPaginationLinksList(currentPage, perPage, totalPages, currentPageURL));
        model.addObject("isExtended", isExtendedTable);
        model.setViewName("browse");
        return model;
    }

    @RequestMapping(value = "/123456789/{itemId}/browse")
    public ModelAndView browseInCommunity(ModelAndView model, HttpServletRequest request, HttpServletResponse response, @PathVariable("itemId") String itemId) throws ServletException, AuthorizeException, IOException, SQLException, BrowseException, SortException {
        Context dspaceContext = UIUtil.obtainContext(request);
        DSpaceObject dSpaceObject = handleService.resolveToObject(dspaceContext, "123456789/" + itemId);
        if(authorizeService.authorizeActionBoolean(dspaceContext, dSpaceObject, Constants.READ)) {
            if (dSpaceObject.getType() == Constants.COLLECTION) {
                    request.setAttribute("dspace.collection", dSpaceObject);
            }
            if (dSpaceObject.getType() == Constants.COMMUNITY) {
                request.setAttribute("dspace.community", dSpaceObject);
            }
        }
        return getBrowseItems(model, request, response);
    }

    @RequestMapping("/browse")
    public ModelAndView getBrowseItems(ModelAndView model, HttpServletRequest request, HttpServletResponse response) throws SQLException, BrowseException, SortException, ServletException, IOException, AuthorizeException {
        String type = request.getParameter("type");
        String value = request.getParameter("value");

        Context dspaceContext = UIUtil.obtainContext(request);

        BrowseInfo browseInfo = new BrowseContext().getBrowseInfo(dspaceContext, request, response);
        Boolean isExtendedTable = false;
        List<ItemResponse> items;
        if (("author".equals(type) || "subject".equals(type)) && (value == null || value.isEmpty())) {
            items = communityService.getShortList(dspaceContext, browseInfo);

        } else {
            items = communityService.getItems(dspaceContext, browseInfo);
            isExtendedTable = true;
        }

        fillModelWithData(model, items, browseInfo, request, isExtendedTable);

        return model;
    }

}
