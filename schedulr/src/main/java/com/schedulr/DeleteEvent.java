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
public class DeleteEvent extends HttpServlet{
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
			
			ResultSet eventQuery = null;
	        int targetID = Integer.parseInt(req.getParameter("eventID"));
			
			switch(resp) {
				case 1:
					 try {
				            eventQuery = con.createStatement().executeQuery("SELECT * FROM events WHERE hostUID = " + currentUID + " AND eventID = " + targetID);
				            if (eventQuery.next()) {
				                con.createStatement().executeUpdate("DELETE FROM events WHERE eventID = " + targetID);
				                System.out.println("Event successfully deleted!");
				                res.sendRedirect("delete-event-success.html");
				            } else {
				                System.out.println("Event does not exist, or you are not the owner!");
				                res.sendRedirect("delete-event-fail.html");
				            }
				        } catch (Exception e) {e.printStackTrace();}
					break;
				default:
					break;
			}
		} catch(Exception e) {e.printStackTrace();}
	}
}