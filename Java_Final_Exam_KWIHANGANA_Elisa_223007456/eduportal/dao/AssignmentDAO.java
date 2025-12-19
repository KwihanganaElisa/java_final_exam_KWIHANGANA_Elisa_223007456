package com.eduportal.dao;

import com.eduportal.model.Assignment;
import com.eduportal.model.Course;
import com.eduportal.dao.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Data Access Object for the Assignment entity.
 * Updated to match the lowercase 'assignment' table and generic 'Course' attributes.
 */
public class AssignmentDAO implements BaseDAO<Assignment> {

    // Matches lowercase table name 'assignment' from your DB
    private static final String INSERT_SQL = 
        "INSERT INTO assignment (CourseID, Title, Type, MaxScore, ReferenceID, Description, DueDate, Status, Remarks, CreatedAt) " + 
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
    private static final String SELECT_ALL_SQL = "SELECT * FROM assignment"; 
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM assignment WHERE AssignmentID = ?";
    
    private static final String UPDATE_SQL = 
        "UPDATE assignment SET CourseID=?, Title=?, Type=?, MaxScore=?, ReferenceID=?, Description=?, DueDate=?, Status=?, Remarks=? " + 
        "WHERE AssignmentID=?";
        
    private static final String DELETE_SQL = "DELETE FROM assignment WHERE AssignmentID=?";

    private Assignment extractAssignmentFromResultSet(ResultSet rs) throws SQLException {
        // Using the full constructor to ensure all fields are captured
        return new Assignment(
            rs.getInt("AssignmentID"),
            rs.getInt("CourseID"),
            rs.getString("Title"),
            rs.getString("Type"),
            rs.getInt("MaxScore"),
            rs.getString("ReferenceID"),
            rs.getString("Description"),
            rs.getTimestamp("DueDate"),
            rs.getString("Status"),
            rs.getString("Remarks"),
            rs.getTimestamp("CreatedAt")
        );
    }

    @Override
    public boolean insert(Assignment assignment) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            
            ps.setInt(1, assignment.getCourseID());
            ps.setString(2, assignment.getTitle()); 
            ps.setString(3, assignment.getType());
            ps.setInt(4, assignment.getMaxScore());
            ps.setString(5, assignment.getReferenceID()); 
            ps.setString(6, assignment.getDescription());
            
            if (assignment.getDueDate() != null) {
                ps.setTimestamp(7, new Timestamp(assignment.getDueDate().getTime()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }
            
            ps.setString(8, assignment.getStatus());
            ps.setString(9, assignment.getRemarks());
            ps.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Assignment getById(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractAssignmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Assignment> getAll() {
        List<Assignment> assignments = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                assignments.add(extractAssignmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }
    
    @Override
    public boolean update(Assignment assignment) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            
            ps.setInt(1, assignment.getCourseID());
            ps.setString(2, assignment.getTitle());
            ps.setString(3, assignment.getType());
            ps.setInt(4, assignment.getMaxScore());
            ps.setString(5, assignment.getReferenceID());
            ps.setString(6, assignment.getDescription());
            
            if (assignment.getDueDate() != null) {
                ps.setTimestamp(7, new Timestamp(assignment.getDueDate().getTime()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }
            
            ps.setString(8, assignment.getStatus());
            ps.setString(9, assignment.getRemarks());
            ps.setInt(10, assignment.getAssignmentID());
            
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

    // --- HELPER METHODS FOR INSTRUCTOR DASHBOARD ---

    public List<Course> getCoursesByInstructor(int instructorID) throws SQLException { 
        List<Course> courses = new ArrayList<>();
        // Using 'Course' (Title Case) and generic attributes as per your DESCRIBE output
        String sql = "SELECT * FROM Course WHERE InstructorID = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, instructorID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Attribute1 = Name, Attribute2 = Code, Attribute3 = Credits
                    courses.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("Attribute2"), // Course Code
                        rs.getString("Attribute1"), // Course Name
                        "Instructor ID: " + rs.getInt("InstructorID"), 
                        rs.getTimestamp("CreatedAt")
                    ));
                }
            }
        }
        return courses;
    }

    public List<Assignment> getAssignmentsByCourse(int courseID) throws SQLException { 
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignment WHERE CourseID = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, courseID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assignments.add(extractAssignmentFromResultSet(rs));
                }
            }
        }
        return assignments;
    }

    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { }
        try { if (conn != null) conn.close(); } catch (SQLException e) { }
    }
}