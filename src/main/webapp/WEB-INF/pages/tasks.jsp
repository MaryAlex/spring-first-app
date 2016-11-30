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
            <div>
                <c:if test="${not empty param.get('Achievement')}">
                    <div class="achievement center">${param.get('Achievement')}</div>
                </c:if>
                <c:if test="${tasks.size() > 0}">
                    <c:forEach var="task" items="${tasks}">
                        <div>
                            <a href="<c:url value="/task?id=${task.getId()}"/>">
                                <h4 class="output_task"><c:out value="${task.getTitle()}"/></h4>
                            </a>
                            <div>
                                Type: <a href="<c:url value="/tasks?type=${task.getType()}"/>"><c:out value="${task.getType()}"/></a>
                            </div>
                            <div>
                                Difficulty: <c:out value="${task.getDifficulty()}"/>
                            </div>
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
                        </div>
                        <hr>
                    </c:forEach>
                </c:if>
                <c:if test="${tasks.size() == 0}">
                    <strong class="center">No tasks for you request</strong>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>
