package com.eduportal.view;

import com.eduportal.dao.CourseDAO;
import com.eduportal.dao.StudentDAO;
import com.eduportal.model.Course;
import com.eduportal.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for instructors to view the student roster (enrollment list) for their courses.
 * Uses CourseDAO (to find instructor's courses) and StudentDAO (to find enrolled students).
 */
public class ManageRosterPanel extends JPanel {

    private final int instructorID;
    private CourseDAO courseDAO;
    private StudentDAO studentDAO;

    // UI Components
    private JComboBox<Course> courseComboBox;
    private JTable rosterTable;
    private DefaultTableModel tableModel;

    public ManageRosterPanel(int instructorID) {
        this.instructorID = instructorID;
        this.courseDAO = new CourseDAO();
        this.studentDAO = new StudentDAO();

        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        loadCourses();
    }

    private void initializeComponents() {
        // Dropdowns
        courseComboBox = new JComboBox<>();
        
        // Table Model
        // We use StudentID, Name (Attribute1), Username, and Enrollment Status (which we will mock or assume from a future join)
        String[] columnNames = {"Student ID", "Name", "Username", "Enrollment Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };

        rosterTable = new JTable(tableModel);
    }

    private void layoutComponents() {
        // --- Selection Panel (North) ---
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.add(new JLabel("Select Course to View Roster:"));
        selectionPanel.add(courseComboBox);

        // Title
        JLabel titleLabel = new JLabel("Course Roster Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(selectionPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(rosterTable), BorderLayout.CENTER);
    }

    private void setupListeners() {
        courseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check action command to prevent running on programmatic calls
                if (e.getActionCommand().equals("comboBoxChanged")) {
                    loadRosterData();
                }
            }
        });
    }

    // --- Data Loading ---

    private void loadCourses() {
        courseComboBox.removeAllItems();
        try {
            List<Course> courses = courseDAO.getCoursesByInstructor(instructorID);
            for (Course course : courses) {
                courseComboBox.addItem(course);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading your assigned courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRosterData() {
        tableModel.setRowCount(0); // Clear existing data
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        
        if (selectedCourse == null) return;
        
        try {
            // Relies on the newly implemented getStudentsByCourse method in StudentDAO
            List<Student> students = studentDAO.getStudentsByCourse(selectedCourse.getCourseID());
            
            for (Student student : students) {
                tableModel.addRow(new Object[]{
                    student.getStudentID(),
                    student.getName(), // Uses the helper getName() which maps to Attribute1
                    student.getUsername(),
                    "Enrolled" // Placeholder for enrollment status, requires EnrollmentDAO if status tracking is needed
                });
            }
            if (students.isEmpty()) {
                 tableModel.addRow(new Object[]{"", "No students enrolled in this course.", "", ""});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading student roster: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}