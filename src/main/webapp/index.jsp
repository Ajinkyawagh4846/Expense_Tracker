<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    if (session != null && session.getAttribute("currentUser") != null) {
        response.sendRedirect("dashboard");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Online Expense Tracker</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="#">Expense Tracker</a>
        <div class="d-flex">
            <a href="login" class="btn btn-outline-light me-2">Login</a>
            <a href="register" class="btn btn-light text-primary">Register</a>
        </div>
    </div>
</nav>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 text-center">
            <h1 class="display-5 fw-bold mb-3 text-primary">Manage Your Expenses Smartly</h1>
            <p class="lead mb-4">
                Track your daily spending, analyze where your money goes, and take control of your finances
                with our simple and powerful online expense tracker.
            </p>
            <div class="d-flex justify-content-center gap-3">
                <a href="register" class="btn btn-primary btn-lg px-4">Get Started</a>
                <a href="login" class="btn btn-outline-primary btn-lg px-4">Already have an account?</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

