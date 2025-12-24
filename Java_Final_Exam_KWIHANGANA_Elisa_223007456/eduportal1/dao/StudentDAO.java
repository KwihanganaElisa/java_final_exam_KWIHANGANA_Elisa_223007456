package com.eduportal.dao;

import com.eduportal.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Data Access Object for the Student entity.
 * FIXED: Resolved constructor mismatch and optimized resource management.
 */
public class StudentDAO implements BaseDAO<Student> {

    // --- SQL Statements - Updated to lowercase 'student' ---
    private static final String INSERT_SQL = "INSERT INTO student (Username, Password, Role, full_name, email, course_name, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT StudentID, Username, Password, Role, full_name, email, course_name, CreatedAt FROM student";
    private static final String SELECT_BY_ID_SQL = "SELECT StudentID, Username, Password, Role, full_name, email, course_name, CreatedAt FROM student WHERE StudentID = ?";
    private static final String SELECT_BY_USERNAME_SQL = "SELECT StudentID, Username, Password, Role, full_name, email, course_name, CreatedAt FROM student WHERE Username = ?";
    private static final String UPDATE_SQL = "UPDATE student SET Username=?, Password=?, Role=?, full_name=?, email=?, course_name=? WHERE StudentID=?";
    private static final String DELETE_SQL = "DELETE FROM student WHERE StudentID=?";

    /**
     * Helper method to convert a ResultSet row into a Student object.
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("StudentID"),
            rs.getString("Username"),
            rs.getString("Password"),
            rs.getString("Role"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("course_name"),
            rs.getTimestamp("CreatedAt")
        );
    }

    @Override
    public boolean insert(Student student) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getPassword());
            ps.setString(3, student.getRole());
            ps.setString(4, student.getfull_name());
            ps.setString(5, student.getemail());
            ps.setString(6, student.getcourse_name());
            ps.setTimestamp(7, new Timestamp(student.getCreatedAt() != null ? student.getCreatedAt().getTime() : System.currentTimeMillis()));
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Student getById(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student getByUsername(String username) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME_SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    
    @Override
    public boolean update(Student student) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getPassword());
            ps.setString(3, student.getRole());
            ps.setString(4, student.getfull_name());
            ps.setString(5, student.getemail());
            ps.setString(6, student.getcourse_name());
            ps.setInt(7, student.getStudentID());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Specialized Dashboard Methods ---

    public List<Student> getStudentsByCourse(String courseName) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE course_name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> getStudentsByCourse(int courseID) {
        List<Student> students = new ArrayList<>();
        // Join with enrollment table to find who is registered
        String sql = "SELECT s.* FROM student s " +
                     "JOIN enrollment e ON s.StudentID = e.StudentID " +
                     "WHERE e.CourseID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // This now calls the new 4-parameter constructor in Student model
                    students.add(new Student(
                        rs.getInt("StudentID"),
                        rs.getString("Username"),
                        rs.getString("full_name"), 
                        rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return students;
    }

    public boolean saveNewUser(String username, String password, String fullName, String email) {
        Student newStudent = new Student(0, username, password, "Student", fullName, email, "Not Assigned", new Date());
        return insert(newStudent);
    }
}