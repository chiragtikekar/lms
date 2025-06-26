package com.lms.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lms.util.DatabaseUtil;

public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // Get all users
            String sql = "SELECT u.*, s.student_id, s.department, s.status FROM users u " +
                        "LEFT JOIN students s ON u.id = s.id";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            response.setContentType("text/html");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h2>Users in Database:</h2>");
            response.getWriter().println("<table border='1'>");
            response.getWriter().println("<tr><th>ID</th><th>Name</th><th>Email</th><th>Type</th><th>Student ID</th><th>Status</th></tr>");
            
            while (rs.next()) {
                response.getWriter().println("<tr>");
                response.getWriter().println("<td>" + rs.getInt("id") + "</td>");
                response.getWriter().println("<td>" + rs.getString("first_name") + " " + rs.getString("last_name") + "</td>");
                response.getWriter().println("<td>" + rs.getString("email") + "</td>");
                response.getWriter().println("<td>" + rs.getString("user_type") + "</td>");
                response.getWriter().println("<td>" + rs.getString("student_id") + "</td>");
                response.getWriter().println("<td>" + rs.getString("status") + "</td>");
                response.getWriter().println("</tr>");
            }
            
            response.getWriter().println("</table>");
            response.getWriter().println("</body></html>");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 