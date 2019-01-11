<%-- 
    Author     : Kent Yeh
--%>

<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%--Context path--%>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
    <head>
        <title>${member.name}</title>
        <script src="${cp}/wro/all.js"></script>
        <script type="text/javascript">
            var idx = 0;
            function addAuthority(){
                var html= '<tr><td colspan="2" style="position: relative">'+
                        '<input type="hidden" name="authorities['+idx+'].account" value="${member.account}"/>'+
                        '<input type="text" name="authorities['+(idx++)+'].authority" width="30" maxlength="50"/>'+
                        '<input type="button" value="x" title="Remove this" onclick="$(this).closest(\'tr\').remove();" style="position: absolute;right: 2px"/>'+
                        '</td></tr>';
                $("tfoot").append($(html));
            }
        </script>
    </head>
    <body>
        <table border="0" style="width:100%">
            <sec:authorize access="authenticated" var="logined">
                <tr>
                    <td><a href="${cp}/user/myinfo"><fmt:message key="myinfo"/></a></td>
                    <td align="right">
                        <form action="${cp}/j_spring_security_logout" method="post" style="display: inline">
                            <!--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>-->
                            <sec:csrfInput />
                            <input type="submit" value="<sec:authentication property="principal.username"/><fmt:message key="logout"/>"/>
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
        <form action="${cp}/member/update" method="POST"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <table style="margin-left: auto;margin-right: auto">
                <thead>
                    <c:if test="${not empty errorMsg}"><tr><th colspan="2" style="color: red"><fmt:message key='exception'/>:${errorMsg}</th></tr></c:if>
                    <tr><th colspan="2" style="text-align: center"><input type="hidden" name="account" value="${member.account}">${member.account}
                        <input type="hidden" name="password" value="********"></th></tr></thead>
                <tbody>
                    <tr><td colspan="2" style="text-align: center"><input type="submit"/></td></tr>
                    <tr><td><fmt:message key="name"/>:</td><td><input type="text" name="name" value="${member.name}" width="20" maxlength="16"/></td></tr>
                    <tr><td><fmt:message key="enabled"/>:</td><td><input type="radio" name="enabled" value="Y" id="enabledY" ${"Y" eq member.enabled?"checked":""}/>
                            <label for="enabledY"><fmt:message key="true"/></label>&nbsp;
                            <input type="radio" name="enabled" value="N" id="enabledN" ${"Y" ne member.enabled?"checked":""}/>
                            <label for="enabledN"><fmt:message key="false"/></label>
                        </td></tr>
                    <tr><td><fmt:message key="birthday"/>:</td><td><input type="date" width="12" max="10" name="birthday" value="<fmt:formatDate value="${member.birthday}"/>"/></td></tr>
                </tbody>
                <tfoot>
                    <tr><td><fmt:message key="role"/></td><td style="text-align: center"><input type="button" onclick="addAuthority()" value="+"/></td></tr>
                            <c:forEach var="authority" items="${member.authorities}" varStatus="status">
                        <tr><td colspan="2" style="position: relative">
                                <input type="hidden" name="authorities[${status.index}].account" value="${member.account}"/>
                                <input type="text" name="authorities[${status.index}].authority" value="${authority.authority}" width="30" maxlength="50"/>
                                <input type="button" value="x" title="Remove this" style="position: absolute;right: 2px" onclick="$(this).closest('tr').remove();"/>
                            </td></tr>
                            <c:set var="idx" value="${status.count}"/>
                        </c:forEach>
                    <script type="text/javascript">idx=${idx};</script>
                </tfoot>
            </table>
        </form>
    </body>
</html>
