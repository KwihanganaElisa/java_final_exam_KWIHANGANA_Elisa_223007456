package com.eduportal.view;

import com.eduportal.dao.InstructorDAO;
import com.eduportal.model.Instructor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * Modal dialog for adding a new Instructor or editing an existing one.
 */
public class InstructorFormDialog extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JTextField nameField;
    private JTextField identifierField;
    private JComboBox<String> statusComboBox;
    private JTextField locationField;
    private JTextField contactField;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private InstructorDAO instructorDAO;
    private Instructor currentInstructor; 
    private ManageInstructorsPanel parentPanel; 

    private static final String[] ROLES = {"Instructor", "Admin", "Student"};
    private static final String[] STATUSES = {"Active", "Inactive", "On Leave"};

    public InstructorFormDialog(JFrame parentFrame, ManageInstructorsPanel parentPanel, Instructor instructor) {
        super(parentFrame, instructor == null ? "Add New Instructor" : "Edit Instructor", true);
        this.instructorDAO = new InstructorDAO();
        this.currentInstructor = instructor;
        this.parentPanel = parentPanel;

        setSize(550, 550);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        if (currentInstructor != null) {
            loadInstructorData();
        } else {
            // Default settings for new instructor
            roleComboBox.setSelectedItem("Instructor");
            statusComboBox.setSelectedItem("Active");
        }
    }

    private void initializeComponents() {
        usernameField = new JTextField(25);
        passwordField = new JPasswordField(25);
        roleComboBox = new JComboBox<>(ROLES);
        nameField = new JTextField(25);
        identifierField = new JTextField(25);
        statusComboBox = new JComboBox<>(STATUSES);
        locationField = new JTextField(25);
        contactField = new JTextField(25);
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(52, 168, 83)); // Green
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(219, 68, 55)); // Red
        cancelButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Increased padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;

        // Row 1: Username
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        // Row 2: Password
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        String passwordLabel = (currentInstructor == null) ? "Password:" : "Password (Leave blank to keep old):";
        formPanel.add(new JLabel(passwordLabel), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Row 3: Role
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.5; // Give combobox some stretch
        formPanel.add(roleComboBox, gbc);
        gbc.weightx = 0.0;
        
        // Row 4: Full Name
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameField, gbc);

        // Row 5: Identifier (Employee ID)
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Identifier (ID):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(identifierField, gbc);

        // Row 6: Status
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(statusComboBox, gbc);

        // Row 7: Location
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Office Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(locationField, gbc);
        
        // Row 8: Contact
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Contact Info:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(contactField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadInstructorData() {
        setTitle("Edit Instructor (ID: " + currentInstructor.getInstructorID() + ")");
        usernameField.setText(currentInstructor.getUsername());
        nameField.setText(currentInstructor.getName());
        identifierField.setText(currentInstructor.getIdentifier());
        statusComboBox.setSelectedItem(currentInstructor.getStatus());
        locationField.setText(currentInstructor.getLocation());
        contactField.setText(currentInstructor.getContact());
        roleComboBox.setSelectedItem(currentInstructor.getRole());
        
        // Disable editing the username for existing records
        usernameField.setEditable(false);
    }

    private void setupListeners() {
        // Save Button Listener
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveInstructor();
            }
        });

        // Cancel Button Listener
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void saveInstructor() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();
        String name = nameField.getText().trim();
        String identifier = identifierField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();

        // 1. Validation
        if (username.isEmpty() || name.isEmpty() || identifier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, Full Name, and Identifier are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (currentInstructor == null && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new instructors.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Prepare Instructor Object and save
        Instructor instructorToSave;
        boolean success;
        String action;

        if (currentInstructor == null) {
            // INSERT (New Instructor)
            instructorToSave = new Instructor(
                0, // ID will be auto-generated
                username,
                password,
                role,
                name,
                identifier,
                status,
                location,
                contact,
                new Date() // AssignedSince
            );
            success = instructorDAO.insert(instructorToSave);
            action = "added";
        } else {
            // UPDATE (Existing Instructor)
            instructorToSave = currentInstructor;
            
            instructorToSave.setRole(role);
            instructorToSave.setName(name);
            instructorToSave.setIdentifier(identifier);
            instructorToSave.setStatus(status);
            instructorToSave.setLocation(location);
            instructorToSave.setContact(contact);
            
            // Update password only if the field is not empty
            if (!password.isEmpty()) {
                instructorToSave.setPassword(password);
            }
            
            success = instructorDAO.update(instructorToSave);
            action = "updated";
        }

        // 3. Handle Result
        if (success) {
            JOptionPane.showMessageDialog(this, "Instructor record successfully " + action + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadInstructorData(); // Refresh the table in the parent panel
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save instructor record. Check for duplicate username/identifier or a database error.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}