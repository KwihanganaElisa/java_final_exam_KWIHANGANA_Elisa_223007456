package com.eduportal.view;

import com.eduportal.dao.GradeDAO;
import com.eduportal.dao.StudentDAO;
import com.eduportal.dao.AssignmentDAO;
import com.eduportal.model.Grade;
import com.eduportal.model.Student;
import com.eduportal.model.Assignment;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

/**
 * Modal dialog for adding a new Grade or editing an existing one,
 * updated to match the specific fields in the Grade model.
 */
public class GradeFormDialog extends JDialog {

    private JComboBox<String> studentComboBox;
    private JComboBox<String> assignmentComboBox;
    private JComboBox<String> gradeTypeComboBox; // New field
    private JSpinner scoreSpinner;            // Now handles doubles
    private JTextField letterGradeField;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private GradeDAO gradeDAO;
    private StudentDAO studentDAO;
    private AssignmentDAO assignmentDAO;
    
    private Grade currentGrade; 
    private ManageGradesPanel parentPanel; 

    private List<Student> students;
    private List<Assignment> assignments;

    private static final String[] GRADE_TYPES = {"Exam", "Quiz", "Project", "Homework", "Final"};

    public GradeFormDialog(JFrame parentFrame, ManageGradesPanel parentPanel, Grade grade) {
        super(parentFrame, grade == null ? "Add New Grade" : "Edit Grade", true);
        
        this.gradeDAO = new GradeDAO();
        this.studentDAO = new StudentDAO();
        this.assignmentDAO = new AssignmentDAO();
        
        this.currentGrade = grade;
        this.parentPanel = parentPanel;
        
        loadDropdownData();

        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        if (currentGrade != null) {
            loadGradeData();
        } else {
            gradeTypeComboBox.setSelectedItem("Exam");
        }
    }
    
    /**
     * Loads the list of students and assignments for the combo boxes.
     */
    private void loadDropdownData() {
        students = studentDAO.getAll();
        assignments = assignmentDAO.getAll();
    }

    private void initializeComponents() {
        // Student ComboBox
        studentComboBox = new JComboBox<>();
        for (Student s : students) {
            // Assuming Attribute1 is the Student Name
            studentComboBox.addItem(s.getfull_name() + " (ID: " + s.getStudentID() + ")");
        }
        
        // Assignment ComboBox
        assignmentComboBox = new JComboBox<>();
        for (Assignment a : assignments) {
            // Assuming ReferenceID is the assignment title/code
            assignmentComboBox.addItem(a.getReferenceID() + " (Assignment ID: " + a.getAssignmentID() + ")");
        }
        
        // Grade Type ComboBox
        gradeTypeComboBox = new JComboBox<>(GRADE_TYPES);
        
        // Spinner for Score (from 0.0 to 1000.0, step 0.5, default 75.0)
        SpinnerNumberModel scoreModel = new SpinnerNumberModel(75.0, 0.0, 1000.0, 0.5);
        scoreSpinner = new JSpinner(scoreModel);
        
        letterGradeField = new JTextField(5);
        
        saveButton = new JButton("Save Grade");
        cancelButton = new JButton("Cancel");
        
        // Apply styling
        saveButton.setBackground(new Color(52, 168, 83));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(219, 68, 55));
        cancelButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;

        // Row 1: Student
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(studentComboBox, gbc);
        gbc.weightx = 0.0;

        // Row 2: Assignment
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Assignment:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(assignmentComboBox, gbc);
        gbc.weightx = 0.0;

        // Row 3: Grade Type
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Grade Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(gradeTypeComboBox, gbc);
        gbc.weightx = 0.0;
        
        // Row 4: Score
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Score:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(scoreSpinner, gbc);
        gbc.weightx = 0.0;
        
        // Row 5: Letter Grade
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Letter Grade (e.g., A+):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(letterGradeField, gbc);
        gbc.weightx = 0.0;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Populates fields with data from the existing Grade object.
     */
    private void loadGradeData() {
        setTitle("Edit Grade (ID: " + currentGrade.getGradeID() + ")");

        // Helper to select the correct item in the combo box
        selectComboBoxItem(studentComboBox, currentGrade.getStudentID());
        selectComboBoxItem(assignmentComboBox, currentGrade.getAssignmentID());

        gradeTypeComboBox.setSelectedItem(currentGrade.getGradeType());
        scoreSpinner.setValue(currentGrade.getScore());
        letterGradeField.setText(currentGrade.getLetterGrade());
    }
    
    /**
     * Tries to select an item in a combo box based on the ID embedded in the string.
     */
    private void selectComboBoxItem(JComboBox<String> comboBox, int targetId) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            String item = comboBox.getItemAt(i);
            // Extract the ID from the end of the string (e.g., "Name (ID: 123)")
            int start = item.lastIndexOf(':') + 2;
            int end = item.lastIndexOf(')');
            if (start > 0 && end > start) {
                try {
                    int id = Integer.parseInt(item.substring(start, end));
                    if (id == targetId) {
                        comboBox.setSelectedIndex(i);
                        return;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric IDs
                }
            }
        }
    }

    private void setupListeners() {
        // Save Button Listener
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGrade();
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

    private void saveGrade() {
        int selectedStudentIndex = studentComboBox.getSelectedIndex();
        int selectedAssignmentIndex = assignmentComboBox.getSelectedIndex();
        
        String gradeType = (String) gradeTypeComboBox.getSelectedItem();
        double score = (Double) scoreSpinner.getValue();
        String letterGrade = letterGradeField.getText().trim();

        // 1. Validation
        if (selectedStudentIndex == -1 || selectedAssignmentIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Student and an Assignment.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (letterGrade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Letter Grade is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Retrieve the actual IDs from the corresponding lists
        int studentID = students.get(selectedStudentIndex).getStudentID();
        int assignmentID = assignments.get(selectedAssignmentIndex).getAssignmentID();
        
        // 2. Prepare Grade Object and save
        Grade gradeToSave;
        boolean success;
        String action;

        if (currentGrade == null) {
            // INSERT (New Grade)
            gradeToSave = new Grade(
                0, // ID will be auto-generated
                studentID,
                assignmentID,
                gradeType,
                score,
                letterGrade,
                new Date() // CreatedAt
            );
            success = gradeDAO.insert(gradeToSave);
            action = "added";
        } else {
            // UPDATE (Existing Grade)
            gradeToSave = currentGrade;
            gradeToSave.setStudentID(studentID);
            gradeToSave.setAssignmentID(assignmentID);
            gradeToSave.setGradeType(gradeType);
            gradeToSave.setScore(score);
            gradeToSave.setLetterGrade(letterGrade);
            // CreatedAt is typically not updated
            
            success = gradeDAO.update(gradeToSave);
            action = "updated";
        }

        // 3. Handle Result
        if (success) {
            JOptionPane.showMessageDialog(this, "Grade record successfully " + action + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadGradeData(); // Refresh the table
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save grade record. Check for database constraints (e.g., student already has a grade for this assignment) or a database error.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}