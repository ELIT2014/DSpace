<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<dspace:layout locbar="commLink" titlekey="jsp.top50items" feedData="NONE">
    <div class="panel panel-primary">
        <div class="panel-heading text-center">
            <fmt:message key="browse.full.range">
                <fmt:param value="${startIndex}"/>
                <fmt:param value="${finishIndex}"/>
                <fmt:param value="${totalItems}"/>
            </fmt:message>
            <a href="#" class="pull-right glyphicon glyphicon-cog" aria-hidden="true"  data-toggle="modal" data-target="#searchModal"></a>
        </div>


        <table align="center" class="table" summary="This table browses all dspace content">
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
        <div class="panel-footer text-center">
            <ul class="cd-pagination no-space move-buttons custom-icons">
                <li class="button">
                    <a href="${prevPageUrl}" class = "${prevPageDisabled}"><fmt:message key="pagination.prev"/></a>
                </li>
                <c:forEach items="${links}" var="link">
                    ${link}
                </c:forEach>

                <li class="button">
                    <a href="${nextPageUrl}" class = "${nextPageDisabled}"><fmt:message key="pagination.next"/></a>
                </li>
            </ul>
        </div>
    </div>

    <div class="modal fade" id="searchModal" tabindex="-1" role="dialog" aria-labelledby="searchModalLabel"
         aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <form action="dateissued-browse" method="get" autocomplete="off">
                    <div class="modal-header">

                        <h4 class="modal-title" id="searchModalLabel"><fmt:message key="jsp.search.filter.applied"/>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        </h4>
                    </div>
                    <div class="modal-body">


                        <div class="form-group row">
                            <label for="sort_by" class="col-sm-6 col-form-label"><fmt:message
                                    key="browse.full.sort-by"/></label>
                            <div class="col-sm-6">
                                <select class="form-control" id="sort_by" name="sort_by">
                                    <c:forEach items="${sortOptions}" var="sortOption">
                                        <c:choose>
                                            <c:when test="${sortOption.name.equals(sortedBy.name)}">
                                                <option value="${sortOption.number}" selected="selected"><fmt:message
                                                        key="browse.sort-by.${sortOption.name}"/></option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${sortOption.number}"><fmt:message
                                                        key="browse.sort-by.${sortOption.name}"/></option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group row">
                            <label for="order" class="col-sm-6 col-form-label"><fmt:message key="browse.full.order"/></label>
                            <div class="col-sm-6">
                                <select id = "order" name="order" class="form-control">
                                    <option value="ASC"
                                    <c:if test="${\"ACS\".equals(sortOrder)}">
                                        selected="selected"
                                    </c:if>
                                    ><fmt:message key="browse.order.asc" /></option>
                                    <option value="DESC"
                                            <c:if test="${\"DESC\".equals(sortOrder)}">
                                                selected="selected"
                                            </c:if>
                                    ><fmt:message key="browse.order.desc" /></option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group row">
                            <label for="rpp" class="col-sm-6 col-form-label"><fmt:message key="browse.full.rpp"/></label>
                        <div class="col-sm-6">
                            <select id = "rpp" name="rpp" class="form-control">
                                <c:forEach begin="5" end="100" step="5" var="index">
                                    <option value="${index}"
                                            <c:if test="${index == rpp}">
                                                selected="selected"
                                            </c:if>
                                    >${index}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label for="year" class="col-sm-6 col-form-label"><fmt:message key="browse.nav.date.jump"/></label>
                        <div class="col-sm-6">
                            <input type="text" class="yearpicker form-control" value="${selectedYear}" name="year" id="year"/>
                        </div>
                    </div>

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal"><fmt:message key="jsp.tools.group-select-list.close.button"/></button>
                        <button type="submit" class="btn btn-primary"><fmt:message key="browse.nav.go"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function(){
            $('.yearpicker').yearpicker();
        });
    </script>
</dspace:layout>