<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Task</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value="resources/css/bootstrap.min.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/stars.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/toastr.min.css" />">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
</head>
<body>
<sec:authentication property="principal.username" var="usersNickname" />
<jsp:include page="../pages/head.jsp"/>
    <div class="container">
        <div class="jumbotron">
            <c:if test="${not empty param.get('Achievement')}">
                <div class="achievement center">${param.get('Achievement')}</div>
            </c:if>
            <div>
              <h3><c:out value="${task.getTitle()}"/></h3>
            </div>
            <div>
                Type: <a href="<c:url value="/tasks?type=${task.getType()}"/>"><c:out value="${task.getType()}"/></a>
            </div>
            <div>
                Difficulty: <c:out value="${task.getDifficulty()}"/>
            </div>
            <div>
                Condition:
                ${task.getText()}
            </div>
            <div>
                <c:if test="${task.getRatesByUsers().size() != 0}">
                    Already answered <c:out value="${task.getRatesByUsers().size()}"/> people!
                </c:if>
                <c:if test="${task.getRatesByUsers().size() == 0}">
                    Nobody answered this question yet :(
                </c:if>
            </div>
            <div>
                Rate: <c:out value="${rate}" />
            </div>
              <c:if test="${empty isUsers}">
                  <c:if test="${empty isAnswered}">
                      <div>
                          <form:form cssClass="form-horizontal" modelAttribute="answer" action="/task?id=${task.getId()}" method="post">
                             <div class="inline">
                                <form:label path="text">Answer:</form:label>
                                <form:input cssClass="form-control" path="text"/>
                             </div>
                              <form:button class="btn">OK</form:button>
                          </form:form>
                      </div>
                  </c:if>
                  <c:if test="${not empty isAnswered}">
                      <div>
                        <strong>Choose a rating: </strong>
                      </div>
                      <div>
                        <span class="star-rating">
                          <input type="radio" name="rating" value="1"><i></i>
                          <input type="radio" name="rating" value="2"><i></i>
                          <input type="radio" name="rating" value="3"><i></i>
                          <input type="radio" name="rating" value="4"><i></i>
                          <input type="radio" name="rating" value="5"><i></i>
                        </span>
                      </div>
                  </c:if>
              </c:if>
            <div>
              Tags:
              <c:if test="${task.getTags().size() != 0}">
                  <c:forEach var="tag" items="${task.getTags()}">
                      <a href="<c:url value="/tasks?tag=${tag.getTag()}"/>"><c:out value="${tag.getTag()}"/></a>
                  </c:forEach>
              </c:if>
              <c:if test="${task.getTags().size() == 0}">
                  No tags.
              </c:if>
            </div>
            <c:if test="${not empty isUsers}">
                <div class="center button">
                    <a href="<c:url value="/edittask?id=${task.getId()}"/>" class="btn btn-info">Edit</a>
                    <a href="<c:url value="/deletetask?id=${task.getId()}"/>" class="btn btn-danger">Delete</a>
                </div>
            </c:if>
            <c:if test="${not empty param.get('error')}">
                <div class="error center">${param.get('error')}</div>
            </c:if>
            <c:if test="${not empty param.get('answerRight')}">
                <div class="answer center">${param.get('answerRight')}</div>
            </c:if>
        </div>
        <c:if test="${not empty task.getComments()}">
            <div>
                <c:forEach var="comment" items="${task.getComments()}">
                    <div class="comment">
                        <h3>${comment.getUser().getNickname()}</h3>
                        <p><c:out value="${comment.getComment()}"/><p>
                        <c:if test="${comment.getUser().getNickname() == usersNickname}">
                        <div class="right"><a href="<c:url value="/deletecomment?id=${comment.getId()}"/>">Delete comment</a></div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:if>
        <div>
            <form:form cssClass="form-horizontal" modelAttribute="comment" action="/task?id=${task.getId()}" method="post">
                <div class="inline">
                    <form:label path="comment">New comment:</form:label>
                    <form:textarea cssClass="form-control" path="comment"/>
                </div>
                <div>
                    <form:button class="btn">Add</form:button>
                </div>
            </form:form>
        </div>
    </div>
    <script src="<c:url value="resources/js/jquery-2.1.4.min.js" />"></script>
    <script src="<c:url value="resources/js/toastr.min.js" />"></script>
    <script>
        $('.star-rating input').click(function () {
            toastr.success('Your vote has been saved<br>' + '${Achievement}');
            $.post('/changerate',{rate: $(this).val(), id: ${task.getId()}, Achievement:"${Achievement}"}, function () {});
        });
    </script>
</body>
</html>
