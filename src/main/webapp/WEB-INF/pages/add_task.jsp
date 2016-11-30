<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <title>Add New Task</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value="resources/css/bootstrap.min.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/bootstrap-markdown.min.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/font-awesome.min.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/select2.css" />">
    <link rel="stylesheet" href="<c:url value="resources/css/select2-bootstrap.css" />">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
</head>
<body>
<jsp:include page="../pages/head.jsp"/>
<div class="container">
  <h3 class="center margin_top">Add task</h3>
  <c:if test="${not empty error}">
    <div class="error center">${error}</div>
  </c:if>
  <c:if test="${not empty param.get('error')}">
    <div class="error center">${param.get('error')}</div>
  </c:if>
  <form:form modelAttribute="taskAttribute" method="POST" action="${pageContext.request.contextPath}">
    <div class="form-group">
      <form:input path="title" class="form-control" placeholder="Title"/>
    </div>
    <div class="form-group">
      <form:textarea data-provide="markdown" id="add" path="text" cols="30" rows="10" class="form-control" placeholder="Text"/>
    </div>
    <div class="form-group">
        <div class="input_fields_wrap">
            <form:input type="text" class="form-control inline answer_div" placeholder="Answer" path="answers[0].text"/>
            <button class="add_field_button btn btn-success answer_add inline"><span class="fa fa-plus"></span></button>
            <c:forEach items="${taskAttribute.answers}" var="Answer" varStatus="i" begin="1">
                <div>
                    <form:input type="text" class="form-control inline answer_div" placeholder="Answer" path="answers[${i.index}].text"/>
                    <button class='btn btn-danger answer_delete remove_field'><span class='fa fa-close'></span></button>
                </div>
            </c:forEach>
        </div>
    </div>
    <div>
        <div class="form-group inline">
            <form:select path="difficulty" class="form-control">
                <form:option value="1">Very Easy</form:option>
                <form:option value="2">Easy</form:option>
                <form:option value="3">Middle</form:option>
                <form:option value="4">Difficult</form:option>
                <form:option value="5">Very difficult</form:option>
            </form:select>
        </div>
        <div class="form-group inline select_margin">
            <form:select path="type" class="form-control">
                <form:option value="Java">Java</form:option>
                <form:option value="C#">C#</form:option>
                <form:option value="Python">Python</form:option>
                <form:option value="Ruby">Ruby</form:option>
            </form:select>
        </div>
    </div>
    <div class="form-group">
            <form:input path="tags[0].tag" cssClass="js-example-tags form-control" multiple="multiple" placeholder="Tags"/>
    </div>
    <div class="form-group">
      <button class="btn btn-success">Add</button>
    </div>
  </form:form>
</div>
<script src="<c:url value="resources/js/jquery-2.1.4.min.js" />"></script>
<script src="<c:url value="resources/js/bootstrap-markdown.js" />"></script>
<script src="<c:url value="resources/js/to-markdown.js" />"></script>
<script src="<c:url value="resources/js/markdown.js" />"></script>
<script src="<c:url value="resources/js/select2.min.js" />"></script>
<script>
    $(document).ready(function() {
        var max_fields      = 10; //maximum input boxes allowed
        var wrapper         = $(".input_fields_wrap"); //Fields wrapper
        var add_button      = $(".add_field_button"); //Add button ID

        var x = ${taskAttribute.answers.size()}; //initlal text box count
        $(add_button).click(function(e){ //on add input button click
            e.preventDefault();
            if(x < max_fields){ //max input box allowed
                x++; //text box increment
                $(wrapper).append("<div><input type='text' class='form-control answer_input inline answer_div' placeholder='Answer' name='answers["+(x-1)+"].text'/><button class='btn btn-danger answer_delete remove_field'><span class='fa fa-close'></span></button></div>"); //add input box
            }
        });

        $(wrapper).on("click",".remove_field", function(e){ //user click on remove text
            e.preventDefault(); $(this).parent('div').remove(); x--;
        })
    });
</script>
<script>
    $(".js-example-tags").select2({
        tags:[
            <c:forEach var="tag" items="${tags}">
            "${tag.getTag()}",
            </c:forEach>
        ]
    })
</script>
</body>
</html>
