<%@ taglib prefix="dspace" uri="http://www.dspace.org/dspace-tags.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<dspace:layout title="${title}">
    <div class="well">
        <fmt:message key="jsp.display-item.identifier"/>
        <code>${uri}</code>
    </div>

    <table class="table table-hover">
        <tr>
            <td>Title</td>
            <td>${title}</td>
        </tr>

        <tr>
            <td>Title alternative</td>
            <td>${titleAlternative}</td>
        </tr>
        <tr>
            <td>Author</td>
            <td>
                <c:forEach items="${authors}" var="author">
                    <a href="/browse/author/${author}">${author}</a><br/>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td>Keywords</td>
            <td>
                <c:forEach items="${keywords}" var="keyword">
                    <a href="/browse/keyword/${keyword}">${keyword}</a><br/>
                </c:forEach>
            </td>
        </tr>

        <tr>
            <td>Type</td>
            <td>${type}</td>
        </tr>

        <tr>
            <td>Year</td>
            <td>${year}</td>
        </tr>

        <tr>
            <td>URI</td>
            <td><a href="${uri}">${uri}</a></td>
        </tr>

        <tr>
            <td>Publisher</td>
            <td>${publisher}</td>
        </tr>

        <tr>
            <td>Citation</td>
            <td>${citation}</td>
        </tr>
        <tr>
            <td>Abstract</td>
            <td>
                <c:forEach items="${abstracts}" var="abstractText">
                    ${abstractText}<br/>
                </c:forEach>
            </td>
        </tr>

        <tr>
            <td>In collections</td>
            <td>
                <c:forEach items="${owningCollections}" var="collection">
                    <a href = "${collection.handle}">${collection.name}</a> <br/>
                </c:forEach>
            </td>
        </tr>
    </table>
</dspace:layout>