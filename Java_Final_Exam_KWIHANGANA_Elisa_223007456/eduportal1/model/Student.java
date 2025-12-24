package com.eduportal.model;

import java.util.Date;

/**
 * Represents a Student in the Education Portal System.
 */
public class Student {
    
    private int studentID;  
    private String username;
    private String password;
    private String role;
    
    private String full_name;
    private String email;
    private String course_name;
    private Date createdAt;

    // --- NEW CONSTRUCTOR: Fixes the 'Undefined' error in StudentDAO ---
    // This allows the DAO to create a student with just the core ID, User, Name, and Email
    public Student(int studentID, String username, String full_name, String email) {
        this.studentID = studentID;
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.role = "Student"; // Default role
        this.createdAt = new Date(); // Default timestamp
    }

    // --- Constructor 1: Full constructor (from Database) ---
    public Student(int studentID, String username, String password, String role, 
                    String attribute1, String attribute2, String attribute3, Date createdAt) {
        this.studentID = studentID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.full_name = attribute1;
        this.email = attribute2;
        this.course_name = attribute3;
        this.createdAt = createdAt;
    }

    // --- Constructor 2: Convenience constructor (for New Students) ---
    public Student(int studentID, String username, String password, String role, 
                    String name, String email, String course) {
        this(studentID, username, password, role, name, email, course, new Date());
    }

    // --- THE FIX FOR JCOMBOBOX ---
    @Override
    public String toString() {
        return (full_name != null ? full_name : "Unknown") + " (ID: " + studentID + ")";
    }

    // --- Getters and Setters ---
    public int getStudentID() { return studentID; }
    public void setStudentID(int studentID) { this.studentID = studentID; }
    
    public String getName() {
        return this.full_name; 
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getfull_name() { return full_name; }
    public void setfull_name(String attribute1) { this.full_name = attribute1; }

    public String getEmail() {
        return email;
    }

    public String getemail() { return email; }
    public void setemail(String attribute2) { this.email = attribute2; }

    public String getcourse_name() { return course_name; }
    public void setcourse_name(String attribute3) { this.course_name = attribute3; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // Aliases for GUI compatibility
    public void setName(String newName) { this.full_name = newName; }
    public void setEmail(String newEmail) { this.email = newEmail; }
    public void setCourseName(String newCourseName) { this.course_name = newCourseName; }
}