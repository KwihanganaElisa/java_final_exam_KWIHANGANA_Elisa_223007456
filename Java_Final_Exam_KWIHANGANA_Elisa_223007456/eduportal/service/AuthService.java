package com.eduportal.service;

import com.eduportal.dao.AdminDAO;
import com.eduportal.dao.StudentDAO;
import com.eduportal.dao.InstructorDAO;
import com.eduportal.model.Admin;
import com.eduportal.model.Student;
import com.eduportal.model.Instructor;

/**
 * Service class to handle user authentication and authorization logic.
 * UPDATED: Includes the necessary register method for the RegisterFrame.
 */
public class AuthService {

    private AdminDAO adminDAO;
    private StudentDAO studentDAO;
    private InstructorDAO instructorDAO;

    public AuthService() {
        // Initialize DAOs
        this.adminDAO = new AdminDAO();
        this.studentDAO = new StudentDAO();
        this.instructorDAO = new InstructorDAO();
    }

    /**
     * Attempts to authenticate a user against the database tables based on the selected role.
     * @param username The username input.
     * @param password The password input.
     * @param role The selected role ("Admin", "Instructor", or "Student").
     * @return The authenticated user object (Student, Instructor, or Admin) or null.
     */
    public Object authenticate(String username, String password, String role) {
        try {
            if ("Admin".equals(role)) {
                Admin admin = adminDAO.getByUsername(username);
                // **NOTE: Assumes plain text passwords in DB.**
                if (admin != null && admin.getPassword().equals(password)) {
                    return admin;
                }
            } else if ("Instructor".equals(role)) {
                // **FIX: Calling the real DAO method.**
                Instructor instructor = instructorDAO.getByUsername(username);
                if (instructor != null && instructor.getPassword().equals(password)) {
                    return instructor;
                }
            } else if ("Student".equals(role)) {
                // **FIX: Calling the real DAO method.**
                Student student = studentDAO.getByUsername(username);
                if (student != null && student.getPassword().equals(password)) {
                    return student;
                }
            }
        } catch (Exception e) {
            // Catches any exception, typically a NullPointerException if a DAO is null 
            // or an unexpected exception from getByUsername.
            System.err.println("Authentication Error for user " + username + ": " + e.getMessage());
            // e.printStackTrace(); // Keep this for deeper debugging
            return null; 
        }

        return null;
    }

    /**
     * Handles the registration of new users based on role.
     * In a real application, this calls the respective DAO's save/insert method.
     * @return true if registration was successful, false otherwise (e.g., username taken).
     */
    public boolean register(String username, String password, String fullName, String email, String role) {
        // Simple mock logic: fail if username is "fail" to test error message
        if (username.toLowerCase().contains("fail")) {
            return false;
        }

        try {
            if ("Student".equals(role)) {
                // This calls the DAO method that must exist: studentDAO.saveNewUser(String, String, String, String)
                System.out.println("Registering new Student: " + username);
                return studentDAO.saveNewUser(username, password, fullName, email);
            } else if ("Instructor".equals(role)) {
                // This calls the DAO method that must exist: instructorDAO.saveNewUser(String, String, String, String)
                System.out.println("Registering new Instructor: " + username);
                return instructorDAO.saveNewUser(username, password, fullName, email);
            }
        } catch (Exception e) {
            // Log database error
            e.printStackTrace();
            return false;
        }

        return false; // Should not reach here for Student/Instructor
    }

    /**
     * Checks if the selected role is allowed to register (Admin is excluded).
     * @param role The selected role.
     * @return true if registration is allowed for the role, false otherwise.
     */
    public boolean canRegister(String role) {
        return !"Admin".equals(role);
    }
}