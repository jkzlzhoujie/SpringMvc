<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
	<head>
		<title>errorl.jsp</title>
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="this is my page">
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	</head>

	<body>
		<div class="main-error">
			<h1>
				I am sorry,something is wrong
			</h1>
			
			<P>&nbsp;</P>
			
			<h2 align="center" style="color: red; font-size: 20px;">${(message)!''}</h2>
		</div>
	</body>
</html>
