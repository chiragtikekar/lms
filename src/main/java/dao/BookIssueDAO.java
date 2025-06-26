package dao;

import models.BookIssue;
import utils.DBConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookIssueDAO {
    public boolean create(BookIssue bookIssue) {
        String sql = "INSERT INTO book_issues (book_id, student_id, due_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, bookIssue.getBookId());
            stmt.setInt(2, bookIssue.getStudentId());
            stmt.setTimestamp(3, bookIssue.getDueDate());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bookIssue.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BookIssue findById(int id) {
        String sql = "SELECT bi.*, b.title as book_title, b.isbn, " +
                    "CONCAT(u.first_name, ' ', u.last_name) as student_name " +
                    "FROM book_issues bi " +
                    "JOIN books b ON bi.book_id = b.id " +
                    "JOIN students s ON bi.student_id = s.id " +
                    "JOIN users u ON s.id = u.id " +
                    "WHERE bi.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBookIssue(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BookIssue> findByStudentId(int studentId) {
        List<BookIssue> bookIssues = new ArrayList<>();
        String sql = "SELECT bi.*, b.title as book_title, b.isbn, " +
                    "CONCAT(u.first_name, ' ', u.last_name) as student_name " +
                    "FROM book_issues bi " +
                    "JOIN books b ON bi.book_id = b.id " +
                    "JOIN students s ON bi.student_id = s.id " +
                    "JOIN users u ON s.id = u.id " +
                    "WHERE bi.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookIssues.add(mapBookIssue(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookIssues;
    }

    public List<BookIssue> findCurrentIssues(int studentId) {
        List<BookIssue> bookIssues = new ArrayList<>();
        String sql = "SELECT bi.*, b.title as book_title, b.isbn, " +
                    "CONCAT(u.first_name, ' ', u.last_name) as student_name " +
                    "FROM book_issues bi " +
                    "JOIN books b ON bi.book_id = b.id " +
                    "JOIN students s ON bi.student_id = s.id " +
                    "JOIN users u ON s.id = u.id " +
                    "WHERE bi.student_id = ? AND bi.status = 'issued'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookIssues.add(mapBookIssue(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookIssues;
    }

    public List<BookIssue> findOverdueIssues() {
        List<BookIssue> bookIssues = new ArrayList<>();
        String sql = "SELECT bi.*, b.title as book_title, b.isbn, " +
                    "CONCAT(u.first_name, ' ', u.last_name) as student_name " +
                    "FROM book_issues bi " +
                    "JOIN books b ON bi.book_id = b.id " +
                    "JOIN students s ON bi.student_id = s.id " +
                    "JOIN users u ON s.id = u.id " +
                    "WHERE bi.status = 'issued' AND bi.due_date < CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookIssues.add(mapBookIssue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookIssues;
    }

    public boolean returnBook(int issueId, BigDecimal fineAmount) {
        String sql = "UPDATE book_issues SET status = 'returned', return_date = CURRENT_TIMESTAMP, " +
                    "fine_amount = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, fineAmount);
            stmt.setInt(2, issueId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int issueId, String status) {
        String sql = "UPDATE book_issues SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, issueId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private BookIssue mapBookIssue(ResultSet rs) throws SQLException {
        BookIssue bookIssue = new BookIssue();
        bookIssue.setId(rs.getInt("id"));
        bookIssue.setBookId(rs.getInt("book_id"));
        bookIssue.setStudentId(rs.getInt("student_id"));
        bookIssue.setIssueDate(rs.getTimestamp("issue_date"));
        bookIssue.setDueDate(rs.getTimestamp("due_date"));
        bookIssue.setReturnDate(rs.getTimestamp("return_date"));
        bookIssue.setStatus(rs.getString("status"));
        bookIssue.setFineAmount(rs.getBigDecimal("fine_amount"));
        bookIssue.setBookTitle(rs.getString("book_title"));
        bookIssue.setStudentName(rs.getString("student_name"));
        bookIssue.setIsbn(rs.getString("isbn"));
        return bookIssue;
    }
} 