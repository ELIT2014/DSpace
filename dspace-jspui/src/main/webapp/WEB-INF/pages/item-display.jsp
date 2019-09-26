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
                    <a href="${collection.handle}">${collection.name}</a> <br/>
                </c:forEach>
            </td>
        </tr>
    </table>
    <div class="row">
        <div class="col-md-6">
            <div class="panel panel-info">
                <div class="panel-heading text-center"><h3 class="panel-title">Views</h3></div>
                <div class="panel-body">
                    <c:forEach items="${views}" var="country">
                        <div class="row">
                            <div class="col-md-8">
                                <img src="/flags/${country.countryCode.toLowerCase()}.gif"
                                     alt="${country.countryName}"> ${country.countryName}
                            </div>
                            <div class="col-md-3">
                                    ${country.count}
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="panel panel-info">
                <div class="panel-heading text-center"><h3 class="panel-title">Downloads</h3></div>
                <div class="panel-body">
                    <c:forEach items="${downloads}" var="country">
                        <div class="row">
                            <div class="col-md-8">
                                <img src="/flags/${country.countryCode.toLowerCase()}.gif"
                                     alt="${country.countryName}"> ${country.countryName}
                            </div>
                            <div class="col-md-3">
                                    ${country.count}
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

    </div>


    <div class="panel panel-info">
        <div class="panel-heading text-center"><h3 class="panel-title">Files</h3></div>
        <div class="panel-body">
            <table class="table">
                <thead>
                <tr>
                    <th>File</th>
                    <th>Size</th>
                    <th>Format</th>
                    <th>Downloads</th>
                    <%--<th></th>--%>
                </tr>
                </thead>
                <tbody>
                    <c:forEach items="${bundles}" var="bundle">
                        <tr>
                            <td><a href="${bundle.link}">${bundle.filename}</a></td>
                            <td>${bundle.size}</td>
                            <td>${bundle.format}</td>
                            <td>${bundle.downloadCount}</td>
                            <%--<td><a class="btn btn-primary" target="_blank" href="/bitstream/123456789/29791/1/m3451.pdf">Download</a></td>--%>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <p class="submitFormHelp alert alert-info"><fmt:message key="jsp.display-item.copyright"/></p>
</dspace:layout>