<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>登录页面</title>

    <link rel="stylesheet" type="text/css" href="/bo/css/login.css"/>
    <script type="text/javascript" src="/bo/js/jquery/jquery-1.2.6.js"></script>
    <script type="text/javascript" src="/bo/js/login.js"></script>
</head>

<body>
<div id="login_frame">

    <p id="image_logo"><img src="/bo/images/fly.png"></p>

    <form method="post" action="">
        <p>
            <label class="label_input">用户名</label>
            <input type="text" id="username" class="text_field"/>
        </p>
        <p>
            <label class="label_input">密码</label>
            <input type="text" id="password" class="text_field"/>
        </p>
        <div id="login_control">
            <input type="button" id="btn_login" value="登录" onclick="login()"/>
            <a id="forget_pwd" href="forget_pwd.jsp">忘记密码？</a>
        </div>
    </form>
</div>

</body>
</html>