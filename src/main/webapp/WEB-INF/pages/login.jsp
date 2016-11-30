<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
  <meta charset="UTF-8">
  <title>Login</title>
  <link rel="stylesheet" href="<c:url value="resources/css/bootstrap.min.css" />">
  <link rel="stylesheet" href="<c:url value="resources/css/main.css" />">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
</head>
<body>
<div id="login-box">
  <div class="login__form">
    <form name='loginForm' class="form-horizontal" action="<c:url value='/login' />" method='POST'>
      <h1 class="center">Task manager</h1>
      <c:if test="${not empty error}">
        <div class="error">${error}</div>
      </c:if>
      <c:if test="${not empty msg}">
        <div class="msg">${msg}</div>
      </c:if>
      <input type="text" name="username" class="form-control" placeholder="Email">
      <input type="password" name="password" class="form-control" placeholder="Password">
      <div class="center">
        <button class="btn btn-success">Login</button>
        <a class="btn btn-info"  href="<c:url value='/registration' />">Sing-up</a>
      </div>
    </form>
    <div class="text-center">
      <div class="auth-button">
        <a href="${pageContext.request.contextPath}/auth/twitter"><img src="resources/sign-in-with-twitter-gray.png"></a>
      </div>
        <div id="login_button" class="auth-button" onclick="VK.Auth.login(authInfo);"></div>
      <div class="auth-button facebook-button">
        <a href="${pageContext.request.contextPath}/facebook"><img class="facebook-btn" src="resources/facebook_login.png"></a>
      </div>
    </div>
  </div>
</div>
<sec:authorize access="isAuthenticated()"><c:redirect url="/"/></sec:authorize>
<script src="<c:url value="resources/js/jquery-2.1.4.min.js" />"></script>
<script src="//vk.com/js/api/openapi.js" type="text/javascript"></script>
<script language="javascript">
  VK.init({
    apiId: 5066953
  });
  function authInfo(response) {
    console.log(response);
    if (response.session) {
      var user = response.session.user;
      $.post('/callbackvk', {first_name:user.first_name, last_name: user.last_name, user_id: user.id }, function () {
        location.href = '/';
      });
    } else {
      alert('not auth');
    }
  }
  VK.Auth.getLoginStatus(authInfo);
  VK.UI.button('login_button');
</script>
</body>
</html>