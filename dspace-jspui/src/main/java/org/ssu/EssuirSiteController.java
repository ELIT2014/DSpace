package org.ssu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.dspace.app.webui.components.RecentSubmissionsException;
import org.dspace.app.webui.components.RecentSubmissionsManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.browse.*;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataSchema;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.core.factory.CoreServiceFactory;
import org.dspace.core.service.NewsService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.sort.SortException;
import org.dspace.sort.SortOption;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.ssu.entity.AuthorLocalization;
import org.ssu.entity.response.CommunityResponse;
import org.ssu.entity.response.ItemResponse;
import org.ssu.entity.response.ItemTypeResponse;
import org.ssu.entity.response.RecentItem;
import org.ssu.localization.TypeLocalization;
import org.ssu.statistics.EssuirStatistics;
import org.ssu.statistics.GeneralStatisticsService;
import org.ssu.statistics.ScheduledTasks;
import org.ssu.statistics.StatisticsData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequestMapping("/")
@Controller
public class EssuirSiteController {
    private static Logger log = Logger.getLogger(EssuirSiteController.class);
    @Resource
    private TypeLocalization typeLocalization;

    @Resource
    private CommunityService communityService;

    @Resource
    private EssuirStatistics essuirStatistics;

    @Resource
    private GeneralStatisticsService generalStatisticsService;

    @Resource
    private ScheduledTasks scheduledTasks;

    @RequestMapping("/")
    public ModelAndView homePage(ModelAndView model, HttpServletRequest request) throws SQLException, ItemCountException {
        Context dspaceContext = UIUtil.obtainContext(request);
        ItemCounter ic = new ItemCounter(dspaceContext);
        Locale locale = dspaceContext.getCurrentLocale();

        NewsService newsService = CoreServiceFactory.getInstance().getNewsService();
        String topNews = newsService.readNewsFile(I18nUtil.getMessage("news-top.html", locale));
        String sideNews = newsService.readNewsFile(I18nUtil.getMessage("news-side.html", locale));
        List<Community> communities = ContentServiceFactory.getInstance().getCommunityService().findAll(dspaceContext);

        Map<Community, Integer> communityResponse = communities
                .stream()
                .filter(item -> item.getParentCommunities().isEmpty())
                .collect(Collectors.toMap(item -> item, community -> {
                    try {
                        return ic.getCount(community);
                    } catch (ItemCountException e) {

                    }
                    return 0;
                }));

        StatisticsData totalStatistic = essuirStatistics.getTotalStatistic();
        List<ItemTypeResponse> submissionStatisticsByType = typeLocalization.getSubmissionStatisticsByType(locale);
        model.addObject("topNews", String.format(topNews, totalStatistic.getTotalCount(), totalStatistic.getLastUpdate()));
        model.addObject("sideNews", sideNews);
        model.addObject("submissions", submissionStatisticsByType);
        model.addObject("communities", communityResponse);

        model.setViewName("home");
        return model;
    }

    @RequestMapping("/provision")
    public String provisionPage() {
        return "position";
    }

    @RequestMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @RequestMapping("/instruction")
    public String instructionPage() {
        return "instruction";
    }

    @RequestMapping("/contacts")
    public String contactsPage() {
        return "contacts";
    }

    @RequestMapping("/application1")
    public String application1Page() {
        return "application1";
    }

    @RequestMapping("/application2")
    public String application2Page() {
        return "application2";
    }

    @RequestMapping("/structure")
    public String structurePage() {
        return "structure";
    }


    @RequestMapping("/recent-items")
    public ModelAndView recentItemsPage(ModelAndView model, HttpServletRequest request) throws SQLException, RecentSubmissionsException {
        Context context = UIUtil.obtainContext(request);
        Locale locale = context.getCurrentLocale();
        List<Item> items = new RecentSubmissionsManager(context).getRecentSubmissions(null).getRecentSubmissions();

        List<RecentItem> recentItems = items.stream()
                .map(item -> new RecentItem.Builder()
                        .withTitle(item.getName())
                        .withType(typeLocalization.getTypeLocalized(item.getItemService().getMetadataFirstValue(item, MetadataSchema.DC_SCHEMA, "type", null, Item.ANY), locale))
                        .withHandle(item.getHandle())
                        .build())
                .collect(Collectors.toList());
        model.addObject("recentItems", recentItems);
        model.setViewName("recent-items");
        return model;
    }

    @RequestMapping("/faq")
    public ModelAndView faqPage(ModelAndView model, HttpServletRequest request) throws SQLException {
        Context dspaceContext = UIUtil.obtainContext(request);
        Locale locale = dspaceContext.getCurrentLocale();
        NewsService newsService = CoreServiceFactory.getInstance().getNewsService();
        String faqFilePath = String.format("faq%s.html", locale.getLanguage().equals("en") ? "" : "_" + locale.getLanguage());
        model.addObject("faq", newsService.readNewsFile(faqFilePath));
        model.setViewName("faq");

        return model;
    }

    @RequestMapping("/top-publications")
    public ModelAndView topPublicationsPage(ModelAndView model, HttpServletRequest request) throws SQLException {
        List<org.ssu.entity.Item> publications = essuirStatistics.topPublications(DSpaceServicesFactory.getInstance().getConfigurationService().getIntProperty("jsp.view.top_publications_count"));
        model.addObject("publicationList", publications);
        model.setViewName("top-publications");
        model.addObject("listSize", publications.size());
        return model;
    }

    @RequestMapping("/top-authors")
    public ModelAndView topAuthorsPage(ModelAndView model, HttpServletRequest request) throws SQLException {
        Context dspaceContext = UIUtil.obtainContext(request);
        Locale locale = dspaceContext.getCurrentLocale();
        Function<AuthorLocalization, String> extractAuthorData = (author) -> String.format("%s, %s", author.getSurname(locale), author.getInitials(locale));
        List<Pair<String, Long>> authors = essuirStatistics.topAuthors(DSpaceServicesFactory.getInstance().getConfigurationService().getIntProperty("jsp.view.top_authors_count"))
                .stream()
                .map(author -> Pair.of(extractAuthorData.apply(author.getKey()), author.getValue()))
                .collect(Collectors.toList());
        model.addObject("authorList", authors);
        model.addObject("listSize", authors.size());
        model.setViewName("top-authors");
        return model;
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @ResponseBody
    public String getTotalStatistics(HttpServletRequest request) throws JsonProcessingException {
        return new ObjectMapper()
                .writeValueAsString(generalStatisticsService.collectGeneralStatistics());
    }

    @RequestMapping(value = "/general-statistics", method = RequestMethod.GET)
    public String getGeneralStatistics(ModelMap model) {
        model.addAttribute("listYearStatistics", generalStatisticsService.getListYearsStatistics());
        return "pub_stat";
    }


    @RequestMapping(value = "/update-statistics", method = RequestMethod.GET)
    public void update() {
        scheduledTasks.finalizeMonthStatistics();
    }

    @RequestMapping("community-list")
    public ModelAndView getCommunityList(ModelAndView model, HttpServletRequest request, HttpServletResponse response) throws SQLException, ItemCountException {
        Context dspaceContext = UIUtil.obtainContext(request);
        CommunityResponse communityResponse = communityService.build(dspaceContext);

        model.addObject("communities", communityResponse.getCommunities());
        model.addObject("innerCommunities", communityResponse.getCommMap());
        model.addObject("isAdmin", communityResponse.getIsAdmin());
        model.addObject("itemCounter", new ItemCounter(dspaceContext));

        model.setViewName("community-list");
        return model;

    }

    @RequestMapping("/dateissued-browse")
    public ModelAndView getItemsByDate(ModelAndView model, HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam(value = "sort_by", defaultValue = "1") Integer sortBy,
                                       @RequestParam(value="order", defaultValue = "ASC") String sortOrder,
                                       @RequestParam(value="year", required = false) Integer year,
                                       @RequestParam(value="page", required = false, defaultValue = "1") Integer page,
                                       @RequestParam(value = "rpp") String rpp) throws SQLException, BrowseException, SortException {

        Integer resultsPerPage = Optional.ofNullable(rpp).map(Integer::parseInt).orElse(DSpaceServicesFactory.getInstance().getConfigurationService().getIntProperty("webui.collectionhome.perpage", 20));

        Context dspaceContext = UIUtil.obtainContext(request);
        BrowseEngine browseEngine = new BrowseEngine(dspaceContext);
        BrowserScope browserScope = new BrowserScope(dspaceContext);
        if(year != null) {
            browserScope.setStartsWith(year.toString());
//            browserScope.setJumpToValue(year.toString());
        }

        BrowseIndex newBi = BrowseIndex.getItemBrowseIndex();
        browserScope.setSortBy(sortBy);
        browserScope.setOrder(sortOrder);
        browserScope.setResultsPerPage(resultsPerPage);
        browserScope.setBrowseIndex(newBi);
        browserScope.setOffset(resultsPerPage * (page - 1));

        BrowseInfo browseInfo = browseEngine.browse(browserScope);


        List<ItemResponse> items = communityService.getItems(dspaceContext, browseInfo);

        model.addObject("items", items);
        model.addObject("startIndex", browseInfo.getStart());
        model.addObject("finishIndex", browseInfo.getFinish());
        model.addObject("totalItems", browseInfo.getTotal());
        model.addObject("sortedBy", browseInfo.getSortOption());
        model.addObject("sortOrder", sortOrder);
        model.addObject("rpp", resultsPerPage);
        model.addObject("selectedYear", year != null ? year : LocalDate.now().getYear());
        model.addObject("sortOptions", SortOption.getSortOptions().stream().filter(SortOption::isVisible).collect(Collectors.toSet()));

        String currentPageURL = (request.getRequestURL().toString() + "?" + request.getQueryString()).replaceAll("&page=\\d+", "");
        int currentPage = (page == null) ? browseInfo.getOffset() / resultsPerPage + 1 : page;
        int totalPages = (int) Math.ceil(Double.valueOf(browseInfo.getTotal()) / resultsPerPage);

        if(currentPage == 1 && browseInfo.hasPrevPage()) {
            currentPage++;
        }

        if(totalPages == 1 && browseInfo.hasPrevPage()) {
            totalPages = 2;
        }

        model.addObject("prevPageUrl", String.format("%s&page=%d", currentPageURL, currentPage - 1));
        model.addObject("prevPageDisabled", browseInfo.hasPrevPage()? "" : "disabled");

        model.addObject("nextPageUrl", String.format("%s&page=%d", currentPageURL, currentPage + 1));
        model.addObject("nextPageDisabled", browseInfo.hasNextPage() ? "" : "disabled");
        List<String> links = new ArrayList<>();

        if(currentPage - 2 > 1) {
            links.add(String.format("<li><a href=\"%s&page=%d\">%d</a></li>", currentPageURL, 1, 1));

        }
        if(currentPage - 2 > 2) {
            links.add("<li><span>...</span></li>");
        }


        for(int cur = Math.max(1, currentPage - 2); cur <= Math.min(totalPages, currentPage + 2); cur++) {
            links.add(String.format("<li><a href=\"%s&page=%d\" class = \"%s\">%d</a></li>", currentPageURL, cur, (cur == page) ? "current" : "", cur));
        }
        if(currentPage + 2 < totalPages - 1) {
            links.add("<li><span>...</span></li>");
        }
        if(currentPage + 2 < totalPages) {
            links.add(String.format("<li><a href=\"%s&page=%d\">%d</a></li>", currentPageURL, totalPages, totalPages));
        }

        model.addObject("links", links);
        model.setViewName("dateissued-browse");
        return model;

    }
}
