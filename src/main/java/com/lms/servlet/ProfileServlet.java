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

public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null) {
            response.sendRedirect("../login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT u.*, s.student_id, s.department, s.status " +
                        "FROM users u LEFT JOIN students s ON u.id = s.id " +
                        "WHERE u.id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                request.setAttribute("firstName", rs.getString("first_name"));
                request.setAttribute("lastName", rs.getString("last_name"));
                request.setAttribute("email", rs.getString("email"));
                
                if ("student".equals(userType)) {
                    request.setAttribute("studentId", rs.getString("student_id"));
                    request.setAttribute("department", rs.getString("department"));
                    request.setAttribute("status", rs.getString("status"));
                    request.getRequestDispatcher("/student/profile.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/admin/profile.jsp").forward(request, response);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("../error.jsp");
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null) {
            response.sendRedirect("../login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // Verify current password if changing password
            if (newPassword != null && !newPassword.isEmpty()) {
                String verifySql = "SELECT password FROM users WHERE id = ?";
                pstmt = conn.prepareStatement(verifySql);
                pstmt.setInt(1, userId);
                rs = pstmt.executeQuery();
                
                if (rs.next() && !rs.getString("password").equals(currentPassword)) {
                    response.sendRedirect("ProfileServlet?error=Current password is incorrect");
                    return;
                }
            }
            
            // Update user information
            String updateSql = "UPDATE users SET first_name = ?, last_name = ?, email = ?" +
                             (newPassword != null && !newPassword.isEmpty() ? ", password = ?" : "") +
                             " WHERE id = ?";
            
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            
            if (newPassword != null && !newPassword.isEmpty()) {
                pstmt.setString(4, newPassword);
                pstmt.setInt(5, userId);
            } else {
                pstmt.setInt(4, userId);
            }
            
            pstmt.executeUpdate();
            
            // Update session attributes
            session.setAttribute("firstName", firstName);
            session.setAttribute("lastName", lastName);
            session.setAttribute("email", email);
            
            response.sendRedirect("/lms/ProfileServlet?message=Profile updated successfully");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("/lms/ProfileServlet?error=Error updating profile");
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