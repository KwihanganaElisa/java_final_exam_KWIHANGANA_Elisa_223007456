package com.eduportal.model;

import java.util.Date;

/**
 * Represents an Instructor in the Education Portal System.
 * Fully compatible with JComboBox and CourseDAO.
 */
public class Instructor {

    private int instructorID;
    private String username;
    private String password;
    private String role;
    
    private String name;
    private String identifier;
    private String status;
    private String location;
    private String contact;
    
    private Date assignedSince;

    public Instructor(int instructorID, String username, String password, String role,
                      String name, String identifier, String status, String location,
                      String contact, Date assignedSince) {
        this.instructorID = instructorID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.identifier = identifier;
        this.status = status;
        this.location = location;
        this.contact = contact;
        this.assignedSince = assignedSince;
    }
    
    public Instructor(int instructorID, String username, String password, String role,
                      String name, String identifier, String status, String location,
                      String contact) {
        this(instructorID, username, password, role, 
             name, identifier, status, location, 
             contact, new Date());
    }

    /**
     * UPDATED: This method ensures the JComboBox displays the Name.
     */
    @Override
    public String toString() {
        return (name != null && !name.isEmpty()) ? name : "Unknown Instructor";
    }

    // --- Getters and Setters (All Retained) ---
    
    public int getInstructorID() { return instructorID; }
    public void setInstructorID(int instructorID) { this.instructorID = instructorID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public Date getAssignedSince() { return assignedSince; }
    public void setAssignedSince(Date assignedSince) { this.assignedSince = assignedSince; }
}