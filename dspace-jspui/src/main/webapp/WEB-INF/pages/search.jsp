<%@ page import="org.dspace.content.DSpaceObject" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.dspace.discovery.configuration.DiscoverySearchFilterFacet" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.dspace.discovery.DiscoverResult" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="org.dspace.discovery.DiscoverQuery" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@page import="org.dspace.core.Utils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="essuir" tagdir="/WEB-INF/tags/essuir" %>


<c:set var="dspace.layout.head.last" scope="request">
    <script type="text/javascript">
        var jQ = jQuery.noConflict();
        jQ(document).ready(function() {
            jQ( "#spellCheckQuery").click(function(){
                jQ("#query").val(jQ(this).attr('data-spell'));
                jQ("#main-query-submit").click();
            });
            jQ( "#filterquery" )
                .autocomplete({
                    source: function( request, response ) {
                        jQ.ajax({
                            <%--url: "<%= request.getContextPath() %>/json/discovery/autocomplete?query=<%= URLEncoder.encode(query,"UTF-8")%><%= httpFilters.replaceAll("&amp;","&") %>",--%>
                            dataType: "json",
                            cache: false,
                            data: {
                                auto_idx: jQ("#filtername").val(),
                                auto_query: request.term,
                                auto_sort: 'count',
                                auto_type: jQ("#filtertype").val(),
                                location: '${searchScope}'
                            },
                            success: function( data ) {
                                response( jQ.map( data.autocomplete, function( item ) {
                                    var tmp_val = item.authorityKey;
                                    if (tmp_val == null || tmp_val == '')
                                    {
                                        tmp_val = item.displayedValue;
                                    }
                                    return {
                                        label: item.displayedValue + " (" + item.count + ")",
                                        value: tmp_val
                                    };
                                }))
                            }
                        })
                    }
                });
        });
        function validateFilters() {
            return document.getElementById("filterquery").value.length > 0;
        }
    </script>
</c:set>

<dspace:layout titlekey="jsp.search.title">
    <h2><fmt:message key="jsp.search.title"/></h2>


    <div class="discovery-search-form panel-default">
        <div class="discovery-query panel-heading">
            <form action="simple-search" method="get">

                <div class="form-inline">
                    <div class="form-group col-md-10">
                        <select name="location" id="tlocation" class="form-control">
                            <c:choose>
                                <c:when test="scope is null">
                                    <option selected="selected" value="/"><fmt:message key="jsp.general.genericScope"/></option>
                                </c:when>
                                <c:otherwise>
                                    <option value="/"><fmt:message key="jsp.general.genericScope"/></option>
                                </c:otherwise>
                            </c:choose>

                            <c:forEach items="${scopes}" var="scopeDisplay">
                                <c:choose>
                                    <c:when test="${scopeDisplay == searchScope}">
                                        <option value="${scopeDisplay.handle}" selected="selected">
                                            ${scopeDisplay.name}
                                        </option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${scopeDisplay.handle}">
                                                ${scopeDisplay.name}
                                        </option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </div>
                    <input type="submit" id="main-query-submit" class="btn btn-default col-md-2" value="<fmt:message key="jsp.general.go"/>" />
                </div>


                <%--<label for="query"><fmt:message key="jsp.search.results.searchfor"/></label>--%>
                <%--<input type="text" size="50" id="query" name="query" value="<%= (query==null ? "" : Utils.addEntities(query)) %>"/>--%>
                <%--<input type="submit" id="main-query-submit" class="btn btn-primary" value="<fmt:message key="jsp.general.go"/>" />--%>
                <%--<% if (StringUtils.isNotBlank(spellCheckQuery)) {%>--%>
                <%--<p class="lead"><fmt:message key="jsp.search.didyoumean"><fmt:param><a id="spellCheckQuery" data-spell="<%= Utils.addEntities(spellCheckQuery) %>" href="#"><%= spellCheckQuery %></a></fmt:param></fmt:message></p>--%>
                <%--<% } %>--%>
                <input type="hidden" value="${rpp}" name="rpp" />
                <input type="hidden" value="${Utils.addEntities(sortedBy.name)}" name="sort_by" />
                <input type="hidden" value="${Utils.addEntities(order)}" name="order" />

                <%--<% if (appliedFilters.size() > 0 ) { %>--%>
                <%--<div class="discovery-search-appliedFilters">--%>
                    <%--<span><fmt:message key="jsp.search.filter.applied" /></span>--%>
                    <%--<%--%>
                        <%--int idx = 1;--%>
                        <%--for (String[] filter : appliedFilters)--%>
                        <%--{--%>
                            <%--boolean found = false;--%>
                    <%--%>--%>
                    <%--<select id="filter_field_<%=idx %>" name="filter_field_<%=idx %>">--%>
                        <%--<%--%>
                            <%--for (DiscoverySearchFilter searchFilter : availableFilters)--%>
                            <%--{--%>
                                <%--String fkey = "jsp.search.filter." + Escape.uriParam(searchFilter.getIndexFieldName());--%>
                        <%--%><option value="<%= Utils.addEntities(searchFilter.getIndexFieldName()) %>"<%--%>
                        <%--if (searchFilter.getIndexFieldName().equals(filter[0]))--%>
                        <%--{--%>
                    <%--%> selected="selected"<%--%>
                            <%--found = true;--%>
                        <%--}--%>
                    <%--%>><fmt:message key="<%= fkey %>"/></option><%--%>
                        <%--}--%>
                        <%--if (!found)--%>
                        <%--{--%>
                            <%--String fkey = "jsp.search.filter." + Escape.uriParam(filter[0]);--%>
                    <%--%><option value="<%= Utils.addEntities(filter[0]) %>" selected="selected"><fmt:message key="<%= fkey %>"/></option><%--%>
                        <%--}--%>
                    <%--%>--%>
                    <%--</select>--%>
                    <%--<select id="filter_type_<%=idx %>" name="filter_type_<%=idx %>">--%>
                        <%--<%--%>
                            <%--for (String opt : options)--%>
                            <%--{--%>
                                <%--String fkey = "jsp.search.filter.op." + Escape.uriParam(opt);--%>
                        <%--%><option value="<%= Utils.addEntities(opt) %>"<%= opt.equals(filter[1])?" selected=\"selected\"":"" %>><fmt:message key="<%= fkey %>"/></option><%--%>
                        <%--}--%>
                    <%--%>--%>
                    <%--</select>--%>
                    <%--<input type="text" id="filter_value_<%=idx %>" name="filter_value_<%=idx %>" value="<%= Utils.addEntities(filter[2]) %>" size="45"/>--%>
                    <%--<input class="btn btn-default" type="submit" id="submit_filter_remove_<%=idx %>" name="submit_filter_remove_<%=idx %>" value="X" />--%>
                    <%--<br/>--%>
                    <%--<%--%>
                            <%--idx++;--%>
                        <%--}--%>
                    <%--%>--%>
                <%--</div>--%>
                <%--<% } %>--%>
                <a class="btn btn-default" href="<%= request.getContextPath()+"/simple-search" %>"><fmt:message key="jsp.search.general.new-search" /></a>
            </form>
        </div>
    </div>

    <div class="panel panel-primary">
        <div class="panel-heading text-center">
            <fmt:message key="browse.full.range">
                <fmt:param value="${startIndex}"/>
                <fmt:param value="${finishIndex}"/>
                <fmt:param value="${totalItems}"/>
            </fmt:message>
            <a href="#" class="pull-right glyphicon glyphicon-filter" aria-hidden="true"  data-toggle="modal" data-target="#searchModal"></a>
        </div>
         <essuir:browseExtendedTable items="${items}" />

        <div class="panel-footer text-center">
            <essuir:pagination links="${links}" prevPageUrl="${prevPageUrl}" prevPageDisabled="${prevPageDisabled}" nextPageUrl="${nextPageUrl}" nextPageDisabled="${nextPageDisabled}"/>
        </div>
    </div>


    <div class="modal fade" id="searchModal" tabindex="-1" role="dialog" aria-labelledby="searchModalLabel"
         aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <form action="" method="get" autocomplete="off">
                    <div class="modal-header">

                        <h4 class="modal-title" id="searchModalLabel"><fmt:message key="jsp.search.filter.applied"/>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </h4>
                    </div>
                    <div class="modal-body">
                        <essuir:sortOptionField sortOptions="${sortOptions}" sortedBy="${sortedBy}"/>
                        <essuir:sortOrderField sortOrder="${order}" />
                        <essuir:resultsPerPageField rpp="${rpp}" />
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal"><fmt:message key="jsp.tools.group-select-list.close.button"/></button>
                        <button type="submit" class="btn btn-primary"><fmt:message key="browse.nav.go"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>


    <dspace:sidebar>
        <h3 class="facets"><fmt:message key="jsp.search.facet.refine" /></h3>
        <div id="facets" class="facetsBox">
            <c:forEach items="${facets}" var="facet">
                <div id="facet_${facet.indexFieldName}" class="panel panel-success">
                <div class="panel-heading"><fmt:message key="jsp.search.facet.refine.${facet.indexFieldName}" /></div>
                <ul class="list-group">
                    <c:choose>
                        <c:when test="${empty queryresults.getFacetResult(facet.indexFieldName)}">
                            <c:set var="facetResult" value="${queryresults.getFacetResult(String.format(\"%s.year\", facet.indexFieldName))}"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="facetResult" value="${queryresults.getFacetResult(facet.indexFieldName)}"/>
                        </c:otherwise>
                    </c:choose>
                    <c:set value="${facetsLimit.get(facet.indexFieldName)}" var="currentFacetLimit"/>

                    <c:set var="index" value="1"/>
                    <c:forEach items="${facetResult}" var="fvalue" varStatus="idx" begin="1" step="1" end="${currentFacetLimit - 1}">
                        <li class="list-group-item"><span class="badge">${fvalue.count}</span>
                            <c:set var="filterName" value="${URLEncoder.encode(facet.indexFieldName,\"UTF-8\")}"/>
                            <c:set var="filterQuery" value="${URLEncoder.encode(fvalue.asFilterQuery,\"UTF-8\")}"/>
                            <c:set var="filterType" value="${URLEncoder.encode(fvalue.getFilterType(),\"UTF-8\")}"/>
                            <c:set var="index" value="${index + 1}" scope="page"/>

                            <a href="${handle}/simple-search?query=${queryEncoded}&amp;sort_by=${sortedBy.name}&amp;order=${order}&amp;rpp=${rpp}${httpFilters}&amp;filtername=${filterName}&amp;filterquery=${filterQuery}&amp;filtertype=${filterType}"
                               title="<fmt:message key="jsp.search.facet.narrow"><fmt:param>${altTitle}</fmt:param></fmt:message>">
                                    ${StringUtils.abbreviate(fvalue.displayedValue, 36)}
                            </a>
                        </li>
                    </c:forEach>
                    <c:if test="${currentFacetPage > 0 || index == currentFacetLimit}">
                        <li class="list-group-item"><span style="visibility: hidden;">.</span>
                            <c:set value="${facetCurrentPage.get(facet.indexFieldName)}" var="currentFacetPage"/>

                            <c:if test="${currentFacetPage > 0}">
                                <a class="pull-left" href="${handle}/simple-search?query=${queryEncoded}&amp;sort_by=${sortedBy.name}&amp;order=${order}&amp;rpp=${rpp}${httpFilters}&amp;${facet.indexFieldName}_page=${currentFacetPage-1}">
                                        <fmt:message key="jsp.search.facet.refine.previous" />
                                </a>
                            </c:if>


                            <c:if test="${index == currentFacetLimit}">
                                <a class="pull-right" href="${handle}/simple-search?query=${queryEncoded}&amp;sort_by=${sortedBy.name}&amp;order=${order}&amp;rpp=${rpp}${httpFilters}&amp;${facet.indexFieldName}_page=${currentFacetPage+1}">
                                    <fmt:message key="jsp.search.facet.refine.next" />
                                </a>
                            </c:if>
                        </li>
                    </c:if>
                </ul>
                </div>
            </c:forEach>

        </div>

    </dspace:sidebar>

</dspace:layout>