package com.eduportal.dao;

import com.eduportal.model.Instructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Data Access Object for the Instructor entity.
 * FIXED: Matched to lowercase 'instructor' table and standard 'User' fields.
 */
public class InstructorDAO implements BaseDAO<Instructor> {

    // Matches lowercase table 'instructor' from your SHOW TABLES output
    private static final String INSERT_SQL = "INSERT INTO instructor (Username, Password, Role, Name, Identifier, Status, Location, Contact, AssignedSince) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM instructor";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM instructor WHERE InstructorID = ?";
    private static final String SELECT_BY_USERNAME_SQL = "SELECT * FROM instructor WHERE Username = ?";
    private static final String UPDATE_SQL = "UPDATE instructor SET Username=?, Password=?, Role=?, Name=?, Identifier=?, Status=?, Location=?, Contact=?, AssignedSince=? WHERE InstructorID=?";
    private static final String DELETE_SQL = "DELETE FROM instructor WHERE InstructorID=?";

    /**
     * Helper method to convert a ResultSet row into an Instructor object.
     */
    private Instructor extractInstructorFromResultSet(ResultSet rs) throws SQLException {
        return new Instructor(
            rs.getInt("InstructorID"),
            rs.getString("Username"),
            rs.getString("Password"),
            rs.getString("Role"),
            rs.getString("Name"),
            rs.getString("Identifier"),
            rs.getString("Status"),
            rs.getString("Location"),
            rs.getString("Contact"),
            rs.getTimestamp("AssignedSince")
        );
    }

    @Override
    public boolean insert(Instructor instructor) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            
            ps.setString(1, instructor.getUsername());
            ps.setString(2, instructor.getPassword());
            ps.setString(3, instructor.getRole());
            ps.setString(4, instructor.getName());
            ps.setString(5, instructor.getIdentifier());
            ps.setString(6, instructor.getStatus());
            ps.setString(7, instructor.getLocation());
            ps.setString(8, instructor.getContact());
            ps.setTimestamp(9, instructor.getAssignedSince() != null ? new Timestamp(instructor.getAssignedSince().getTime()) : new Timestamp(System.currentTimeMillis()));
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Instructor getByUsername(String username) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME_SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractInstructorFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ InstructorDAO Error: " + e.getMessage());
        }
        return null; 
    }

    @Override
    public Instructor getById(int id) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractInstructorFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Instructor> getAll() {
        List<Instructor> instructors = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                instructors.add(extractInstructorFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }
    
    @Override
    public boolean update(Instructor instructor) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, instructor.getUsername());
            ps.setString(2, instructor.getPassword());
            ps.setString(3, instructor.getRole());
            ps.setString(4, instructor.getName());
            ps.setString(5, instructor.getIdentifier());
            ps.setString(6, instructor.getStatus());
            ps.setString(7, instructor.getLocation());
            ps.setString(8, instructor.getContact());
            ps.setTimestamp(9, instructor.getAssignedSince() != null ? new Timestamp(instructor.getAssignedSince().getTime()) : null);
            ps.setInt(10, instructor.getInstructorID());
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

    /**
     * Helper for login and user creation.
     */
    public boolean saveNewUser(String username, String password, String fullName, String contact) {
        String sql = "INSERT INTO instructor (Username, Password, Role, Name, Contact, Status, AssignedSince) VALUES (?, ?, 'Instructor', ?, ?, 'Active', NOW())";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, contact);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}