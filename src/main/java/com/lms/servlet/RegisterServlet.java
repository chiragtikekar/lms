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

public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get form parameters
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");
        String studentId = request.getParameter("studentId");
        String department = request.getParameter("department");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // Get database connection
            conn = DatabaseUtil.getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Insert into users table
            String userSql = "INSERT INTO users (first_name, last_name, email, password, user_type) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, userType);
            pstmt.executeUpdate();
            
            // Get the generated user ID
            int userId = 0;
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
            }
            
            // Only insert into students table if user is a student
            if ("student".equals(userType)) {
                String studentSql = "INSERT INTO students (id, student_id, department, status) VALUES (?, ?, ?, 'pending')";
                pstmt = conn.prepareStatement(studentSql);
                pstmt.setInt(1, userId);
                pstmt.setString(2, studentId);
                pstmt.setString(3, department);
                pstmt.executeUpdate();
            }
            
            // Commit transaction
            conn.commit();
            
            // Redirect to login page with success message
            response.sendRedirect("login.jsp?message=Registration successful! Please login.");
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=Registration failed. Please try again.");
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