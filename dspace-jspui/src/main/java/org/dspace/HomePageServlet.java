package org.dspace;

import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.MetadataSchema;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.core.factory.CoreServiceFactory;
import org.dspace.core.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.servlet.HttpServletBean;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@Configurable
public class HomePageServlet extends DSpaceServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

    }
    @Override
    protected void doDSGet(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
        NewsService newsService = CoreServiceFactory.getInstance().getNewsService();
        String topNews = newsService.readNewsFile(I18nUtil.getMessage("news-top.html", context.getCurrentLocale()));
        String sideNews  = newsService.readNewsFile(I18nUtil.getMessage("news-side.html", context.getCurrentLocale()));
        request.setAttribute("top-news", topNews);
        request.setAttribute("side-news", sideNews);
        JSPManager.showJSP(request, response, "home.jsp");
    }

}
