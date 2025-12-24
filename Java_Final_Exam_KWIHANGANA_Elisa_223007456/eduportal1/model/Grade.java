package com.eduportal.model;

import java.util.Date;

/**
 * Represents a Grade given for an Assignment (Database Model).
 * UPDATED: Includes a second constructor for displaying aggregated final course grades (View Model).
 */
public class Grade {

    // --- Database Model Fields ---
    private int gradeID;
    private int studentID;
    private int assignmentID;
    private String gradeType;
    private double score;
    private String letterGrade;
    private String finalLetter;
    private Date createdAt;
    
    // --- View Model Fields (Used for aggregated final grades in StudentGradesPanel) ---
    private String courseCode;
    private String courseName;
    private int credits;
    private double finalScore;
    private String semester;
    private double gpaPoints;

    // --- Constructor 1: Full Database Model (7 parameters) ---
    public Grade(int gradeID, int studentID, int assignmentID, String gradeType, double score, String letterGrade, Date createdAt) {
        this.gradeID = gradeID;
        this.studentID = studentID;
        this.assignmentID = assignmentID;
        this.gradeType = gradeType;
        this.score = score;
        this.letterGrade = letterGrade;
        this.createdAt = createdAt;
    }

    // --- Constructor 2: View Model for StudentGradesPanel (8 parameters) ---
    // This constructor handles the aggregated final grades display.
    public Grade(String courseCode, String courseName, int credits, String finalLetter, double finalScore, String semester, double gpaPoints, int studentID) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.letterGrade = finalLetter;
        this.score = finalScore;
        this.semester = semester;
        this.finalScore = finalScore;
        this.finalLetter = finalLetter;
        this.gpaPoints = gpaPoints;
        this.studentID = studentID;
    }

    // --- Getters and Setters (Database Model) ---
    public int getGradeID() { return gradeID; }
    public void setGradeID(int gradeID) { this.gradeID = gradeID; }

    public int getStudentID() { return studentID; }
    public void setStudentID(int studentID) { this.studentID = studentID; }
    
    public int getAssignmentID() { return assignmentID; }
    public void setAssignmentID(int assignmentID) { this.assignmentID = assignmentID; }
    
    public String getGradeType() { return gradeType; }
    public void setGradeType(String gradeType) { this.gradeType = gradeType; }

    // Note: getScore/setScore and getLetterGrade/setLetterGrade are reused by both models
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public double getFinalScore() { return finalScore; }
    public void setFinalScore(){ this.finalScore = score;}

    // --- Getters (View Model) ---
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public String getSemester() { return semester; }
    public double getGpaPoints() { return gpaPoints; } // Used for GPA calculation

    public String getFinalLetter() { 
        return (finalLetter != null) ? finalLetter : letterGrade; 
    }
	
}