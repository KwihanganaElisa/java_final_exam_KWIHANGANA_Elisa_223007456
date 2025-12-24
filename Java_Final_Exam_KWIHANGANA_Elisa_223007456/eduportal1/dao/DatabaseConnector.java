package com.eduportal.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class to manage database connections and ensure table existence.
 * UPDATED: Includes Admin table and correct column names (full_name, credits).
 */
public class DatabaseConnector {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/edu_portal?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "";

    private DatabaseConnector() {
        try { 
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) { 
            System.err.println("MySQL JDBC Driver not found.");
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
            System.err.println("Database Connection Failed: " + e.getMessage());
            return null;
        }
    }

    public void createTablesIfNotExist() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            if (conn == null) return;

            // 1. Admin Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Admin (" +
                    "AdminID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "Username VARCHAR(50) NOT NULL UNIQUE, " +
                    "Password VARCHAR(255) NOT NULL, " +
                    "full_name VARCHAR(255), " +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP)");

         // 2. Student Table - MUST use 'full_name' to match your StudentDAO
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS student (" +
                    "StudentID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "Username VARCHAR(50) NOT NULL UNIQUE, " +
                    "Password VARCHAR(255) NOT NULL, " +
                    "Role VARCHAR(50) DEFAULT 'Student', " +
                    "full_name VARCHAR(255), " +  
                    "email VARCHAR(255), " +      
                    "course_name VARCHAR(255), " +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP)");

         // 3. Instructor Table - MUST use 'Name' to match your InstructorDAO
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS instructor (" +
                    "InstructorID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "Username VARCHAR(50) NOT NULL UNIQUE, " +
                    "Password VARCHAR(255) NOT NULL, " + 
                    "Role VARCHAR(50) DEFAULT 'Instructor', " +
                    "Name VARCHAR(255), " +      
                    "Identifier VARCHAR(50), " +
                    "Status VARCHAR(50) DEFAULT 'Active', " +
                    "Location VARCHAR(255), " +
                    "Contact VARCHAR(255), " +
                    "AssignedSince DATETIME DEFAULT CURRENT_TIMESTAMP)");

            // 4. Course Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Course (" +
                    "CourseID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "course_name VARCHAR(255), " + 
                    "course_code VARCHAR(255), " + 
                    "credits INT DEFAULT 3, " +    
                    "InstructorID INT, " +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (InstructorID) REFERENCES Instructor(InstructorID))");

            // 5. Enrollment Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Enrollment (" +
                    "EnrollmentID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "StudentID INT NOT NULL, " +
                    "CourseID INT NOT NULL, " +
                    "Status VARCHAR(50) DEFAULT 'Registered', " +
                    "EnrollDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (StudentID) REFERENCES Student(StudentID), " +
                    "FOREIGN KEY (CourseID) REFERENCES Course(CourseID))");

            // 6. Assignment Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Assignment (" +
                    "AssignmentID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "CourseID INT NOT NULL, " +
                    "Title VARCHAR(255), " +
                    "DueDate DATETIME, " +
                    "FOREIGN KEY (CourseID) REFERENCES Course(CourseID))");

            // 7. Grade Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Grade (" +
                    "GradeID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "StudentID INT NOT NULL, " +
                    "AssignmentID INT NOT NULL, " +
                    "Score DECIMAL(5,2), " +   
                    "LetterGrade VARCHAR(5), " +
                    "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (StudentID) REFERENCES Student(StudentID), " +
                    "FOREIGN KEY (AssignmentID) REFERENCES Assignment(AssignmentID))");

            System.out.println("Schema verification complete. All tables are up to date.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}