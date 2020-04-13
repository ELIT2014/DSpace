<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@taglib prefix="essuir" tagdir="/WEB-INF/tags/essuir" %>

<dspace:layout style="submission" titlekey="jsp.register.edit-profile.title" nocache="true">
    <c:if test="${not isAllFieldsFilled}">
        <p class="alert alert-info"><fmt:message key="jsp.register.edit-profile.info1"/></p>
    </c:if>

    <c:if test="${not isPasswordOk}">
        <p class="alert alert-warning"><strong><fmt:message key="jsp.register.registration-form.instruct2"/></strong></p>
    </c:if>

    <form class="form-horizontal" action="<%= request.getContextPath() %>/register" method="post">
        <essuir:profilePage language="${language}" chair="${chair}" facultyList="${facultyList}" chairListJson="${chairListJson}" isRegisterPage="true"/>
    </form>
</dspace:layout>