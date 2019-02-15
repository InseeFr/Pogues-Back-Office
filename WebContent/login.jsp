<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pogues Authentication</title>
</head>
<body>
	<h2>Pogues Authentication</h2>
	<h3>Veuillez saisir votre IDEP</h3>
	<form method="POST" name="f"
		action="${pageContext.servletContext.contextPath}/login">
		Login : 
		<input  id="username" name="username" onchange="updatePassword(this.value);"/>
		<br /> <br /> <span id="pass"></span> <input type="submit"
			value="OK"> <input type="reset" value="Cancel">
	</form>
	<script type="text/javascript">
		function updatePassword(val) {
			var input_password = '<input type="hidden" id="password" type="password" name="password" value="' + val +'"/>';
			document.getElementById("pass").innerHTML = input_password;
		}
	</script>
</body>
</html>