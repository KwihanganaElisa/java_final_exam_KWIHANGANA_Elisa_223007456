package com.eduportal.view;

import com.eduportal.model.Student;
import com.eduportal.dao.StudentDAO; // Assume DAO exists for persistence
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for students to view and update their profile details.
 */
public class StudentProfilePanel extends JPanel {
    
    private Student student;
    private JTextField nameField;
    private JTextField emailField;
    private JLabel studentIDLabel;
    
    // Assume StudentDAO exists and has an update method
    private StudentDAO studentDAO = new StudentDAO();

    // UI Configuration Constants
    private static final Color BUTTON_COLOR = new Color(0, 150, 136); // Teal

    public StudentProfilePanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // --- Center Panel for Form Layout ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("My Account Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel title = new JLabel("Student Profile", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(title, gbc);
        
        gbc.gridwidth = 1; // Reset to single column width
        
        // 1. Student ID (Read-only)
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Student ID:"), gbc);
        
        gbc.gridx = 1;
        studentIDLabel = new JLabel(String.valueOf(student.getStudentID()));
        studentIDLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        formPanel.add(studentIDLabel, gbc);
        
        // 2. Full Name
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(student.getName(), 20);
        formPanel.add(nameField, gbc);

        // 3. Email
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(student.getemail(), 20);
        formPanel.add(emailField, gbc);
        
        // 4. Save Button
        JButton saveButton = new JButton("Update Profile");
        saveButton.setBackground(BUTTON_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2; // Span across two columns
        formPanel.add(saveButton, gbc);

        // --- Action Listener ---
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudentProfile();
            }
        });

        // Add the form panel to the center, wrapped in a flow layout for centering
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.add(formPanel);
        
        add(centerWrapper, BorderLayout.CENTER);
    }
    
    private void updateStudentProfile() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        
        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update the model object
        student.setName(newName);
        student.setemail(newEmail);
        
        // Assume StudentDAO handles the update (Mock logic used here)
        // In a real app: studentDAO.update(student);
        
        // Mock success/failure based on the student's ID (simple mock persistence)
        boolean success = studentDAO.update(student); 

        if (success) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile. (Database Error/Mock Failure)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}