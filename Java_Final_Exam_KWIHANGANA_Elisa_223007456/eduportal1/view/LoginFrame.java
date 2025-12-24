package com.eduportal.view;

import com.eduportal.service.AuthService;
import com.eduportal.model.Student;
import com.eduportal.model.Instructor;
import com.eduportal.model.Admin;
import com.eduportal.dao.DatabaseConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

/**
 * Login window for the Education Portal System.
 * UPDATED: Standard ActionListener syntax used (no lambdas).
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox; 
    private AuthService authService; 

    public LoginFrame() {
        super("Education Portal - Login");
        this.authService = new AuthService(); 

        // Frame Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); 
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        inputPanel.add(usernameField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        inputPanel.add(passwordField);

        String[] roles = {"Student", "Instructor", "Admin"};
        roleComboBox = new JComboBox<String>(roles);
        inputPanel.add(new JLabel("Role:"));
        inputPanel.add(roleComboBox);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));

        // --- Event Listeners (Anonymous Inner Classes) ---

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) roleComboBox.getSelectedItem();
                if (authService.canRegister(selectedRole)) {
                    // Launch registration and make visible
                    RegisterFrame rf = new RegisterFrame(selectedRole);
                    rf.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                        "Administrators must be created manually in the database.",
                        "Registration Denied", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack(); 
        setLocationRelativeTo(null);
    }

    private void attemptLogin() {
        // 1. Connection Pre-check to ensure MySQL is up
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, 
                    "Could not connect to Database. Please ensure WAMP/MySQL is running.", 
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            // Error handled by DatabaseConnector
        }

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        // 2. Authenticate through Service
        Object authenticatedUser = authService.authenticate(username, password, role);

        if (authenticatedUser != null) {
            JFrame dashboardFrame = null;
            
            if ("Admin".equals(role)) {
                Admin admin = (Admin) authenticatedUser;
                dashboardFrame = new AdminDashboardFrame("Admin Dashboard - " + admin.getUsername());
                
            } else if ("Instructor".equals(role)) {
                Instructor instructor = (Instructor) authenticatedUser;
                dashboardFrame = new InstructorDashboardFrame(instructor);
                
            } else if ("Student".equals(role)) {
                Student student = (Student) authenticatedUser;
                dashboardFrame = new StudentDashboardFrame(student);
            }

            if (dashboardFrame != null) {
                dashboardFrame.setVisible(true);
                this.dispose(); // Close login
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials or role.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}