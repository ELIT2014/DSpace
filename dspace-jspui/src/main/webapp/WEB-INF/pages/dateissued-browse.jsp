<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>

<dspace:layout locbar="commLink" titlekey="jsp.top50items" feedData="NONE">
    <table class="table table-bordered">
        <thead class="thead-dark">
        <tr>
            <th scope="col">Year</th>
            <th scope="col">Title</th>
            <th scope="col">Authors</th>
            <th scope="col">Type</th>
            <th scope="col">Views</th>
            <th scope="col">Downloads</th>
        </tr>
        </thead>
        <tbody>
    <c:forEach items="${items}" var="item">
        <tr>
            <td>${item.year}</td>
            <td><a href="/handle/${item.handle}">${item.title}</a></td>
            <td>${item.authors}</td>
            <td>${item.type}</td>
            <td>${item.views}</td>
            <td>${item.downloads}</td>
        </tr>
    </c:forEach>
        </tbody>
    </table>
</dspace:layout>