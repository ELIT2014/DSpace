package org.ssu.controller;

import org.dspace.app.webui.util.UIUtil;
import org.dspace.browse.*;
import org.dspace.core.Context;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.sort.SortException;
import org.dspace.sort.SortOption;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.ssu.entity.response.ItemResponse;
import org.ssu.service.CommunityService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/browse")
public class BrowseController {
    @Resource
    private CommunityService communityService;

    @RequestMapping("/dateissued")
    public ModelAndView getItemsByDate(ModelAndView model, HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam(value = "sort_by", defaultValue = "1") Integer sortBy,
                                       @RequestParam(value="order", defaultValue = "ASC") String sortOrder,
                                       @RequestParam(value="year", required = false) Integer yearParameter,
                                       @RequestParam(value="page", required = false, defaultValue = "1") Integer page,
                                       @RequestParam(value = "rpp") String rpp) throws SQLException, BrowseException, SortException {
        Context dspaceContext = UIUtil.obtainContext(request);
        BrowseEngine browseEngine = new BrowseEngine(dspaceContext);
        BrowserScope browserScope = new BrowserScope(dspaceContext);

        Integer resultsPerPage = Optional.ofNullable(rpp)
                .map(Integer::parseInt)
                .orElse(DSpaceServicesFactory.getInstance().getConfigurationService().getIntProperty("webui.collectionhome.perpage", 20));
        Optional<Integer> year = Optional.ofNullable(yearParameter);
        year.ifPresent(value -> browserScope.setStartsWith(value.toString()));
        BrowseIndex newBi = BrowseIndex.getItemBrowseIndex();
        browserScope.setSortBy(sortBy);
        browserScope.setOrder(sortOrder);
        browserScope.setResultsPerPage(resultsPerPage);
        browserScope.setBrowseIndex(newBi);
        browserScope.setOffset(resultsPerPage * (page - 1));
        BrowseInfo browseInfo = browseEngine.browse(browserScope);
        List<ItemResponse> items = communityService.getItems(dspaceContext, browseInfo);


        String currentPageURL = (request.getRequestURL().toString() + "?" + request.getQueryString())
                .replaceAll("&page=\\d+", "")
                .replaceAll("&year=\\d+", "");
        int currentPage = browseInfo.getOffset() / resultsPerPage + 1;
        int totalPages = (int) Math.ceil(Double.valueOf(browseInfo.getTotal()) / resultsPerPage);

        if(currentPage == 1 && browseInfo.hasPrevPage()) {
            currentPage++;
        }

        if(totalPages == 1 && browseInfo.hasPrevPage()) {
            totalPages = 2;
        }

        List<String> links = new ArrayList<>();

        if(currentPage - 2 > 1) {
            links.add(String.format("<li><a href=\"%s&page=%d\">%d</a></li>", currentPageURL, 1, 1));

        }
        if(currentPage - 2 > 2) {
            links.add("<li><span>...</span></li>");
        }


        for(int cur = Math.max(1, currentPage - 2); cur <= Math.min(totalPages, currentPage + 2); cur++) {
            links.add(String.format("<li><a href=\"%s&page=%d\" class = \"%s\">%d</a></li>", currentPageURL, cur, (cur == currentPage) ? "current" : "", cur));
        }
        if(currentPage + 2 < totalPages - 1) {
            links.add("<li><span>...</span></li>");
        }
        if(currentPage + 2 < totalPages) {
            links.add(String.format("<li><a href=\"%s&page=%d\">%d</a></li>", currentPageURL, totalPages, totalPages));
        }

        model.addObject("items", items);
        model.addObject("startIndex", browseInfo.getStart());
        model.addObject("finishIndex", browseInfo.getFinish());
        model.addObject("totalItems", browseInfo.getTotal());
        model.addObject("sortedBy", browseInfo.getSortOption());
        model.addObject("sortOrder", sortOrder);
        model.addObject("rpp", resultsPerPage);
        model.addObject("selectedYear", year.orElse(LocalDate.now().getYear()));
        model.addObject("sortOptions", SortOption.getSortOptions().stream().filter(SortOption::isVisible).collect(Collectors.toSet()));
        model.addObject("prevPageUrl", String.format("%s&page=%d", currentPageURL, currentPage - 1));
        model.addObject("prevPageDisabled", browseInfo.hasPrevPage()? "" : "disabled");
        model.addObject("nextPageUrl", String.format("%s&page=%d", currentPageURL, currentPage + 1));
        model.addObject("nextPageDisabled", browseInfo.hasNextPage() ? "" : "disabled");
        model.addObject("links", links);
        model.setViewName("dateissued-browse");
        return model;

    }
}
