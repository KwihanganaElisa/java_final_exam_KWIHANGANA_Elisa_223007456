package com.eduportal.view;

import com.eduportal.dao.CourseDAO;
import com.eduportal.model.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel within the AdminDashboardFrame for managing Course entities.
 * UPDATED: Cleaned up redundant edit logic and finalized selection handling.
 */
public class ManageCoursesPanel extends JPanel {

    private CourseDAO courseDAO;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JFrame parentFrame; 
    
    private JTextField searchField; // New component for search
    private List<Course> allCourses; // Cache all courses for local filtering

    // Custom Colors (Reused)
    private static final Color ADD_COLOR = new Color(52, 168, 83);   // Green
    private static final Color EDIT_COLOR = new Color(66, 133, 244);  // Blue
    private static final Color DELETE_COLOR = new Color(219, 68, 55); // Red
    private static final Color UTILITY_COLOR = new Color(100, 100, 100); // Gray

    public ManageCoursesPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.courseDAO = new CourseDAO();
        setLayout(new BorderLayout(10, 10));
        
        initializeTableModel();
        loadCourseData();
        
        // --- Search Panel Setup ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search by Name/Code/ID");
        
        searchButton.setBackground(UTILITY_COLOR);
        searchButton.setForeground(Color.WHITE); 
        searchButton.setFocusPainted(false); 

        searchPanel.add(new JLabel("Search Course:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table setup
        courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        // Button panel for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addButton = new JButton("Add New Course");
        JButton editButton = new JButton("Edit Selected Course");
        JButton deleteButton = new JButton("Delete Selected Course");
        JButton refreshButton = new JButton("Refresh Data");

        // Apply colors and styling
        addButton.setBackground(ADD_COLOR);
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(EDIT_COLOR);
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(DELETE_COLOR);
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(UTILITY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        
        addButton.setFocusPainted(false);
        editButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        refreshButton.setFocusPainted(false);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Add components to the panel
        add(searchPanel, BorderLayout.NORTH); 
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // --- Action Listeners ---
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterCourseData(searchField.getText());
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCourseData();
                searchField.setText(""); 
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCourseForm(null); 
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEditAction();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteAction();
            }
        });
    }
    
    private void initializeTableModel() {
        String[] columnNames = {"ID", "Course Name", "Course Code", "Credits", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
    }

    public void loadCourseData() {
        allCourses = courseDAO.getAll();
        displayCourses(allCourses);
    }
    
    private void displayCourses(List<Course> coursesToDisplay) {
        tableModel.setRowCount(0); 
        for (Course course : coursesToDisplay) {
            tableModel.addRow(new Object[]{
                course.getCourseID(),
                course.getAttribute1(),
                course.getAttribute2(),
                course.getAttribute3(),
                course.getCreatedAt()
            });
        }
    }
    
    private void filterCourseData(String searchText) {
        String filter = searchText.trim().toLowerCase();
        if (filter.isEmpty()) {
            displayCourses(allCourses);
            return;
        }

        List<Course> filteredList = new java.util.ArrayList<>();
        for (Course course : allCourses) {
            String courseIDStr = String.valueOf(course.getCourseID());
            String name = course.getAttribute1().toLowerCase(); 
            String code = course.getAttribute2().toLowerCase(); 

            if (courseIDStr.contains(filter) || name.contains(filter) || code.contains(filter)) {
                filteredList.add(course);
            }
        }
        displayCourses(filteredList);
        if (filteredList.isEmpty()) {
             JOptionPane.showMessageDialog(this, "No courses found matching '" + searchText + "'.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openCourseForm(Course course) {
        CourseFormDialog dialog = new CourseFormDialog(parentFrame, this, course);
        dialog.setVisible(true);
    }

    private void handleEditAction() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get ID from the model
            int courseID = (int) tableModel.getValueAt(selectedRow, 0);
            
            // Fetch fresh data from DAO to ensure we have the InstructorID
            Course courseToEdit = courseDAO.getById(courseID);
            
            if (courseToEdit != null) {
                openCourseForm(courseToEdit);
            } else {
                JOptionPane.showMessageDialog(this, "Error: Course data could not be retrieved from the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing selection: " + e.getMessage());
        }
    }
    
    private void handleDeleteAction() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseID = (int) tableModel.getValueAt(selectedRow, 0);
        String courseName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete course: " + courseName + " (ID: " + courseID + ")?\nThis will break enrollments and assignments!", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (courseDAO.delete(courseID)) {
                JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourseData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete course. Check database constraints.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}