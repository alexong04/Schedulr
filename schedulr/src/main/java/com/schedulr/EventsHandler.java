package com.schedulr;

import java.io.IOException;
import java.sql.*;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;  

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.cj.util.StringUtils;

@SuppressWarnings("deprecation")
public class EventsHandler extends HttpServlet{
	//@SuppressWarnings("deprecation")
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		
		//DATABASE CONNECTIVITY
		Connection con = null;
		ResultSet rs = null;
		String url = "jdbc:mysql://localhost:3306/schedulr";
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, "root", "");
			
			int resp = Integer.parseInt(req.getParameter("resp"));
			boolean run = true;
			
			//SESSION
			HttpSession session = req.getSession();
			String currentUser = (String)session.getAttribute("currentUser");
			int currentUID = (int)session.getAttribute("currentUID");
			
			System.out.println("Current user: " + currentUser);
			System.out.println("Current UID: " + currentUID);
			
			//do {
				switch(resp) {
					case 1:
						System.out.println("View Hosted Events");
						res.sendRedirect("view-hosted-events.jsp");
			            //viewHostedEvents(currentUID, currentUser);
			            break;
			            
			        case 2:
			        	System.out.println("View Joined Events");
			        	res.sendRedirect("view-joined-events.jsp");
			            //viewJoinedEvents(currentUID);
			            break;
			
			        case 3:
			        	System.out.println("Create Event");
			            res.sendRedirect("create-event.html");
			            break;
			            
			        case 4: 
			        	System.out.println("Join Event");
			        	res.sendRedirect("join-event.html");
			            break;
			            
			        case 5:
			        	System.out.println("Delete Event");
			        	res.sendRedirect("delete-event.html");
			            break; 
			            
			        default:
			            run = false;
			            break;
					}
		//} while (run);
			
		} catch(Exception e) {res.getWriter().println(e);}
	}
	
	
	
	//HELPER METHODS
	//FIND INDEX
	public static int findIndex(String[] days, String day) {
		for (int i=0; i<days.length; ++i) {
			if (days[i].equals(day)) {
				return i;
			}
		}
		return -1;
	}
	
	
	//IS PARTICIPANT
	public static boolean isParticipant(String participantString, int UID) {
		if (participantString.equals("")) {
			return false;
		}
		
		ArrayList<Integer> userIDs = new ArrayList<Integer>();
		String[] usersList = participantString.split(" ");
		
		for (String s : usersList) {
			userIDs.add(Integer.parseInt(s));
		}
		
		if (userIDs.contains(UID)) {
			return true;
		}
		return false;
	}
	
	
	//CHECK AVAILABILITY
	public static boolean checkAvailable(int currentUID, Date startDate, Date stopDate) throws SQLException {
		//DATABASE CONNECTIVITY
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/schedulr";
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, "root", "");
		} catch(Exception e) {e.printStackTrace();}
		
		ResultSet eventQuery = null;
		eventQuery = con.createStatement().executeQuery("SELECT hostUID, year, month, day, startHr, startMin, stopHr, stopMin, participants FROM events");
		
		while (eventQuery.next()) {
			Date startCompareDate = new Date(eventQuery.getInt(2)-1900, eventQuery.getInt(3), eventQuery.getInt(4), eventQuery.getInt(5), eventQuery.getInt(6));
            Date stopCompareDate = new Date(eventQuery.getInt(2)-1900, eventQuery.getInt(3), eventQuery.getInt(4), eventQuery.getInt(7), eventQuery.getInt(8));
		
            if(eventQuery.getInt(1) == currentUID || isParticipant(eventQuery.getString(9), currentUID)) {
            	if (startDate.equals(startCompareDate) || stopDate.equals(stopCompareDate)) {
                    return false;
                }
                if (startDate.before(startCompareDate) && stopDate.after(startCompareDate)) {
                    return false;
                }
                if (startDate.after(startCompareDate) && stopDate.before(stopCompareDate)) {
                    return false;
                }
            }
		}
		return true;
	}
}