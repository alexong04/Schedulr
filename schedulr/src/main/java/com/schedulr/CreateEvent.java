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

import org.apache.commons.lang3.time.DateUtils;

import com.mysql.cj.util.StringUtils;

@SuppressWarnings("deprecation")
public class CreateEvent extends HttpServlet{
	//@SuppressWarnings("deprecation")
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		//DATABASE CONNECTIVITY
				String url = "jdbc:mysql://localhost:3306/schedulr";
				String username = "root";
				String pass = "";
				
				boolean error = false;
		        Date startDate = null;
		        Date stopDate = null;
				
				//GET SESSION UID
				HttpSession session = req.getSession();
				int currentUID = (int)session.getAttribute("currentUID");
				
				//INPUT EVENT DETAILS
				String Name = req.getParameter("eventName");
				
				boolean isInd = false;
					if (req.getParameter("isInd").equals("true")) {
					  isInd = true;
					}
				
				boolean isOnl = false;
					if (req.getParameter("isOnl").equals("true")) {
					  isOnl = true;
					}
				
				String Loc = req.getParameter("Loc");
				
				String Date = req.getParameter("Date");
					int dateYear = Integer.parseInt(Date.substring(0,4));
					int dateMonth = Integer.parseInt(Date.substring(5,7))-1;
					int dateDay = Integer.parseInt(Date.substring(8,10));
					
				String stopDateInput = req.getParameter("stopDate");
					int stopYear = Integer.parseInt(stopDateInput.substring(0,4));
					int stopMonth = Integer.parseInt(stopDateInput.substring(5,7))-1;
					int stopDay = Integer.parseInt(stopDateInput.substring(8,10));		
					
				String startTime = req.getParameter("startTime");
					int startHr = Integer.parseInt(startTime.substring(0,2));
					int startMin = Integer.parseInt(startTime.substring(3,5));
				
				System.out.println(startTime);
					
				String stopTime = req.getParameter("stopTime");
					int stopHr = Integer.parseInt(stopTime.substring(0,2));
					int stopMin = Integer.parseInt(stopTime.substring(3,5));
					
				System.out.println(startTime);
					
				int repeatDay = Integer.parseInt(req.getParameter("Repeating"));
					
				boolean isAvail = true;
					
				//IF REPEATING
				if (repeatDay != -1) {
					try {
						startDate = new Date(dateYear-1900, dateMonth, dateDay, startHr, startMin);
						stopDate = new Date(stopYear-1900, stopMonth, stopDay, stopHr, stopMin);
					} catch (Exception e) {e.printStackTrace();}
				} else {
					try {
						startDate = new Date(dateYear-1900, dateMonth, dateDay, startHr, startMin);
						stopDate = new Date(dateYear-1900, dateMonth, dateDay, stopHr, stopMin);
					} catch (Exception e) {e.printStackTrace();}
				}
				
				//CHECK FOR DATE ERRORS
				if (startDate.after(stopDate)) {
					error = true;
					System.out.println("Date error!");
				}
				
				//IF NO DATE ERROR
				try {
					if (!error) {
						if (repeatDay != -1) {
							Date iteratorDate = new Date(startDate.getTime());
							System.out.println("Repeat day = " + repeatDay + iteratorDate.getDay());
							while (iteratorDate.before(stopDate) || DateUtils.isSameDay(iteratorDate, stopDate)) {
								if (repeatDay == iteratorDate.getDay()) {
									Date tempStopTime = new Date(iteratorDate.getTime());
		                            tempStopTime.setHours(stopDate.getHours());
		                            tempStopTime.setMinutes(stopDate.getMinutes());
		                            error = !checkAvailable(currentUID, iteratorDate, tempStopTime);
								}
								iteratorDate = DateUtils.addDays(iteratorDate, 1);
							}
						} else {
							error = !checkAvailable(currentUID, startDate, stopDate);
						}
					}
				} catch (Exception e) {e.printStackTrace();}
				
				if (error) {
					System.out.println("Something went wrong!");
					res.sendRedirect("event-creation-fail.html");
				} else {
					String query_insert = "insert into events (hostUID, eventName, isIndividual, isOnline, location, year, month, day, startHr, startMin, stopHr, stopMin, available, participants) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					Connection con = null;
					int count = 0;
						
					try {
						Class.forName("com.mysql.cj.jdbc.Driver");
						con = DriverManager.getConnection(url,username,pass);
						PreparedStatement st;
						
						if (repeatDay != -1) {
							
							Date iteratorDate = new Date(startDate.getTime());
							System.out.println("Repeat day = " + repeatDay + iteratorDate.getDay());
							while (iteratorDate.before(stopDate) || DateUtils.isSameDay(iteratorDate, stopDate)) {
								if (repeatDay == iteratorDate.getDay()) {
									Date tempStopTime = new Date(iteratorDate.getTime());
		                            tempStopTime.setHours(stopDate.getHours());
		                            tempStopTime.setMinutes(stopDate.getMinutes());
		                            
		                            st = con.prepareStatement(query_insert);
									st.setInt(1, currentUID);
									st.setString(2, Name);
									st.setBoolean(3, isInd);
									st.setBoolean(4, isOnl);
									st.setString(5, Loc);
									st.setInt(6, iteratorDate.getYear()+1900);
									st.setInt(7, iteratorDate.getMonth());
									st.setInt(8, iteratorDate.getDate());
									st.setInt(9, iteratorDate.getHours());
									st.setInt(10, iteratorDate.getMinutes());
									st.setInt(11, tempStopTime.getHours());
									st.setInt(12, tempStopTime.getMinutes());
									st.setBoolean(13, isAvail);
									st.setString(14, "");
									count = st.executeUpdate();
								}
								iteratorDate = DateUtils.addDays(iteratorDate, 1);
							}
						} else {
							st = con.prepareStatement(query_insert);
							st.setInt(1, currentUID);
							st.setString(2, Name);
							st.setBoolean(3, isInd);
							st.setBoolean(4, isOnl);
							st.setString(5, Loc);
							st.setInt(6, dateYear);
							st.setInt(7, dateMonth);
							st.setInt(8, dateDay);
							st.setInt(9, startHr);
							st.setInt(10, startMin);
							st.setInt(11, stopHr);
							st.setInt(12, stopMin);
							st.setBoolean(13, isAvail);
							st.setString(14, "");
							count = st.executeUpdate();
						}
						
						System.out.println(count + " row/s affected");
						System.out.println("Event added to database");
							

						//GET SESSION EVENT ID
						ResultSet last = null;
						last = con.createStatement().executeQuery("select eventID from events where eventID = (SELECT LAST_INSERT_ID())");
						
						int currEvID = 0;
						while (last.next()) {
							currEvID = last.getInt(1);
						}
						
						session.setAttribute("currEvent", currEvID);
						
						res.sendRedirect("event-creation-success.html");
						
					} catch (Exception e) {res.getWriter().print(e);}
				}
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
				con = DriverManager.getConnection(url, "root", "databasepassword");
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