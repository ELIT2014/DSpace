
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
    <essuir:profilePage language="${language}" chair="${chair}" facultyList="${facultyList}" chairListJson="${chairListJson}" isRegisterPage="false"/>
    <div class="col-md-offset-5">
        <input type="hidden" id="step" name="step" value="2"/>
        <input class="btn btn-success col-md-4" type="submit" name="submit"
               value="<fmt:message key="jsp.register.edit-profile.update.button"/>"/>
    </div>
</dspace:layout>