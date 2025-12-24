package com.eduportal.dao;

import com.eduportal.model.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GradeDAO implements BaseDAO<Grade> {

    // Match table name 'grade' (lowercase is safer in MySQL)
    private static final String INSERT_SQL = "INSERT INTO grade (StudentID, AssignmentID, Score, LetterGrade) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM grade";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM grade WHERE GradeID = ?";
    private static final String UPDATE_SQL = "UPDATE grade SET StudentID=?, AssignmentID=?, Score=?, LetterGrade=? WHERE GradeID=?";
    private static final String DELETE_SQL = "DELETE FROM grade WHERE GradeID=?";

    private Grade extractGradeFromResultSet(ResultSet rs) throws SQLException {
        // Syncing with your 'desc grade' output
        return new Grade(
            rs.getInt("GradeID"),
            rs.getInt("StudentID"),
            rs.getInt("AssignmentID"),
            "Final", 
            rs.getDouble("Score"),
            rs.getString("LetterGrade"),
            rs.getTimestamp("CreatedAt") != null ? rs.getTimestamp("CreatedAt") : new Date()
        );
    }

    @Override
    public boolean insert(Grade grade) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, grade.getStudentID());
            ps.setInt(2, grade.getAssignmentID());
            ps.setDouble(3, grade.getScore());
            ps.setString(4, grade.getLetterGrade());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Grade> getAll() {
        List<Grade> grades = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                grades.add(extractGradeFromResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return grades;
    }

    // Specialized query for Student Dashboard
    public List<Grade> getFinalGradesByStudentID(int studentID) {
        List<Grade> grades = new ArrayList<>();
        // JOIN query to get Course details along with Grade
        String sql = "SELECT c.course_code, c.course_name, c.credits, " +
                     "g.LetterGrade, g.Score, a.Title AS Semester " +
                     "FROM grade g " +
                     "JOIN assignment a ON g.AssignmentID = a.AssignmentID " +
                     "JOIN course c ON a.CourseID = c.CourseID " +
                     "WHERE g.StudentID = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double score = rs.getDouble("Score");
                    grades.add(new Grade(
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getString("LetterGrade"),
                        score,
                        rs.getString("Semester"),
                        calculateGPAPoints(score),
                        studentID
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Grade retrieval error: " + e.getMessage());
        }
        return grades;
    }

    public List<Grade> getGradesByAssignment(int assignmentID) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grade WHERE AssignmentID = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(extractGradeFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("GradeDAO Error: " + e.getMessage());
        }
        return grades;
    }
 // --- SPECIALIZED STUDENT VIEW METHODS ---

    public List<Grade> getGradesByStudentID(int studentID) {
        return getFinalGradesByStudentID(studentID);
    }
    private double calculateGPAPoints(double score) {
        if (score >= 90) return 4.0;
        if (score >= 80) return 3.0;
        if (score >= 70) return 2.0;
        if (score >= 60) return 1.0;
        return 0.0;
    }

    // Standard CRUD overrides
    @Override public Grade getById(int id) { /* same logic using extract method */ return null; }
    @Override public boolean update(Grade grade) { /* ... */ return false; }
    @Override public boolean delete(int id) { /* ... */ return false; }
}