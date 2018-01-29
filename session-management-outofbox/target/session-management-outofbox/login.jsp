<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Mr Market Login</title>
</head>
<body>
	<form id="loginForm" method="post" action="/BIGBANK/BigBankAuthServlet">
		<label><b>Username</b></label>
	    <input type="text" placeholder="Enter Username" name="username" required>
	    <br>	
	    <label><b>Password</b></label>
	    <input type="password" placeholder="Enter Password" name="password" required>	
	    <br>
	    <button type="submit" onclick="postLoginForm">Login</button>
	</form>
	<script type="text/javascript">
		function postLoginForm()
		{
			document.getElementById("loginForm").submit();
		}		
	</script>
</body>
</html>