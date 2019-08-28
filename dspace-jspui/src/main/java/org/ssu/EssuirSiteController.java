package org.ssu;

import org.dspace.core.I18nUtil;
import org.dspace.core.factory.CoreServiceFactory;
import org.dspace.core.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@RequestMapping("/")
@Controller
public class EssuirSiteController {
    @RequestMapping("/")
    public ModelAndView homePage(ModelAndView model, HttpServletRequest request) {
        Locale locale = new Locale(request.getParameter("locale"));
        NewsService newsService = CoreServiceFactory.getInstance().getNewsService();
        String topNews = newsService.readNewsFile(I18nUtil.getMessage("news-top.html", locale));
        String sideNews = newsService.readNewsFile(I18nUtil.getMessage("news-side.html", locale));

        model.addObject("topNews", topNews);
        model.addObject("sideNews", sideNews);

        model.setViewName("home");
        return model;
    }
}
