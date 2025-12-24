package com.eduportal.view;

import com.eduportal.dao.StudentDAO;
import com.eduportal.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel within the AdminDashboardFrame for managing Student entities.
 * UPDATED:
 * 1. Added Search functionality by Name or ID.
 * 2. Added colors to CRUD buttons for better aesthetics and recognition.
 */
public class ManageStudentsPanel extends JPanel {

    private StudentDAO studentDAO;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JFrame parentFrame; 
    
    private JTextField searchField; // New component for search
    private List<Student> allStudents; // Cache all students for local filtering

    // Custom Colors
    private static final Color ADD_COLOR = new Color(52, 168, 83);   // Green
    private static final Color EDIT_COLOR = new Color(66, 133, 244);  // Blue
    private static final Color DELETE_COLOR = new Color(219, 68, 55); // Red
    private static final Color UTILITY_COLOR = new Color(100, 100, 100); // Gray

    public ManageStudentsPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.studentDAO = new StudentDAO();
        setLayout(new BorderLayout(10, 10));
        
        initializeTableModel();
        // Load data initially, populating the cache
        loadStudentData(); 
        
        // --- Search Panel Setup (NEW) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search by Name/ID");
        
        // Apply color to the Search button
        searchButton.setBackground(UTILITY_COLOR);
        searchButton.setForeground(Color.WHITE); 
        searchButton.setFocusPainted(false); 

        searchPanel.add(new JLabel("Search Student:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Table setup
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        JScrollPane scrollPane = new JScrollPane(studentTable);
        
        // Button panel for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addButton = new JButton("Add New Student");
        JButton editButton = new JButton("Edit Selected Student");
        JButton deleteButton = new JButton("Delete Selected Student");
        JButton refreshButton = new JButton("Refresh Data");

        // Apply colors to CRUD buttons
        addButton.setBackground(ADD_COLOR);
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(EDIT_COLOR);
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(DELETE_COLOR);
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(UTILITY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        
        // Remove focus border for better aesthetics
        addButton.setFocusPainted(false);
        editButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        refreshButton.setFocusPainted(false);


        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Add components to the panel
        add(searchPanel, BorderLayout.NORTH); // Add search bar to the top
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // --- Action Listeners (Anonymous Inner Classes) ---
        
        // Search Button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterStudentData(searchField.getText());
            }
        });

        // Refresh Button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When refreshing, reload all data from DB and clear search filter
                loadStudentData(); 
                searchField.setText("");
            }
        });
        
        // Add Button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openStudentForm(null);
            }
        });
        
        // Edit Button
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEditAction();
            }
        });
        
        // Delete Button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteAction();
            }
        });
    }
    
    /**
     * Sets up the column names for the JTable.
     */
    private void initializeTableModel() {
        String[] columnNames = {"ID", "Username", "Role", "Full Name", "Email", "Course_name", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
    }

    /**
     * Fetches all students from the database, caches them, and updates the table.
     */
    public void loadStudentData() {
        // 1. Fetch all data
        allStudents = studentDAO.getAll();
        // 2. Display the full list
        displayStudents(allStudents);
    }
    
    /**
     * Displays the given list of students in the table model.
     * @param studentsToDisplay The filtered or full list of Student objects.
     */
    private void displayStudents(List<Student> studentsToDisplay) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Student student : studentsToDisplay) {
            tableModel.addRow(new Object[]{
                student.getStudentID(),
                student.getUsername(),
                student.getRole(),
                student.getfull_name(), // Full Name
                student.getemail(), // Email
                student.getcourse_name(),
                student.getCreatedAt()
            });
        }
    }
    
    /**
     * Filters the cached student data based on the search text (ID or Name).
     * @param searchText The text to filter by.
     */
    private void filterStudentData(String searchText) {
        String filter = searchText.trim().toLowerCase();
        if (filter.isEmpty()) {
            displayStudents(allStudents);
            return;
        }

        List<Student> filteredList = new java.util.ArrayList<>();
        
        for (Student student : allStudents) {
            String studentIDStr = String.valueOf(student.getStudentID());
            String fullName = student.getfull_name().toLowerCase(); // Full Name

            // Check if filter matches ID or Full Name
            if (studentIDStr.contains(filter) || fullName.contains(filter)) {
                filteredList.add(student);
            }
        }
        
        displayStudents(filteredList);
        
        if (filteredList.isEmpty()) {
             JOptionPane.showMessageDialog(this, "No students found matching '" + searchText + "'.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Handles the opening of the Student Form (JDialog) for Add or Edit.
     * @param student The Student object to edit, or null for a new student.
     */
    private void openStudentForm(Student student) {
        StudentFormDialog dialog = new StudentFormDialog(parentFrame, this, student);
        dialog.setVisible(true);
    }

    /**
     * Handles the Edit button click.
     */
    private void handleEditAction() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentID = (int) tableModel.getValueAt(selectedRow, 0);
        Student studentToEdit = studentDAO.getById(studentID);
        
        if (studentToEdit != null) {
            openStudentForm(studentToEdit);
        } else {
            JOptionPane.showMessageDialog(this, "Error: Student not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles the Delete button click.
     */
    private void handleDeleteAction() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int studentID = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete student: " + username + " (ID: " + studentID + ")?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (studentDAO.delete(studentID)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudentData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student. Check database constraints (e.g., existing enrollments).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}