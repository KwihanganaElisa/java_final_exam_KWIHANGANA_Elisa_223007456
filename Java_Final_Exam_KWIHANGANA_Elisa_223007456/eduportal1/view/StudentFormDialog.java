package com.eduportal.view;

import com.eduportal.dao.StudentDAO;
import com.eduportal.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * Modal dialog for adding a new Student or editing an existing Student.
 */
public class StudentFormDialog extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JTextField attr1Field; // Maps to Full Name
    private JTextField attr2Field; // Maps to Email
    private JTextField attr3Field; // Maps to couse_name
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private StudentDAO studentDAO;
    private Student currentStudent; // Null for insert, object for update
    private ManageStudentsPanel parentPanel; // Reference to refresh the table

    private static final String[] ROLES = {"Student", "Instructor", "Admin"};

    public StudentFormDialog(JFrame parentFrame, ManageStudentsPanel parentPanel, Student student) {
        // Modal dialog
        super(parentFrame, student == null ? "Add New Student" : "Edit Student", true);
        this.studentDAO = new StudentDAO();
        this.currentStudent = student;
        this.parentPanel = parentPanel;

        setSize(500, 450);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        // Load data if editing an existing student
        if (currentStudent != null) {
            loadStudentData();
        } else {
            roleComboBox.setSelectedItem("Student");
        }
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(ROLES);
        attr1Field = new JTextField(20);
        attr2Field = new JTextField(20);
        attr3Field = new JTextField(20);
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;

        // Username
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        // Conditional label for password field
        String passwordLabel = (currentStudent == null) ? "Password:" : "Password (Leave blank to keep old):";
        formPanel.add(new JLabel(passwordLabel), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(roleComboBox, gbc);

        // Attribute 1 (Full Name)
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(attr1Field, gbc);

        // Attribute 2 (Email)
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(attr2Field, gbc);
        
        // Attribute 3 (Other)
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("course_name"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(attr3Field, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadStudentData() {
        setTitle("Edit Student (ID: " + currentStudent.getStudentID() + ")");
        usernameField.setText(currentStudent.getUsername());
        // Do not display password in the field
        roleComboBox.setSelectedItem(currentStudent.getRole());
        attr1Field.setText(currentStudent.getfull_name());
        attr2Field.setText(currentStudent.getemail());
        attr3Field.setText(currentStudent.getcourse_name());
    }

    private void setupListeners() {
        // Save Button Listener
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveStudent();
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

    private void saveStudent() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();
        String attr1 = attr1Field.getText().trim();
        String attr2 = attr2Field.getText().trim();
        String attr3 = attr3Field.getText().trim();

        // 1. Validation
        if (username.isEmpty() || attr1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Full Name are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (currentStudent == null && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new students.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Prepare Student Object and save
        Student studentToSave;
        boolean success;
        String action;

        if (currentStudent == null) {
            // INSERT (New Student)
            studentToSave = new Student(
                0, // ID will be auto-generated
                username,
                password,
                role,
                attr1,
                attr2,
                attr3,
                new Date() // CreatedAt
            );
            success = studentDAO.insert(studentToSave);
            action = "added";
        } else {
            // UPDATE (Existing Student)
            studentToSave = currentStudent;
            studentToSave.setUsername(username);
            studentToSave.setRole(role);
            studentToSave.setfull_name(attr1);
            studentToSave.setemail(attr2);
            studentToSave.setcourse_name(attr3);
            
            // Update password only if the field is not empty
            if (!password.isEmpty()) {
                studentToSave.setPassword(password);
            }
            
            success = studentDAO.update(studentToSave);
            action = "updated";
        }

        // 3. Handle Result
        if (success) {
            JOptionPane.showMessageDialog(this, "Student record successfully " + action + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadStudentData(); // Refresh the table in the parent panel
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save student record. This might be due to a duplicate username or a database issue.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}