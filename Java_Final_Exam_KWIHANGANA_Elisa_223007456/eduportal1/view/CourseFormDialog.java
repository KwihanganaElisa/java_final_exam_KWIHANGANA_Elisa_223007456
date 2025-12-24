package com.eduportal.view;

import com.eduportal.dao.CourseDAO;
import com.eduportal.dao.InstructorDAO;
import com.eduportal.model.Course;
import com.eduportal.model.Instructor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

/**
 * Modal dialog for adding a new Course or editing an existing one.
 * UPDATED: Fixed variable names and selection logic for full execution.
 */
public class CourseFormDialog extends JDialog {

    private JTextField nameField;    // Maps to Attribute1
    private JTextField codeField;    // Maps to Attribute2
    private JSpinner creditsSpinner; // Maps to Attribute3 (as String)
    private JComboBox<Instructor> instructorCombo; // For Instructor Assignment
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private CourseDAO courseDAO;
    private InstructorDAO instructorDAO; 
    private Course currentCourse; 
    private ManageCoursesPanel parentPanel; 

    public CourseFormDialog(JFrame parentFrame, ManageCoursesPanel parentPanel, Course course) {
        super(parentFrame, course == null ? "Add New Course" : "Edit Course", true);
        this.courseDAO = new CourseDAO();
        this.instructorDAO = new InstructorDAO(); 
        this.currentCourse = course;
        this.parentPanel = parentPanel;

        setSize(450, 400); 
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        if (currentCourse != null) {
            loadCourseData();
        }
    }

    private void initializeComponents() {
        nameField = new JTextField(20);
        codeField = new JTextField(20);
        
        // Spinner for Credits (from 1 to 10, step 0.5, default 3.0)
        SpinnerNumberModel model = new SpinnerNumberModel(3.0, 1.0, 10.0, 0.5);
        creditsSpinner = new JSpinner(model);
        
        // Initialize Instructor Combo Box
        instructorCombo = new JComboBox<Instructor>();
        populateInstructors(); 
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(52, 168, 83)); // Green
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(219, 68, 55)); // Red
        cancelButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);
    }

    private void populateInstructors() {
        List<Instructor> list = instructorDAO.getAll(); 
        for (Instructor ins : list) {
            instructorCombo.addItem(ins);
        }
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;

        // Row 1: Course Name
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Course Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameField, gbc);

        // Row 2: Course Code
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(codeField, gbc);

        // Row 3: Credits
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(creditsSpinner, gbc);

        // Row 4: Instructor Assignment
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Assign Instructor:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(instructorCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadCourseData() {
        if (currentCourse != null) {
            setTitle("Edit Course (ID: " + currentCourse.getCourseID() + ")");
            nameField.setText(currentCourse.getAttribute1());
            codeField.setText(currentCourse.getAttribute2());
            
            try {
                double credits = Double.parseDouble(currentCourse.getAttribute3());
                creditsSpinner.setValue(credits);
            } catch (Exception e) {
                creditsSpinner.setValue(3.0);
            }

            // Logic to select the currently assigned instructor
            int assignedID = currentCourse.getInstructorID(); 
            for (int i = 0; i < instructorCombo.getItemCount(); i++) {
                Instructor item = instructorCombo.getItemAt(i);
                if (item.getInstructorID() == assignedID) {
                    instructorCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void setupListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCourse();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void saveCourse() {
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String credits = String.valueOf(creditsSpinner.getValue());
        
        Instructor selectedInstructor = (Instructor) instructorCombo.getSelectedItem();

        if (name.isEmpty() || code.isEmpty() || selectedInstructor == null) {
            JOptionPane.showMessageDialog(this, "All fields including Instructor are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success;
        String action;

        if (currentCourse == null) {
            Course newCourse = new Course(0, name, code, credits, new Date());
            success = courseDAO.insertWithInstructor(newCourse, selectedInstructor.getInstructorID());
            action = "added";
        } else {
            currentCourse.setAttribute1(name);
            currentCourse.setAttribute2(code);
            currentCourse.setAttribute3(credits);
            success = courseDAO.updateWithInstructor(currentCourse, selectedInstructor.getInstructorID());
            action = "updated";
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Course successfully " + action + " and assigned.", "Success", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadCourseData();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save course record.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}