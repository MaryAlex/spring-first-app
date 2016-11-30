<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title></title>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="<c:url value="resources/css/font-awesome.min.css" />">
</head>
<body>
<c:url value="/logout" var="logoutUrl" />

<!-- csrt support -->
<form action="${logoutUrl}" method="post" id="logoutForm">
  <input type="hidden"
         name="${_csrf.parameterName}"
         value="${_csrf.token}" />
</form>
<script>
  function formSubmit() {
    document.getElementById("logoutForm").submit();
  }
</script>
<div class="header">
  <div class="header__nav">
    <div class="header__nav__bar header__nav__bar-main"><a href="<c:url value="/user?id=0"/>"><sec:authentication property="principal.username" /></a></div>
    <div class="header__nav__bar"><a href="<c:url value="/welcome"/>">Main</a></div>
    <div class="header__nav__bar"><a href="<c:url value="/tasks"/>">Tasks</a></div>
    <div class="header__nav__bar"><a href="<c:url value="/tasks?id=0"/>">My tasks</a></div>
    <div class="header__nav__bar"><a href="<c:url value="/add_task"/>">Add task</a></div>
    <div class="header__nav__bar"><a href="<c:url value="/users"/>">Profiles</a></div>
  </div>
  <div class="header__nav__right">
    <div class="header__nav__right__bar">
      <input class="form-control inline search" id="lolz" placeholder="Search"/>
      <a href="#" id="search" class="btn inline"><span class='fa fa-search'></span></a>
      <a href="javascript:formSubmit()" class="inline"> Logout</a>
    </div>
  </div>
</div>
<script src="<c:url value="resources/js/jquery-2.1.4.min.js" />"></script>
<script type="text/javascript">
  $(document).ready(function() {
    var btn = $('#search');
    btn.click(function() {
      var search = document.getElementById("lolz").value;
      location.href='/search?search='+search;
    });
  });
</script>
</body>
</html>
