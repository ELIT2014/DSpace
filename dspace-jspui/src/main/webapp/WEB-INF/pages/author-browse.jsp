<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@taglib prefix="essuir" tagdir="/WEB-INF/tags/essuir"%>

<dspace:layout locbar="commLink" titlekey="browse.page-author">



    <div class="panel panel-primary">
        <div class="panel-heading text-center">
            <fmt:message key="browse.full.range">
                <fmt:param value="${startIndex}"/>
                <fmt:param value="${finishIndex}"/>
                <fmt:param value="${totalItems}"/>
            </fmt:message>
            <a href="#" class="pull-right glyphicon glyphicon-filter" aria-hidden="true"  data-toggle="modal" data-target="#searchModal"></a>
        </div>

        <c:forEach items="${itemList}" var="author">
            ${author}<br/>
        </c:forEach>

        <div class="panel-footer text-center">
            <essuir:pagination links="${links}" prevPageUrl="${prevPageUrl}" prevPageDisabled="${prevPageDisabled}" nextPageUrl="${nextPageUrl}" nextPageDisabled="${nextPageDisabled}"/>
        </div>
    </div>

</dspace:layout>