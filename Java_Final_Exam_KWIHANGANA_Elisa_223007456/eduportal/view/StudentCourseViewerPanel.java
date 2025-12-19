package com.eduportal.view;

import com.eduportal.model.Course;
import com.eduportal.dao.CourseDAO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel to display the list of courses a student is registered for.
 * Updated with refreshData capabilities to sync with the database.
 */
public class StudentCourseViewerPanel extends JPanel {
    
    private int studentID;
    private CourseDAO courseDAO;
    private JTable courseTable;

    public StudentCourseViewerPanel(int studentID) {
        this.studentID = studentID;
        this.courseDAO = new CourseDAO();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 1. Title
        JLabel title = new JLabel("My Current and Completed Courses", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);
        
        // 2. Load Data and Initialize Table
        // We initialize with the current data from the database
        List<Course> courses = courseDAO.getCoursesByStudentID(studentID);
        CourseTableModel tableModel = new CourseTableModel(courses);
        courseTable = new JTable(tableModel);
        
        // Styling
        courseTable.setFillsViewportHeight(true);
        courseTable.setRowHeight(25);
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // 3. Add Table to ScrollPane and to Panel
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createTitledBorder("Registered Courses for ID: " + studentID));
    }

    /**
     * Call this method to refresh the table whenever a student enrolls in a new course
     * or when switching to this view.
     */
    public void refreshData() {
        // 1. Fetch fresh data from the database
        List<Course> courses = courseDAO.getCoursesByStudentID(studentID);
        
        // 2. Update the Table Model safely
        CourseTableModel model = (CourseTableModel) courseTable.getModel();
        model.setCourseList(courses);
        
        // 3. Notify the UI to redraw with the new data
        model.fireTableDataChanged();
    }
    
    // --- Inner Class for Table Model ---
    private class CourseTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {"Code", "Course Name", "Instructor", "Credits", "Status"};
        private List<Course> courseList;

        public CourseTableModel(List<Course> courseList) {
            this.courseList = courseList;
        }

        /**
         * Updates the internal list of courses. 
         * Used by the refreshData() method.
         */
        public void setCourseList(List<Course> courseList) {
            this.courseList = courseList;
        }

        @Override
        public int getRowCount() {
            return courseList != null ? courseList.size() : 0;
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return COLUMN_NAMES[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (courseList == null || rowIndex >= courseList.size()) {
                return null;
            }

            Course course = courseList.get(rowIndex);
            switch (columnIndex) {
                case 0: return course.getCourseCode();
                case 1: return course.getCourseName();
                case 2: return (course.getInstructorName() != null) ? course.getInstructorName() : "TBA";
                case 3: return course.getCredits();
                case 4: return course.getEnrollmentStatus();
                default: return null;
            }
        }
    }
}