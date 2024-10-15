package com.schedulr;

import java.io.IOException;

import java.sql.*; 

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RegisterHandler extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Connection con = null;
		ResultSet rs = null;
		String url = "jdbc:mysql://localhost:3306/schedulr";
		
		String username = req.getParameter("username");
		String pass = req.getParameter("password");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, "root", "");
		} catch (Exception e) {e.printStackTrace();}
		
		HttpSession session = req.getSession();
		
		try {
			rs = con.createStatement().executeQuery("select * from users where username = '" + username + "'");
			if (rs.next()) {
				System.out.println("User already exists");
				session.setAttribute("exitCode", 3);
			} else {
				con.createStatement().executeUpdate("insert into users (username, password) values ('" + username + "', '" + pass + "')");
				System.out.println("Registration successful");
			}
			res.sendRedirect("index.jsp");
		} catch (Exception e) {res.getWriter().print(e);}
		
    }
}
