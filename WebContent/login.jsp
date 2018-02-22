<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pogues Authentication</title>
</head>
<body>
	<form method="POST" name="f"
		action="${pageContext.servletContext.contextPath}/login">
		Login : <select id="username" name="username"
			onchange="updatePassword(this.value);">
			<option value="NULL">Select user</option>
			<option value="SRMTXN">Guillaume Bichler</option>
			<option value="OBM0V4">Dorothée Ast</option>
			<option value="OM4DU7">Laura Castel</option>
			<option value="GR8UH9">Tania Gluminsky</option>
			<option value="CMMPZ2">Bénédicte Mordier</option>
			<option value="D5WQNO">Heïdi Koumarianos</option>
			<option value="E3GEEH">Hélène Moron</option>
			<option value="B6L03Q">Maud Micollet</option>
			<option value="WHX33V">Johanne Aude</option>
			<option value="N6DLXN">Estelle Beretti</option>
			<option value="K4RXTU">Christelle Rieg</option>
			<option value="B94UWU">Florian Vucko</option>
			<option value="ST8PBR">Matthieu Bullot</option>
			<option value="JAT8JU">Aurore Domps</option>
			<option value="Q78DGU">Klara Vinceneux</option>
			<option value="DEQB4I">Marguerite Garnero</option>
			<option value="YLZBWC">Thomas Dubois</option>
			<option value="I6VWID">Benoît Werquin</option>
		</select> <br /> <br /> <span id="pass"></span> <input type="submit"
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