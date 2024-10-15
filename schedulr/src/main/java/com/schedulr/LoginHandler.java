package com.schedulr;

import java.io.IOException;

import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginHandler extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Connection con = null;
		ResultSet rs = null;
		String url = "jdbc:mysql://localhost:3306/schedulr";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, "root", "");
		} catch (Exception e) {}
		
		HttpSession session = req.getSession();
		String username = req.getParameter("username");
		String pass = req.getParameter("password");
		
		session.setAttribute("currentUser", username);
		session.setAttribute("currentPassword", pass);
		
		try {
			rs = con.createStatement().executeQuery("SELECT * FROM users WHERE username = '" + username + "'");
			if (rs.next()) {
				if (rs.getString(3).equals(pass)) {
					session.setAttribute("currentUID", rs.getInt(1));
					System.out.println("Successful log-in");
					res.sendRedirect("main-menu.jsp");
				} else {
					System.out.println("Incorrect credentials");
					session.setAttribute("exitCode", 1);
					res.sendRedirect("index.jsp");
				}
			} else {
				System.out.println("Username not found");
				session.setAttribute("exitCode", 2);
				res.sendRedirect("index.jsp");
			}
		} catch (Exception e) {res.getWriter().print(e);}
    }
}

