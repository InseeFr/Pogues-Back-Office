<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pogues Authentication</title>
</head>
<body>

<form method="POST" action="j_security_check">

Login : <select id="j_username" name="j_username" onchange="updatePassword(this.value);">
<option value="I6VWID">Werquin Benoît</option>
<option value="D5WQNO">Koumarianos Heïdi</option>
<option value="ETGAIL">Duffes Guillaume</option>
<option value="SRMTXN">Bichler Guillaume</option>
<option value="CMMPZ2">Mordier Bénédicte</option>
</select>
<br />
<br />
<span id="pass"></span>
<input type="submit" value="OK">
<input type="reset" value="Cancel">

</form>

<script type="text/javascript">
		function updatePassword(val) {
			var input_password = '<input type="hidden" id="j_password" type="password" name="j_password" value="' + val +'"/>';
			document.getElementById("pass").innerHTML = input_password;
		}
	</script>
</body>
</html>