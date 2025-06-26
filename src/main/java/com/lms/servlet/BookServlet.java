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

public class BookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        if (session.getAttribute("userType") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            if ("list".equals(action)) {
                listBooks(request, response);
            } else if ("view".equals(action)) {
                viewBook(request, response);
            } else {
                listBooks(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        if (session.getAttribute("userType") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            if ("add".equals(action)) {
                addBook(request, response);
            } else if ("update".equals(action)) {
                updateBook(request, response);
            } else if ("delete".equals(action)) {
                deleteBook(request, response);
            } else if ("issue".equals(action)) {
                issueBook(request, response);
            } else if ("return".equals(action)) {
                returnBook(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM books ORDER BY title";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setIsbn(rs.getString("isbn"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setDescription(rs.getString("description"));
                book.setTotalCopies(rs.getInt("total_copies"));
                book.setAvailableCopies(rs.getInt("available_copies"));
                books.add(book);
            }
            
            request.setAttribute("books", books);
            
            // Forward to the appropriate JSP based on user type
            HttpSession session = request.getSession();
            String userType = (String) session.getAttribute("userType");
            
            if ("admin".equals(userType)) {
                request.getRequestDispatcher("/admin/books.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/books/list.jsp").forward(request, response);
            }
            
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void addBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String isbn = request.getParameter("isbn");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        int totalCopies = Integer.parseInt(request.getParameter("totalCopies"));
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO books (isbn, title, author, category, description, total_copies, available_copies) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, isbn);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, category);
            pstmt.setString(5, description);
            pstmt.setInt(6, totalCopies);
            pstmt.setInt(7, totalCopies);
            pstmt.executeUpdate();
            
            response.sendRedirect("BookServlet?action=list");
            
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void issueBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Check if book is available
            String checkSql = "SELECT available_copies FROM books WHERE id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getInt("available_copies") > 0) {
                // Create book issue record
                String issueSql = "INSERT INTO book_issues (book_id, student_id, issue_date, due_date, status) " +
                                "VALUES (?, ?, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 14 DAY), 'issued')";
                pstmt = conn.prepareStatement(issueSql);
                pstmt.setInt(1, bookId);
                pstmt.setInt(2, studentId);
                pstmt.executeUpdate();
                
                // Update available copies
                String updateSql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setInt(1, bookId);
                pstmt.executeUpdate();
                
                conn.commit();
                response.sendRedirect("BookServlet?action=list&message=Book issued successfully");
            } else {
                response.sendRedirect("BookServlet?action=list&error=Book not available");
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void returnBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        int issueId = Integer.parseInt(request.getParameter("issueId"));
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Get book ID from issue record
            String getBookSql = "SELECT book_id FROM book_issues WHERE id = ?";
            pstmt = conn.prepareStatement(getBookSql);
            pstmt.setInt(1, issueId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int bookId = rs.getInt("book_id");
                
                // Update issue record
                String updateIssueSql = "UPDATE book_issues SET return_date = CURRENT_TIMESTAMP, status = 'returned' " +
                                      "WHERE id = ?";
                pstmt = conn.prepareStatement(updateIssueSql);
                pstmt.setInt(1, issueId);
                pstmt.executeUpdate();
                
                // Update available copies
                String updateBookSql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";
                pstmt = conn.prepareStatement(updateBookSql);
                pstmt.setInt(1, bookId);
                pstmt.executeUpdate();
                
                conn.commit();
                response.sendRedirect("BookServlet?action=list&message=Book returned successfully");
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void viewBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        
        int bookId = Integer.parseInt(request.getParameter("id"));
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM books WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setIsbn(rs.getString("isbn"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setDescription(rs.getString("description"));
                book.setTotalCopies(rs.getInt("total_copies"));
                book.setAvailableCopies(rs.getInt("available_copies"));
                
                request.setAttribute("book", book);
                request.getRequestDispatcher("/books/view.jsp").forward(request, response);
            } else {
                response.sendRedirect("BookServlet?action=list&error=Book not found");
            }
            
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        int bookId = Integer.parseInt(request.getParameter("id"));
        String isbn = request.getParameter("isbn");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        int totalCopies = Integer.parseInt(request.getParameter("totalCopies"));
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, category = ?, " +
                        "description = ?, total_copies = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, isbn);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, category);
            pstmt.setString(5, description);
            pstmt.setInt(6, totalCopies);
            pstmt.setInt(7, bookId);
            pstmt.executeUpdate();
            
            response.sendRedirect("BookServlet?action=list&message=Book updated successfully");
            
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        int bookId = Integer.parseInt(request.getParameter("id"));
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Check if book has any active issues
            String checkSql = "SELECT COUNT(*) FROM book_issues WHERE book_id = ? AND status = 'issued'";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Delete book
                String deleteSql = "DELETE FROM books WHERE id = ?";
                pstmt = conn.prepareStatement(deleteSql);
                pstmt.setInt(1, bookId);
                pstmt.executeUpdate();
                
                conn.commit();
                response.sendRedirect("BookServlet?action=list&message=Book deleted successfully");
            } else {
                response.sendRedirect("BookServlet?action=list&error=Cannot delete book with active issues");
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    // Inner class for Book object
    public static class Book {
        private int id;
        private String isbn;
        private String title;
        private String author;
        private String category;
        private String description;
        private int totalCopies;
        private int availableCopies;

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getTotalCopies() { return totalCopies; }
        public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
        public int getAvailableCopies() { return availableCopies; }
        public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    }
} 