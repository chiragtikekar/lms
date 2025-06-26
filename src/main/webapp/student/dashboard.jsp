	<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .sidebar {
            position: fixed;
            top: 0;
            left: 0;
            height: 100vh;
            width: 250px;
            background: #343a40;
            padding-top: 20px;
            color: white;
        }
        .sidebar .nav-link {
            color: rgba(255,255,255,0.8);
            padding: 15px 20px;
            transition: all 0.3s;
        }
        .sidebar .nav-link:hover {
            background: rgba(255,255,255,0.1);
            color: white;
        }
        .sidebar .nav-link i {
            margin-right: 10px;
        }
        .main-content {
            margin-left: 250px;
            padding: 20px;
        }
        .stats-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .stats-card i {
            font-size: 40px;
            margin-bottom: 10px;
            color: #0d6efd;
        }
        .book-card {
            height: 100%;
            transition: transform 0.3s;
        }
        .book-card:hover {
            transform: translateY(-5px);
        }
    </style>
</head>
<body class="bg-light">
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="text-center mb-4">
            <i class="fas fa-user-graduate fa-3x mb-3"></i>
            <h4>Student Portal</h4>
        </div>
        <nav class="nav flex-column">
            <a class="nav-link active" href="${pageContext.request.contextPath}/student/dashboard.jsp">
                <i class="fas fa-tachometer-alt"></i> Dashboard
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/BookServlet?action=list">
                <i class="fas fa-book"></i> Browse Books
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/MyBooksServlet">
                <i class="fas fa-bookmark"></i> My Books
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/student/history.jsp">
                <i class="fas fa-history"></i> Issue History
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/ProfileServlet">
                <i class="fas fa-user-circle"></i> Profile
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/LoginServlet?action=logout">
                <i class="fas fa-sign-out-alt"></i> Logout
            </a>
        </nav>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <div class="container-fluid">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Welcome, ${student.firstName}!</h2>
                <div class="text-muted">
                    <i class="fas fa-id-card"></i> Student ID: ${student.studentId}
                </div>
            </div>
            
            <!-- Stats Cards -->
            <div class="row">
                <div class="col-md-4">
                    <div class="stats-card text-center">
                        <i class="fas fa-book-open"></i>
                        <h3>${currentlyBorrowed}</h3>
                        <p class="text-muted">Currently Borrowed</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stats-card text-center">
                        <i class="fas fa-history"></i>
                        <h3>${totalBorrowed}</h3>
                        <p class="text-muted">Total Books Borrowed</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stats-card text-center">
                        <i class="fas fa-clock"></i>
                        <h3>${dueBooks}</h3>
                        <p class="text-muted">Books Due Soon</p>
                    </div>
                </div>
            </div>

            <!-- Currently Borrowed Books -->
            <div class="card mt-4">
                <div class="card-header">
                    <h5 class="card-title mb-0">Currently Borrowed Books</h5>
                </div>
                <div class="card-body">
                    <!-- Debug output -->
                    <c:if test="${empty borrowedBooks}">
                        <div class="alert alert-info">
                            Debug: No books found in borrowedBooks attribute
                        </div>
                    </c:if>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Book Title</th>
                                    <th>Author</th>
                                    <th>ISBN</th>
                                    <th>Issue Date</th>
                                    <th>Due Date</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${borrowedBooks}" var="book">
                                    <tr>
                                        <td>${book.title}</td>
                                        <td>${book.author}</td>
                                        <td>${book.isbn}</td>
                                        <td>${book.issueDate}</td>
                                        <td class="${book.overdue ? 'text-danger' : ''}">${book.dueDate}</td>
                                        <td>
                                            <span class="badge bg-${book.overdue ? 'danger' : 'success'}">
                                                ${book.overdue ? 'Overdue' : 'On Time'}
                                            </span>
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/BookServlet" method="post" style="display: inline;">
                                                <input type="hidden" name="action" value="return">
                                                <input type="hidden" name="issueId" value="${book.id}">
                                                <button type="submit" class="btn btn-sm btn-primary">
                                                    <i class="fas fa-undo"></i> Return
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty borrowedBooks}">
                                    <tr>
                                        <td colspan="7" class="text-center">No books currently borrowed</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Recommended Books -->
            <div class="card mt-4">
                <div class="card-header">
                    <h5 class="card-title mb-0">Recommended Books</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <c:forEach items="${recommendedBooks}" var="book">
                            <div class="col-md-3 mb-4">
                                <div class="card book-card">
                                    <img src="${book.coverImage}" class="card-img-top" alt="${book.title}">
                                    <div class="card-body">
                                        <h5 class="card-title">${book.title}</h5>
                                        <p class="card-text text-muted">${book.author}</p>
                                        <a href="book-details?id=${book.id}" class="btn btn-primary btn-sm">View Details</a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 