<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Students - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.24/css/dataTables.bootstrap5.min.css">
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
        .nav-tabs .nav-link {
            color: #495057;
        }
        .nav-tabs .nav-link.active {
            font-weight: bold;
        }
    </style>
</head>
<body class="bg-light">
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="text-center mb-4">
            <i class="fas fa-book-reader fa-3x mb-3"></i>
            <h4>Admin Panel</h4>
        </div>
        <nav class="nav flex-column">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard.jsp">
                <i class="fas fa-tachometer-alt"></i> Dashboard
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/books.jsp">
                <i class="fas fa-book"></i> Manage Books
            </a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/StudentManagementServlet">
                <i class="fas fa-users"></i> Manage Students
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/issues.jsp">
                <i class="fas fa-clipboard-list"></i> Book Issues
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
                <h2>Manage Students</h2>
            </div>

            <!-- Stats Cards -->
            <div class="row mb-4">
                <div class="col-md-4">
                    <div class="card bg-primary text-white">
                        <div class="card-body">
                            <h5 class="card-title">Total Students</h5>
                            <p class="card-text display-4">${students.size()}</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card bg-warning text-white">
                        <div class="card-body">
                            <h5 class="card-title">Pending Approvals</h5>
                            <p class="card-text display-4">${pendingCount}</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card bg-success text-white">
                        <div class="card-body">
                            <h5 class="card-title">Approved Students</h5>
                            <p class="card-text display-4">${approvedCount}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Tabs -->
            <ul class="nav nav-tabs mb-4" id="studentTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" id="all-tab" data-bs-toggle="tab" data-bs-target="#all" type="button" role="tab">
                        All Students
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="pending-tab" data-bs-toggle="tab" data-bs-target="#pending" type="button" role="tab">
                        Pending Approvals
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="approved-tab" data-bs-toggle="tab" data-bs-target="#approved" type="button" role="tab">
                        Approved Students
                    </button>
                </li>
            </ul>

            <!-- Tab Content -->
            <div class="tab-content" id="studentTabsContent">
                <!-- All Students Tab -->
                <div class="tab-pane fade show active" id="all" role="tabpanel">
                    <div class="card">
                        <div class="card-body">
                            <table id="allStudentsTable" class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Student ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Department</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${students}" var="student">
                                        <tr>
                                            <td>${student.studentId}</td>
                                            <td>${student.firstName} ${student.lastName}</td>
                                            <td>${student.email}</td>
                                            <td>${student.department}</td>
                                            <td>
                                                <span class="badge bg-${student.status == 'approved' ? 'success' : 'warning'}">
                                                    ${student.status}
                                                </span>
                                            </td>
                                            <td>
                                                <c:if test="${student.status == 'pending'}">
                                                    <form action="${pageContext.request.contextPath}/StudentManagementServlet" method="post" style="display: inline;">
                                                        <input type="hidden" name="action" value="approve">
                                                        <input type="hidden" name="studentId" value="${student.id}">
                                                        <button type="submit" class="btn btn-sm btn-success">
                                                            <i class="fas fa-check"></i> Approve
                                                        </button>
                                                    </form>
                                                    <form action="${pageContext.request.contextPath}/StudentManagementServlet" method="post" style="display: inline;">
                                                        <input type="hidden" name="action" value="reject">
                                                        <input type="hidden" name="studentId" value="${student.id}">
                                                        <button type="submit" class="btn btn-sm btn-danger">
                                                            <i class="fas fa-times"></i> Reject
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Pending Students Tab -->
                <div class="tab-pane fade" id="pending" role="tabpanel">
                    <div class="card">
                        <div class="card-body">
                            <table id="pendingStudentsTable" class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Student ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Department</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${students}" var="student">
                                        <c:if test="${student.status == 'pending'}">
                                            <tr>
                                                <td>${student.studentId}</td>
                                                <td>${student.firstName} ${student.lastName}</td>
                                                <td>${student.email}</td>
                                                <td>${student.department}</td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/StudentManagementServlet" method="post" style="display: inline;">
                                                        <input type="hidden" name="action" value="approve">
                                                        <input type="hidden" name="studentId" value="${student.id}">
                                                        <button type="submit" class="btn btn-sm btn-success">
                                                            <i class="fas fa-check"></i> Approve
                                                        </button>
                                                    </form>
                                                    <form action="${pageContext.request.contextPath}/StudentManagementServlet" method="post" style="display: inline;">
                                                        <input type="hidden" name="action" value="reject">
                                                        <input type="hidden" name="studentId" value="${student.id}">
                                                        <button type="submit" class="btn btn-sm btn-danger">
                                                            <i class="fas fa-times"></i> Reject
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Approved Students Tab -->
                <div class="tab-pane fade" id="approved" role="tabpanel">
                    <div class="card">
                        <div class="card-body">
                            <table id="approvedStudentsTable" class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Student ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Department</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${students}" var="student">
                                        <c:if test="${student.status == 'approved'}">
                                            <tr>
                                                <td>${student.studentId}</td>
                                                <td>${student.firstName} ${student.lastName}</td>
                                                <td>${student.email}</td>
                                                <td>${student.department}</td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.datatables.net/1.10.24/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.10.24/js/dataTables.bootstrap5.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#allStudentsTable').DataTable({
                "pageLength": 10,
                "order": [[0, "asc"]]
            });
            $('#pendingStudentsTable').DataTable({
                "pageLength": 10,
                "order": [[0, "asc"]]
            });
            $('#approvedStudentsTable').DataTable({
                "pageLength": 10,
                "order": [[0, "asc"]]
            });
        });
    </script>
</body>
</html> 