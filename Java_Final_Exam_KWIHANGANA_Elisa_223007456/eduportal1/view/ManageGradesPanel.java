package com.eduportal.view;

import com.eduportal.dao.GradeDAO;
import com.eduportal.model.Grade;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel within the AdminDashboardFrame for managing Grade entities.
 * UPDATED: Replaced placeholder JOptionPanes with actual calls to GradeFormDialog.
 */
public class ManageGradesPanel extends JPanel {

    private GradeDAO gradeDAO;
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JFrame parentFrame; 

    private JTextField searchField; // New component for search
    private List<Grade> allGrades; // Cache all grades for local filtering

    // Custom Colors (Reused)
    private static final Color ADD_COLOR = new Color(52, 168, 83);   // Green
    private static final Color EDIT_COLOR = new Color(66, 133, 244);  // Blue
    private static final Color DELETE_COLOR = new Color(219, 68, 55); // Red
    private static final Color UTILITY_COLOR = new Color(100, 100, 100); // Gray

    public ManageGradesPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.gradeDAO = new GradeDAO();
        setLayout(new BorderLayout(10, 10));
        
        initializeTableModel();
        loadGradeData();
        
        // --- Search Panel Setup ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchField = new JTextField(30);
        JButton searchButton = new JButton("Search by Grade/Student/Assignment ID");
        
        searchButton.setBackground(UTILITY_COLOR);
        searchButton.setForeground(Color.WHITE); 
        searchButton.setFocusPainted(false); 

        searchPanel.add(new JLabel("Search Grade Record:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table setup
        gradeTable = new JTable(tableModel);
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        
        // Button panel for CRUD operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addButton = new JButton("Add New Grade");
        JButton editButton = new JButton("Edit Selected Grade");
        JButton deleteButton = new JButton("Delete Selected Grade");
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
                filterGradeData(searchField.getText());
            }
        });
        
        // Refresh Button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGradeData();
                searchField.setText("");
            }
        });
        
        // ADD Button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openGradeForm(null);
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
        String[] columnNames = {"ID", "Student ID", "Assignment ID", "Grade Type", "Score", "Letter Grade", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
    }

    /**
     * Fetches all grades from the database, caches them, and updates the table.
     */
    public void loadGradeData() {
        allGrades = gradeDAO.getAll();
        displayGrades(allGrades);
    }
    
    /**
     * Displays the given list of grades in the table model.
     */
    private void displayGrades(List<Grade> gradesToDisplay) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Grade grade : gradesToDisplay) {
            tableModel.addRow(new Object[]{
                grade.getGradeID(),
                grade.getStudentID(),
                grade.getAssignmentID(),
                grade.getGradeType(),
                grade.getScore(),
                grade.getLetterGrade(),
                grade.getCreatedAt()
            });
        }
    }
    
    /**
     * Filters the cached grade data based on the search text (Grade ID, Student ID, or Assignment ID).
     */
    private void filterGradeData(String searchText) {
        String filter = searchText.trim().toLowerCase();
        if (filter.isEmpty()) {
            displayGrades(allGrades);
            return;
        }

        List<Grade> filteredList = new java.util.ArrayList<>();
        
        for (Grade grade : allGrades) {
            String gradeIDStr = String.valueOf(grade.getGradeID());
            String studentIDStr = String.valueOf(grade.getStudentID());
            String assignmentIDStr = String.valueOf(grade.getAssignmentID());

            // Check if filter matches any ID
            if (gradeIDStr.contains(filter) || studentIDStr.contains(filter) || assignmentIDStr.contains(filter)) {
                filteredList.add(grade);
            }
        }
        
        displayGrades(filteredList);
        
        if (filteredList.isEmpty()) {
             JOptionPane.showMessageDialog(this, "No grade records found matching '" + searchText + "'.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Opens the Grade Form (JDialog) for Add or Edit.
     */
    private void openGradeForm(Grade grade) {
        // --- REAL DIALOG CALL ---
        GradeFormDialog dialog = new GradeFormDialog(parentFrame, this, grade);
        dialog.setVisible(true);
    }

    /**
     * Handles the Edit button click.
     */
    private void handleEditAction() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a grade record to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int gradeID = (int) tableModel.getValueAt(selectedRow, 0);
        Grade gradeToEdit = gradeDAO.getById(gradeID);
        
        if (gradeToEdit != null) {
            openGradeForm(gradeToEdit); // --- REAL DIALOG CALL ---
        } else {
            JOptionPane.showMessageDialog(this, "Error: Grade record not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles the Delete button click.
     */
    private void handleDeleteAction() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a grade record to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int gradeID = (int) tableModel.getValueAt(selectedRow, 0);
        int studentID = (int) tableModel.getValueAt(selectedRow, 1);
        int assignmentID = (int) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete grade record (ID: " + gradeID + ") for Student " + studentID + " on Assignment " + assignmentID + "?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (gradeDAO.delete(gradeID)) {
                JOptionPane.showMessageDialog(this, "Grade record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadGradeData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete grade record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}