package com.eduportal.model;

import java.util.Date;

/**
 * Represents a specific Assignment for a Course.
 * SYNCHRONIZED: Updated to include Title, Type, MaxScore, and CreatedAt 
 * to support the InstructorAssignmentPanel view.
 */
public class Assignment {

    // Primary Key
    private int assignmentID;
    
    // Foreign Key
    private int courseID; // Links to the Course table
    
    // Descriptive/Tracking Attributes used in UI
    private String title;       // ADDED: For Assignment List View
    private String type;        // ADDED: For Assignment List View (e.g., Homework, Exam)
    private int maxScore;       // ADDED: For Assignment List View
    
    // Existing fields mapped to new UI needs
    private String referenceID; 
    private String description;
    private Date dueDate;       // Already exists
    private String status;      
    private String remarks;
    private Date createdAt;     // ADDED: For tracking creation time

    // --- LEGACY Constructor (ADDED to support old DAO retrieval logic) ---
    public Assignment(int assignmentID, int courseID, String referenceID, String description, Date dueDate, String status, String remarks) {
        this.assignmentID = assignmentID;
        this.courseID = courseID;
        this.referenceID = referenceID;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.remarks = remarks;
        
        // Initialize new fields with safe defaults
        this.title = referenceID; // Use reference ID as title fallback
        this.type = "Assignment"; 
        this.maxScore = 0;
        this.createdAt = new Date();
    }
    
    // --- Constructor (UPDATED to match InstructorAssignmentPanel needs) ---
    public Assignment(int assignmentID, int courseID, String title, String type, Date dueDate, int maxScore, Date createdAt) {
        this.assignmentID = assignmentID;
        this.courseID = courseID;
        this.title = title;
        this.type = type;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.createdAt = createdAt;
        // Default values for less critical fields
        this.referenceID = "";
        this.description = title;
        this.status = "Active";
        this.remarks = "";
    }
    
    // --- Full Constructor (for persistence layer/DB retrieval) ---
    public Assignment(int assignmentID, int courseID, String title, String type, int maxScore, 
                      String referenceID, String description, Date dueDate, String status, String remarks, Date createdAt) {
        this.assignmentID = assignmentID;
        this.courseID = courseID;
        this.title = title;
        this.type = type;
        this.maxScore = maxScore;
        this.referenceID = referenceID;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }


    // --- Getters and Setters (Updated) ---
    
    public int getAssignmentID() { return assignmentID; }
    public void setAssignmentID(int assignmentID) { this.assignmentID = assignmentID; }

    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }

    // Existing fields
    public String getReferenceID() { return referenceID; }
    public void setReferenceID(String referenceID) { this.referenceID = referenceID; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}