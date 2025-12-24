package com.eduportal.view;

import com.eduportal.dao.CourseDAO;
import com.eduportal.dao.EnrollmentDAO;
import com.eduportal.dao.StudentDAO;
import com.eduportal.model.Course;
import com.eduportal.model.Enrollment;
import com.eduportal.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

/**
 * Modal dialog for adding a new Enrollment or editing an existing one.
 * UPDATED: Uses JComboBox<Student> and JComboBox<Course> for direct object mapping.
 */
public class EnrollmentFormDialog extends JDialog {

    // UPDATED: Changed from <String> to <Student> and <Course>
    private JComboBox<Student> studentComboBox; 
    private JComboBox<Course> courseComboBox;  
    private JTextField referenceField;
    private JComboBox<String> statusComboBox;
    private JTextArea descriptionArea;
    private JTextArea remarksArea;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private EnrollmentDAO enrollmentDAO;
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    
    private Enrollment currentEnrollment; 
    private ManageEnrollmentsPanel parentPanel; 

    private List<Student> students;
    private List<Course> courses;

    private static final String[] STATUSES = {"Active", "Pending", "Dropped", "Completed"};

    public EnrollmentFormDialog(JFrame parentFrame, ManageEnrollmentsPanel parentPanel, Enrollment enrollment) {
        super(parentFrame, enrollment == null ? "Add New Enrollment" : "Edit Enrollment", true);
        
        this.enrollmentDAO = new EnrollmentDAO();
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        
        this.currentEnrollment = enrollment;
        this.parentPanel = parentPanel;
        
        loadDropdownData();

        setSize(600, 500);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        if (currentEnrollment != null) {
            loadEnrollmentData();
        } else {
            statusComboBox.setSelectedItem("Active");
        }
    }
    
    private void loadDropdownData() {
        students = studentDAO.getAll();
        courses = courseDAO.getAll();
    }

    private void initializeComponents() {
        // UPDATED: Populating with objects directly
        studentComboBox = new JComboBox<>(students.toArray(new Student[0]));
        courseComboBox = new JComboBox<>(courses.toArray(new Course[0]));
        
        referenceField = new JTextField(20);
        statusComboBox = new JComboBox<>(STATUSES);
        descriptionArea = new JTextArea(3, 20);
        remarksArea = new JTextArea(3, 20);
        
        saveButton = new JButton("Save Enrollment");
        cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(52, 168, 83)); 
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(219, 68, 55)); 
        cancelButton.setForeground(Color.WHITE);
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        
        int row = 0;

        // Row 1: Student
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(studentComboBox, gbc);

        // Row 2: Course
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(courseComboBox, gbc);

        // Row 3: Reference ID
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Reference ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; 
        // FIXED: Changed gbc.add to formPanel.add
        formPanel.add(referenceField, gbc); 
        
        // Row 4: Status
        gbc.gridx = 0; gbc.gridy = row; 
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; 
        // FIXED: Changed gbc.add to formPanel.add
        formPanel.add(statusComboBox, gbc);

        // Row 5: Description
        gbc.gridx = 0; gbc.gridy = row; 
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weighty = 0.5;
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Row 6: Remarks
        gbc.gridx = 0; gbc.gridy = row; 
        formPanel.add(new JLabel("Remarks:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weighty = 0.5;
        formPanel.add(new JScrollPane(remarksArea), gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * UPDATED: Cleaner logic for selecting items in the dropdown
     */
    private void loadEnrollmentData() {
        setTitle("Edit Enrollment (ID: " + currentEnrollment.getEnrollmentID() + ")");

        // Select student
        for (Student s : students) {
            if (s.getStudentID() == currentEnrollment.getStudentID()) {
                studentComboBox.setSelectedItem(s);
                break;
            }
        }

        // Select course
        for (Course c : courses) {
            if (c.getCourseID() == currentEnrollment.getCourseID()) {
                courseComboBox.setSelectedItem(c);
                break;
            }
        }

        referenceField.setText(currentEnrollment.getReferenceID());
        statusComboBox.setSelectedItem(currentEnrollment.getStatus());
        descriptionArea.setText(currentEnrollment.getDescription());
        remarksArea.setText(currentEnrollment.getRemarks());
    }

    private void setupListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { saveEnrollment(); }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { dispose(); }
        });
    }

    private void saveEnrollment() {
        // UPDATED: Get objects directly from ComboBox
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        
        String referenceID = referenceField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();
        String description = descriptionArea.getText().trim();
        String remarks = remarksArea.getText().trim();

        if (selectedStudent == null || selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a Student and a Course.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int studentID = selectedStudent.getStudentID();
        int courseID = selectedCourse.getCourseID();
        
        Enrollment enrollmentToSave;
        boolean success;
        String action;

        if (currentEnrollment == null) {
            enrollmentToSave = new Enrollment(0, studentID, courseID, referenceID, description, new Date(), status, remarks);
            success = enrollmentDAO.insert(enrollmentToSave);
            action = "added";
        } else {
            enrollmentToSave = currentEnrollment;
            enrollmentToSave.setStudentID(studentID);
            enrollmentToSave.setCourseID(courseID);
            enrollmentToSave.setReferenceID(referenceID);
            enrollmentToSave.setDescription(description);
            enrollmentToSave.setStatus(status);
            enrollmentToSave.setRemarks(remarks);
            
            success = enrollmentDAO.update(enrollmentToSave);
            action = "updated";
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Enrollment record successfully " + action + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.loadEnrollmentData(); 
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save enrollment. Check database connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}