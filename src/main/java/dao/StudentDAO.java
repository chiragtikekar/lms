package dao;

import models.Student;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private UserDAO userDAO = new UserDAO();

    public boolean create(Student student) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // First create the user
            boolean userCreated = userDAO.create(student);
            if (!userCreated) {
                conn.rollback();
                return false;
            }

            // Then create the student
            String sql = "INSERT INTO students (id, student_id, department, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, student.getId());
                stmt.setString(2, student.getStudentId());
                stmt.setString(3, student.getDepartment());
                stmt.setString(4, student.getStatus());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Student findById(int id) {
        String sql = "SELECT u.*, s.student_id, s.department, s.status " +
                    "FROM users u " +
                    "JOIN students s ON u.id = s.id " +
                    "WHERE u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student findByStudentId(String studentId) {
        String sql = "SELECT u.*, s.student_id, s.department, s.status " +
                    "FROM users u " +
                    "JOIN students s ON u.id = s.id " +
                    "WHERE s.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT u.*, s.student_id, s.department, s.status " +
                    "FROM users u " +
                    "JOIN students s ON u.id = s.id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public boolean update(Student student) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Update user information
            boolean userUpdated = userDAO.update(student);
            if (!userUpdated) {
                conn.rollback();
                return false;
            }

            // Update student information
            String sql = "UPDATE students SET student_id = ?, department = ?, status = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.getStudentId());
                stmt.setString(2, student.getDepartment());
                stmt.setString(3, student.getStatus());
                stmt.setInt(4, student.getId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE students SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        // The user will be deleted automatically due to CASCADE
        return userDAO.delete(id);
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPassword(rs.getString("password"));
        student.setUserType(rs.getString("user_type"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        student.setStudentId(rs.getString("student_id"));
        student.setDepartment(rs.getString("department"));
        student.setStatus(rs.getString("status"));
        return student;
    }
} 