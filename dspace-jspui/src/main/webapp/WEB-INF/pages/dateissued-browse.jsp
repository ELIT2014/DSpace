<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>

<dspace:layout locbar="commLink" titlekey="jsp.top50items" feedData="NONE">
    <div class="panel panel-primary">
        <div class="panel-heading text-center">
            <fmt:message key="browse.full.range">
                <fmt:param value="${startIndex}"/>
                <fmt:param value="${finishIndex}"/>
                <fmt:param value="${totalItems}"/>
            </fmt:message>
        </div>
        <table align="center" class="table" summary="This table browses all dspace content" >
            <thead>
            <tr>
                <th id="t1" class="oddRowEvenCol"><strong>Рік випуску</strong></th>
                <th id="t2" class="oddRowOddCol">Назва</th>
                <th id="t3" class="oddRowEvenCol">Автор(и)</th>
                <th id="t4" class="oddRowOddCol">Вид документа</th>
                <th id="t5" class="oddRowEvenCol">Переглянуто</th>
                <th id="t6" class="oddRowOddCol">Завантажено</th>
            </tr>
            </thead>
            <tbody>
        <c:forEach items="${items}" var="item">
            <tr>
                <td align="right" class="oddRowEvenCol"><strong>${item.year}</strong></td>
                <td class="oddRowOddCol"><a href="/handle/${item.handle}">${item.title}</a></td>
                <td class="oddRowEvenCol">${item.authors}</td>
                <td class="oddRowOddCol">${item.type}</td>
                <td class="oddRowEvenCol">${item.views}</td>
                <td class="oddRowOddCol">${item.downloads}</td>
            </tr>
        </c:forEach>
            </tbody>
        </table>
    </div>
</dspace:layout>