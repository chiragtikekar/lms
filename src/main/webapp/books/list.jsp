<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Browse Books - Library Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css">
    <style>
        .book-card {
            transition: transform 0.2s;
        }
        .book-card:hover {
            transform: translateY(-5px);
        }
        .available {
            color: green;
        }
        .unavailable {
            color: red;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">Library Management System</a>
            <div class="d-flex">
                <a href="${pageContext.request.contextPath}/LoginServlet?action=logout" class="btn btn-outline-light">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row mb-4">
            <div class="col-md-6">
                <h2>Browse Books</h2>
            </div>
            <div class="col-md-6 text-end">
                <a href="${pageContext.request.contextPath}/student/dashboard.jsp" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>

        <c:if test="${not empty param.message}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${param.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row mb-4">
            <div class="col-md-12">
                <form action="${pageContext.request.contextPath}/BookServlet" method="get" class="row g-3">
                    <input type="hidden" name="action" value="list">
                    <div class="col-md-3">
                        <input type="text" name="search" class="form-control" placeholder="Search by title or author" value="${param.search}">
                    </div>
                    <div class="col-md-2">
                        <select name="category" class="form-select">
                            <option value="">All Categories</option>
                            <option value="Fiction" ${param.category == 'Fiction' ? 'selected' : ''}>Fiction</option>
                            <option value="Non-Fiction" ${param.category == 'Non-Fiction' ? 'selected' : ''}>Non-Fiction</option>
                            <option value="Science" ${param.category == 'Science' ? 'selected' : ''}>Science</option>
                            <option value="History" ${param.category == 'History' ? 'selected' : ''}>History</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="row">
            <c:forEach items="${books}" var="book">
                <div class="col-md-4 mb-4">
                    <div class="card book-card h-100">
                        <div class="card-body">
                            <h5 class="card-title">${book.title}</h5>
                            <h6 class="card-subtitle mb-2 text-muted">${book.author}</h6>
                            <p class="card-text">
                                <strong>ISBN:</strong> ${book.isbn}<br>
                                <strong>Category:</strong> ${book.category}<br>
                                <strong>Copies:</strong> 
                                <span class="${book.availableCopies > 0 ? 'available' : 'unavailable'}">
                                    ${book.availableCopies} / ${book.totalCopies} available
                                </span>
                            </p>
                            <div class="d-flex justify-content-between">
                                <a href="${pageContext.request.contextPath}/BookServlet?action=view&id=${book.id}" class="btn btn-sm btn-info">
                                    <i class="bi bi-eye"></i> View
                                </a>
                                <c:if test="${sessionScope.userType == 'student' && book.availableCopies > 0}">
                                    <form action="${pageContext.request.contextPath}/BookServlet" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="issue">
                                        <input type="hidden" name="bookId" value="${book.id}">
                                        <input type="hidden" name="studentId" value="${sessionScope.userId}">
                                        <button type="submit" class="btn btn-sm btn-success">
                                            <i class="bi bi-book"></i> Issue
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 