<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.Arrays" %>
<%@page import="com.schedulr.EventsHandler" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>View Hosted Events</title>
<style>
	body {
		margin: 0;
		padding: 0;
	}
</style>
</head>
<body>
	<h1><%= session.getAttribute("currentUser") %>'s Hosted Events</h1>
	<div class="events-container">
		<%
			//DATABASE CONNECTIVITY
			String url = "jdbc:mysql://localhost:3306/schedulr";
			String username = "root";
			String pass = "";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url,username,pass);
			
			int currentUID = (Integer)session.getAttribute("currentUID");
				
			ResultSet eventQuery = null;
			ResultSet userQuery = null;
			ResultSet participantsQuery = null;
			Date startDate = null;
			Date stopDate = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM/dd/yyyy");
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
			
			
			eventQuery = con.createStatement().executeQuery("select * from events where hostUID = " + currentUID);
			
			while (eventQuery.next()){
				
				startDate = new Date(eventQuery.getInt(7)-1900, eventQuery.getInt(8), eventQuery.getInt(9), eventQuery.getInt(10), eventQuery.getInt(11));
				stopDate = new Date(eventQuery.getInt(7)-1900, eventQuery.getInt(8), eventQuery.getInt(9), eventQuery.getInt(12), eventQuery.getInt(13));
				
				//STORE EVENT DETAILS INTO VARIABLES
				int eventID = eventQuery.getInt(1);
				String eventName = eventQuery.getString(3);
				String eventHost = (String)session.getAttribute("currentUser");
				String locationType = "";
					if (eventQuery.getBoolean(5)) {
						locationType = "Online";
					} else {
						locationType = "Face-to-face";
					}
				String location = eventQuery.getString(6);
				String eventDate = dateFormat.format(startDate);
				String startTime = timeFormat.format(startDate);
				String stopTime = timeFormat.format(stopDate);
				
				String participants[] = {};
				ArrayList<String> participantsList = new ArrayList<String>(Arrays.asList(participants));
				participantsQuery = con.createStatement().executeQuery("SELECT UID, username FROM users");
				while (participantsQuery.next()) {
					if (EventsHandler.isParticipant(eventQuery.getString(15), participantsQuery.getInt(1))) {
						participantsList.add(participantsQuery.getString(2)); 
                    }
				}
				participants = participantsList.toArray(participants);
				
		%>
			<br><br>
			<div class="event-entry">
				<p class="event-detail">Event ID: <%= eventID %></p>
				<p class="event-detail">Event Name: <%= eventName %></p>
				<p class="event-detail">Event Host: <%= eventHost %></p>
				<p class="event-detail">Location: <%= locationType %> @ <%= location %></p>
				<p class="event-detail">Date: <%= eventDate %></p>
				<p class="event-detail">Time: <%= startTime %> - <%= stopTime %></p>
				<p class="event-detail">Participants: <br>
					<%
						for(int i=0; i<participants.length;i++){
					%>
						<%= participants[i] + "<br/>"%>
					<%
						}
					%>
				</p>
			</div>
		<%
			}
		%>
	</div>
	
	<br><br><a href="main-menu.jsp">Return to main menu</a><br>
	<a href="index.jsp">Log out</a>
</body>
</html>