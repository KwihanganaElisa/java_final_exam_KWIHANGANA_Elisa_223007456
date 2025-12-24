package com.eduportal.view;

import com.eduportal.dao.InstructorDAO;
import com.eduportal.model.Instructor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel within the AdminDashboardFrame for managing Instructor entities.
 * UPDATED: Replaced placeholder JOptionPanes with actual calls to InstructorFormDialog.
 */
public class ManageInstructorsPanel extends JPanel {

    private InstructorDAO instructorDAO;
    private JTable instructorTable;
    private DefaultTableModel tableModel;
    private JFrame parentFrame; 
    
    private JTextField searchField; // New component for search
    private List<Instructor> allInstructors; // Cache all instructors for local filtering

    // Custom Colors (Reused from Students Panel)
    private static final Color ADD_COLOR = new Color(52, 168, 83);   // Green
    private static final Color EDIT_COLOR = new Color(66, 133, 244);  // Blue
    private static final Color DELETE_COLOR = new Color(219, 68, 55); // Red
    private static final Color UTILITY_COLOR = new Color(100, 100, 100); // Gray

    public ManageInstructorsPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.instructorDAO = new InstructorDAO();
        setLayout(new BorderLayout(10, 10));
        
        initializeTableModel();
        loadInstructorData();
        
        // --- Search Panel Setup ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search by Name/ID/Identifier");
        
        searchButton.setBackground(UTILITY_COLOR);
        searchButton.setForeground(Color.WHITE); 
        searchButton.setFocusPainted(false); 

        searchPanel.add(new JLabel("Search Instructor:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table setup
        instructorTable = new JTable(tableModel);
        instructorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(instructorTable);
        
        // Button panel for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addButton = new JButton("Add New Instructor");
        JButton editButton = new JButton("Edit Selected Instructor");
        JButton deleteButton = new JButton("Delete Selected Instructor");
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
        add(searchPanel, BorderLayout.NORTH); // Added search panel
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // --- Action Listeners (Anonymous Inner Classes) ---
        
        // Search Button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterInstructorData(searchField.getText());
            }
        });

        // Refresh Button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadInstructorData();
                searchField.setText(""); // Clear search field
            }
        });
        
        // Add Button (Opens form for new record - instructor = null)
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openInstructorForm(null);
            }
        });
        
        // Edit Button (Opens form for selected record)
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
        String[] columnNames = {"ID", "Username", "Name", "Identifier", "Status", "Location", "Contact", "Assigned Since"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
    }

    /**
     * Fetches all instructors from the database, caches them, and updates the table.
     */
    public void loadInstructorData() {
        allInstructors = instructorDAO.getAll();
        displayInstructors(allInstructors);
    }
    
    /**
     * Displays the given list of instructors in the table model.
     */
    private void displayInstructors(List<Instructor> instructorsToDisplay) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Instructor instructor : instructorsToDisplay) {
            tableModel.addRow(new Object[]{
                instructor.getInstructorID(),
                instructor.getUsername(),
                instructor.getName(),
                instructor.getIdentifier(),
                instructor.getStatus(),
                instructor.getLocation(),
                instructor.getContact(),
                instructor.getAssignedSince()
            });
        }
    }
    
    /**
     * Filters the cached instructor data based on the search text (ID, Name, or Identifier).
     * @param searchText The text to filter by.
     */
    private void filterInstructorData(String searchText) {
        String filter = searchText.trim().toLowerCase();
        if (filter.isEmpty()) {
            displayInstructors(allInstructors);
            return;
        }

        List<Instructor> filteredList = new java.util.ArrayList<>();
        
        for (Instructor instructor : allInstructors) {
            String instructorIDStr = String.valueOf(instructor.getInstructorID());
            String name = instructor.getName().toLowerCase();
            String identifier = instructor.getIdentifier().toLowerCase();

            // Check if filter matches ID, Name, or Identifier
            if (instructorIDStr.contains(filter) || name.contains(filter) || identifier.contains(filter)) {
                filteredList.add(instructor);
            }
        }
        
        displayInstructors(filteredList);
        
        if (filteredList.isEmpty()) {
             JOptionPane.showMessageDialog(this, "No instructors found matching '" + searchText + "'.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Opens the Instructor Form (JDialog) for Add or Edit.
     */
    private void openInstructorForm(Instructor instructor) {
        // ACTUAL CALL TO THE DIALOG
        InstructorFormDialog dialog = new InstructorFormDialog(parentFrame, this, instructor); 
        dialog.setVisible(true);
    }

    /**
     * Handles the Edit button click.
     */
    private void handleEditAction() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int instructorID = (int) tableModel.getValueAt(selectedRow, 0);
        Instructor instructorToEdit = instructorDAO.getById(instructorID);
        
        if (instructorToEdit != null) {
            openInstructorForm(instructorToEdit); // ACTUAL CALL
        } else {
            JOptionPane.showMessageDialog(this, "Error: Instructor not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles the Delete button click.
     */
    private void handleDeleteAction() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int instructorID = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete instructor: " + name + " (ID: " + instructorID + ")?\nThis action cannot be undone.", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (instructorDAO.delete(instructorID)) {
                JOptionPane.showMessageDialog(this, "Instructor deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInstructorData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete instructor. Check database constraints (e.g., still assigned to a course).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}