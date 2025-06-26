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

public class MyBooksServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Log the request path for debugging
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        
        HttpSession session = request.getSession();
        if (session.getAttribute("userType") == null || !"student".equals(session.getAttribute("userType"))) {
            System.out.println("Unauthorized access attempt - userType: " + session.getAttribute("userType"));
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Unauthorized access");
            return;
        }

        int studentId = (int) session.getAttribute("userId");
        System.out.println("Fetching books for student ID: " + studentId);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            System.out.println("Database connection established");
            
            // Get all books issued to the student
            String booksSql = "SELECT bi.*, b.title, b.author, b.isbn " +
                            "FROM book_issues bi " +
                            "JOIN books b ON bi.book_id = b.id " +
                            "WHERE bi.student_id = ? " +
                            "ORDER BY bi.issue_date DESC";
            System.out.println("Executing SQL: " + booksSql);
            
            pstmt = conn.prepareStatement(booksSql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            List<IssuedBook> issuedBooks = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            
            while (rs.next()) {
                IssuedBook book = new IssuedBook();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setIssueDate(dateFormat.format(rs.getDate("issue_date")));
                book.setDueDate(dateFormat.format(rs.getDate("due_date")));
                book.setStatus(rs.getString("status"));
                
                // Check if book is overdue
                Date dueDate = rs.getDate("due_date");
                if (dueDate != null && "issued".equals(book.getStatus())) {
                    book.setOverdue(dueDate.before(today));
                }
                
                issuedBooks.add(book);
                System.out.println("Added book: " + book.getTitle() + " (Status: " + book.getStatus() + ")");
            }
            
            System.out.println("Total books found: " + issuedBooks.size());
            request.setAttribute("issuedBooks", issuedBooks);
            
            // Forward to my-books JSP
            String jspPath = "/student/my-books.jsp";
            System.out.println("Forwarding to JSP: " + jspPath);
            request.getRequestDispatcher(jspPath).forward(request, response);
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/student/my-books.jsp?error=Error loading books");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
                System.out.println("Database resources closed");
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // Inner class for IssuedBook object
    public static class IssuedBook {
        private int id;
        private String title;
        private String author;
        private String isbn;
        private String issueDate;
        private String dueDate;
        private String status;
        private boolean isOverdue;
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
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