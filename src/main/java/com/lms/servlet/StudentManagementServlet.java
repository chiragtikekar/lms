package com.lms.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lms.util.DatabaseUtil;

public class StudentManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("StudentManagementServlet: doGet called");
        
        HttpSession session = request.getSession();
        if (session.getAttribute("userType") == null || !"admin".equals(session.getAttribute("userType"))) {
            System.out.println("StudentManagementServlet: Unauthorized access attempt");
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Unauthorized access");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            System.out.println("StudentManagementServlet: Getting database connection");
            conn = DatabaseUtil.getConnection();
            
            // Query to get all students (both pending and approved)
            String sql = "SELECT u.id, u.first_name, u.last_name, u.email, s.student_id, s.department, s.status " +
                        "FROM users u JOIN students s ON u.id = s.id " +
                        "ORDER BY s.status, u.first_name";
            
            System.out.println("StudentManagementServlet: Executing query: " + sql);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            List<Student> allStudents = new ArrayList<>();
            int pendingCount = 0;
            int approvedCount = 0;
            
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setStudentId(rs.getString("student_id"));
                student.setDepartment(rs.getString("department"));
                student.setStatus(rs.getString("status"));
                allStudents.add(student);
                
                if ("pending".equals(student.getStatus())) {
                    pendingCount++;
                } else if ("approved".equals(student.getStatus())) {
                    approvedCount++;
                }
                
                System.out.println("StudentManagementServlet: Found student - " + 
                    student.getFirstName() + " " + student.getLastName() + 
                    " (ID: " + student.getId() + ", Status: " + student.getStatus() + ")");
            }
            
            System.out.println("StudentManagementServlet: Total students found: " + allStudents.size());
            System.out.println("StudentManagementServlet: Pending students: " + pendingCount);
            System.out.println("StudentManagementServlet: Approved students: " + approvedCount);
            
            request.setAttribute("students", allStudents);
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("approvedCount", approvedCount);
            request.getRequestDispatcher("/admin/students.jsp").forward(request, response);
            
        } catch (SQLException e) {
            System.out.println("StudentManagementServlet: SQL Error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp?error=Error loading students");
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
        if (session.getAttribute("userType") == null || !"admin".equals(session.getAttribute("userType"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Unauthorized access");
            return;
        }

        String action = request.getParameter("action");
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE students SET status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            
            if ("approve".equals(action)) {
                pstmt.setString(1, "approved");
            } else if ("reject".equals(action)) {
                pstmt.setString(1, "blocked");
            }
            
            pstmt.setInt(2, studentId);
            pstmt.executeUpdate();
            
            response.sendRedirect(request.getContextPath() + "/StudentManagementServlet?message=Student " + 
                                ("approve".equals(action) ? "approved" : "rejected") + " successfully");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/StudentManagementServlet?error=Error processing request");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Inner class for Student object
    public static class Student {
        private int id;
        private String firstName;
        private String lastName;
        private String email;
        private String studentId;
        private String department;
        private String status;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
} 