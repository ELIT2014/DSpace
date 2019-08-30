package org.ssu;

import org.dspace.core.I18nUtil;
import org.dspace.core.factory.CoreServiceFactory;
import org.dspace.core.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.ssu.entity.ItemTypeResponse;
import org.ssu.types.TypeLocalization;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/")
@Controller
public class EssuirSiteController {

    @Resource
    private TypeLocalization typeLocalization;

    @RequestMapping("/")
    public ModelAndView homePage(ModelAndView model, HttpServletRequest request) {
        Locale locale = new Locale(Optional.ofNullable(request.getParameter("locale")).orElse("en"));
        NewsService newsService = CoreServiceFactory.getInstance().getNewsService();
        String topNews = newsService.readNewsFile(I18nUtil.getMessage("news-top.html", locale));
        String sideNews = newsService.readNewsFile(I18nUtil.getMessage("news-side.html", locale));

        List<ItemTypeResponse> submissionStatisticsByType = typeLocalization.getSubmissionStatisticsByType(locale.getLanguage());
        model.addObject("topNews", topNews);
        model.addObject("sideNews", sideNews);
        model.addObject("submissions", submissionStatisticsByType);

        model.setViewName("home");
        return model;
    }
}
