<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
  <title>User</title>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="<c:url value="resources/css/bootstrap.min.css" />">
  <link rel="stylesheet" href="<c:url value="resources/css/main.css" />">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
</head>
<body>
<jsp:include page="../pages/head.jsp"/>
<div class="container">
  <div class="jumbotron">
    <div class="row">
      <div class="col-sm-6">
        <h2>${user.getUsername()} "${user.getNickname()}"</h2>
        <h3><a href="mailto:${user.getEmail()}">${user.getEmail()}</a></h3>
      </div>
      <div class="col-sm-6">
        <div class="row">
          <p><a href="<c:url value="/tasks?id=${user.getId()}"/>">Tasks: <c:out value="${user.getTasks().size()}"/></a></p>
        </div>
        <div class="row">
          <p>Tasks rate: <c:out value="${rateOfTasks}"/></p>
        </div>
        <div class="row">
          <p>Answers: <c:out value="${user.getRatesOfAnsweredTasks().size()}"/></p>
        </div>
        <div class="row">
          <p>Answers rate: <c:out value="${user.getAnswersRate()}"/></p>
        </div>
        <div class="row">
          <p>Rate: <c:out value="${rate}" /></p>
        </div>
      </div>
    </div>
    <hr/>
    <div class="row text-center">
      <c:forEach var="achievement" items="${user.getAchievements()}">
        <div class="col-sm-2 text-center">
          <img src="${achievement.getPicture()}" title="${achievement.getText()}" class="achievement-out">
          <p style="font-size: 15px"><c:out value="${achievement.getTitle()}"/></p>
        </div>
      </c:forEach>
    </div>
  </div>
</div>
</body>
</html>
