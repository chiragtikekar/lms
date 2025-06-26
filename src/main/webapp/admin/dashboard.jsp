<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Library Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css">
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
    </style>
</head>
<body class="bg-light">
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="text-center mb-4">
            <i class="bi bi-book-reader fs-1 mb-3"></i>
            <h4>Admin Panel</h4>
        </div>
        <nav class="nav flex-column">
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/dashboard.jsp">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/BookServlet?action=list">
                <i class="bi bi-book"></i> Manage Books
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/StudentManagementServlet">
                <i class="bi bi-people"></i> Manage Students
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/issues">
                <i class="bi bi-journal-text"></i> Book Issues
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/ProfileServlet">
                <i class="bi bi-person-circle"></i> Profile
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/LoginServlet?action=logout">
                <i class="bi bi-box-arrow-right"></i> Logout
            </a>
        </nav>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <div class="container-fluid">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Admin Dashboard</h2>
            </div>

            <!-- Stats Cards -->
            <div class="row">
                <div class="col-md-4 mb-4">
                    <div class="card bg-primary text-white">
                        <div class="card-body">
                            <h5 class="card-title">Total Books</h5>
                            <p class="card-text display-4">${totalBooks}</p>
                            <a href="../BookServlet?action=list" class="btn btn-light">Manage Books</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-4">
                    <div class="card bg-success text-white">
                        <div class="card-body">
                            <h5 class="card-title">Total Students</h5>
                            <p class="card-text display-4">${totalStudents}</p>
                            <a href="../StudentManagementServlet" class="btn btn-light">Manage Students</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-4">
                    <div class="card bg-warning text-white">
                        <div class="card-body">
                            <h5 class="card-title">Pending Approvals</h5>
                            <p class="card-text display-4">${pendingApprovals}</p>
                            <a href="../StudentManagementServlet" class="btn btn-light">View Pending</a>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12 mb-4">
                    <div class="card">
                        <div class="card-header">
                            <h5 class="card-title mb-0">All Book Issues</h5>
                        </div>
                        <div class="card-body">
                            <!-- Debug output -->
                            <c:if test="${empty recentIssues}">
                                <div class="alert alert-info">
                                    Debug: No issues found in recentIssues attribute
                                </div>
                            </c:if>
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Book Title</th>
                                            <th>Student Name</th>
                                            <th>Student ID</th>
                                            <th>Issue Date</th>
                                            <th>Due Date</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${recentIssues}" var="issue">
                                            <tr>
                                                <td>${issue.bookTitle}</td>
                                                <td>${issue.studentName}</td>
                                                <td>${issue.studentId}</td>
                                                <td>${issue.issueDate}</td>
                                                <td>${issue.dueDate}</td>
                                                <td>
                                                    <span class="badge bg-${issue.status == 'issued' ? 'primary' : 'success'}">
                                                        ${issue.status}
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty recentIssues}">
                                            <tr>
                                                <td colspan="6" class="text-center">No book issues found</td>
                                            </tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-12 mb-4">
                    <div class="card">
                        <div class="card-header">
                            <h5 class="card-title mb-0">Recent Student Registrations</h5>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Student ID</th>
                                            <th>Department</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${recentStudents}" var="student">
                                            <tr>
                                                <td>${student.firstName} ${student.lastName}</td>
                                                <td>${student.studentId}</td>
                                                <td>${student.department}</td>
                                                <td>
                                                    <span class="badge bg-${student.status == 'approved' ? 'success' : 'warning'}">
                                                        ${student.status}
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
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