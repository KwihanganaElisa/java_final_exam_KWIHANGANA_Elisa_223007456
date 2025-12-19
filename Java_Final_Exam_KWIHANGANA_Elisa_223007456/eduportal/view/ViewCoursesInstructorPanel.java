package com.eduportal.view;

import com.eduportal.dao.CourseDAO;
import com.eduportal.model.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for instructors to view all courses they are currently assigned to teach.
 * Dependencies on Course.getCourseID(), getCourseCode(), getTitle(), getInstructorID() 
 * and CourseDAO.getCoursesByInstructor() are now resolved.
 */
public class ViewCoursesInstructorPanel extends JPanel {
    
    private final int instructorID;
    private CourseDAO courseDAO;
    private JTable courseTable;
    private DefaultTableModel tableModel;

    public ViewCoursesInstructorPanel(int instructorID) {
        this.instructorID = instructorID;
        this.courseDAO = new CourseDAO();
        setLayout(new BorderLayout(10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Your Assigned Courses (Instructor ID: " + instructorID + ")", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        initializeTableModel();
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        add(scrollPane, BorderLayout.CENTER);
        
        loadCourseData();
    }
    
    private void initializeTableModel() {
        String[] columnNames = {"Course ID", "Course Code", "Title", "Instructor ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
    }

    /**
     * Fetches and displays the courses taught by this instructor.
     */
    public void loadCourseData() {
        tableModel.setRowCount(0); // Clear existing data
        
        try {
            List<Course> courses = courseDAO.getCoursesByInstructor(instructorID);
            
            for (Course course : courses) {
                tableModel.addRow(new Object[]{
                    course.getCourseID(),
                    course.getCourseCode(),
                    course.getTitle(),
                    course.getInstructorID()
                });
            }
        } catch (Exception ex) {
            // Displays error if the DAO mock throws an exception
            JOptionPane.showMessageDialog(this, "Error loading course data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}