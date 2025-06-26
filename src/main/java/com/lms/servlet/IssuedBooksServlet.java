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

public class IssuedBooksServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("userType") == null || !"student".equals(session.getAttribute("userType"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Unauthorized access");
            return;
        }

        int studentId = (int) session.getAttribute("userId");
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT bi.*, b.title as book_title, b.author as book_author " +
                        "FROM book_issues bi " +
                        "JOIN books b ON bi.book_id = b.id " +
                        "WHERE bi.student_id = ? " +
                        "ORDER BY bi.issue_date DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            List<BookIssue> issuedBooks = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            
            while (rs.next()) {
                BookIssue issue = new BookIssue();
                issue.setId(rs.getInt("id"));
                issue.setBookTitle(rs.getString("book_title"));
                issue.setBookAuthor(rs.getString("book_author"));
                issue.setIssueDate(dateFormat.format(rs.getDate("issue_date")));
                issue.setDueDate(dateFormat.format(rs.getDate("due_date")));
                issue.setReturnDate(rs.getDate("return_date") != null ? 
                    dateFormat.format(rs.getDate("return_date")) : null);
                issue.setStatus(rs.getString("status"));
                
                // Check if book is overdue or due soon
                Date dueDate = rs.getDate("due_date");
                if (dueDate != null) {
                    long diff = dueDate.getTime() - today.getTime();
                    long daysDiff = diff / (24 * 60 * 60 * 1000);
                    
                    issue.setOverdue(daysDiff < 0);
                    issue.setDueSoon(daysDiff >= 0 && daysDiff <= 3);
                }
                
                issuedBooks.add(issue);
            }
            
            request.setAttribute("issuedBooks", issuedBooks);
            request.getRequestDispatcher("/student/my-books.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/student/dashboard.jsp?error=Error loading issued books");
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
        private String issueDate;
        private String dueDate;
        private String returnDate;
        private String status;
        private boolean isOverdue;
        private boolean isDueSoon;

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getBookAuthor() { return bookAuthor; }
        public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
        public String getIssueDate() { return issueDate; }
        public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
        public String getDueDate() { return dueDate; }
        public void setDueDate(String dueDate) { this.dueDate = dueDate; }
        public String getReturnDate() { return returnDate; }
        public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isOverdue() { return isOverdue; }
        public void setOverdue(boolean overdue) { isOverdue = overdue; }
        public boolean isDueSoon() { return isDueSoon; }
        public void setDueSoon(boolean dueSoon) { isDueSoon = dueSoon; }
    }
} 