<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Books - Library Management System</title>
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
            <a class="nav-link active" href="${pageContext.request.contextPath}/BookServlet?action=list">
                <i class="fas fa-book"></i> Manage Books
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/StudentManagementServlet">
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
                <h2>Manage Books</h2>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addBookModal">
                    <i class="fas fa-plus"></i> Add New Book
                </button>
            </div>

            <!-- Alerts -->
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

            <!-- Books Table -->
            <div class="card">
                <div class="card-body">
                    <table id="booksTable" class="table table-striped">
                        <thead>
                            <tr>
                                <th>ISBN</th>
                                <th>Title</th>
                                <th>Author</th>
                                <th>Category</th>
                                <th>Total Copies</th>
                                <th>Available</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${books}" var="book">
                                <tr>
                                    <td>${book.isbn}</td>
                                    <td>${book.title}</td>
                                    <td>${book.author}</td>
                                    <td>${book.category}</td>
                                    <td>${book.totalCopies}</td>
                                    <td>${book.availableCopies}</td>
                                    <td>
                                        <button class="btn btn-sm btn-info" onclick="editBook(${book.id})">
                                            <i class="fas fa-edit"></i> Edit
                                        </button>
                                        <button class="btn btn-sm btn-danger" onclick="deleteBook(${book.id})">
                                            <i class="fas fa-trash"></i> Delete
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Add Book Modal -->
    <div class="modal fade" id="addBookModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add New Book</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/BookServlet" method="post">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="isbn" class="form-label">ISBN</label>
                            <input type="text" class="form-control" id="isbn" name="isbn" required>
                        </div>
                        <div class="mb-3">
                            <label for="title" class="form-label">Title</label>
                            <input type="text" class="form-control" id="title" name="title" required>
                        </div>
                        <div class="mb-3">
                            <label for="author" class="form-label">Author</label>
                            <input type="text" class="form-control" id="author" name="author" required>
                        </div>
                        <div class="mb-3">
                            <label for="category" class="form-label">Category</label>
                            <select class="form-select" id="category" name="category" required>
                                <option value="">Select Category</option>
                                <option value="Fiction">Fiction</option>
                                <option value="Non-Fiction">Non-Fiction</option>
                                <option value="Science">Science</option>
                                <option value="History">History</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="totalCopies" class="form-label">Total Copies</label>
                            <input type="number" class="form-control" id="totalCopies" name="totalCopies" min="1" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Add Book</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.datatables.net/1.10.24/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.10.24/js/dataTables.bootstrap5.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#booksTable').DataTable({
                "pageLength": 10,
                "order": [[1, "asc"]]
            });
        });

        function editBook(id) {
            window.location.href = '${pageContext.request.contextPath}/BookServlet?action=view&id=' + id;
        }

        function deleteBook(id) {
            if(confirm('Are you sure you want to delete this book?')) {
                window.location.href = '${pageContext.request.contextPath}/BookServlet?action=delete&id=' + id;
            }
        }
    </script>
</body>
</html> 