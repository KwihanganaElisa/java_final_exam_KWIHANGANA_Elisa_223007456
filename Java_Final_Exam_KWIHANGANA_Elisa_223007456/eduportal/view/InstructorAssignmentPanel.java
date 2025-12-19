package com.eduportal.view;

import com.eduportal.dao.AssignmentDAO;
import com.eduportal.dao.CourseDAO;
import com.eduportal.model.Assignment;
import com.eduportal.model.Course;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList; // Used in dummy DAOs

/**
 * Panel for instructors to manage, create, and edit assignments for their courses.
 * Renamed from ManageAssignmentsPanel to avoid conflicts.
 * Workflow: Select Course -> View/Manage Assignments.
 */
public class InstructorAssignmentPanel extends JPanel { // RENAMED CLASS

    private final int instructorID;
    private CourseDAO courseDAO;
    private AssignmentDAO assignmentDAO;

    // UI Components
    private JComboBox<Course> courseComboBox;
    private JTable assignmentTable;
    private DefaultTableModel tableModel;
    private JButton createNewButton;
    private JButton editSelectedButton;

    // Column indices
    private static final int COL_ID = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_TYPE = 2;
    private static final int COL_DUE_DATE = 3;
    private static final int COL_MAX_SCORE = 4;
    
    // Date formatting helper
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public InstructorAssignmentPanel(int instructorID) {
        this.instructorID = instructorID;
        // Assume DAOs are initialized correctly and use the database
        // **USING DUMMY IMPLEMENTATIONS BELOW FOR RUNNABLE CODE**
        this.courseDAO = new CourseDAO(); 
        this.assignmentDAO = new AssignmentDAO();

        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        loadCourses();
    }

    private void initializeComponents() {
        // Dropdowns
        courseComboBox = new JComboBox<Course>();

        // Table Model
        String[] columnNames = {"ID", "Title", "Type", "Due Date", "Max Score"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false; // Assignments are edited via a dialog, not inline
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                 if (columnIndex == COL_ID || columnIndex == COL_MAX_SCORE) return Integer.class;
                 return super.getColumnClass(columnIndex);
            }
        };

        assignmentTable = new JTable(tableModel);
        // Hide the ID column
        assignmentTable.getColumnModel().getColumn(COL_ID).setMaxWidth(0);
        assignmentTable.getColumnModel().getColumn(COL_ID).setMinWidth(0);
        assignmentTable.getColumnModel().getColumn(COL_ID).setPreferredWidth(0);
        
        createNewButton = new JButton("Create New Assignment");
        editSelectedButton = new JButton("Edit Selected Assignment");
        editSelectedButton.setEnabled(false);
    }

    private void layoutComponents() {
        // --- Selection Panel (North) ---
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.add(new JLabel("Select Course:"));
        selectionPanel.add(courseComboBox);

        // --- Action Panel (South) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(createNewButton);
        actionPanel.add(editSelectedButton);

        add(selectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(assignmentTable), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        // 1. Course ComboBox Listener
        courseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("comboBoxChanged")) {
                    loadAssignments();
                }
            }
        });
        
        // 2. Table Selection Listener (Converted from Lambda)
        assignmentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Enable/disable Edit button based on row selection
                if (!e.getValueIsAdjusting()) {
                    editSelectedButton.setEnabled(assignmentTable.getSelectedRow() != -1);
                }
            }
        });
        
        // 3. Create New Button Listener (Converted from Lambda)
        createNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAssignmentDialog(null);
            }
        });

        // 4. Edit Selected Button Listener (Converted from Lambda)
        editSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = assignmentTable.getSelectedRow();
                if (selectedRow != -1) {
                    int assignmentId = (int) tableModel.getValueAt(selectedRow, COL_ID);
                    Assignment assignment = assignmentDAO.getById(assignmentId);
                    showAssignmentDialog(assignment);
                }
            }
        });
    }

    // --- Data Loading ---

    private void loadCourses() {
        courseComboBox.removeAllItems();
        try {
            List<Course> courses = courseDAO.getCoursesByInstructor(instructorID);
            for (Course course : courses) {
                courseComboBox.addItem(course);
            }
            if (!courses.isEmpty()) {
                loadAssignments();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAssignments() {
        tableModel.setRowCount(0);
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        
        if (selectedCourse == null) return;
        
        try {
            List<Assignment> assignments = assignmentDAO.getAssignmentsByCourse(selectedCourse.getCourseID());
            
            for (Assignment assignment : assignments) {
                String dueDateStr = (assignment.getDueDate() != null) ? DATE_FORMAT.format(assignment.getDueDate()) : "N/A";
                
                tableModel.addRow(new Object[]{
                    assignment.getAssignmentID(),
                    assignment.getTitle(),
                    assignment.getType(),
                    dueDateStr,
                    assignment.getMaxScore()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading assignments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- Assignment Management Dialog ---

    private void showAssignmentDialog(final Assignment assignmentToEdit) {
        final Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        
        // FIX: Verify a course is selected before even opening the dialog
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course from the dropdown first.", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), 
            assignmentToEdit == null ? "Create New Assignment" : "Edit Assignment", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 320); // Slightly increased height

        final JTextField titleField = new JTextField(20);
        final JTextField typeField = new JTextField(20);
        final JTextField dueDateField = new JTextField(20); 
        final JTextField maxScoreField = new JTextField(20);
        JButton saveButton = new JButton("Save Assignment");
        
        // Styling the button for visibility
        saveButton.setBackground(new Color(66, 133, 244));
        saveButton.setForeground(Color.WHITE);

        if (assignmentToEdit != null) {
            titleField.setText(assignmentToEdit.getTitle());
            typeField.setText(assignmentToEdit.getType());
            if (assignmentToEdit.getDueDate() != null) {
                dueDateField.setText(DATE_FORMAT.format(assignmentToEdit.getDueDate()));
            }
            maxScoreField.setText(String.valueOf(assignmentToEdit.getMaxScore()));
        }

        dialog.add(new JLabel(" Course:"));
        dialog.add(new JLabel(selectedCourse.getAttribute1())); // Show course name as read-only
        dialog.add(new JLabel(" Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel(" Type (Homework/Exam):"));
        dialog.add(typeField);
        dialog.add(new JLabel(" Due Date (YYYY-MM-DD):"));
        dialog.add(dueDateField);
        dialog.add(new JLabel(" Max Score:"));
        dialog.add(maxScoreField);
        dialog.add(new JLabel("")); 
        dialog.add(saveButton);
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String title = titleField.getText().trim();
                    String type = typeField.getText().trim();
                    String dateInput = dueDateField.getText().trim();
                    String scoreInput = maxScoreField.getText().trim();

                    // Validation Logic
                    if (title.isEmpty() || type.isEmpty() || dateInput.isEmpty() || scoreInput.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Date dueDate = DATE_FORMAT.parse(dateInput);
                    int maxScore = Integer.parseInt(scoreInput);

                    boolean success;
                    if (assignmentToEdit == null) {
                        // FIX: Ensure we use the ID from selectedCourse
                        Assignment newAssign = new Assignment(0, selectedCourse.getCourseID(), title, type, dueDate, maxScore, new Date());
                        success = assignmentDAO.insert(newAssign);
                    } else {
                        assignmentToEdit.setTitle(title);
                        assignmentToEdit.setType(type);
                        assignmentToEdit.setDueDate(dueDate);
                        assignmentToEdit.setMaxScore(maxScore);
                        success = assignmentDAO.update(assignmentToEdit);
                    }

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Assignment Saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadAssignments();
                    } else {
                        // This is the error from your screenshot
                        JOptionPane.showMessageDialog(dialog, "Database Error: Ensure the Course ID exists and fields are correct.", "Save Failed", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Date must be in YYYY-MM-DD format.", "Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Max Score must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // --- Main method for testing ---
    public static void main(String[] args) {
        // Anonymous Inner Class for SwingUtilities.invokeLater
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Instructor Assignment Management Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 500);
                // Use a dummy instructor ID
                frame.add(new InstructorAssignmentPanel(101)); 
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}