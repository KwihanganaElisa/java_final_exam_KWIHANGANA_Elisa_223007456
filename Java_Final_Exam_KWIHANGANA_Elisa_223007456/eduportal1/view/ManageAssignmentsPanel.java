package com.eduportal.view;

import com.eduportal.dao.AssignmentDAO;
import com.eduportal.dao.CourseDAO; // Needed for dummy AssignmentFormDialog
import com.eduportal.model.Assignment;
import com.eduportal.model.Course; // Needed for dummy AssignmentFormDialog
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList; // Needed for List implementation

/**
 * Panel within the AdminDashboardFrame for managing Assignment entities.
 * UPDATED: Replaced placeholder JOptionPanes with actual calls to AssignmentFormDialog.
 */
public class ManageAssignmentsPanel extends JPanel {

    private AssignmentDAO assignmentDAO;
    private JTable assignmentTable;
    private DefaultTableModel tableModel;
    private JFrame parentFrame; 

    private JTextField searchField; // New component for search
    private List<Assignment> allAssignments; // Cache all assignments for local filtering

    // Custom Colors (Reused)
    private static final Color ADD_COLOR = new Color(52, 168, 83);   // Green
    private static final Color EDIT_COLOR = new Color(66, 133, 244);  // Blue
    private static final Color DELETE_COLOR = new Color(219, 68, 55); // Red
    private static final Color UTILITY_COLOR = new Color(100, 100, 100); // Gray
    
    // Column indices used by the tableModel
    private static final int COL_ID = 0;

    public ManageAssignmentsPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        // Use dummy DAO implementations for runnable code
        this.assignmentDAO = new AssignmentDAO(); 
        setLayout(new BorderLayout(10, 10));
        
        initializeTableModel();
        // Load data first, then set up the panel components
        
        // --- Search Panel Setup ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(30);
        JButton searchButton = new JButton("Search by Assignment ID/Course ID/Reference");
        
        searchButton.setBackground(UTILITY_COLOR);
        searchButton.setForeground(Color.WHITE); 
        searchButton.setFocusPainted(false); 

        searchPanel.add(new JLabel("Search Assignment:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table setup
        assignmentTable = new JTable(tableModel);
        assignmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(assignmentTable);
        
        // Button panel for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addButton = new JButton("Add New Assignment");
        JButton editButton = new JButton("Edit Selected Assignment");
        JButton deleteButton = new JButton("Delete Selected Assignment");
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
        
        loadAssignmentData(); // Load data after components are initialized
        
        // --- Action Listeners (Anonymous Inner Classes) ---
        
        // Search Button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterAssignmentData(searchField.getText());
            }
        });
        
        // Refresh Button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAssignmentData();
                searchField.setText("");
            }
        });
        
        // ADD Button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAssignmentForm(null); // Passing null indicates a new assignment
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
     * Sets up the column names for the JTable using the new model fields.
     */
    private void initializeTableModel() {
        // Columns needed by the display logic: ID, Course ID, Reference ID, Description, Due Date, Status, Remarks
        String[] columnNames = {"ID", "Course ID", "Reference ID", "Description", "Due Date", "Status", "Remarks"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
            // Ensure ID is treated as an Integer for sorting purposes
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                 if (columnIndex == COL_ID) return Integer.class;
                 return super.getColumnClass(columnIndex);
            }
        };
    }

    /**
     * Fetches all assignments from the database, caches them, and updates the table.
     */
    public void loadAssignmentData() {
        try {
            allAssignments = assignmentDAO.getAll();
            displayAssignments(allAssignments);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading assignments: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            allAssignments = new ArrayList<Assignment>(); // Prevent NullPointer in filter
        }
    }
    
    /**
     * Displays the given list of assignments in the table model.
     */
    private void displayAssignments(List<Assignment> assignmentsToDisplay) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Assignment assignment : assignmentsToDisplay) {
            // NOTE: The Due Date must be converted to a readable String/Date object for display.
            tableModel.addRow(new Object[]{
                assignment.getAssignmentID(),
                assignment.getCourseID(),
                assignment.getReferenceID(),
                assignment.getDescription(),
                assignment.getDueDate(), // Date object should be fine, JTable renders it via toString()
                assignment.getStatus(),
                assignment.getRemarks()
            });
        }
    }
    
    /**
     * Filters the cached assignment data based on the search text (Assignment ID, Course ID, or Reference ID).
     */
    private void filterAssignmentData(String searchText) {
        String filter = searchText.trim().toLowerCase();
        if (filter.isEmpty()) {
            displayAssignments(allAssignments);
            return;
        }

        List<Assignment> filteredList = new ArrayList<Assignment>();
        
        for (Assignment assignment : allAssignments) {
            String assignmentIDStr = String.valueOf(assignment.getAssignmentID());
            String courseIDStr = String.valueOf(assignment.getCourseID());
            String referenceID = assignment.getReferenceID() != null ? assignment.getReferenceID().toLowerCase() : "";

            // Check if filter matches any ID or Reference ID
            if (assignmentIDStr.contains(filter) || courseIDStr.contains(filter) || referenceID.contains(filter)) {
                filteredList.add(assignment);
            }
        }
        
        displayAssignments(filteredList);
        
        if (filteredList.isEmpty() && !filter.isEmpty()) {
              JOptionPane.showMessageDialog(this, "No assignment records found matching '" + searchText + "'.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Opens the Assignment Form (JDialog) for Add or Edit.
     */
    private void openAssignmentForm(Assignment assignment) {
        // Admin user (ID 0) is a safe placeholder when not instructor-specific
        int adminID = 0; 
        AssignmentFormDialog dialog = new AssignmentFormDialog(parentFrame, this, adminID, assignment);
        dialog.setVisible(true);
    }

    /**
     * Handles the Edit button click.
     */
    private void handleEditAction() {
        int selectedRow = assignmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an assignment to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Need to convert model row index if the table is sorted/filtered, but simple selectedRow is fine for now
        int assignmentID = (int) tableModel.getValueAt(selectedRow, COL_ID);
        Assignment assignmentToEdit = assignmentDAO.getById(assignmentID);
        
        if (assignmentToEdit != null) {
            openAssignmentForm(assignmentToEdit);
        } else {
            JOptionPane.showMessageDialog(this, "Error: Assignment not found in data source.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles the Delete button click.
     */
    private void handleDeleteAction() {
        int selectedRow = assignmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an assignment to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int assignmentID = (int) tableModel.getValueAt(selectedRow, COL_ID);
        String referenceID = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete assignment: " + referenceID + " (ID: " + assignmentID + ")?\nThis will remove all associated grades!", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (assignmentDAO.delete(assignmentID)) {
                JOptionPane.showMessageDialog(this, "Assignment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssignmentData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete assignment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // --- Main method for testing ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Admin Assignment Management Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1000, 600);
                frame.add(new ManageAssignmentsPanel(frame)); 
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}