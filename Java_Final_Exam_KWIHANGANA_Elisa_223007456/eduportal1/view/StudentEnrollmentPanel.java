package com.eduportal.view;

import com.eduportal.model.Course;
import com.eduportal.dao.CourseDAO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for students to search for and enroll in new courses.
 * FIXED: Added refreshData() and improved button lifecycle management.
 */
public class StudentEnrollmentPanel extends JPanel {

    private int studentID;
    private CourseDAO courseDAO;
    private JTextField searchField;
    private JTable courseTable;
    private EnrollmentTableModel tableModel;

    public StudentEnrollmentPanel(int studentID) {
        this.studentID = studentID;
        this.courseDAO = new CourseDAO(); 
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 1. Search Panel
        add(createSearchPanel(), BorderLayout.NORTH);
        
        // 2. Table Panel
        initializeTable();
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        loadCourses(null); 
        
        setBorder(BorderFactory.createTitledBorder("Course Enrollment & Registration"));
    }

    /**
     * Called by the DashboardFrame to refresh the list of available courses.
     */
    public void refreshData() {
        searchField.setText("");
        loadCourses(null);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.add(new JLabel("Search Courses (Code/Name):"));
        searchField = new JTextField(20);
        panel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadCourses(searchField.getText().trim());
            }
        });
        panel.add(searchButton);
        
        return panel;
    }

    private void initializeTable() {
        tableModel = new EnrollmentTableModel(new ArrayList<Course>());
        courseTable = new JTable(tableModel);
        
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(60);  
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(200); 
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(50);  
        courseTable.getColumnModel().getColumn(5).setPreferredWidth(80);  

        courseTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        courseTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JTextField(), this));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        courseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        courseTable.setRowHeight(35); 
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseTable.setFillsViewportHeight(true);
    }
    
    private void loadCourses(String query) {
        // CourseDAO needs searchCourses(query) and enrollStudentInCourse(...)
        List<Course> courses = courseDAO.searchCourses(query);
        tableModel.setCourseList(courses);
        tableModel.fireTableDataChanged();
    }
    
    public void performEnrollment(int rowIndex) {
        Course selectedCourse = tableModel.getCourse(rowIndex);
        String status = (String) tableModel.getValueAt(rowIndex, 4);

        if ("Registered".equalsIgnoreCase(status) || "Pending".equalsIgnoreCase(status)) {
             JOptionPane.showMessageDialog(this, 
                 "You are already " + status + " for " + selectedCourse.getCourseCode(),
                 "Enrollment Error", JOptionPane.WARNING_MESSAGE);
             return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Enroll in: " + selectedCourse.getCourseName() + "?", 
            "Confirm Enrollment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = courseDAO.enrollStudentInCourse(studentID, selectedCourse.getCourseID());
            if (success) {
                JOptionPane.showMessageDialog(this, "Successfully enrolled!");
                loadCourses(searchField.getText().trim()); // Refresh current view
            } else {
                JOptionPane.showMessageDialog(this, "Enrollment failed. Already registered?", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // --- Table Model ---
    private class EnrollmentTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {"Code", "Course Name", "Instructor", "Credits", "Status", "Action"};
        private List<Course> courseList;

        public EnrollmentTableModel(List<Course> courseList) { this.courseList = courseList; }
        public void setCourseList(List<Course> list) { this.courseList = list; }
        public Course getCourse(int row) { return courseList.get(row); }

        @Override public int getRowCount() { return courseList.size(); }
        @Override public int getColumnCount() { return COLUMN_NAMES.length; }
        @Override public String getColumnName(int col) { return COLUMN_NAMES[col]; }
        @Override public boolean isCellEditable(int row, int col) { return col == 5; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Course course = courseList.get(rowIndex);
            String status = course.getEnrollmentStatus();
            if (status == null || status.isEmpty() || "Unknown".equalsIgnoreCase(status)) status = "Open";

            switch (columnIndex) {
                case 0: return course.getCourseCode();
                case 1: return course.getCourseName();
                case 2: return course.getInstructorName();
                case 3: return course.getCredits();
                case 4: return status;
                case 5: return ("Registered".equalsIgnoreCase(status)) ? "Enrolled" : "Enroll";
                default: return null;
            }
        }
    }
    
    // --- Button Handling ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
            setText((value == null) ? "" : value.toString());
            if ("Enroll".equals(getText())) {
                setBackground(new Color(76, 175, 80));
                setForeground(Color.WHITE);
                setEnabled(true);
            } else {
                setBackground(Color.LIGHT_GRAY);
                setForeground(Color.DARK_GRAY);
                setEnabled(false);
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private int clickedRow;
        private StudentEnrollmentPanel parent;

        public ButtonEditor(JTextField txt, StudentEnrollmentPanel panel) {
            super(txt);
            this.parent = panel;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { fireEditingStopped(); }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSel, int row, int col) {
            clickedRow = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if ("Enroll".equals(label)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() { parent.performEnrollment(clickedRow); }
                });
            }
            return label;
        }
    }
}