-- Create the database
CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;

-- Users table (for both admin and students)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('admin', 'student') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Students table (extends users)
CREATE TABLE students (
    id INT PRIMARY KEY,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(50),
    status ENUM('pending', 'approved', 'blocked') DEFAULT 'pending',
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- Books table
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    cover_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Book issues table
CREATE TABLE book_issues (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    student_id INT NOT NULL,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP NULL,
    status ENUM('issued', 'returned', 'overdue') DEFAULT 'issued',
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Book reviews table
CREATE TABLE book_reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    student_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Insert default admin user
INSERT INTO users (first_name, last_name, email, password, user_type)
VALUES ('Admin', 'User', 'admin@library.com', 'admin123', 'admin');

-- Create triggers to manage book copies
DELIMITER //
CREATE TRIGGER after_issue_insert
AFTER INSERT ON book_issues
FOR EACH ROW
BEGIN
    UPDATE books 
    SET available_copies = available_copies - 1
    WHERE id = NEW.book_id;
END//

CREATE TRIGGER after_issue_update
AFTER UPDATE ON book_issues
FOR EACH ROW
BEGIN
    IF NEW.status = 'returned' AND OLD.status != 'returned' THEN
        UPDATE books 
        SET available_copies = available_copies + 1
        WHERE id = NEW.book_id;
    END IF;
END//
DELIMITER ;

-- Create indexes for better performance
CREATE INDEX idx_book_issues_student ON book_issues(student_id);
CREATE INDEX idx_book_issues_book ON book_issues(book_id);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_students_student_id ON students(student_id); 