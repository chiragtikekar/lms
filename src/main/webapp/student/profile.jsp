<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student Profile - Library Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css">
    <style>
        .sidebar {
            width: 250px;
            position: fixed;
            top: 0;
            left: 0;
            height: 100vh;
            background: #343a40;
            padding: 20px;
            color: white;
        }
        .main-content {
            margin-left: 250px;
            padding: 20px;
        }
        .nav-link {
            color: rgba(255,255,255,.8);
            margin-bottom: 5px;
        }
        .nav-link:hover {
            color: white;
        }
        .nav-link.active {
            color: white;
            background: rgba(255,255,255,.1);
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="text-center mb-4">
            <i class="bi bi-book-reader fs-1 mb-3"></i>
            <h4>Student Panel</h4>
        </div>
        <nav class="nav flex-column">
            <a class="nav-link" href="${pageContext.request.contextPath}/student/dashboard.jsp">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/BookServlet?action=list">
                <i class="bi bi-book"></i> Browse Books
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/student/issues.jsp">
                <i class="bi bi-clipboard-check"></i> My Issues
            </a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/ProfileServlet">
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
                <h2>My Profile</h2>
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

            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Personal Information</h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/ProfileServlet" method="post">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="firstName" class="form-label">First Name</label>
                                <input type="text" class="form-control" id="firstName" name="firstName" 
                                       value="${firstName}" required>
                            </div>
                            <div class="col-md-6">
                                <label for="lastName" class="form-label">Last Name</label>
                                <input type="text" class="form-control" id="lastName" name="lastName" 
                                       value="${lastName}" required>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" 
                                   value="${email}" required>
                        </div>
                        <div class="mb-3">
                            <label for="studentId" class="form-label">Student ID</label>
                            <input type="text" class="form-control" id="studentId" 
                                   value="${studentId}" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="department" class="form-label">Department</label>
                            <input type="text" class="form-control" id="department" 
                                   value="${department}" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="status" class="form-label">Status</label>
                            <input type="text" class="form-control" id="status" 
                                   value="${status}" readonly>
                        </div>

                        <h5 class="mt-4 mb-3">Change Password</h5>
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">Current Password</label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword">
                        </div>
                        <div class="mb-3">
                            <label for="newPassword" class="form-label">New Password</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword">
                        </div>

                        <button type="submit" class="btn btn-primary">Update Profile</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 