package com.eduportal.view;

import com.eduportal.model.Course;
import com.eduportal.dao.CourseDAO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel to display the list of courses a student is registered for.
 * Final Version: No lambdas used.
 */
public class StudentCourseViewerPanel extends JPanel {
    
    private int studentID;
    private CourseDAO courseDAO;
    private JTable courseTable;
    private JLabel totalCreditsLabel;

    public StudentCourseViewerPanel(int studentID) {
        this.studentID = studentID;
        this.courseDAO = new CourseDAO();
        
        // Layout and Border setup
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // --- 1. Header Area ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("My Academic Records", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        headerPanel.add(title, BorderLayout.WEST);
        
        JButton refreshBtn = new JButton("Refresh List");
        
        // TRADITIONAL ACTION LISTENER (No Lambda)
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // --- 2. Table Initialization ---
        List<Course> courses = courseDAO.getCoursesByStudentID(studentID);
        CourseTableModel tableModel = new CourseTableModel(courses);
        courseTable = new JTable(tableModel);
        
        // Table Styling for a professional look
        courseTable.setFillsViewportHeight(true);
        courseTable.setRowHeight(30);
        courseTable.setSelectionBackground(new Color(232, 240, 254));
        courseTable.setSelectionForeground(Color.BLACK);
        courseTable.getTableHeader().setBackground(new Color(248, 249, 250));
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // --- 3. Footer Area (Summary) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalCreditsLabel = new JLabel("Total Credits: 0");
        totalCreditsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        footerPanel.add(totalCreditsLabel);
        
        add(footerPanel, BorderLayout.SOUTH);

        // Initial credit calculation
        updateTotalCredits(courses);
    }

    /**
     * Re-queries the database and updates the table.
     */
    public void refreshData() {
        List<Course> courses = courseDAO.getCoursesByStudentID(studentID);
        CourseTableModel model = (CourseTableModel) courseTable.getModel();
        model.setCourseList(courses);
        model.fireTableDataChanged();
        updateTotalCredits(courses);
    }

    /**
     * Logic to calculate the sum of credits for display.
     */
    private void updateTotalCredits(List<Course> courses) {
        int total = 0;
        if (courses != null) {
            for (Course c : courses) {
                total += c.getCredits();
            }
        }
        totalCreditsLabel.setText("Total Enrolled Credits: " + total);
    }
    
    // --- Inner Class for Table Model ---
    private class CourseTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {"Code", "Course Name", "Instructor", "Credits", "Status"};
        private List<Course> courseList;

        public CourseTableModel(List<Course> courseList) {
            this.courseList = courseList;
        }

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
        public String getColumnName(int col) { 
            return COLUMN_NAMES[col]; 
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (courseList == null || rowIndex >= courseList.size()) return null;

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