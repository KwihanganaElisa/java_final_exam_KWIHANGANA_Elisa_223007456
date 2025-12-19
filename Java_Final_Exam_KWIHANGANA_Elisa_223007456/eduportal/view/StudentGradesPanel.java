package com.eduportal.view;

import com.eduportal.model.Grade;
import com.eduportal.dao.GradeDAO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel to display the student's academic transcript and GPA.
 */
public class StudentGradesPanel extends JPanel {

    private int studentID;
    private GradeDAO gradeDAO;
    private JLabel gpaLabel;

    public StudentGradesPanel(int studentID) {
        this.studentID = studentID;
        // NOTE: GradeDAO must be implemented/mocked next
        this.gradeDAO = new GradeDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 1. Title and GPA Header (North)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // 2. Grades Table (Center)
        List<Grade> grades = gradeDAO.getGradesByStudentID(studentID);
        GradesTableModel tableModel = new GradesTableModel(grades);
        JTable gradesTable = new JTable(tableModel);
        
        gradesTable.setRowHeight(25);
        gradesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        gradesTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Calculate and display GPA
        calculateAndSetGPA(grades);
        
        setBorder(BorderFactory.createTitledBorder("Academic Performance and Transcripts"));
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("Academic Results (ID: " + studentID + ")", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.WEST);
        
        gpaLabel = new JLabel("Cumulative GPA: N/A", SwingConstants.RIGHT);
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gpaLabel.setForeground(new Color(0, 150, 136)); // Teal
        panel.add(gpaLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void calculateAndSetGPA(List<Grade> grades) {
        if (grades.isEmpty()) {
            gpaLabel.setText("Cumulative GPA: 0.00");
            return;
        }

        double totalQualityPoints = 0;
        int totalCredits = 0;
        
        // No Lambdas/Streams
        for (Grade grade : grades) {
            totalQualityPoints += grade.getGpaPoints() * grade.getCredits();
            totalCredits += grade.getCredits();
        }
        
        if (totalCredits == 0) {
            gpaLabel.setText("Cumulative GPA: 0.00");
            return;
        }
        
        double cumulativeGPA = totalQualityPoints / totalCredits;
        
        gpaLabel.setText(String.format("Cumulative GPA: %.2f (Credits: %d)", cumulativeGPA, Integer.valueOf(totalCredits)));
        
        // Set color based on performance (Mock)
        if (cumulativeGPA >= 3.5) {
            gpaLabel.setForeground(new Color(0, 150, 136)); // High GPA (Teal)
        } else if (cumulativeGPA < 2.0) {
            gpaLabel.setForeground(new Color(255, 99, 71)); // Low GPA (Red)
        }
    }

    // --- Inner Class for Grades Table Model ---
    private class GradesTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {"Semester", "Code", "Course Name", "Credits", "Score (%)", "Letter Grade", "GPA Points"};
        private List<Grade> gradeList;

        public GradesTableModel(List<Grade> gradeList) {
            this.gradeList = gradeList;
        }

        @Override public int getRowCount() { return gradeList.size(); }
        @Override public int getColumnCount() { return COLUMN_NAMES.length; }
        @Override public String getColumnName(int columnIndex) { return COLUMN_NAMES[columnIndex]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Grade grade = gradeList.get(rowIndex);
            switch (columnIndex) {
                case 0: return grade.getSemester();
                case 1: return grade.getCourseCode();
                case 2: return grade.getCourseName();
                case 3: return Integer.valueOf(grade.getCredits());
                case 4: return String.format("%.1f", Double.valueOf(grade.getFinalScore()));
                case 5: return grade.getFinalLetter();
                case 6: return String.format("%.1f", Double.valueOf(grade.getGpaPoints()));
                default: return null;
            }
        }
        
        // Ensure column 3 (Credits), 4 (Score), and 6 (GPA Points) are treated as numbers/doubles
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 3) return Integer.class;
            if (columnIndex == 4 || columnIndex == 6) return Double.class;
            return String.class;
        }
    }
}