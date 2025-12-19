package com.eduportal.dao;

import com.eduportal.model.Grade;
import com.eduportal.dao.DatabaseConnector; // Assuming this utility exists
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for the Grade entity.
 * Implements full CRUD operations and specialized queries for student views.
 */
public class GradeDAO implements BaseDAO<Grade> {

    // --- Existing SQL Statements (CRUD) ---
    private static final String INSERT_SQL = "INSERT INTO Grade (StudentID, AssignmentID, GradeType, Score, LetterGrade, CreatedAt) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT GradeID, StudentID, AssignmentID, GradeType, Score, LetterGrade, CreatedAt FROM Grade";
    private static final String SELECT_BY_ID_SQL = "SELECT GradeID, StudentID, AssignmentID, GradeType, Score, LetterGrade, CreatedAt FROM Grade WHERE GradeID = ?";
    private static final String UPDATE_SQL = "UPDATE Grade SET StudentID=?, AssignmentID=?, GradeType=?, Score=?, LetterGrade=? WHERE GradeID=?";
    private static final String DELETE_SQL = "DELETE FROM Grade WHERE GradeID=?";

    // --- NEW SQL Statement for StudentGradesPanel (KEPT FOR REFERENCE) ---
    // NOTE: This complex SQL is bypassed by mock data for compilation/testing.
    private static final String SELECT_FINAL_GRADES_SQL = 
        "SELECT " +
        "    c.CourseCode, c.CourseName, c.Credits, e.Semester, " +
        "    (SELECT AVG(g.Score) FROM Grade g JOIN Assignment a ON g.AssignmentID = a.AssignmentID WHERE a.CourseID = c.CourseID AND g.StudentID = ?), " + // Simplified: AVG score
        "    (SELECT fn_Calculate_LetterGrade(AVG(g.Score)) FROM Grade g JOIN Assignment a ON g.AssignmentID = a.AssignmentID WHERE a.CourseID = c.CourseID AND g.StudentID = ?), " + // Letter Grade
        "    (SELECT fn_Calculate_GPAPoints(fn_Calculate_LetterGrade(AVG(g.Score))) FROM Grade g JOIN Assignment a ON g.AssignmentID = a.AssignmentID WHERE a.CourseID = c.CourseID AND g.StudentID = ?) " + // GPA points
        "FROM Enrollment e " +
        "JOIN Course c ON e.CourseID = c.CourseID " +
        "WHERE e.StudentID = ? AND e.Status = 'Completed'";

    /**
     * Helper method to convert a ResultSet row into a Grade object (Database Model).
     * Uses the 7-parameter constructor.
     */
    private Grade extractGradeFromResultSet(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("CreatedAt");
        Date createdAt = (ts != null) ? new Date(ts.getTime()) : null;
        
        return new Grade(
            rs.getInt("GradeID"),
            rs.getInt("StudentID"),
            rs.getInt("AssignmentID"),
            rs.getString("GradeType"),
            rs.getDouble("Score"),
            rs.getString("LetterGrade"),
            createdAt
        );
    }
    
    // --- MOCK/HELPER METHOD: Initializes and returns a list of mock grades ---
    /**
     * FIX: Uses the exact 8-parameter View Model constructor defined in the Grade class.
     */
    private List<Grade> initializeMockGrades(int studentID) {
        List<Grade> grades = new ArrayList<Grade>();
        if (studentID == 300) {
            // View Model Constructor signature:
            // (CourseCode, CourseName, Credits, finalLetter, finalScore, Semester, gpaPoints, studentID)
            grades.add(new Grade("CS101", "Intro to Programming", 3, "A-", 89.5, "Fall 2024", 3.7, 300));
            grades.add(new Grade("MA205", "Calculus III", 4, "C", 75.0, "Fall 2024", 2.0, 300));
            grades.add(new Grade("PH100", "Physics Fundamentals", 3, "A", 94.2, "Spring 2025", 4.0, 300));
            grades.add(new Grade("EN320", "Technical Writing", 3, "B", 82.0, "Spring 2025", 3.0, 300));
        }
        return grades;
    }

    // --- NEW METHOD: Get Final Course Grades for StudentGradesPanel ---
    /**
     * Retrieves a list of aggregated final course grades for a student.
     * TEMPORARILY MOCKED to avoid complex DB setup.
     */
    public List<Grade> getFinalGradesByStudentID(int studentID) {
         // FIX: Returning the mock data for now.
         return initializeMockGrades(studentID); 
    }


    // --- Existing CRUD Methods Follow ---

    @Override
    public boolean insert(Grade grade) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS); 
            
            ps.setInt(1, grade.getStudentID());
            ps.setInt(2, grade.getAssignmentID());
            ps.setString(3, grade.getGradeType());
            ps.setDouble(4, grade.getScore());
            ps.setString(5, grade.getLetterGrade());
            ps.setTimestamp(6, new Timestamp(grade.getCreatedAt() != null ? grade.getCreatedAt().getTime() : System.currentTimeMillis()));
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    @Override
    public Grade getById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(SELECT_BY_ID_SQL);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractGradeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignore */ }
        }
        return null;
    }

    @Override
    public List<Grade> getAll() {
        List<Grade> grades = new ArrayList<Grade>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SELECT_ALL_SQL);
            while (rs.next()) {
                grades.add(extractGradeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignore */ }
        }
        return grades;
    }
    
    @Override
    public boolean update(Grade grade) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(UPDATE_SQL);
            
            ps.setInt(1, grade.getStudentID());
            ps.setInt(2, grade.getAssignmentID());
            ps.setString(3, grade.getGradeType());
            ps.setDouble(4, grade.getScore());
            ps.setString(5, grade.getLetterGrade());
            ps.setInt(6, grade.getGradeID()); // WHERE clause
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(DELETE_SQL);
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // Keep the existing skeleton method
    public List<Grade> getGradesByAssignment(int assignmentID) {
        System.out.println("DEBUG: getGradesByAssignment called (Not fully implemented yet).");
        return new ArrayList<Grade>();
    }

    /**
     * Alias for getFinalGradesByStudentID to support the older panel implementation.
     */
    public List<Grade> getGradesByStudentID(int studentID) {
        return getFinalGradesByStudentID(studentID);
    }
}