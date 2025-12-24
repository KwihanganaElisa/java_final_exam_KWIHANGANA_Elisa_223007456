package com.eduportal.dao;

import com.eduportal.model.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO implements BaseDAO<Enrollment> {

    // CLEANED SQL: Matches your database schema (5 columns)
    private static final String INSERT_SQL = "INSERT INTO enrollment (StudentID, CourseID, Status, EnrollDate) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM enrollment";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM enrollment WHERE EnrollmentID = ?";
    private static final String UPDATE_SQL = "UPDATE enrollment SET StudentID=?, CourseID=?, Status=?, EnrollDate=? WHERE EnrollmentID=?";
    private static final String DELETE_SQL = "DELETE FROM enrollment WHERE EnrollmentID=?";

    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        // We map the 5 DB columns to the Enrollment Model. 
        // We pass "" for ReferenceID, Description, and Remarks so the Model doesn't break.
        return new Enrollment(
            rs.getInt("EnrollmentID"),
            rs.getInt("StudentID"),
            rs.getInt("CourseID"),
            "N/A",                      // Mapping missing ReferenceID
            "No Description",           // Mapping missing Description
            rs.getTimestamp("EnrollDate"),
            rs.getString("Status"),
            ""                          // Mapping missing Remarks
        );
    }

    @Override
    public boolean insert(Enrollment enrollment) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, enrollment.getStudentID());
            ps.setInt(2, enrollment.getCourseID());
            ps.setString(3, enrollment.getStatus());
            ps.setTimestamp(4, new Timestamp(enrollment.getEnrollDate().getTime()));
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        enrollment.setEnrollmentID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Insert): " + e.getMessage());
        }
        return false;
    }

    @Override
    public Enrollment getById(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractEnrollmentFromResultSet(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Enrollment> getAll() {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return enrollments;
    }

    @Override
    public boolean update(Enrollment enrollment) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setInt(1, enrollment.getStudentID());
            ps.setInt(2, enrollment.getCourseID());
            ps.setString(3, enrollment.getStatus());
            ps.setTimestamp(4, new Timestamp(enrollment.getEnrollDate().getTime()));
            ps.setInt(5, enrollment.getEnrollmentID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public String getStudentCourseStatus(int studentID, int courseID) {
        String sql = "SELECT Status FROM enrollment WHERE StudentID = ? AND CourseID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentID);
            ps.setInt(2, courseID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("Status");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Not Enrolled"; 
    }
}