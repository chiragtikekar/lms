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

public class StudentDashboardServlet extends HttpServlet {
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
            
            // Get student details
            String studentSql = "SELECT u.*, s.student_id, s.department FROM users u " +
                              "JOIN students s ON u.id = s.id WHERE u.id = ?";
            pstmt = conn.prepareStatement(studentSql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                request.setAttribute("student", new Student(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("student_id"),
                    rs.getString("department")
                ));
            }
            
            // Get currently borrowed books count
            String borrowedSql = "SELECT COUNT(*) FROM book_issues WHERE student_id = ? AND status = 'issued'";
            pstmt = conn.prepareStatement(borrowedSql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("currentlyBorrowed", rs.getInt(1));
            }
            
            // Get total borrowed books count
            String totalSql = "SELECT COUNT(*) FROM book_issues WHERE student_id = ?";
            pstmt = conn.prepareStatement(totalSql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("totalBorrowed", rs.getInt(1));
            }
            
            // Get due books count (due within 3 days)
            String dueSql = "SELECT COUNT(*) FROM book_issues WHERE student_id = ? AND status = 'issued' " +
                           "AND due_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL 3 DAY";
            pstmt = conn.prepareStatement(dueSql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("dueBooks", rs.getInt(1));
            }
            
            // Get currently borrowed books with details
            String booksSql = "SELECT bi.*, b.title, b.author, b.isbn " +
                            "FROM book_issues bi " +
                            "JOIN books b ON bi.book_id = b.id " +
                            "WHERE bi.student_id = ? AND bi.status = 'issued' " +
                            "ORDER BY bi.due_date ASC";
            pstmt = conn.prepareStatement(booksSql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            List<BorrowedBook> borrowedBooks = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            
            System.out.println("Fetching borrowed books for student ID: " + studentId);
            
            while (rs.next()) {
                BorrowedBook book = new BorrowedBook();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setIssueDate(dateFormat.format(rs.getDate("issue_date")));
                book.setDueDate(dateFormat.format(rs.getDate("due_date")));
                
                // Check if book is overdue
                Date dueDate = rs.getDate("due_date");
                if (dueDate != null) {
                    book.setOverdue(dueDate.before(today));
                }
                
                borrowedBooks.add(book);
                System.out.println("Added book to list: " + book.getTitle());
            }
            
            System.out.println("Total borrowed books found: " + borrowedBooks.size());
            request.setAttribute("borrowedBooks", borrowedBooks);
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/student/dashboard.jsp?error=Error loading dashboard");
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
    
    // Inner class for Student object
    public static class Student {
        private int id;
        private String firstName;
        private String lastName;
        private String email;
        private String studentId;
        private String department;
        
        public Student(int id, String firstName, String lastName, String email, String studentId, String department) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.studentId = studentId;
            this.department = department;
        }
        
        // Getters
        public int getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getStudentId() { return studentId; }
        public String getDepartment() { return department; }
    }
    
    // Inner class for BorrowedBook object
    public static class BorrowedBook {
        private int id;
        private String title;
        private String author;
        private String isbn;
        private String issueDate;
        private String dueDate;
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
        public boolean isOverdue() { return isOverdue; }
        public void setOverdue(boolean overdue) { isOverdue = overdue; }
    }
} 