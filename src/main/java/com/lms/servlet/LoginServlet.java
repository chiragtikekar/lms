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
import javax.servlet.http.HttpSession;

import com.lms.util.DatabaseUtil;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("login.jsp");
            return;
        }
        
        response.sendRedirect("login.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");
        
        System.out.println("Login attempt - Email: " + email + ", Type: " + userType);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT u.*, s.student_id, s.department, s.status FROM users u " +
                        "LEFT JOIN students s ON u.id = s.id " +
                        "WHERE u.email = ? AND u.password = ? AND u.user_type = ?";
            
            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: " + email + ", " + password + ", " + userType);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, userType);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("User found in database");
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getInt("id"));
                session.setAttribute("userType", rs.getString("user_type"));
                session.setAttribute("firstName", rs.getString("first_name"));
                session.setAttribute("lastName", rs.getString("last_name"));
                
                if ("student".equals(rs.getString("user_type"))) {
                    System.out.println("Student login - Status: " + rs.getString("status"));
                    if ("pending".equals(rs.getString("status"))) {
                        response.sendRedirect("login.jsp?error=Your account is pending approval");
                        return;
                    } else if ("blocked".equals(rs.getString("status"))) {
                        response.sendRedirect("login.jsp?error=Your account has been blocked");
                        return;
                    }
                    session.setAttribute("studentId", rs.getString("student_id"));
                    session.setAttribute("department", rs.getString("department"));
                    response.sendRedirect("student/dashboard.jsp");
                } else {
                    System.out.println("Admin login successful");
                    response.sendRedirect("admin/dashboard.jsp");
                }
            } else {
                System.out.println("No matching user found");
                response.sendRedirect("login.jsp?error=Invalid email or password");
            }
            
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=An error occurred. Please try again.");
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