package com.eduportal.model;

import java.util.Date;

/**
 * Represents a Student's Enrollment in a Course.
 */
public class Enrollment {

    // Primary Key
    private int enrollmentID;
    
    // Foreign Keys
    private int studentID;
    private int courseID;
    
    // Descriptive Attributes
    private String referenceID;
    private String description;
    private Date enrollDate;
    private String status;
    private String remarks;

    // --- Constructor 1: Full Constructor (Used by DAO) ---
    public Enrollment(int enrollmentID, int studentID, int courseID, String referenceID, 
                      String description, Date enrollDate, String status, String remarks) {
        this.enrollmentID = enrollmentID;
        this.studentID = studentID;
        this.courseID = courseID;
        this.referenceID = referenceID;
        this.description = description;
        this.enrollDate = enrollDate;
        this.status = status;
        this.remarks = remarks;
    }

    // --- Constructor 2: Convenience Constructor (For New Enrollments in UI) ---
    public Enrollment(int studentID, int courseID, String referenceID, String status) {
        this(0, studentID, courseID, referenceID, "", new Date(), status, "");
    }

    // --- Getters and Setters ---
    public int getEnrollmentID() { return enrollmentID; }
    public void setEnrollmentID(int enrollmentID) { this.enrollmentID = enrollmentID; }

    public int getStudentID() { return studentID; }
    public void setStudentID(int studentID) { this.studentID = studentID; }

    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }

    public String getReferenceID() { return referenceID; }
    public void setReferenceID(String referenceID) { this.referenceID = referenceID; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEnrollDate() { return enrollDate; }
    public void setEnrollDate(Date enrollDate) { this.enrollDate = enrollDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}