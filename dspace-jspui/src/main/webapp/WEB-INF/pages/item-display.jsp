<%@ taglib prefix="dspace" uri="http://www.dspace.org/dspace-tags.tld" %>

<dspace:layout title="${title}">
    <table class="table table-hover">
        <tr>
            <td>Title</td>
            <td>${title}</td>
        </tr>

        <tr>
            <td>Type</td>
            <td>${type}</td>
        </tr>

        <tr>
            <td>Author</td>
            <td>${authors}</td>
        </tr>
    </table>
</dspace:layout>