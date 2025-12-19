package com.eduportal.dao;

import com.eduportal.model.Admin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Admin entity.
 */
public class AdminDAO implements BaseDAO<Admin> {

    private static final String SELECT_BY_USERNAME_SQL = "SELECT * FROM Admin WHERE Username = ?";
    
    // Minimal CRUD operations are needed for Admin login, so we omit full CRUD for now
    
    /**
     * Helper method to convert a ResultSet row into an Admin object.
     */
    private Admin extractAdminFromResultSet(ResultSet rs) throws SQLException {
        return new Admin(
            rs.getInt("AdminID"),
            rs.getString("Username"),
            rs.getString("Password"), // CHANGED from PasswordHash
            rs.getString("Role"),
            rs.getString("FullName"),
            rs.getString("Email"),
            rs.getTimestamp("CreatedAt")
        );
    }
    
    /**
     * Finds an Admin user by username for authentication.
     */
    public Admin getByUsername(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(SELECT_BY_USERNAME_SQL);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractAdminFromResultSet(rs);
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

    // Implementing the required BaseDAO methods as placeholders for now
    @Override public boolean insert(Admin object) { return false; }
    @Override public Admin getById(int id) { return null; }
    @Override public List<Admin> getAll() { return new ArrayList<Admin>(); }
    @Override public boolean update(Admin object) { return false; }
    @Override public boolean delete(int id) { return false; }
}