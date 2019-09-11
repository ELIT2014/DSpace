<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="C" uri="http://java.sun.com/jsp/jstl/core" %>

<%@taglib prefix="mytaglib" tagdir="/WEB-INF/tags/mytaglib"%>

<%
    org.dspace.core.Context context = org.dspace.app.webui.util.UIUtil.obtainContext(request);
%>

<dspace:layout locbar="commLink" titlekey="jsp.community-list.title" feedData="NONE">
    <h1><fmt:message key="jsp.community-list.title"/></h1>
    <p><fmt:message key="jsp.community-list.text1"/></p>
    <div class = "tree well">
        <ul>
        <c:forEach items="${communities}" var="community">

            <mytaglib:displayCommunity community="${community}"/>
            <ul>
                <c:forEach items="${commMap.get(community.ID.toString())}" var="inner">
                    <mytaglib:displayCommunity community="${inner}"/>
                </c:forEach>
            </ul>
        </c:forEach>
        </ul>
    </div>


    <script>
        // $(function () {
        //     $('.tree li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');
        //     $('.tree li.parent_li > .icon-plus-sign').on('click', function (e) {
        //         var children = $(this).parent('li.parent_li').find(' > ul > li');
        //         children.show('fast');
        //         $(this).attr('title', 'Collapse this branch').find(' > i').addClass('icon-minus-sign').removeClass('icon-plus-sign');
        //     }
        //     $('.tree li.parent_li > .icon-minus-sign').on('click', function (e) {
        //         var children = $(this).parent('li.parent_li').find(' > ul > li');
        //         children.hide('fast');
        //         $(this).attr('title', 'Expand this branch').find(' > i').addClass('icon-plus-sign').removeClass('icon-minus-sign');
        //     }
        //     e.stopPropagation();
        // });
    </script>

</dspace:layout>