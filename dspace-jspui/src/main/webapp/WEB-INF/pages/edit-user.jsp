<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
           prefix="fmt" %>


<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@taglib prefix="essuir" tagdir="/WEB-INF/tags/essuir" %>

<dspace:layout style="submission" titlekey="jsp.dspace-admin.eperson-main.title"
               navbar="admin"
               locbar="link"
               parenttitlekey="jsp.administer"
               parentlink="/dspace-admin">
    <h1><fmt:message key="jsp.dspace-admin.eperson-edit.heading">
        <fmt:param>${email}</fmt:param>
    </fmt:message>
        <dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext, \"help.site-admin\") + \"#epeople\"%>"><fmt:message key="jsp.help"/></dspace:popup>
    </h1>

    <form class="form-horizontal" action="<%= request.getContextPath() %>/dspace-admin/edit-epeople" method="post">
        <div class="form-group">
            <label class="col-md-offset-3 col-md-2 control-label" for="email"><fmt:message
                    key="jsp.dspace-admin.eperson-edit.email"/></label>
            <div class="col-md-3">
                <input class="form-control" type="text" name="email" id="email" size="40" value="${email}"/>
            </div>
        </div>

        <essuir:profilePage language="${language}" chair="${chair}" facultyList="${facultyList}" chairListJson="${chairListJson}" isRegisterPage="false" isEditUserPage="true"/>

        <div>
            <input type="hidden" id="step" name="step" value="2"/>
            <input class="btn btn-success col-md-4" type="submit" name="submit"
                   value="<fmt:message key="jsp.register.edit-profile.update.button"/>"/>
        </div>
    </form>
</dspace:layout>