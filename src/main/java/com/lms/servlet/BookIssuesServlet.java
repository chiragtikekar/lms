package com.lms.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lms.util.DatabaseUtil;

public class BookIssuesServlet extends HttpServlet {
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
            
            // Get all book issues
            String sql = "SELECT bi.*, b.title as book_title, b.author as book_author, " +
                        "s.first_name, s.last_name " +
                        "FROM book_issues bi " +
                        "JOIN books b ON bi.book_id = b.id " +
                        "JOIN students s ON bi.student_id = s.id " +
                        "ORDER BY bi.issue_date DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            List<BookIssue> issues = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            
            while (rs.next()) {
                BookIssue issue = new BookIssue();
                issue.setId(rs.getInt("id"));
                issue.setBookTitle(rs.getString("book_title"));
                issue.setBookAuthor(rs.getString("book_author"));
                issue.setStudentId(rs.getInt("student_id"));
                issue.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                issue.setIssueDate(dateFormat.format(rs.getDate("issue_date")));
                issue.setDueDate(dateFormat.format(rs.getDate("due_date")));
                issue.setStatus(rs.getString("status"));
                
                // Check if book is overdue
                Date dueDate = rs.getDate("due_date");
                if (dueDate != null && "issued".equals(issue.getStatus())) {
                    issue.setOverdue(dueDate.before(today));
                }
                
                issues.add(issue);
            }
            
            request.setAttribute("recentIssues", issues);
            request.getRequestDispatcher("/admin/issues.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/issues.jsp?error=Error loading book issues");
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
        private String bookAuthor;
        private int studentId;
        private String studentName;
        private String issueDate;
        private String dueDate;
        private String status;
        private boolean isOverdue;
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getBookAuthor() { return bookAuthor; }
        public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
        public int getStudentId() { return studentId; }
        public void setStudentId(int studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getIssueDate() { return issueDate; }
        public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
        public String getDueDate() { return dueDate; }
        public void setDueDate(String dueDate) { this.dueDate = dueDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isOverdue() { return isOverdue; }
        public void setOverdue(boolean overdue) { isOverdue = overdue; }
    }
} 