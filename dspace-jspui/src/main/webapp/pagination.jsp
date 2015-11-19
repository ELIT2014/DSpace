<%
    int perPage = ConfigurationManager.getIntProperty("webui.collectionhome.perpage", 20);
    int totalPublications = bi.getTotal();
    int from = bi.getStart();
    int to = bi.getFinish();
    int totalPages = (int) Math.ceil(Double.valueOf(totalPublications) / perPage);
    int currentPage = bi.getOffset() / perPage + 1;


    int leftPage = Math.max(1, currentPage - 2);
    int rightPage = Math.min(totalPages, currentPage + 2);
%>

<nav role="pagination">
    <ul class="cd-pagination no-space move-buttons custom-icons">
        <li class="button">
            <a href="<%= prev %>"
                <% if(!bi.hasPrevPage()) { %>
                  class = "disabled"
                <% } %>
            ><fmt:message key="pagination.prev"/></a></li>

        <% if(leftPage > 1) {%>
            <li><a href="<%= linkBase %>" <% if(1 == currentPage) { %> class="current" <% } %> >1</a></li>
            <% if(leftPage > 2) {%>
                <li><span>...</span></li>
            <%  }  %>
        <%  }  %>

        <% for(int i = leftPage; i <= rightPage; i++) {
                String link = linkBase + "?offset=" + Integer.valueOf(perPage * (i - 1)).toString();

        %>
            <li><a href="<%= link %>" <% if(i == currentPage) { %> class="current" <% } %> > <%= i %></a></li>
        <%  }  %>

        <% if(rightPage < totalPages) {%>
            <% if(rightPage < totalPages - 1) {%>
                <li><span>...</span></li>
            <%  }  %>
             <li><a href="<%= linkBase + "?offset=" + Integer.valueOf(perPage * (totalPages - 1)).toString() %>" <% if(totalPages == currentPage) { %> class="current" <% } %> ><%= totalPages %></a></li>
        <%  }  %>


        <li class="button">
            <a href="<%= next %>"
                <% if(!bi.hasNextPage()) { %>
                    class = "disabled"
                 <% } %>
             ><fmt:message key="pagination.next"/></a></li>


    </ul>
</nav>
<%--


Per page: <%= perPage %> <br/>
Total publications: <%= totalPublications %><br/>
From: <%= from %><br/>
To: <%= to %><br/>
Total pages: <%= totalPages %><br/>
--%>
