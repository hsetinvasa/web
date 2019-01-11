<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%--Context path--%>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <title>Home page</title>
        <link rel="stylesheet" href="${cp}/wro/all.css"/>
        <script src="${cp}/wro/all.js"></script>
        <script>
            function ajaxGetUser(){
                $.ajax({
                    type : "POST",
                    url : "${cp}/admin/users",
                    dataType : "json",
                    headers: {"${_csrf.headerName}":"${_csrf.token}"},
                    data: {},
                    cache: false,
                    error:function(jqXHR,  statusText){
                        alert("Exception prone when fetch users' data with "+statusText+":["+jqXHR.status+"]:\n\t"+jqXHR.statusText);
                    },
                    success:function(data){  
                        if(data.total==0){
                            $("#listuser").html("");
                        }else{
                            var html="<table border=\"1\"  class=\"ui-widget-content ui-corner-all\"><thead><tr><th><fmt:message key="account"/></th><th><fmt:message key="name"/></th><th><fmt:message key="birthday"/></th></tr></thead><tbody>";
                            for(var i=0;i<data.total;i++){
                                var user = data.users[i];
                                <sec:authorize access="hasRole('ROLE_ADMIN')" var="isAdmin">
                                html = html + "<tr><td><a href=\"${cp}/member/edit/"+user.account+"\">"+user.account+"</a></td><td>"+user.name+"</td><td>"+user.birthday+"</td></tr>";
                                </sec:authorize>
                                <c:if test="${not isAdmin}">
                                html = html + "<tr><td>"+user.account+"</td><td>"+user.name+"</td><td>"+user.birthday+"</td></tr>";
                                </c:if>
                            }
                            html += "</tbody></table>";
                            $("#listuser").hide().html(html).show("bounce",{},2000);
                        }
                    }
                });
            }
            $(function() {
                $("center") .show("puff");
            });
        </script>
    </head>
    <body>
        <table border="0" style="width:100%">
            <sec:authorize access="authenticated" var="logined">
                <tr>
                    <td><a href="${cp}/changePassword"><fmt:message key="changePassword"/></a></td>
                    <td align="right">
                        <form action="${cp}/j_spring_security_logout" method="post" style="display: inline">
                            <!--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>-->
                            <sec:csrfInput />
                            <input type="submit" value="<sec:authentication property="principal.username"/>&nbsp;<fmt:message key="logout"/>"/>
                        </form>
                    </td>
                </tr>
            </sec:authorize>
            <c:if test="${not logined}">
                <tr>
                    <td align="right"><a href="${cp}/user/myinfo"><fmt:message key="myinfo"/></a></td>
                </tr>
            </c:if>
        </table>

    <center  style="display: none"><c:set var="world"><fmt:message key="world"/></c:set>
        <h1><fmt:message key="hello"/> ${empty member?world:member.name}</h1>
        <c:if test="${not empty member}">
            <table id="myinfo" border="1" class="ui-widget-content ui-corner-all">
                <tr><th colspan="2"><fmt:message key="myinfo"/></th></tr>
                <tr><td><fmt:message key="account"/>:</td><td>${member.account}</td></tr>
                <tr><td><fmt:message key="name"/>:</td><td>${member.name}</td></tr>
                <tr><td><fmt:message key="enabled"/>:</td><td><c:if test="${'Y' eq member.enabled}"><fmt:message key="true"/></c:if>
                        <c:if test="${'Y' ne member.enabled}"><fmt:message key="false"/></c:if></td></tr>
                <tr><td><fmt:message key="birthday"/>:</td><td><fmt:formatDate value="${member.birthday}" pattern="yyyy/MM/dd"/></td></tr>
                <tr><td><fmt:message key="role"/>:</td><td>
                        <c:forEach var="authority" items="${member.authorities}">
                            ${authority.authority}&nbsp;
                        </c:forEach>
                    </td></tr>
            </table>
        </c:if><br/>
        <c:set var="adminAjaxList"><fmt:message key="adminAjaxList"/></c:set>
        <input type="button" onclick="ajaxGetUser()" value="<c:out value="${adminAjaxList}" escapeXml="true"/>"/>
        <div id="listuser"></div>
    </center>
</body>
</html>
