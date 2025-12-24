package com.eduportal.view;

import com.eduportal.model.Grade;
import com.eduportal.dao.GradeDAO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class StudentGradesPanel extends JPanel {

    private int studentID;
    private GradeDAO gradeDAO;
    private JLabel gpaLabel;
    private JTable gradesTable; 

    public StudentGradesPanel(int studentID) {
        this.studentID = studentID;
        this.gradeDAO = new GradeDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 1. Title and GPA Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // 2. Grades Table
        List<Grade> grades = gradeDAO.getGradesByStudentID(studentID);
        GradesTableModel tableModel = new GradesTableModel(grades);
        gradesTable = new JTable(tableModel);
        
        gradesTable.setRowHeight(30);
        gradesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        gradesTable.getTableHeader().setReorderingAllowed(false);
        
        // Center-align the numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 3; i <= 6; i++) {
            gradesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Initialize GPA
        calculateAndSetGPA(grades);
        
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Academic Performance and Transcripts"));
    }
    
    public void refreshData() {
        List<Grade> freshGrades = gradeDAO.getGradesByStudentID(this.studentID);
        
        // Update the existing model instead of creating a new one to keep selection/scroll state
        GradesTableModel model = (GradesTableModel) gradesTable.getModel();
        model.updateGrades(freshGrades); // Add this method to your inner class
        
        // Re-apply centering
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 3; i < gradesTable.getColumnCount(); i++) {
            gradesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        calculateAndSetGPA(freshGrades);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("Academic Results (Student ID: " + studentID + ")", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.WEST);
        
        gpaLabel = new JLabel("Cumulative GPA: 0.00", SwingConstants.RIGHT);
        gpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gpaLabel.setForeground(new Color(0, 150, 136)); // Teal
        panel.add(gpaLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void calculateAndSetGPA(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            gpaLabel.setText("Cumulative GPA: 0.00");
            return;
        }

        double totalQualityPoints = 0;
        int totalCredits = 0;
        
        for (Grade grade : grades) {
            totalQualityPoints += (grade.getGpaPoints() * grade.getCredits());
            totalCredits += grade.getCredits();
        }
        
        if (totalCredits == 0) {
            gpaLabel.setText("Cumulative GPA: 0.00");
            return;
        }
        
        double cumulativeGPA = totalQualityPoints / totalCredits;
        gpaLabel.setText(String.format("Cumulative GPA: %.2f (Total Credits: %d)", cumulativeGPA, totalCredits));
        
        // Color coding
        if (cumulativeGPA >= 3.5) gpaLabel.setForeground(new Color(0, 150, 136));
        else if (cumulativeGPA < 2.0) gpaLabel.setForeground(Color.RED);
        else gpaLabel.setForeground(Color.DARK_GRAY);
    }

    private class GradesTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {"Semester", "Code", "Course Name", "Credits", "Score (%)", "Letter Grade", "GPA Points"};
        private List<Grade> gradeList;

        public GradesTableModel(List<Grade> gradeList) {
            this.gradeList = gradeList;
        }

        @Override public int getRowCount() { return gradeList != null ? gradeList.size() : 0; }
        @Override public int getColumnCount() { return COLUMN_NAMES.length; }
        @Override public String getColumnName(int col) { return COLUMN_NAMES[col]; }
        public void updateGrades(List<Grade> newGrades) {
            this.gradeList = newGrades;
            fireTableDataChanged();
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Grade grade = gradeList.get(rowIndex);
            switch (columnIndex) {
                case 0: return grade.getSemester();
                case 1: return grade.getCourseCode();
                case 2: return grade.getCourseName();
                case 3: return grade.getCredits();
                case 4: return String.format("%.1f", grade.getFinalScore());
                case 5: return grade.getFinalLetter(); 
                case 6: return String.format("%.1f", grade.getGpaPoints());
                default: return null;
            }
        }
    }
}