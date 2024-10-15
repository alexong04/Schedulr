<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Schedulr</title>
<style>
	body {
		margin: 0;
		padding: 0;
	}
	#button-div {
		border: 1px solid black;
		background-color: lightgray;
		max-width: 55px;
		padding: 2px;
		cursor: pointer;
	}
	#modal {
		z-index: 2;
		position: fixed;
		background-color: white;
		padding: 25px;
	}
	#modal-close {
		cursor: pointer;
	}
	#dim-screen {
		z-index: 1;
		position: fixed;
		top: 0;
		left: 0;
		height: 200vh;
		width: 200vw;
		background-color: rgba(0, 0, 0, 0.5);
	}
</style>
</head>
<body>
	<%	
		if (session.getAttribute("exitCode") == null) {
			session.setAttribute("exitCode", 0);
		}
		if ((int)session.getAttribute("exitCode") == 1) {
			session.setAttribute("exitCode", 0);
	%>
			<div id="dim-screen"></div>
			<div id="modal">
				<h1>Incorrect password</h1>
				<button id="modal-close" onclick="closeModal()">close</button>
			</div>
	<%
		}
	%>
	<%
		if ((int)session.getAttribute("exitCode") == 2) {
			session.setAttribute("exitCode", 0);
	%>	
			<div id="dim-screen"></div>
			<div id="modal">
				<h1>User not found</h1>
				<button id="modal-close" onclick="closeModal()">close</button>
			</div>
	<%
		}
	%>
	<%
		if ((int)session.getAttribute("exitCode") == 3) {
			session.setAttribute("exitCode", 0);
	%>
			<div id="dim-screen"></div>
			<div id="modal">
				<h1>User already exists</h1>
				<button id="modal-close" onclick="closeModal()">close</button>
			</div>
	<%
		}
	%>
	<h1>Login</h1>
    <form id="login-form" action="process-login" method="post">
        <h2>Enter username :  </h2> <input type="text" name="username">
        <h2>Enter password :  </h2> <input type="text" name="password">
        <div id="button-div" onclick="processType(0)">Register</div>
        <div id="button-div" onclick="processType(1)">Sign in</div>
        <input type="submit">
    </form>
	<script>
		function processType(n) {
			/*function for choosing process type*/
			loginform = document.getElementById("login-form");
			if (n == 0) {
				loginform.action = "register";
			} else {
				loginform.action = "process-login";
			}
 		}
		
		function closeModal() {
			/*function for closing error messages*/
			document.getElementById("dim-screen").style.display = "none";
			document.getElementById("modal").style.display = "none";
		}	
	</script>
</body>
</html>