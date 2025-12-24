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
import java.sql.Timestamp;

public class InstructorAssignmentPanel extends JPanel {

    private final int instructorID;
    private CourseDAO courseDAO;
    private AssignmentDAO assignmentDAO;

    private JComboBox<Course> courseComboBox;
    private JTable assignmentTable;
    private DefaultTableModel tableModel;
    private JButton createNewButton;
    private JButton editSelectedButton;

    private static final int COL_ID = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_DUE_DATE = 2;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public InstructorAssignmentPanel(int instructorID) {
        this.instructorID = instructorID;
        this.courseDAO = new CourseDAO(); 
        this.assignmentDAO = new AssignmentDAO();

        setLayout(new BorderLayout(10, 10));
        initializeComponents();
        layoutComponents();
        setupListeners();
        loadCourses();
    }

    private void initializeComponents() {
        courseComboBox = new JComboBox<Course>();

        String[] columnNames = {"ID", "Assignment Title", "Due Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        assignmentTable = new JTable(tableModel);
        assignmentTable.getColumnModel().getColumn(COL_ID).setMaxWidth(0);
        assignmentTable.getColumnModel().getColumn(COL_ID).setMinWidth(0);
        assignmentTable.getColumnModel().getColumn(COL_ID).setPreferredWidth(0);
        
        createNewButton = new JButton("Add Assignment");
        editSelectedButton = new JButton("Modify Selected");
        editSelectedButton.setEnabled(false);
    }

    private void layoutComponents() {
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.add(new JLabel("Your Courses:"));
        selectionPanel.add(courseComboBox);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(createNewButton);
        actionPanel.add(editSelectedButton);

        add(selectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(assignmentTable), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        // --- Course Selection Listener ---
        courseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAssignments();
            }
        });

        // --- Table Selection Listener ---
        assignmentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    editSelectedButton.setEnabled(assignmentTable.getSelectedRow() != -1);
                }
            }
        });

        // --- Create Button Listener ---
        createNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAssignmentDialog(null);
            }
        });

        // --- Edit Button Listener ---
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

    private void loadCourses() {
        courseComboBox.removeAllItems();
        try {
            List<Course> courses = courseDAO.getCoursesByInstructor(instructorID);
            for (Course c : courses) {
                courseComboBox.addItem(c);
            }
            if (!courses.isEmpty()) {
                loadAssignments();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadAssignments() {
        tableModel.setRowCount(0);
        Course selected = (Course) courseComboBox.getSelectedItem();
        if (selected == null) return;
        
        try {
            List<Assignment> assignments = assignmentDAO.getAssignmentsByCourse(selected.getCourseID());
            for (Assignment a : assignments) {
                String date = (a.getDueDate() != null) ? DATE_FORMAT.format(a.getDueDate()) : "N/A";
                tableModel.addRow(new Object[]{ a.getAssignmentID(), a.getTitle(), date });
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
    }

    private void showAssignmentDialog(final Assignment toEdit) {
        final Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null) return;

        final JDialog dialog = new JDialog((Frame)null, toEdit == null ? "New Assignment" : "Edit", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(350, 200);

        final JTextField titleField = new JTextField(toEdit != null ? toEdit.getTitle() : "");
        final JTextField dateField = new JTextField(toEdit != null && toEdit.getDueDate() != null ? 
                                              DATE_FORMAT.format(toEdit.getDueDate()) : DATE_FORMAT.format(new Date()));
        JButton saveBtn = new JButton("Save to Database");

        dialog.add(new JLabel(" Course:"));
        dialog.add(new JLabel(selectedCourse.toString()));
        dialog.add(new JLabel(" Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel(" Due Date (YYYY-MM-DD):"));
        dialog.add(dateField);
        dialog.add(new JLabel(""));
        dialog.add(saveBtn);

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String title = titleField.getText().trim();
                    Date dueDate = DATE_FORMAT.parse(dateField.getText().trim());

                    boolean success;
                    if (toEdit == null) {
                        // Creating a new assignment using the CourseID from the selection
                        Assignment a = new Assignment(0, selectedCourse.getCourseID(), title, "General", 100, "", "", dueDate, "Active", "", new Timestamp(System.currentTimeMillis()));
                        success = assignmentDAO.insert(a);
                    } else {
                        toEdit.setTitle(title);
                        toEdit.setDueDate(dueDate);
                        success = assignmentDAO.update(toEdit);
                    }

                    if (success) {
                        dialog.dispose();
                        loadAssignments();
                        JOptionPane.showMessageDialog(InstructorAssignmentPanel.this, "Assignment Saved Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to save assignment to database.");
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}