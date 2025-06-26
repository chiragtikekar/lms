<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Book - Library Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css">
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
                    <div class="card-header">
                        <h4>Edit Book</h4>
                    </div>
                    <div class="card-body">
                        <form action="BookServlet" method="post">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="${book.id}">
                            
                            <div class="mb-3">
                                <label for="isbn" class="form-label">ISBN</label>
                                <input type="text" class="form-control" id="isbn" name="isbn" value="${book.isbn}" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="title" class="form-label">Title</label>
                                <input type="text" class="form-control" id="title" name="title" value="${book.title}" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="author" class="form-label">Author</label>
                                <input type="text" class="form-control" id="author" name="author" value="${book.author}" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="category" class="form-label">Category</label>
                                <select class="form-select" id="category" name="category" required>
                                    <option value="">Select Category</option>
                                    <option value="Fiction" ${book.category == 'Fiction' ? 'selected' : ''}>Fiction</option>
                                    <option value="Non-Fiction" ${book.category == 'Non-Fiction' ? 'selected' : ''}>Non-Fiction</option>
                                    <option value="Science" ${book.category == 'Science' ? 'selected' : ''}>Science</option>
                                    <option value="History" ${book.category == 'History' ? 'selected' : ''}>History</option>
                                    <option value="Biography" ${book.category == 'Biography' ? 'selected' : ''}>Biography</option>
                                    <option value="Technology" ${book.category == 'Technology' ? 'selected' : ''}>Technology</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="description" class="form-label">Description</label>
                                <textarea class="form-control" id="description" name="description" rows="3">${book.description}</textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label for="totalCopies" class="form-label">Total Copies</label>
                                <input type="number" class="form-control" id="totalCopies" name="totalCopies" 
                                       value="${book.totalCopies}" min="1" required>
                                <small class="text-muted">Currently available: ${book.availableCopies}</small>
                            </div>
                            
                            <div class="d-flex justify-content-between">
                                <a href="BookServlet?action=list" class="btn btn-secondary">
                                    <i class="bi bi-arrow-left"></i> Back to List
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-save"></i> Update Book
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 