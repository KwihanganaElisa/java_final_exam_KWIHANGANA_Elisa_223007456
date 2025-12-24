package com.eduportal.dao;

import com.eduportal.model.Assignment;
import com.eduportal.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Assignment entity.
 * SYNCED with database schema: AssignmentID, CourseID, Title, DueDate
 */
public class AssignmentDAO implements BaseDAO<Assignment> {

    // SQL strictly matching your 4-column schema
    private static final String INSERT_SQL = "INSERT INTO assignment (CourseID, Title, DueDate) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM assignment"; 
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM assignment WHERE AssignmentID = ?";
    private static final String UPDATE_SQL = "UPDATE assignment SET CourseID=?, Title=?, DueDate=? WHERE AssignmentID=?";
    private static final String DELETE_SQL = "DELETE FROM assignment WHERE AssignmentID=?";

    private Assignment extractAssignmentFromResultSet(ResultSet rs) throws SQLException {
        // We map the 4 real columns and provide defaults for the others to avoid "Column Not Found" errors
        return new Assignment(
            rs.getInt("AssignmentID"),
            rs.getInt("CourseID"),
            rs.getString("Title"),
            "General",       // Default for missing 'Type'
            100,             // Default for missing 'MaxScore'
            "REF-000",       // Default for missing 'ReferenceID'
            "No description",// Default for missing 'Description'
            rs.getTimestamp("DueDate"),
            "Active",        // Default for missing 'Status'
            "",              // Default for missing 'Remarks'
            new Timestamp(System.currentTimeMillis()) // Default for missing 'CreatedAt'
        );
    }

    @Override
    public boolean insert(Assignment assignment) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            
            ps.setInt(1, assignment.getCourseID());
            ps.setString(2, assignment.getTitle()); 
            
            if (assignment.getDueDate() != null) {
                ps.setTimestamp(3, new Timestamp(assignment.getDueDate().getTime()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting assignment: " + e.getMessage());
            return false;
        }
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
            
            if (assignment.getDueDate() != null) {
                ps.setTimestamp(3, new Timestamp(assignment.getDueDate().getTime()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            
            ps.setInt(4, assignment.getAssignmentID());
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
    @Override
    public Assignment getById(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractAssignmentFromResultSet(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}