package com.eduportal.view;

import com.eduportal.service.AuthService; // Assuming this service handles registration logic
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Functional registration frame for a new user, tailored by role.
 * Includes input fields, basic validation, and colorful UI elements.
 * IMPORTANT: This version uses traditional Anonymous Inner Classes instead of Java 8+ Lambdas.
 */
public class RegisterFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    
    private JButton registerButton;
    private JButton cancelButton;
    
    private AuthService authService; 
    private String role;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(0, 150, 136); // Teal for headers
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80); // Green for button
    private static final Color CANCEL_COLOR = new Color(244, 67, 54); // Red for cancel

    public RegisterFrame(String role) {
        super("Register New " + role);
        this.role = role;
        this.authService = new AuthService(); // Initialize service

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Use BorderLayout for main structure
        setLayout(new BorderLayout(10, 10));
        
        // --- 1. Header ---
        JLabel headerLabel = new JLabel("<html><h1 style='color:white;'>New " + role + " Registration</h1></html>", SwingConstants.CENTER);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(PRIMARY_COLOR);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(headerLabel, BorderLayout.NORTH);

        // --- 2. Input Panel ---
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.CENTER);

        // --- 3. Button Panel ---
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Finalize frame properties
        setSize(450, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Username
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        fullNameField = new JTextField(20);
        panel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // Spacer to push fields to the top
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        registerButton = new JButton("Register " + role);
        registerButton.setBackground(SUCCESS_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(CANCEL_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        // --- Listeners (Anonymous Inner Classes) ---
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRegistration();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        panel.add(registerButton);
        panel.add(cancelButton);
        return panel;
    }
    
    /**
     * Validates input fields and attempts to register the new user.
     */
    private void attemptRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Logic to call the Service Layer for registration ---
        try {
            // This method would typically call a DAO to save the new user record.
            boolean success = authService.register(username, password, fullName, email, role);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Registration successful! You may now log in as a " + role + ".", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Registration failed. Username may already exist.", 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "An unexpected error occurred during registration: " + e.getMessage(), 
                "System Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Main method for testing (Optional)
     */
    public static void main(String[] args) {
        // --- FIX: Using Anonymous Inner Class instead of Lambda ---
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegisterFrame("Student");
            }
        });
    }
}