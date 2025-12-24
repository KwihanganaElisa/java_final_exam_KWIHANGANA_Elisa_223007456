package com.eduportal.view;

import com.eduportal.dao.EnrollmentDAO;
import com.eduportal.model.Enrollment;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel within the AdminDashboardFrame for managing Enrollment entities.
 * UPDATED: Replaced placeholder JOptionPanes with actual calls to EnrollmentFormDialog.
 */
public class ManageEnrollmentsPanel extends JPanel {

    private EnrollmentDAO enrollmentDAO;
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;
    private JFrame parentFrame; 
    
    private JTextField searchField; // New component for search
    private List<Enrollment> allEnrollments; // Cache all enrollments for local filtering

    // Custom Colors (Reused)
    private static final Color ADD_COLOR = new Color(52, 168, 83);   // Green
    private static final Color EDIT_COLOR = new Color(66, 133, 244);  // Blue
    private static final Color DELETE_COLOR = new Color(219, 68, 55); // Red
    private static final Color UTILITY_COLOR = new Color(100, 100, 100); // Gray

    public ManageEnrollmentsPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.enrollmentDAO = new EnrollmentDAO();
        setLayout(new BorderLayout(10, 10));
        
        initializeTableModel();
        loadEnrollmentData();
        
        // --- Search Panel Setup ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(30);
        JButton searchButton = new JButton("Search by Enrollment/Student/Course ID");
        
        searchButton.setBackground(UTILITY_COLOR);
        searchButton.setForeground(Color.WHITE); 
        searchButton.setFocusPainted(false); 

        searchPanel.add(new JLabel("Search Enrollment:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table setup
        enrollmentTable = new JTable(tableModel);
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        
        // Button panel for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addButton = new JButton("Add New Enrollment");
        JButton editButton = new JButton("Edit Selected Enrollment");
        JButton deleteButton = new JButton("Delete Selected Enrollment");
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

        add(searchPanel, BorderLayout.NORTH); // Added search panel
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // --- Action Listeners (Anonymous Inner Classes) ---
        
        // Search Button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterEnrollmentData(searchField.getText());
            }
        });
        
        // Refresh Button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadEnrollmentData();
                searchField.setText("");
            }
        });
        
        // ADD Button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEnrollmentForm(null); // Passing null indicates a new enrollment
            }
        });
        
        // EDIT Button
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEditAction();
            }
        });
        
        // DELETE Button
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
        String[] columnNames = {"ID", "Student ID", "Course ID", "Reference ID", "Description", "Enroll Date", "Status", "Remarks"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
    }

    /**
     * Fetches all enrollments from the database, caches them, and updates the table.
     */
    public void loadEnrollmentData() {
        allEnrollments = enrollmentDAO.getAll();
        displayEnrollments(allEnrollments);
    }
    
    /**
     * Displays the given list of enrollments in the table model.
     */
    private void displayEnrollments(List<Enrollment> enrollmentsToDisplay) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Enrollment enrollment : enrollmentsToDisplay) {
            tableModel.addRow(new Object[]{
                enrollment.getEnrollmentID(),
                enrollment.getStudentID(),
                enrollment.getCourseID(),
                enrollment.getReferenceID(),
                enrollment.getDescription(),
                enrollment.getEnrollDate(),
                enrollment.getStatus(),
                enrollment.getRemarks()
            });
        }
    }
    
    /**
     * Filters the cached enrollment data based on the search text (Enrollment ID, Student ID, or Course ID).
     */
    private void filterEnrollmentData(String searchText) {
        String filter = searchText.trim().toLowerCase();
        if (filter.isEmpty()) {
            displayEnrollments(allEnrollments);
            return;
        }

        List<Enrollment> filteredList = new java.util.ArrayList<>();
        
        for (Enrollment enrollment : allEnrollments) {
            String enrollmentIDStr = String.valueOf(enrollment.getEnrollmentID());
            String studentIDStr = String.valueOf(enrollment.getStudentID());
            String courseIDStr = String.valueOf(enrollment.getCourseID());

            // Check if filter matches any ID
            if (enrollmentIDStr.contains(filter) || studentIDStr.contains(filter) || courseIDStr.contains(filter)) {
                filteredList.add(enrollment);
            }
        }
        
        displayEnrollments(filteredList);
        
        if (filteredList.isEmpty()) {
             JOptionPane.showMessageDialog(this, "No enrollment records found matching '" + searchText + "'.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Opens the Enrollment Form (JDialog) for Add or Edit.
     */
    private void openEnrollmentForm(Enrollment enrollment) {
        // --- REAL DIALOG CALL ---
        EnrollmentFormDialog dialog = new EnrollmentFormDialog(parentFrame, this, enrollment);
        dialog.setVisible(true);
    }

    /**
     * Handles the Edit button click.
     */
    private void handleEditAction() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment record to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrollmentID = (int) tableModel.getValueAt(selectedRow, 0);
        Enrollment enrollmentToEdit = enrollmentDAO.getById(enrollmentID);
        
        if (enrollmentToEdit != null) {
            openEnrollmentForm(enrollmentToEdit); // --- REAL DIALOG CALL ---
        } else {
            JOptionPane.showMessageDialog(this, "Error: Enrollment record not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles the Delete button click.
     */
    private void handleDeleteAction() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment record to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int enrollmentID = (int) tableModel.getValueAt(selectedRow, 0);
        int studentID = (int) tableModel.getValueAt(selectedRow, 1);
        int courseID = (int) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete enrollment record (ID: " + enrollmentID + ") for Student " + studentID + " in Course " + courseID + "?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (enrollmentDAO.delete(enrollmentID)) {
                JOptionPane.showMessageDialog(this, "Enrollment record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEnrollmentData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete enrollment record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}