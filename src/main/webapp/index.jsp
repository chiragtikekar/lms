<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Management System</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hero-section {
            background: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7)),
                        url('assets/images/library-bg.jpg');
            background-size: cover;
            background-position: center;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
        }
        .auth-buttons .btn {
            margin: 0 10px;
            padding: 10px 30px;
        }
    </style>
</head>
<body>
    <div class="hero-section">
        <div class="container text-center">
            <h1 class="display-4 mb-4">Welcome to Library Management System</h1>
            <p class="lead mb-5">A comprehensive solution for managing library resources and user access</p>
            
            <div class="auth-buttons">
                <a href="login.jsp" class="btn btn-primary btn-lg">Login</a>
                <a href="register.jsp" class="btn btn-outline-light btn-lg">Register</a>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS and dependencies -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 