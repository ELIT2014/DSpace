<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%@ page import="java.util.Locale" %>


<dspace:layout locbar="nolink" title="Authors list" feedData="NONE">


    <c:if test="${hasMessage}">
        <div class="alert alert-success" role="alert">test alert message after saving</div>
    </c:if>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">
                <c:choose>
                    <c:when test="${empty author}">
                        Add new author
                    </c:when>
                    <c:otherwise>
                        Edit author
                    </c:otherwise>
                </c:choose>
            </h3>
        </div>
        <div class="panel-body">
            <form method="post" action="" class="form-horizontal">
                <div class="form-group">
                    <label for="surnameEn" class="col-sm-2 control-label">Surname in English</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="surnameEn" id="surnameEn" placeholder="Surname in English" value="${author.getSurname(Locale.ENGLISH)}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="initialsEn" class="col-sm-2 control-label">Initials in English</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="initialsEn" id="initialsEn" placeholder="Initials in English" value="${author.getInitials(Locale.ENGLISH)}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="surnameRu" class="col-sm-2 control-label">Фамилия на русском</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="surnameRu" id="surnameRu" placeholder="Фамилия на русском" value="${author.getSurname(Locale.forLanguageTag("ru"))}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="initialsRu" class="col-sm-2 control-label">Инициалы на русском</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="initialsRu" id="initialsRu" placeholder="Инициалы на русском" value="${author.getInitials(Locale.forLanguageTag("ru"))}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="surnameUk" class="col-sm-2 control-label">Прізвище українською</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="surnameUk" id="surnameUk" placeholder="Прізвище українською" value="${author.getSurname(Locale.forLanguageTag("uk"))}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="initialsUk" class="col-sm-2 control-label">Ініціали українською</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="initialsUk" id="initialsUk" placeholder="Ініціали українською"value="${author.getInitials(Locale.forLanguageTag("uk"))}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="orcid" class="col-sm-2 control-label">ORCID</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="orcid" id="orcid" placeholder="ORCID" value="${author.orcid}">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="submit" class="btn btn-success">Save</button>
                    </div>
                </div>
            </form>
        </div>
    </div>


</dspace:layout>
