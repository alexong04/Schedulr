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
public class JoinEvent extends HttpServlet{
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		
		//DATABASE CONNECTIVITY
		Connection con = null;
		ResultSet rs = null;
		String url = "jdbc:mysql://localhost:3306/schedulr";
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, "root", "");
			
			int resp = Integer.parseInt(req.getParameter("resp"));
			
			//SESSION
			HttpSession session = req.getSession();
			String currentUser = (String)session.getAttribute("currentUser");
			int currentUID = (int)session.getAttribute("currentUID");
			
			switch(resp) {
				case 1:
					//JOIN EVENT
		            int targetEventID = Integer.parseInt(req.getParameter("eventID"));
		          
		            //DATABASE CONNECTIVITY
			        ResultSet eventQuery = null;
			        boolean eventExists = false;

						try {
							eventQuery = con.createStatement().executeQuery("SELECT * FROM events WHERE eventID = " + targetEventID);
				            while (eventQuery.next()) {
				                eventExists = true;
				                //if event is available, and if the user isn't already a participant 
				                if (eventQuery.getBoolean(14) && !isParticipant(eventQuery.getString(15), currentUID)) {
				                    //date construction from db details
				                    Date startDate = new Date(eventQuery.getInt(7)-1900, eventQuery.getInt(8), eventQuery.getInt(9), eventQuery.getInt(10), eventQuery.getInt(11));
				                    Date stopDate = new Date(eventQuery.getInt(7)-1900, eventQuery.getInt(8), eventQuery.getInt(9), eventQuery.getInt(12), eventQuery.getInt(13));
				                    //check if the user has any conflicts with the event they wanna join
				                    if (checkAvailable(currentUID, startDate, stopDate)) {
				                        //if event is an individual event, then set it's availability to false since it's being booked rn
				                        if (eventQuery.getBoolean(4)) {
				                            con.createStatement().executeUpdate("UPDATE events SET available = false WHERE eventID = " + eventQuery.getInt(1));
				                        }
				                        //add curent user's UID to the participant list
				                        String participantList = eventQuery.getString(15);
				                        participantList += Integer.toString(currentUID) + " ";
				                        con.createStatement().executeUpdate("UPDATE events SET participants = '" + participantList + "' WHERE eventID = " + eventQuery.getInt(1));
				                        
				                        System.out.println("Successfully joined event!");
				                        res.sendRedirect("join-event-success.html");
				                    } else {
				                        System.out.println("The event conflicts with your schedule!");
				                        res.sendRedirect("join-event-fail.html");
				                    }
				                } else {
				                    System.out.println("The event is closed, or you are already a participant!");
				                    res.sendRedirect("join-event-fail.html");
				                }
				            }
						} catch(Exception e) {res.getWriter().print(e);}
						
						if (!eventExists) {
				            System.out.println("Event doesn't exist!");
				            res.sendRedirect("join-event-fail.html");
				        }
		            break;
		            
		        default:
		            break;
			}
		} catch(Exception e) {res.getWriter().print(e);}
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