package com.lms.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lms.util.DatabaseUtil;

public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("userType") == null || !"admin".equals(session.getAttribute("userType"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Unauthorized access");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // Get total books count
            String booksSql = "SELECT COUNT(*) FROM books";
            pstmt = conn.prepareStatement(booksSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("totalBooks", rs.getInt(1));
            }
            
            // Get total students count
            String studentsSql = "SELECT COUNT(*) FROM students";
            pstmt = conn.prepareStatement(studentsSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("totalStudents", rs.getInt(1));
            }
            
            // Get pending approvals count
            String pendingSql = "SELECT COUNT(*) FROM students WHERE status = 'pending'";
            pstmt = conn.prepareStatement(pendingSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("pendingApprovals", rs.getInt(1));
            }
            
            // Get recent book issues
            String recentIssuesSql = "SELECT bi.*, b.title as book_title, " +
                                   "u.first_name, u.last_name, s.student_id " +
                                   "FROM book_issues bi " +
                                   "JOIN books b ON bi.book_id = b.id " +
                                   "JOIN students s ON bi.student_id = s.id " +
                                   "JOIN users u ON s.id = u.id " +
                                   "ORDER BY bi.issue_date DESC";
            pstmt = conn.prepareStatement(recentIssuesSql);
            rs = pstmt.executeQuery();
            
            List<BookIssue> recentIssues = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            System.out.println("Fetching book issues...");
            
            while (rs.next()) {
                BookIssue issue = new BookIssue();
                issue.setId(rs.getInt("id"));
                issue.setBookTitle(rs.getString("book_title"));
                issue.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                issue.setStudentId(rs.getString("student_id"));
                issue.setIssueDate(dateFormat.format(rs.getDate("issue_date")));
                issue.setDueDate(dateFormat.format(rs.getDate("due_date")));
                issue.setStatus(rs.getString("status"));
                recentIssues.add(issue);
                System.out.println("Added issue: " + issue.getBookTitle() + " for student: " + issue.getStudentName());
            }
            
            System.out.println("Total issues found: " + recentIssues.size());
            request.setAttribute("recentIssues", recentIssues);
            
            // Get recent student registrations
            String recentStudentsSql = "SELECT u.*, s.student_id, s.department, s.status " +
                                     "FROM users u JOIN students s ON u.id = s.id " +
                                     "ORDER BY u.id DESC LIMIT 5";
            pstmt = conn.prepareStatement(recentStudentsSql);
            rs = pstmt.executeQuery();
            
            List<Student> recentStudents = new ArrayList<>();
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("student_id"),
                    rs.getString("department"),
                    rs.getString("status")
                );
                recentStudents.add(student);
            }
            
            request.setAttribute("recentStudents", recentStudents);
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp?error=Error loading dashboard");
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
    
    // Inner class for BookIssue object
    public static class BookIssue {
        private int id;
        private String bookTitle;
        private String studentName;
        private String studentId;
        private String issueDate;
        private String dueDate;
        private String status;
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getIssueDate() { return issueDate; }
        public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
        public String getDueDate() { return dueDate; }
        public void setDueDate(String dueDate) { this.dueDate = dueDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    // Inner class for Student object
    public static class Student {
        private int id;
        private String firstName;
        private String lastName;
        private String studentId;
        private String department;
        private String status;
        
        public Student(int id, String firstName, String lastName, String studentId, String department, String status) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.studentId = studentId;
            this.department = department;
            this.status = status;
        }
        
        // Getters
        public int getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getStudentId() { return studentId; }
        public String getDepartment() { return department; }
        public String getStatus() { return status; }
    }
} 