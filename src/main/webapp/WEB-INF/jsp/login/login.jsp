<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>登录页面</title>
    <link rel="stylesheet" type="text/css" href="/bo/css/login.css"/>
    <script type="text/javascript" src="/bo/js/jquery/jquery-1.2.6.js"></script>
</head>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<div id="login_frame">

    <label class="">自定义表单验证</label>
    <p id="image_logo"><img src="/bo/images/fly.png"></p>
    <form name="f" action="/authentication/form" method="post">
        <p>
            <label class="label_input">用户名</label>
            <input type="text" name="username" placeholder="name"><br/>
        </p>
        <p>
            <label class="label_input">密码</label>
            <input type="password" name="password" placeholder="password"><br/>
        </p>
        <div id="login_control">
            <input name="submit" type="submit" id="btn_login" value="登录">
            <a id="forget_pwd" href="forget_pwd.jsp">忘记密码？</a>
        </div>
    </form>
</div>
</body>
</html>