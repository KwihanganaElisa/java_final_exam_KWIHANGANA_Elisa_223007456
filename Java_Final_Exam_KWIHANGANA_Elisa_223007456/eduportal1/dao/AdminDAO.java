package com.eduportal.dao;

import com.eduportal.model.Admin;
import java.sql.*;
import java.util.Date;

public class AdminDAO {

    // Use lowercase 'admin' to match your 'desc admin' output
    private static final String SELECT_BY_USERNAME_SQL = "SELECT * FROM admin WHERE Username = ?";

    public Admin getByUsername(String username) {
        // Try-with-resources ensures the connection closes even if an error occurs
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME_SQL)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // MAP DATABASE COLUMNS TO MODEL CONSTRUCTOR
                    return new Admin(
                        rs.getInt("AdminID"),        // Matches DB
                        rs.getString("Username"),   // Matches DB
                        rs.getString("Password"),   // Matches DB
                        "Administrator",             // DEFAULT: 'Role' column is missing in your DB
                        rs.getString("full_name"),  // Matches DB (lowercase with underscore)
                        "admin@eduportal.com",      // DEFAULT: 'Email' column is missing in your DB
                        rs.getTimestamp("CreatedAt") // Matches DB
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error in AdminDAO: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}