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
        
        // --- 1. Search Panel (North) ---
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // --- 2. Table Panel (Center) ---
        initializeTable();
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        loadCourses(null); 
        
        setBorder(BorderFactory.createTitledBorder("Course Enrollment & Registration"));
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
        
        // Custom column widths
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Code
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(50);  // Credits
        courseTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Action (Button)

        // Add custom renderer and editor for the Enroll button column
        courseTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        courseTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JTextField(), this));
        
        // Center-align the Credits column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        courseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        courseTable.setRowHeight(30); // Increased slightly for better button fit
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseTable.setFillsViewportHeight(true);
    }
    
    private void loadCourses(String query) {
        List<Course> courses = courseDAO.searchCourses(query);
        tableModel.setCourseList(courses);
        tableModel.fireTableDataChanged();
    }
    
    public void performEnrollment(int rowIndex) {
        Course selectedCourse = tableModel.getCourse(rowIndex);
        
        // Validating current display status
        String status = (String) tableModel.getValueAt(rowIndex, 4);
        if ("Registered".equalsIgnoreCase(status) || "Pending".equalsIgnoreCase(status)) {
             JOptionPane.showMessageDialog(this, 
                 "You are already " + status + " for " + selectedCourse.getCourseCode(),
                 "Enrollment Error", JOptionPane.WARNING_MESSAGE);
             return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to enroll in " + selectedCourse.getCourseName() + "?", 
            "Confirm Enrollment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = courseDAO.enrollStudentInCourse(studentID, selectedCourse.getCourseID());
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    selectedCourse.getCourseCode() + " successfully added!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourses(searchField.getText().trim()); 
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Enrollment failed. Please check if you are already registered.", 
                    "Enrollment Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // --- Inner Class for Table Model ---
    private class EnrollmentTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {"Code", "Course Name", "Instructor", "Credits", "Status", "Action"};
        private List<Course> courseList;

        public EnrollmentTableModel(List<Course> courseList) {
            this.courseList = courseList;
        }
        
        public void setCourseList(List<Course> courseList) {
            this.courseList = courseList;
        }
        
        public Course getCourse(int rowIndex) {
            return courseList.get(rowIndex);
        }

        @Override public int getRowCount() { return courseList.size(); }
        @Override public int getColumnCount() { return COLUMN_NAMES.length; }
        @Override public String getColumnName(int columnIndex) { return COLUMN_NAMES[columnIndex]; }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 3) return Integer.class;
            if (columnIndex == 5) return JButton.class;
            return String.class;
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 5;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Course course = courseList.get(rowIndex);

            // Logic to handle empty data from DB
            String currentStatus = course.getEnrollmentStatus();
            if (currentStatus == null || currentStatus.isEmpty() || "Unknown".equalsIgnoreCase(currentStatus)) {
                currentStatus = "Open"; 
            }

            String instructor = course.getInstructorName();
            if (instructor == null || instructor.isEmpty() || "N/A".equals(instructor)) {
                instructor = "TBA";
            }

            switch (columnIndex) {
                case 0: return course.getCourseCode();
                case 1: return course.getCourseName();
                case 2: return instructor;
                case 3: return Integer.valueOf(course.getCredits());
                case 4: return currentStatus;
                case 5: 
                    if ("Registered".equalsIgnoreCase(currentStatus) || "Pending".equalsIgnoreCase(currentStatus)) {
                        return "N/A";
                    }
                    return "Enroll";
                default: return null;
            }
        }
    }
    
    // --- Inner Classes for Button Rendering/Editing ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorderPainted(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            String actionText = (value == null) ? "" : value.toString();
            setText(actionText);
            
            if ("Enroll".equals(actionText)) {
                setBackground(new Color(76, 175, 80)); // Green
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
        private boolean isPushed;
        private int clickedRow;
        private StudentEnrollmentPanel parentPanel;

        public ButtonEditor(JTextField textField, StudentEnrollmentPanel panel) {
            super(textField);
            this.parentPanel = panel;
            setClickCountToStart(1);
            
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ButtonEditor.this.fireEditingStopped(); 
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            clickedRow = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            
            if ("Enroll".equals(label)) {
                button.setBackground(new Color(76, 175, 80));
                button.setForeground(Color.WHITE);
                isPushed = true;
            } else {
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.DARK_GRAY);
                isPushed = false;
            }
            
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && "Enroll".equals(label)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        parentPanel.performEnrollment(clickedRow);
                    }
                });
            }
            isPushed = false;
            return label; 
        }
    }
}