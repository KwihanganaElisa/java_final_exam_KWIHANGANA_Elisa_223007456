package com.eduportal.view;

import com.eduportal.service.AuthService;
import com.eduportal.model.Student;
import com.eduportal.model.Instructor;
import com.eduportal.model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date; // Required for mock object creation

/**
 * Login window for the Education Portal System.
 * Connects to AuthService for backend logic and launches the appropriate dashboard.
 * FINAL FIX: Correctly launches AdminDashboardFrame, InstructorDashboardFrame, 
 * and StudentDashboardFrame based on the role.
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox; // Use generics for better typing
    private AuthService authService; // Service for authentication logic

    // =========================================================================
    // MOCK CLASSES: Included here for the sake of making this single file compilable.
    // In a real project, these would be in their own files (ManageStudentsPanel.java, etc.).
    // You should remove these inner classes once you ensure your project includes them
    // as separate public classes in the correct package.
    // =========================================================================
    private class ManageStudentsPanel extends JPanel { public ManageStudentsPanel(JFrame parent) { super(new BorderLayout()); add(new JLabel("Manage Students Panel Ready", SwingConstants.CENTER)); } }
    private class ManageInstructorsPanel extends JPanel { public ManageInstructorsPanel(JFrame parent) { super(new BorderLayout()); add(new JLabel("Manage Instructors Panel Ready", SwingConstants.CENTER)); } }
    private class ManageCoursesPanel extends JPanel { public ManageCoursesPanel(JFrame parent) { super(new BorderLayout()); add(new JLabel("Manage Courses Panel Ready", SwingConstants.CENTER)); } }
    private class ManageEnrollmentsPanel extends JPanel { public ManageEnrollmentsPanel(JFrame parent) { super(new BorderLayout()); add(new JLabel("Manage Enrollments Panel Ready", SwingConstants.CENTER)); } }
    private class ManageAssignmentsPanel extends JPanel { public ManageAssignmentsPanel(JFrame parent) { super(new BorderLayout()); add(new JLabel("Manage Assignments Panel Ready", SwingConstants.CENTER)); } }
    private class ManageGradesPanel extends JPanel { public ManageGradesPanel(JFrame parent) { super(new BorderLayout()); add(new JLabel("Manage Grades Panel Ready", SwingConstants.CENTER)); } }

    private class ViewCoursesInstructorPanel extends JPanel { public ViewCoursesInstructorPanel(int id) { super(new BorderLayout()); add(new JLabel("View Courses Panel Ready", SwingConstants.CENTER)); } }
    private class GradeStudentsPanel extends JPanel { public GradeStudentsPanel(int id) { super(new BorderLayout()); add(new JLabel("Grade Students Panel Ready", SwingConstants.CENTER)); } }
    private class InstructorAssignmentPanel extends JPanel { public InstructorAssignmentPanel(int id) { super(new BorderLayout()); add(new JLabel("Instructor Assignment Panel Ready", SwingConstants.CENTER)); } }
    private class ManageRosterPanel extends JPanel { public ManageRosterPanel(int id) { super(new BorderLayout()); add(new JLabel("Manage Roster Panel Ready", SwingConstants.CENTER)); } }
    // =========================================================================

    public LoginFrame() {
        super("Education Portal - Login");
        // Initialize service (AuthService will now use DAOs)
        this.authService = new AuthService(); 

        // Frame Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the frame
        
        // Use a 4x2 GridLayout for the input fields
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // 1. Username Field
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        inputPanel.add(usernameField);

        // 2. Password Field
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        inputPanel.add(passwordField);

        // 3. Role Selection
        String[] roles = {"Student", "Instructor", "Admin"};
        roleComboBox = new JComboBox<String>(roles);
        inputPanel.add(new JLabel("Role:"));
        inputPanel.add(roleComboBox);

        // 4. Spacer for alignment
        inputPanel.add(new JLabel("")); // Empty label for spacing

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // --- Event Listeners (Anonymous Inner Classes) ---

        // Login Button Action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        // Register Button Action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) roleComboBox.getSelectedItem();

                if (authService.canRegister(selectedRole)) {
                    // Launches your existing RegisterFrame class
                    new RegisterFrame(selectedRole);
                } else {
                    JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "Administrators cannot register through this form.",
                        "Registration Denied",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Assemble the Frame
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Finalize
        pack(); // Adjusts window size to fit components
    }

    /**
     * Contains the logic to validate credentials and switch to the correct dashboard.
     * FIX: Now correctly instantiates and launches the provided dashboard classes.
     */
    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        // Call the authentication service
        Object authenticatedUser = authService.authenticate(username, password, role);

        if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(this, "Login Successful! Role: " + role, "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Launch the CORRECT specific Frame
            JFrame dashboardFrame = null;
            
            if ("Admin".equals(role)) {
                // Launch your provided AdminDashboardFrame
                Admin admin = (Admin) authenticatedUser;
                // Assuming AdminDashboardFrame takes a String title from its constructor
                dashboardFrame = new AdminDashboardFrame("Admin Dashboard - " + admin.getUsername());
                
            } else if ("Instructor".equals(role)) {
                // Launch your provided InstructorDashboardFrame
                Instructor instructor = (Instructor) authenticatedUser;
                // InstructorDashboardFrame takes the Instructor model object
                dashboardFrame = new InstructorDashboardFrame(instructor);
                
            } else if ("Student".equals(role)) {
                // Launch your provided StudentDashboardFrame
                Student student = (Student) authenticatedUser;
                // StudentDashboardFrame takes the Student model object
                dashboardFrame = new StudentDashboardFrame(student);
            }

            if (dashboardFrame != null) {
                dashboardFrame.setVisible(true);
            }
            
            // Close the LoginFrame
            dispose();

        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username, Password, or Role.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText(""); // Clear password field on failure
        }
    }

    /**
     * Main method for testing LoginFrame directly.
     * IMPORTANT: This method should be removed if com.eduportal.main.AppStarter is the main entry point.
     */
    public static void main(String[] args) {
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 new LoginFrame().setVisible(true);
             }
         });
    }
}