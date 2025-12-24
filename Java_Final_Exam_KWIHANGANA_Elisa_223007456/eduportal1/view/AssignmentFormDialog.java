package com.eduportal.view;

import com.eduportal.dao.AssignmentDAO;
import com.eduportal.dao.CourseDAO;
import com.eduportal.model.Assignment;
import com.eduportal.model.Course;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class AssignmentFormDialog extends JDialog {

    private JComboBox<String> courseComboBox;
    private JTextField titleField;          
    private JTextField typeField;           
    private JTextField maxScoreField;       
    private JTextField referenceField; 
    private JTextArea descriptionArea;
    private JTextField dueDateField; 
    private JComboBox<String> statusComboBox;
    private JTextArea remarksArea;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private AssignmentDAO assignmentDAO;
    private CourseDAO courseDAO;
    
    private Assignment currentAssignment; 
    private ManageAssignmentsPanel parentPanel; 
    private List<Course> courses;
    private int ownerID; // Used for loading courses (Admin ID or Instructor ID)

    private static final String[] STATUSES = {"Pending", "Active", "Completed", "Archived"};
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Constructor updated to take ownerID (Admin/Instructor)
    public AssignmentFormDialog(JFrame parentFrame, ManageAssignmentsPanel parentPanel, int ownerID, Assignment assignment) {
        super(parentFrame, assignment == null ? "Add New Assignment" : "Edit Assignment", true);
        
        this.assignmentDAO = new AssignmentDAO();
        this.courseDAO = new CourseDAO();
        this.currentAssignment = assignment;
        this.parentPanel = parentPanel;
        this.ownerID = ownerID;
        
        loadDropdownData();

        setSize(600, 600);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        if (currentAssignment != null) {
            loadAssignmentData();
        } else {
            statusComboBox.setSelectedItem("Pending");
        }
    }
    
    private void loadDropdownData() {
        // --- FIX 1: Handle Unhandled Exception from DAO call ---
        try {
            // Calls the DAO method which handles the ownerID logic
            courses = courseDAO.getCoursesByInstructor(ownerID);
        } catch (Exception e) {
            courses = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        courseComboBox = new JComboBox<String>();
        for (Course c : courses) {
            // --- FIX 2 is located in the Course model implementation below ---
             courseComboBox.addItem(c.getCourseCode() + " - " + c.getCourseName() + " (" + c.getCourseID() + ")");
        }
        
        titleField = new JTextField(20);          
        typeField = new JTextField("Homework", 20); 
        maxScoreField = new JTextField("100", 20); 
        
        referenceField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        dueDateField = new JTextField(DATE_FORMAT.format(new Date()), 20); 
        statusComboBox = new JComboBox<String>(STATUSES);
        remarksArea = new JTextArea(3, 20);
        
        saveButton = new JButton("Save Assignment");
        cancelButton = new JButton("Cancel");
        
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
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(courseComboBox, gbc);
        gbc.weightx = 0.0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Reference ID (e.g., File Code):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(referenceField, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Type (e.g., Exam, HW):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(typeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Max Score:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(maxScoreField, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(dueDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(statusComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0.5;
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Remarks:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0.5;
        formPanel.add(new JScrollPane(remarksArea), gbc);
        gbc.weighty = 0.0;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.NORTH); 
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadAssignmentData() {
        setTitle("Edit Assignment (ID: " + currentAssignment.getAssignmentID() + ")");

        titleField.setText(currentAssignment.getTitle());
        typeField.setText(currentAssignment.getType());
        maxScoreField.setText(String.valueOf(currentAssignment.getMaxScore()));
        
        referenceField.setText(currentAssignment.getReferenceID());
        descriptionArea.setText(currentAssignment.getDescription());
        statusComboBox.setSelectedItem(currentAssignment.getStatus());
        remarksArea.setText(currentAssignment.getRemarks());
        
        if (currentAssignment.getDueDate() != null) {
              dueDateField.setText(DATE_FORMAT.format(currentAssignment.getDueDate()));
        } else {
              dueDateField.setText("");
        }
        selectCourseComboBoxItem(courseComboBox, currentAssignment.getCourseID());
    }
    
    private void selectCourseComboBoxItem(JComboBox<String> comboBox, int targetCourseId) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            String item = comboBox.getItemAt(i);
            int start = item.lastIndexOf('(') + 1;
            int end = item.lastIndexOf(')');
            if (start > 0 && end > start) {
                try {
                    int id = Integer.parseInt(item.substring(start, end));
                    if (id == targetCourseId) {
                        comboBox.setSelectedIndex(i);
                        return;
                    }
                } catch (NumberFormatException e) {}
            }
        }
    }

    private void setupListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAssignment();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void saveAssignment() {
        int selectedCourseIndex = courseComboBox.getSelectedIndex();
        
        String title = titleField.getText().trim();
        String type = typeField.getText().trim();
        String maxScoreStr = maxScoreField.getText().trim();

        String referenceID = referenceField.getText().trim();
        String description = descriptionArea.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();
        String remarks = remarksArea.getText().trim();
        String dueDateStr = dueDateField.getText().trim();

        if (title.isEmpty() || type.isEmpty() || maxScoreStr.isEmpty() || selectedCourseIndex == -1 || dueDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, Type, Max Score, Course, and Due Date are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Date dueDate;
        int maxScore;
        try {
            dueDate = DATE_FORMAT.parse(dueDateStr);
            maxScore = Integer.parseInt(maxScoreStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date format (YYYY-MM-DD) or Max Score must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int courseID = courses.get(selectedCourseIndex).getCourseID();
        
        Assignment assignmentToSave;
        boolean success;
        String action;

        if (currentAssignment == null) {
            // INSERT (New Assignment) - Using 7-argument simplified constructor
            assignmentToSave = new Assignment(
                0, 
                courseID,
                title,       
                type,        
                dueDate,
                maxScore,    
                new Date()   
            );
            // Set the remaining fields using setters
            assignmentToSave.setReferenceID(referenceID);
            assignmentToSave.setDescription(description);
            assignmentToSave.setStatus(status);
            assignmentToSave.setRemarks(remarks);
            
            success = assignmentDAO.insert(assignmentToSave);
            action = "added";
        } else {
            // UPDATE (Existing Assignment)
            assignmentToSave = currentAssignment;
            assignmentToSave.setCourseID(courseID);
            assignmentToSave.setTitle(title);
            assignmentToSave.setType(type);
            assignmentToSave.setMaxScore(maxScore);
            assignmentToSave.setReferenceID(referenceID);
            assignmentToSave.setDescription(description);
            assignmentToSave.setDueDate(dueDate);
            assignmentToSave.setStatus(status);
            assignmentToSave.setRemarks(remarks);
            
            success = assignmentDAO.update(assignmentToSave);
            action = "updated";
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Assignment record successfully " + action + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadAssignmentData();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save assignment record. Check for database errors.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}