<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="essuir" tagdir="/WEB-INF/tags/essuir" %>

<dspace:layout titlekey="jsp.search.title">
    <h2><fmt:message key="jsp.search.title"/></h2>


    <div class="discovery-query panel-heading">
        <form action="simple-search" method="get">
            <label for="tlocation">
                <fmt:message key="jsp.search.results.searchin"/>
            </label>
            <select name="location" id="tlocation">
                <%
                    if (scope == null)
                    {
                        // Scope of the search was all of DSpace.  The scope control will list
                        // "all of DSpace" and the communities.
                %>
                    <%-- <option selected value="/">All of DSpace</option> --%>
                <option selected="selected" value="/"><fmt:message key="jsp.general.genericScope"/></option>
                <%  }
                else
                {
                %>
                <option value="/"><fmt:message key="jsp.general.genericScope"/></option>
                <%  }
                    for (DSpaceObject dso : scopes)
                    {
                %>
                <option value="<%= dso.getHandle() %>" <%=dso.getHandle().equals(searchScope)?"selected=\"selected\"":"" %>>
                    <%= dso.getName() %>
                </option>
                <%
                    }
                %>
            </select><br/>
            <label for="query"><fmt:message key="jsp.search.results.searchfor"/></label>
            <input type="text" size="50" id="query" name="query" value="<%= (query==null ? "" : Utils.addEntities(query)) %>"/>
            <input type="submit" id="main-query-submit" class="btn btn-primary" value="<fmt:message key="jsp.general.go"/>" />
            <% if (StringUtils.isNotBlank(spellCheckQuery)) {%>
            <p class="lead"><fmt:message key="jsp.search.didyoumean"><fmt:param><a id="spellCheckQuery" data-spell="<%= Utils.addEntities(spellCheckQuery) %>" href="#"><%= spellCheckQuery %></a></fmt:param></fmt:message></p>
            <% } %>
            <input type="hidden" value="<%= rpp %>" name="rpp" />
            <input type="hidden" value="<%= Utils.addEntities(sortedBy) %>" name="sort_by" />
            <input type="hidden" value="<%= Utils.addEntities(order) %>" name="order" />
            <% if (appliedFilters.size() > 0 ) { %>
            <div class="discovery-search-appliedFilters">
                <span><fmt:message key="jsp.search.filter.applied" /></span>
                <%
                    int idx = 1;
                    for (String[] filter : appliedFilters)
                    {
                        boolean found = false;
                %>
                <select id="filter_field_<%=idx %>" name="filter_field_<%=idx %>">
                    <%
                        for (DiscoverySearchFilter searchFilter : availableFilters)
                        {
                            String fkey = "jsp.search.filter." + Escape.uriParam(searchFilter.getIndexFieldName());
                    %><option value="<%= Utils.addEntities(searchFilter.getIndexFieldName()) %>"<%
                    if (searchFilter.getIndexFieldName().equals(filter[0]))
                    {
                %> selected="selected"<%
                        found = true;
                    }
                %>><fmt:message key="<%= fkey %>"/></option><%
                    }
                    if (!found)
                    {
                        String fkey = "jsp.search.filter." + Escape.uriParam(filter[0]);
                %><option value="<%= Utils.addEntities(filter[0]) %>" selected="selected"><fmt:message key="<%= fkey %>"/></option><%
                    }
                %>
                </select>
                <select id="filter_type_<%=idx %>" name="filter_type_<%=idx %>">
                    <%
                        for (String opt : options)
                        {
                            String fkey = "jsp.search.filter.op." + Escape.uriParam(opt);
                    %><option value="<%= Utils.addEntities(opt) %>"<%= opt.equals(filter[1])?" selected=\"selected\"":"" %>><fmt:message key="<%= fkey %>"/></option><%
                    }
                %>
                </select>
                <input type="text" id="filter_value_<%=idx %>" name="filter_value_<%=idx %>" value="<%= Utils.addEntities(filter[2]) %>" size="45"/>
                <input class="btn btn-default" type="submit" id="submit_filter_remove_<%=idx %>" name="submit_filter_remove_<%=idx %>" value="X" />
                <br/>
                <%
                        idx++;
                    }
                %>
            </div>
            <% } %>
            <a class="btn btn-default" href="<%= request.getContextPath()+"/simple-search" %>"><fmt:message key="jsp.search.general.new-search" /></a>
        </form>
    </div>


</dspace:layout>