# Library Management System

A professional Library Management System built using Java JSP, Servlet, and MySQL for Advanced Java Programming (AJP) subject.

## Features

### Admin Features
- Login and Registration
- Manage Books (Add, Update, Delete, View)
- Manage Students (View, Approve/Reject, Block/Unblock)
- Manage Book Issues
- View All Transaction History
- Update Profile and Password

### Student Features
- Registration and Login
- View Available Books
- Issue/Return Books
- View Issue History
- Update Profile and Password

## Technical Stack
- Java 8+
- JSP (JavaServer Pages)
- Servlets
- MySQL Database
- HTML5, CSS3, JavaScript
- Bootstrap 5
- JDBC
- Apache Tomcat 9.0

## Setup Instructions
1. Install Java JDK 8 or higher
2. Install Apache Tomcat 9.0
3. Install MySQL Server
4. Import the database schema using provided SQL file
5. Configure database connection in `src/main/java/utils/DBConnection.java`
6. Deploy the project on Tomcat server

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   ├── controllers/    # Servlet controllers
│   │   ├── dao/           # Data Access Objects
│   │   ├── models/        # Java Beans/Models
│   │   └── utils/         # Utility classes
│   └── webapp/
│       ├── WEB-INF/
│       ├── admin/         # Admin JSP pages
│       ├── student/       # Student JSP pages
│       ├── assets/        # CSS, JS, Images
│       └── common/        # Common JSP components
``` 