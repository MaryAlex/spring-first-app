<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
  <meta charset="UTF-8">
  <title>Registration</title>
  <link rel="stylesheet" href="<c:url value="resources/css/bootstrap.min.css" />">
  <link rel="stylesheet" href="<c:url value="resources/css/main.css" />">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
</head>
<body>
  <div id="login-box">
    <div class="login__form">
      <h1 class="center">Registration</h1>

      <c:if test="${not empty error}">
        <div class="error">${error}</div>
      </c:if>

      <form:form cssClass="form-horizontal" modelAttribute="personAttribute" method="POST" action="/registration">
        <table>
          <tr>
            <th><form:label path="nickname">Nickname:</form:label></th>
            <td><form:input cssClass="form-control" path="nickname"/></td>
          </tr>
          <tr>
            <th><form:label path="password">Password:</form:label></th>
            <td><form:password cssClass="form-control" path="password"/></td>
          </tr>
          <tr>
            <th><form:label path="username">Your name:</form:label></th>
            <td><form:input cssClass="form-control" path="username"/></td>
          </tr>
          <tr>
            <th><form:label path="email">Email:</form:label></th>
            <td><form:input type="email" class="form-control" path="email"/></td>
          </tr>
        </table>
        <div class="center">
          <button class="btn btn-success"> Registration </button>
          <a class="btn btn-danger"  href="<c:url value='/login' />">Cancel</a>
        </div>
      </form:form>
      <sec:authorize access="isAuthenticated()"><c:redirect url="/"/> </sec:authorize>
    </div>
  </div>
</body>
</html>