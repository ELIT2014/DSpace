package org.ssu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.core.LogManager;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.ssu.entity.ChairEntity;
import org.ssu.entity.EssuirEperson;
import org.ssu.entity.FacultyEntity;
import org.ssu.service.EpersonService;
import org.ssu.service.FacultyService;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProfileController {
    protected transient EPersonService personService
            = EPersonServiceFactory.getInstance().getEPersonService();

    @Resource
    private EpersonService ePersonService;

    @Resource
    private FacultyService facultyService;

    @RequestMapping("/profile")
    public ModelAndView profilePage(ModelAndView model , HttpServletRequest request, HttpServletResponse response) throws SQLException, JsonProcessingException {
        Context dspaceContext = UIUtil.obtainContext(request);

        EPerson eperson = dspaceContext.getCurrentUser();
        EssuirEperson currentUser = ePersonService.extendEpersonInformation(eperson);

        Boolean attr = (Boolean) request.getAttribute("missing.fields");
        boolean missingFields = (attr != null && attr.booleanValue());

        attr = (Boolean) request.getAttribute("password.problem");
        boolean passwordProblem = (attr != null && attr.booleanValue());

        boolean ldap_enabled = ConfigurationManager.getBooleanProperty("authentication-ldap", "enable");
        boolean ldap_eperson = (ldap_enabled && (currentUser.getNetid() != null) && (currentUser.getNetid().equals("") == false));

        EPersonService epersonService = EPersonServiceFactory.getInstance().getEPersonService();

        // Get non-null values
        String lastName = currentUser.getLastName();
        if (lastName == null) lastName = "";

        String firstName = currentUser.getFirstName();
        if (firstName == null) firstName = "";

        String phone = epersonService.getMetadata(eperson, "phone");
        if (phone == null) phone = "";

        String language = epersonService.getMetadata(eperson, "language");
        if (language == null) language = "";

        Map<Integer, List<ChairEntity>> chairList = facultyService.getFacultyList().stream().collect(Collectors.toMap(FacultyEntity::getId, FacultyEntity::getChairs));
        model.addObject("lastName", lastName);
        model.addObject("firstName", firstName);
        model.addObject("phone", phone);
        model.addObject("language", language);
        model.addObject("position", currentUser.getPosition());
        model.addObject("chair", currentUser.getChairEntity());
        model.addObject("facultyList", facultyService.getFacultyList());
        model.addObject("chairListJson", new ObjectMapper().writeValueAsString(chairList));

        model.addObject("supportedLocales", I18nUtil.getSupportedLocales());
        model.addObject("sessionLocale", UIUtil.getSessionLocale(request));
        model.addObject("passwordProblem", passwordProblem);
        model.addObject("missingFields", missingFields);
        model.setViewName("profile");
        return model;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView updateProfile(ModelAndView model, HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException, AuthorizeException {
        Context dspaceContext = UIUtil.obtainContext(request);
        EPerson eperson = dspaceContext.getCurrentUser();
        boolean settingPassword = false;

        if (!eperson.getRequireCertificate() && !StringUtils.isEmpty(request.getParameter("password")))
        {
            settingPassword = true;
        }

        // Set the user profile info
        boolean ok = updateUserProfile(dspaceContext, eperson, request);

        if (!ok)
        {
            request.setAttribute("missing.fields", Boolean.TRUE);
        }

        if (ok && settingPassword)
        {
            // They want to set a new password.
            ok = confirmAndSetPassword(eperson, request);

            if (!ok)
            {
                request.setAttribute("password.problem", Boolean.TRUE);
            }
        }

        if (ok)
        {

            personService.update(dspaceContext, eperson);

            // Show confirmation
            request.setAttribute("password.updated", settingPassword);
            JSPManager.showJSP(request, response,
                    "/register/profile-updated.jsp");

            dspaceContext.complete();
        }
        else
        {

            request.setAttribute("eperson", eperson);

            JSPManager.showJSP(request, response, "/register/edit-profile.jsp");
        }

        return model;
    }

    /**
     * Update a user's profile information with the information in the given
     * request. This assumes that authentication has occurred. This method
     * doesn't write the changes to the database (i.e. doesn't call update.)
     *
     * @param eperson
     *            the e-person
     * @param request
     *            the request to get values from
     *
     * @return true if the user supplied all the required information, false if
     *         they left something out.
     */
    public boolean updateUserProfile(Context context, EPerson eperson,
                                     HttpServletRequest request) throws SQLException
    {
        // Get the parameters from the form
        String lastName = request.getParameter("last_name");
        String firstName = request.getParameter("first_name");
        String phone = request.getParameter("phone");
        String language = request.getParameter("language");

        // Update the eperson
        eperson.setFirstName(context, firstName);
        eperson.setLastName(context, lastName);
        personService.setMetadataSingleValue(context, eperson, "eperson" , "phone", null, null, phone);
        eperson.setLanguage(context, language);

        // Check all required fields are there
        return (!StringUtils.isEmpty(lastName) && !StringUtils.isEmpty(firstName));
    }

    /**
     * Set an eperson's password, if the passwords they typed match and are
     * acceptible. If all goes well and the password is set, null is returned.
     * Otherwise the problem is returned as a String.
     *
     * @param eperson
     *            the eperson to set the new password for
     * @param request
     *            the request containing the new password
     *
     * @return true if everything went OK, or false
     */
    public  boolean confirmAndSetPassword(EPerson eperson,
                                          HttpServletRequest request)
    {
        // Get the passwords
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("password_confirm");

        // Check it's there and long enough
        if ((password == null) || (password.length() < 6))
        {
            return false;
        }

        // Check the two passwords entered match
        if (!password.equals(passwordConfirm))
        {
            return false;
        }

        // Everything OK so far, change the password
        personService.setPassword(eperson, password);

        return true;
    }
}
