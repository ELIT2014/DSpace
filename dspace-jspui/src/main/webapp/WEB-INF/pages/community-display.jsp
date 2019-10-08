<%@ taglib prefix="dspace" uri="http://www.dspace.org/dspace-tags.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<dspace:layout title="${title}">
    <div class="panel panel-default">
        <div class="panel-body">
                ${title} : ${itemCount}
        </div>

    </div>

    <div class="row">
        <ul class="list-group col-md-6">
            <c:forEach items="${subCommunities}" var="community">

                <li class="list-group-item"><a href = "${community.handle}">${community.title} [${community.itemCount}]</a></li>
            </c:forEach>
        </ul>


        <ul class="list-group col-md-6">
            <c:forEach items="${collections}" var="collection">

                <li class="list-group-item"><a href = "${collection.handle}">${collection.title} [${collection.itemCount}]</a></li>
            </c:forEach>
        </ul>
    </div>
</dspace:layout>