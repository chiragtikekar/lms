<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${book.title} - Library Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css">
    <style>
        .book-cover {
            max-width: 200px;
            height: auto;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">Library Management System</a>
            <div class="d-flex">
                <a href="LoginServlet?action=logout" class="btn btn-outline-light">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h4>Book Details</h4>
                        <a href="BookServlet?action=list" class="btn btn-secondary btn-sm">
                            <i class="bi bi-arrow-left"></i> Back to List
                        </a>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4 text-center">
                                <img src="https://via.placeholder.com/200x300?text=Book+Cover" 
                                     alt="Book Cover" class="book-cover mb-3">
                                <div class="availability-status">
                                    <span class="badge ${book.availableCopies > 0 ? 'bg-success' : 'bg-danger'}">
                                        ${book.availableCopies > 0 ? 'Available' : 'Not Available'}
                                    </span>
                                </div>
                            </div>
                            <div class="col-md-8">
                                <h2>${book.title}</h2>
                                <h5 class="text-muted">by ${book.author}</h5>
                                <hr>
                                
                                <div class="mb-3">
                                    <strong>ISBN:</strong> ${book.isbn}
                                </div>
                                
                                <div class="mb-3">
                                    <strong>Category:</strong> ${book.category}
                                </div>
                                
                                <div class="mb-3">
                                    <strong>Copies:</strong> 
                                    <span class="${book.availableCopies > 0 ? 'text-success' : 'text-danger'}">
                                        ${book.availableCopies} / ${book.totalCopies} available
                                    </span>
                                </div>
                                
                                <div class="mb-3">
                                    <strong>Description:</strong>
                                    <p>${book.description}</p>
                                </div>
                                
                                <div class="d-flex justify-content-between mt-4">
                                    <c:if test="${sessionScope.userType == 'admin'}">
                                        <a href="edit.jsp?id=${book.id}" class="btn btn-warning">
                                            <i class="bi bi-pencil"></i> Edit
                                        </a>
                                        <a href="BookServlet?action=delete&id=${book.id}" 
                                           class="btn btn-danger"
                                           onclick="return confirm('Are you sure you want to delete this book?')">
                                            <i class="bi bi-trash"></i> Delete
                                        </a>
                                    </c:if>
                                    <c:if test="${sessionScope.userType == 'student' && book.availableCopies > 0}">
                                        <a href="BookServlet?action=issue&bookId=${book.id}&studentId=${sessionScope.userId}" 
                                           class="btn btn-success">
                                            <i class="bi bi-book"></i> Issue Book
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 