<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <title>Home</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value="resources/css/bootstrap.min.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/jqcloud.css" />">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
</head>
<body>
<jsp:include page="../pages/head.jsp"/>
    <div class="container" width="100%">
        <div class="jumbotron">
            <div class="row">
                <div class="col-sm-6">
                    <h3><strong>Most popular tasks</strong></h3>
                    <div class="row">
                        <div class="col-sm-5">
                            <h4>Title</h4>
                        </div>
                        <div class="col-sm-3 text-center">
                            <h4>Answers</h4>
                        </div>
                    </div>
                    <c:forEach var="task" items="${NMostPopularTasks}">
                        <div class="row">
                            <div class="col-sm-5">
                                <a href="<c:url value="/task?id=${task.getId()}"/>">
                                    <c:out value="${task.getTitle()}"/>
                                </a>
                            </div>
                            <div class="col-sm-3 text-center">
                                <c:out value="${task.getRatesByUsers().size()}"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <div class="col-sm-6">
                    <h3><strong>Most popular users</strong></h3>
                    <div class="row">
                        <div class="col-sm-5">
                            <h4>User</h4>
                        </div>
                        <div class="col-sm-3 text-center">
                            <h4>Rate</h4>
                        </div>
                    </div>
                    <c:forEach var="user" items="${TopNUsers}">
                        <div class="row">
                            <div class="col-sm-5">
                                <a href="<c:url value="/user?id=${user.getId()}"/>">
                                    <c:out value="${user.getNickname()}"/>
                                </a>
                            </div>
                            <div class="col-sm-3 text-center">
                                <c:out value="${TopNUsersMap.get(user).getRate()}"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">
                    <h3><strong>Latest tasks</strong></h3>
                    <c:forEach var="task" items="${lastNTasks}">
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
                </div>
                <div class="col-sm-6">
                    <h3><strong>Unresolved tasks</strong></h3>
                    <c:forEach var="task" items="${NUnresolvedTasks}">
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
                </div>
            </div>
        </div>
        <div id="wordcloud" class="wordcloud" style="position: absolute"></div>
    </div>
<script src="<c:url value="resources/js/jquery-2.1.4.min.js" />"></script>
<script src="<c:url value="resources/js/jqcloud-1.0.4.min.js" />"></script>
<script>var word_list = [
    <c:forEach var="tag" items="${tagCloud}" varStatus="i">
        {text: "${tag.getTag()}", weight: ${i.index/5}, link: "<c:url value="/tasks?tag=${tag.getTag()}"/>"},
    </c:forEach>
];
$(document).ready(function() {
    $("#wordcloud").jQCloud(word_list, {width: 380, height: 250});
});</script>
</body>
</html>