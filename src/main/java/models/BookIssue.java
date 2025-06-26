package models;

import java.sql.Timestamp;
import java.math.BigDecimal;

public class BookIssue {
    private int id;
    private int bookId;
    private int studentId;
    private Timestamp issueDate;
    private Timestamp dueDate;
    private Timestamp returnDate;
    private String status;
    private BigDecimal fineAmount;

    // Additional fields for display purposes
    private String bookTitle;
    private String studentName;
    private String isbn;

    // Constructors
    public BookIssue() {}

    public BookIssue(int bookId, int studentId, Timestamp dueDate) {
        this.bookId = bookId;
        this.studentId = studentId;
        this.dueDate = dueDate;
        this.status = "issued";
        this.fineAmount = BigDecimal.ZERO;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Timestamp getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Timestamp issueDate) {
        this.issueDate = issueDate;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // Helper methods
    public boolean isReturned() {
        return "returned".equals(status);
    }

    public boolean isOverdue() {
        return "overdue".equals(status);
    }

    public boolean hasFinePending() {
        return fineAmount != null && fineAmount.compareTo(BigDecimal.ZERO) > 0;
    }
} 