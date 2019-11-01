<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@taglib prefix="essuir" tagdir="/WEB-INF/tags/essuir" %>

<dspace:layout style="submission" titlekey="jsp.register.edit-profile.title" nocache="true">
    <c:if test="${missingFields}">
        <p class="alert alert-info"><fmt:message key="jsp.register.edit-profile.info1"/></p>
    </c:if>
    <c:if test="${passwordProblem}">
        <p class="alert alert-warning"><fmt:message key="jsp.register.edit-profile.info2"/></p>
    </c:if>

    <form class="form-horizontal" action="<%= request.getContextPath() %>/profile" method="post">

        <div class="form-group">
            <label class="col-md-offset-3 col-md-2 control-label" for="tfirst_name"><fmt:message
                    key="jsp.register.profile-form.fname.field"/></label>
            <div class="col-md-3">
                <input class="form-control" type="text" name="first_name" id="tfirst_name" size="40" value="${firstName}"/>
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-offset-3 col-md-2 control-label" for="tlast_name"><fmt:message
                    key="jsp.register.profile-form.lname.field"/></label>
            <div class="col-md-3"><input class="form-control" type="text" name="last_name" id="tlast_name" size="40"
                                         value="${lastName}"/></div>
        </div>
        <div class="form-group">
            <label class="col-md-offset-3 col-md-2 control-label" for="tphone"><fmt:message
                    key="jsp.register.profile-form.phone.field"/></label>
            <div class="col-md-3">
                <input class="form-control" type="text" name="phone" id="tphone" size="40" maxlength="32" value="${phone}"/>
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-offset-3 col-md-2 control-label" for="tlanguage"><strong><fmt:message
                    key="jsp.register.profile-form.language.field"/></strong></label>
            <div class="col-md-3">
                <select class="form-control" name="language" id="tlanguage">
                    <c:forEach items="${supportedLocales}" var="supportedLocale">
                        <c:set var="selected" value=""/>
                        <c:if test="${language == supportedLocale.toString()}">
                            <c:set var="selected" value="selected = \"selected\""/>
                        </c:if>

                        <option ${selected} value="${supportedLocale.toString()}">${supportedLocale.getDisplayName(sessionLocale)}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="col-md-offset-5">
            <input class="btn btn-success col-md-4" type="submit" name="submit"
                   value="<fmt:message key="jsp.register.edit-profile.update.button"/>"/>
        </div>
    </form>
</dspace:layout>