package com.eduportal.util;

import com.eduportal.dao.DatabaseConnector;
import java.sql.Connection;
import java.sql.Statement;

public class DataInitializer {

	public static void seedData() {
	    try (Connection conn = DatabaseConnector.getConnection();
	         Statement stmt = conn.createStatement()) {
	        
	        System.out.println("Seeding initial data...");

	        // 1. Insert Student (Matches: Username, Password, Role, full_name, email, course_name)
	        stmt.executeUpdate("INSERT INTO student (Username, Password, Role, full_name, email, course_name) " +
	                           "VALUES ('student1', 'password123', 'Student', 'John Doe', 'john@edu.com', 'Advanced Java')");

	        // 2. Insert Instructor (Matches: Username, Password, Role, Name, Identifier, Status)
	        stmt.executeUpdate("INSERT INTO instructor (Username, Password, Role, Name, Identifier, Status) " +
	                           "VALUES ('prof_smith', 'pass123', 'Instructor', 'Dr. Smith', 'EMP001', 'Active')");

	        // 3. Insert Course (MATCHES YOUR MYSQL DESC: course_name, course_code, credits, InstructorID)
	        // Note: Using 1 for InstructorID assuming prof_smith is the first instructor inserted
	        stmt.executeUpdate("INSERT INTO course (course_name, course_code, credits, InstructorID) " +
	                           "VALUES ('Advanced Java', 'CS301', 4, 1)");
	        
	        stmt.executeUpdate("INSERT INTO course (course_name, course_code, credits, InstructorID) " +
	                           "VALUES ('Database Systems', 'DB202', 3, 1)");

	        System.out.println("✅ Data seeding complete. You can now login!");

	    } catch (Exception e) {
	        System.err.println("❌ Seeding failed: " + e.getMessage());
	        e.printStackTrace(); // This helps see exactly which line failed
	    }
	}
}