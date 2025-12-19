package com.eduportal.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class to manage database connections and ensure table existence.
 * Configured for MySQL (compatible with WAMPserver).
 */
public class DatabaseConnector {

    // --- JDBC Configuration ---
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    // Replace 'edu_portal' with your actual database name in WAMP/MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/edu_portal?useSSL=false";
    // Default WAMP credentials
    private static final String USER = "root";
    private static final String PASS = "";

    // Static instance holder and public getter (unchanged from previous version)
    private DatabaseConnector() {
        try { Class.forName(JDBC_DRIVER); } 
        catch (ClassNotFoundException e) { 
            System.err.println("MySQL JDBC Driver not found. Make sure the JAR is in your build path.");
            e.printStackTrace();
        }
    }
    
    private static class SingletonHelper {
        private static final DatabaseConnector INSTANCE = new DatabaseConnector();
    }

    public static DatabaseConnector getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database. Check if WAMP is running and the database 'edu_portal' exists.");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Executes the CREATE TABLE statements if the tables do not already exist.
     * UPDATED to include authentication fields and Admin table.
     */
    public void createTablesIfNotExist() {
    	Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            if (conn == null) return;
            
            stmt = conn.createStatement();
            
         // 1. Student Table
            String sqlStudent = "CREATE TABLE IF NOT EXISTS Student (" +
                    "StudentID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "Username VARCHAR(50) NOT NULL UNIQUE, " +
                    "Password VARCHAR(255) NOT NULL, " + 
                    "Role VARCHAR(50) DEFAULT 'Student', " +
                    "full_name VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "course_name VARCHAR(255), " +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP)";
            stmt.executeUpdate(sqlStudent);
            
            // 2. Instructor Table (Move this ABOVE Course so Course can reference it)
            String sqlInstructor = "CREATE TABLE IF NOT EXISTS Instructor (" +
                    "InstructorID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "Username VARCHAR(50) NOT NULL UNIQUE, " +
                    "Password VARCHAR(255) NOT NULL, " + 
                    "Role VARCHAR(50) DEFAULT 'Instructor', " +
                    "Name VARCHAR(255) NOT NULL, " +
                    "Identifier VARCHAR(50), " +
                    "Status VARCHAR(50), " +
                    "Location VARCHAR(255), " +
                    "Contact VARCHAR(255), " +
                    "AssignedSince DATETIME)";
            stmt.executeUpdate(sqlInstructor);

            // 3. Course Table (UPDATED with InstructorID column)
            String sqlCourse = "CREATE TABLE IF NOT EXISTS Course (" +
                    "CourseID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "Attribute1 VARCHAR(255), " + // Name
                    "Attribute2 VARCHAR(255), " + // Code
                    "Attribute3 VARCHAR(255), " + // Credits
                    "InstructorID INT, " +        // NEW: Foreign Key column
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (InstructorID) REFERENCES Instructor(InstructorID))";
            stmt.executeUpdate(sqlCourse);
            
            // 4. Admin Table
            String sqlAdmin = "CREATE TABLE IF NOT EXISTS Admin (" +
                    "AdminID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "Username VARCHAR(50) NOT NULL UNIQUE, " +
                    "Password VARCHAR(255) NOT NULL, " + 
                    "Role VARCHAR(50) DEFAULT 'Admin', " +
                    "FullName VARCHAR(255), " +
                    "Email VARCHAR(255), " +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP)";
            stmt.executeUpdate(sqlAdmin);
            
            // 5. Enrollment Table (NO CHANGE)
            String sqlEnrollment = "CREATE TABLE IF NOT EXISTS Enrollment (" +
                    "EnrollmentID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "StudentID INT NOT NULL, " +
                    "CourseID INT NOT NULL, " +
                    "ReferenceID VARCHAR(255), " +
                    "Description VARCHAR(255), " +
                    "EnrollDate DATETIME, " +
                    "Status VARCHAR(50), " +
                    "Remarks TEXT, " +
                    "FOREIGN KEY (StudentID) REFERENCES Student(StudentID), " +
                    "FOREIGN KEY (CourseID) REFERENCES Course(CourseID))";
            stmt.executeUpdate(sqlEnrollment);
            System.out.println("Created Enrollment table.");
            
            // 6. Assignment Table (NO CHANGE)
            String sqlAssignment = "CREATE TABLE IF NOT EXISTS Assignment (" +
                    "AssignmentID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "CourseID INT NOT NULL, " +
                    "ReferenceID VARCHAR(255), " +
                    "Description TEXT, " +
                    "DueDate DATETIME, " +
                    "Status VARCHAR(50), " +
                    "Remarks TEXT, " +
                    "FOREIGN KEY (CourseID) REFERENCES Course(CourseID))";
            stmt.executeUpdate(sqlAssignment);
            System.out.println("Created Assignment table.");

            // 7. Grade Table (NO CHANGE)
            String sqlGrade = "CREATE TABLE IF NOT EXISTS Grade (" +
                    "GradeID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "StudentID INT NOT NULL, " +
                    "AssignmentID INT NOT NULL, " +
                    "GradeType VARCHAR(50), " + 
                    "Score DECIMAL(5,2), " +   
                    "LetterGrade VARCHAR(5), " +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (StudentID) REFERENCES Student(StudentID), " +
                    "FOREIGN KEY (AssignmentID) REFERENCES Assignment(AssignmentID))";
            stmt.executeUpdate(sqlGrade);
            System.out.println("Created Grade table.");
            
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException se2) { /* ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException se) { se.printStackTrace(); }
        }
    }
}