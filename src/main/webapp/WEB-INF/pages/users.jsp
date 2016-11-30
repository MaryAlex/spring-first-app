<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
  <title>Tasks</title>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="<c:url value="resources/css/bootstrap.min.css" />">
  <link rel="stylesheet" href="<c:url value="resources/css/main.css" />">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
</head>
<body>
<jsp:include page="../pages/head.jsp"/>
<div class="container" width="100%">
  <div class="jumbotron">
    <strong>
      <div class="row users_output">
        <div class="col-sm-3">
          <p class="center">Nickname</p>
        </div>
        <div class="col-sm-3">
          <p class="center">Email</p>
        </div>
        <div class="col-sm-1 text-center">
          <p class="center">Tasks</p>
        </div>
        <div class="col-sm-1 text-center">
          <p class="center">Answers</p>
        </div>
        <div class="col-sm-2 text-center">
          <p class="center">Answers rate</p>
        </div>
      </div>
    </strong>
    <hr>
    <c:forEach var="user" items="${users}">
      <div class="row users_output">
        <div class="col-sm-3 text-center">
          <a href="<c:url value="/user?id=${user.getId()}"/>">
            <c:out value="${user.getNickname()}"/>
          </a>
        </div>
        <div class="col-sm-3 text-center">
          <c:out value="${user.getEmail()}"/>
        </div>
        <div class="col-sm-1 text-center">
          <a href="<c:url value="/tasks?id=${user.getId()}"/>"><c:out value="${user.getTasks().size()}"/></a>
        </div>
        <div class="col-sm-1 text-center">
          <p class="inline"> <c:out value="${user.getRatesOfAnsweredTasks().size()}"/></p>
        </div>
        <div class="col-sm-2 text-center">
          <p class="inline"> <c:out value="${user.getAnswersRate()}"/></p>
        </div>
      </div>
      <hr>
    </c:forEach>
  </div>
</div>
</body>
</html>
