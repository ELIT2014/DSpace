package org.ssu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authenticate.factory.AuthenticateServiceFactory;
import org.dspace.authenticate.service.AuthenticationService;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.eperson.ChairEntity;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.FacultyEntity;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.AccountService;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.FacultyService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RegisterController {
    private final transient AuthenticationService authenticationService
            = AuthenticateServiceFactory.getInstance().getAuthenticationService();
    private final transient AccountService accountService
            = EPersonServiceFactory.getInstance().getAccountService();
    protected transient EPersonService personService
            = EPersonServiceFactory.getInstance().getEPersonService();
    private FacultyService facultyService = EPersonServiceFactory.getInstance().getFacultyService();
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerPost(HttpServletRequest request) {
        System.out.println("POST Request! Redirect!");
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:/register-dspace";
    }
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {

        Context context = UIUtil.obtainContext(request);
        String token = request.getParameter("token");
        boolean registering = true;
        if (token == null)
        {
            System.out.println("No Token! make redirect.");
            return new ModelAndView("redirect:/register-dspace");
        }
        else
        {
            System.out.println("WE've got token. Display page!");
            // We have a token. Find out who the it's for
            String email = accountService.getEmail(context, token);

            EPerson eperson = null;

            if (email != null)
            {
                eperson = personService.findByEmail(context, email);
            }

            // Both forms need an EPerson object (if any)
            request.setAttribute("eperson", eperson);

            // And the token
            request.setAttribute("token", token);

            if (registering && (email != null))
            {
                // Indicate if user can set password
                boolean setPassword =
                        authenticationService.allowSetPassword(context, request, email);
                request.setAttribute("set.password", setPassword);
                System.out.println("display page here");
                // Forward to "personal info page"
//                JSPManager.showJSP(request, response,
//                        "/register/registration-form.jsp");
                Map<Integer, List<ChairEntity>> chairList = facultyService.findAll(context).stream().collect(Collectors.toMap(FacultyEntity::getId, FacultyEntity::getChairs));
                ModelAndView model = new ModelAndView();
                model.addObject("facultyList", facultyService.findAll(context));
                model.addObject("chairListJson", new ObjectMapper().writeValueAsString(chairList));

                model.addObject("supportedLocales", I18nUtil.getSupportedLocales());
                model.addObject("sessionLocale", UIUtil.getSessionLocale(request));
                model.addObject("token", token);
                model.setViewName("register");
                return model;
            }
            else {
                System.out.println("invalid token or forgot password - redirect");
                return new ModelAndView("redirect:/register-dspace");
            }
        }
//        return "redirect:/dspace-register";
    }
}
