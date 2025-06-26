<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Issues - Library Management System</title>
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
            <a class="nav-link" href="${pageContext.request.contextPath}/AdminDashboardServlet">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/BookServlet?action=list">
                <i class="bi bi-book"></i> Manage Books
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/StudentManagementServlet">
                <i class="bi bi-people"></i> Manage Students
            </a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/issues.jsp">
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
                <h2>Book Issues</h2>
            </div>

            <!-- Alert Messages -->
            <c:if test="${not empty param.success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${param.success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${param.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Issues Table -->
            <div class="card">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Book Title</th>
                                    <th>Author</th>
                                    <th>Student Name</th>
                                    <th>Student ID</th>
                                    <th>Issue Date</th>
                                    <th>Due Date</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentIssues}" var="issue">
                                    <tr>
                                        <td>${issue.bookTitle}</td>
                                        <td>${issue.bookAuthor}</td>
                                        <td>${issue.studentName}</td>
                                        <td>${issue.studentId}</td>
                                        <td>${issue.issueDate}</td>
                                        <td class="${issue.overdue ? 'text-danger' : ''}">${issue.dueDate}</td>
                                        <td>
                                            <span class="badge bg-${issue.overdue ? 'danger' : (issue.status == 'issued' ? 'success' : 'secondary')}">
                                                ${issue.overdue ? 'Overdue' : (issue.status == 'issued' ? 'Issued' : 'Returned')}
                                            </span>
                                        </td>
                                        <td>
                                            <c:if test="${issue.status == 'issued'}">
                                                <form action="${pageContext.request.contextPath}/BookServlet" method="post" style="display: inline;">
                                                    <input type="hidden" name="action" value="return">
                                                    <input type="hidden" name="issueId" value="${issue.id}">
                                                    <button type="submit" class="btn btn-sm btn-primary">
                                                        <i class="bi bi-arrow-return-left"></i> Return
                                                    </button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty recentIssues}">
                                    <tr>
                                        <td colspan="8" class="text-center">No book issues found</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 