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

public class ManageRosterPanel extends JPanel {

    private final int instructorID;
    private CourseDAO courseDAO;
    private StudentDAO studentDAO;

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
        
        // Load initial data
        loadCourses();
    }

    private void initializeComponents() {
        courseComboBox = new JComboBox<Course>();
        
        // Columns: ID, Name (full_name), Username, Email
        String[] columnNames = {"Student ID", "Full Name", "Username", "Email Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false; // Roster is view-only
            }
        };

        rosterTable = new JTable(tableModel);
        rosterTable.setFillsViewportHeight(true);
        rosterTable.setRowHeight(25);
        
        // Optional: Set specific widths for columns
        rosterTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        rosterTable.getColumnModel().getColumn(1).setPreferredWidth(200);
    }

    private void layoutComponents() {
        // --- Selection Panel ---
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Instructor Filters"));
        selectionPanel.add(new JLabel("Your Courses:"));
        selectionPanel.add(courseComboBox);

        // --- Header Panel ---
        JLabel titleLabel = new JLabel("Class Enrollment Roster", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(selectionPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(rosterTable), BorderLayout.CENTER);
        
        // Add a padding border to the whole panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void setupListeners() {
        courseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // programmatic changes often don't have action commands, 
                // but standard user selection does.
                loadRosterData();
            }
        });
    }

    private void loadCourses() {
        courseComboBox.removeAllItems();
        try {
            List<Course> courses = courseDAO.getCoursesByInstructor(instructorID);
            for (Course course : courses) {
                courseComboBox.addItem(course);
            }
            
            // If courses were found, the first one is selected by default, 
            // trigger the roster load.
            if (courseComboBox.getItemCount() > 0) {
                loadRosterData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching courses: " + ex.getMessage());
        }
    }

    private void loadRosterData() {
        tableModel.setRowCount(0); 
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        
        if (selectedCourse == null) return;
        
        try {
            // This calls the SQL: SELECT s.* FROM student s JOIN enrollment e ...
            List<Student> students = studentDAO.getStudentsByCourse(selectedCourse.getCourseID());
            
            if (students.isEmpty()) {
                // If empty, we show a friendly message in the table
                tableModel.addRow(new Object[]{"-", "No students registered yet", "-", "-"});
            } else {
                for (Student s : students) {
                    tableModel.addRow(new Object[]{
                        s.getStudentID(),
                        s.getName(), // Ensure this returns full_name from the DB
                        s.getUsername(),
                        s.getEmail()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Roster Error: " + ex.getMessage());
        }
    }
}