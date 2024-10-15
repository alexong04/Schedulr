<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Main Menu</title>
<style>
	body {
		margin: 0;
		padding: 0;
	}
</style>
</head>
<body>
	<div class="content">
		<div class="header">
			<h1>Welcome, <%=session.getAttribute("currentUser") %>!</h1>
		</div>
		<div class="subheader">
			<h2>Get Started</h2>
		</div>
		<div class="buttons">
			<form action="events-process" method="post">
				<button name="resp" value="1" type="submit">View hosted events</button><br>
				<button name="resp" value="2" type="submit">View joined events</button><br>
				<button name="resp" value="3" type="submit">Create new event</button><br>
				<button name="resp" value="4" type="submit">Join event</button><br>
				<button name="resp" value="5" type="submit">Delete event</button><br>
			</form>
			
			<br><a href="index.jsp">Log out</a>
		</div>
	</div>
</body>
</html>