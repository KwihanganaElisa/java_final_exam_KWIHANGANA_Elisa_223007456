package com.eduportal.model;

import java.util.Date;

/**
 * Represents a Course offered in the Education Portal System.
 */
public class Course {

    private int courseID;
    
    // Database generic mapping
    private String attribute1; // Course Title
    private String attribute2; // Course Code
    private String attribute3; // Instructor ID (as String)
    
    // UI specific fields
    private String instructorName;
    private int credits;
    private String enrollmentStatus;
    private Date createdAt;

    // --- Constructor 1: DAO Constructor ---
    public Course(int courseID, String attribute1, String attribute2, String attribute3, Date createdAt) {
        this.courseID = courseID;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
        this.attribute3 = attribute3;
        this.createdAt = createdAt;
        this.instructorName = "N/A"; 
        this.credits = 0;
        this.enrollmentStatus = "Unknown";
    }

    // --- Constructor 2: Student View Constructor ---
    public Course(int courseID, String courseCode, String courseName, String instructorName, int credits, String enrollmentStatus) {
        this.courseID = courseID;
        this.attribute1 = courseName;
        this.attribute2 = courseCode;
        this.instructorName = instructorName;
        this.credits = credits;
        this.enrollmentStatus = enrollmentStatus;
        this.createdAt = new Date();
    }

    // --- UI DISPLAY LOGIC ---
    @Override
    public String toString() {
        // This ensures the JComboBox shows: "CS101 - Intro to Java (ID: 5)"
        return (attribute2 != null ? attribute2 : "CODE") + " - " + 
               (attribute1 != null ? attribute1 : "Title") + " (" + courseID + ")";
    }

    // --- ALIAS METHODS ---
    public String getCourseCode() { return attribute2; }
    public String getCourseName() { return attribute1; }
    public String getTitle() { return attribute1; }

    public int getInstructorID() {
        try {
            return Integer.parseInt(attribute3);
        } catch (NumberFormatException | NullPointerException e) {
            return -1; 
        }
    }

    public void setInstructorID(int instructorID) {
        this.attribute3 = String.valueOf(instructorID);
    }

    // --- GETTERS & SETTERS ---
    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }

    public String getAttribute1() { return attribute1; }
    public void setAttribute1(String attribute1) { this.attribute1 = attribute1; }

    public String getAttribute2() { return attribute2; }
    public void setAttribute2(String attribute2) { this.attribute2 = attribute2; }

    public String getAttribute3() { return attribute3; }
    public void setAttribute3(String attribute3) { this.attribute3 = attribute3; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String status) { this.enrollmentStatus = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}