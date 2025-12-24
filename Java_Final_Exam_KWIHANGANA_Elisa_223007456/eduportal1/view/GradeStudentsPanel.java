package com.eduportal.view;

import com.eduportal.dao.*;
import com.eduportal.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Iterator; 
import java.util.ArrayList; // Needed for mock/dummy lists if DAOs are fully mocked

/**
 * Panel for instructors to manage grades for students in their courses.
 * Workflow: Select Course -> Select Assignment -> View/Edit Grades.
 * NOTE: Assumes existence of Course, Assignment, Student, Grade models 
 * and corresponding DAO methods: getCoursesByInstructor, getAssignmentsByCourse,
 * getStudentsByCourse, and getGradesByAssignment.
 */
public class GradeStudentsPanel extends JPanel {

    private final int instructorID;
    private CourseDAO courseDAO;
    private AssignmentDAO assignmentDAO;
    private StudentDAO studentDAO;
    private GradeDAO gradeDAO;

    // UI Components
    private JComboBox<Course> courseComboBox;
    private JComboBox<Assignment> assignmentComboBox;
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JButton saveButton;
    
    private List<Student> currentStudents; // Students enrolled in the selected course
    private Assignment selectedAssignment;

    // Column indices for easy access in table model
    private static final int COL_STUDENT_ID = 0;
    private static final int COL_STUDENT_NAME = 1;
    private static final int COL_GRADE_ID = 2; // GradeID (Hidden/Internal)
    private static final int COL_SCORE = 3;
    private static final int COL_LETTER_GRADE = 4;

    public GradeStudentsPanel(int instructorID) {
        this.instructorID = instructorID;
        // NOTE: These DAO constructors must be defined
        this.courseDAO = new CourseDAO();
        this.assignmentDAO = new AssignmentDAO();
        this.studentDAO = new StudentDAO();
        this.gradeDAO = new GradeDAO();

        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        
        loadCourses();
    }

    private void initializeComponents() {
        // Dropdowns
        courseComboBox = new JComboBox<Course>();
        assignmentComboBox = new JComboBox<Assignment>();
        assignmentComboBox.setEnabled(false); // Disable until course is selected

        // Table Model: Editable Score and Letter Grade
        String[] columnNames = {"Student ID", "Student Name", "Grade ID (Internal)", "Score", "Letter Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int intcolumn) {
               // Only Score (3) and Letter Grade (4) are editable
               return intcolumn == COL_SCORE || intcolumn == COL_LETTER_GRADE;
            }
            // Ensure Score column uses Double type for correct input handling
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == COL_SCORE) return Double.class;
                return super.getColumnClass(columnIndex);
            }
        };

        gradeTable = new JTable(tableModel);
        // This is necessary if you hide the column after the table is created
        if (gradeTable.getColumnModel().getColumnCount() > COL_GRADE_ID) {
            gradeTable.removeColumn(gradeTable.getColumnModel().getColumn(COL_GRADE_ID)); // Hide Grade ID
        }
        
        saveButton = new JButton("Save Grades");
        saveButton.setEnabled(false);
        saveButton.setBackground(new Color(52, 168, 83)); 
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
    }

    private void layoutComponents() {
        // --- Selection Panel (North) ---
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.add(new JLabel("Course:"));
        selectionPanel.add(courseComboBox);
        selectionPanel.add(new JLabel("Assignment:"));
        selectionPanel.add(assignmentComboBox);

        // --- Bottom Panel (South) ---
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(saveButton);

        add(selectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(gradeTable), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        // Course Combo Box Listener
        courseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ignore programmatic selection changes during setup
                if (e.getActionCommand().equals("comboBoxChanged")) {
                    loadAssignments();
                }
            }
        });
        
        // Assignment Combo Box Listener
        assignmentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ignore programmatic selection changes during setup
                 if (e.getActionCommand().equals("comboBoxChanged")) {
                    loadGrades();
                }
            }
        });
        
        // Save Button Listener
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGrades();
            }
        });
    }

    // --- Data Loading Logic ---

    private void loadCourses() {
        courseComboBox.removeAllItems();
        try {
            // FIX: getCoursesByInstructor must be implemented in CourseDAO
            List<Course> courses = courseDAO.getCoursesByInstructor(instructorID);
            for (Course course : courses) {
                courseComboBox.addItem(course);
            }
        } catch (Exception ex) {
            // Added explicit error handling for DAO exceptions
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        courseComboBox.setSelectedIndex(-1);
    }

    private void loadAssignments() {
        assignmentComboBox.removeAllItems();
        assignmentComboBox.setEnabled(false);
        selectedAssignment = null;
        tableModel.setRowCount(0);
        saveButton.setEnabled(false);
        
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null) return;
        
        // FIX: getStudentsByCourse must be implemented in StudentDAO
        currentStudents = studentDAO.getStudentsByCourse(selectedCourse.getCourseID());
        
        try {
            // FIX: getAssignmentsByCourse must be implemented in AssignmentDAO
            List<Assignment> assignments = assignmentDAO.getAssignmentsByCourse(selectedCourse.getCourseID());
            for (Assignment assignment : assignments) {
                assignmentComboBox.addItem(assignment);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading assignments.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        assignmentComboBox.setEnabled(assignmentComboBox.getItemCount() > 0);
        assignmentComboBox.setSelectedIndex(-1);
    }

    private void loadGrades() {
        tableModel.setRowCount(0);
        saveButton.setEnabled(false);
        
        selectedAssignment = (Assignment) assignmentComboBox.getSelectedItem();
        if (selectedAssignment == null || currentStudents == null || currentStudents.isEmpty()) return;
        
        saveButton.setEnabled(true);
        
        // FIX: getGradesByAssignment must be implemented in GradeDAO
        List<Grade> existingGrades = gradeDAO.getGradesByAssignment(selectedAssignment.getAssignmentID());

        for (Student student : currentStudents) {
            Grade grade = findExistingGrade(existingGrades, student.getStudentID());

            Object[] row = new Object[5];
            row[COL_STUDENT_ID] = student.getStudentID();
            // FIX: Assumes student.getName() is implemented (proxies attribute1)
            row[COL_STUDENT_NAME] = student.getName(); 

            if (grade != null) {
                row[COL_GRADE_ID] = grade.getGradeID();
                row[COL_SCORE] = grade.getScore();
                row[COL_LETTER_GRADE] = grade.getLetterGrade();
            } else {
                row[COL_GRADE_ID] = -1; 
                row[COL_SCORE] = 0.0;
                row[COL_LETTER_GRADE] = "N/A";
            }
            tableModel.addRow(row);
        }
    }
    
    /** Helper function to replace stream/lambda for finding a grade */
    private Grade findExistingGrade(List<Grade> grades, int studentID) {
        for (Grade grade : grades) {
            if (grade.getStudentID() == studentID) {
                return grade;
            }
        }
        return null;
    }

    // --- Saving Logic ---

    private void saveGrades() {
        if (selectedAssignment == null) return;

        boolean overallSuccess = true;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int studentID = (int) tableModel.getValueAt(i, COL_STUDENT_ID);
            int gradeID = (int) tableModel.getValueAt(i, COL_GRADE_ID);
            Object scoreObject = tableModel.getValueAt(i, COL_SCORE);
            String letterGrade = (String) tableModel.getValueAt(i, COL_LETTER_GRADE);
            
            Double score = null;
            if (scoreObject instanceof Double) {
                score = (Double) scoreObject;
            } else {
                // Robust parsing for user input
                try {
                    score = Double.parseDouble(scoreObject != null ? scoreObject.toString() : null);
                } catch (NumberFormatException ex) {
                    // Score remains null, handled below
                } catch (NullPointerException ex) {
                    // Score remains null, handled below
                }
            }
            
            if (score == null || letterGrade == null || letterGrade.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Score and Letter Grade cannot be empty or invalid for all students.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                overallSuccess = false;
                break;
            }

            Grade gradeToSave;
            boolean success;
            
            if (gradeID == -1) {
                // INSERT operation
                // gradeType is hardcoded to "Instructor Grade" for simplicity
                gradeToSave = new Grade(0, studentID, selectedAssignment.getAssignmentID(), "Instructor Grade", score.doubleValue(), letterGrade, null);
                success = gradeDAO.insert(gradeToSave);
            } else {
                // UPDATE operation
                gradeToSave = gradeDAO.getById(gradeID);
                if (gradeToSave != null) {
                    gradeToSave.setScore(score.doubleValue());
                    gradeToSave.setLetterGrade(letterGrade);
                    success = gradeDAO.update(gradeToSave);
                } else {
                    success = false;
                    System.err.println("Error: Grade ID " + gradeID + " not found for update.");
                }
            }

            if (!success) {
                overallSuccess = false;
                System.err.println("Failed to save/update grade for Student ID: " + studentID);
            }
        }

        if (overallSuccess) {
            JOptionPane.showMessageDialog(this, "All grades saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Reload to refresh table data, including newly generated Grade IDs
            loadGrades(); 
        } else {
            JOptionPane.showMessageDialog(this, "Some grades failed to save. Check console for details.", "Partial Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}