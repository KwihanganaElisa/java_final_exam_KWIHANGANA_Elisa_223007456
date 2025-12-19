package com.eduportal.model;

import java.util.Date;

/**
 * Represents an Admin in the Education Portal System.
 * This entity is separate for simple management.
 */
public class Admin {
    
    // Primary Key & Authentication Fields
    private int adminID;
    private String username;
    private String password;    // UPDATED from passwordHash
    private String role;
    
    // Descriptive Attributes
    private String fullName;
    private String email;
    
    // Timestamp
    private Date createdAt;

    // --- Constructor ---
    public Admin(int adminID, String username, String password, String role, 
                 String fullName, String email, Date createdAt) {
        this.adminID = adminID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
    }

    // --- Getters and Setters ---
    
    public int getAdminID() { return adminID; }
    public void setAdminID(int adminID) { this.adminID = adminID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}