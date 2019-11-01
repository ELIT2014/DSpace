package org.ssu.controller;

import org.dspace.app.webui.util.UIUtil;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@Controller
public class ProfileController {
    @RequestMapping("/profile")
    public ModelAndView profilePage(ModelAndView model , HttpServletRequest request, HttpServletResponse response) throws SQLException {
        Context dspaceContext = UIUtil.obtainContext(request);

        EPerson eperson = dspaceContext.getCurrentUser();

        Boolean attr = (Boolean) request.getAttribute("missing.fields");
        boolean missingFields = (attr != null && attr.booleanValue());

        attr = (Boolean) request.getAttribute("password.problem");
        boolean passwordProblem = (attr != null && attr.booleanValue());

        boolean ldap_enabled = ConfigurationManager.getBooleanProperty("authentication-ldap", "enable");
        boolean ldap_eperson = (ldap_enabled && (eperson.getNetid() != null) && (eperson.getNetid().equals("") == false));

        EPersonService epersonService = EPersonServiceFactory.getInstance().getEPersonService();

        // Get non-null values
        String lastName = eperson.getLastName();
        if (lastName == null) lastName = "";

        String firstName = eperson.getFirstName();
        if (firstName == null) firstName = "";

        String phone = epersonService.getMetadata(eperson, "phone");
        if (phone == null) phone = "";

        String language = epersonService.getMetadata(eperson, "language");
        if (language == null) language = "";


        model.addObject("lastName", lastName);
        model.addObject("firstName", firstName);
        model.addObject("phone", phone);
        model.addObject("language", language);
        model.addObject("supportedLocales", I18nUtil.getSupportedLocales());
        model.addObject("sessionLocale", UIUtil.getSessionLocale(request));
        model.addObject("passwordProblem", passwordProblem);
        model.addObject("missingFields", missingFields);
        model.setViewName("profile");
        return model;
    }
}
