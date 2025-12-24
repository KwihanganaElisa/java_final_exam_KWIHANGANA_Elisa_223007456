package com.eduportal.view;

import com.eduportal.dao.CourseDAO;
import com.eduportal.model.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CourseRegistrationPanel extends JPanel {
    private int studentID;
    private CourseDAO courseDAO;
    private JTable catalogTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public CourseRegistrationPanel(int studentID) {
        this.studentID = studentID;
        this.courseDAO = new CourseDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Search Bar ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search Catalog");
        
        // Search Action (No Lambda)
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        topPanel.add(new JLabel("Find Course:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        // --- Catalog Table ---
        String[] cols = {"ID", "Code", "Name", "Instructor", "Credits"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        catalogTable = new JTable(tableModel);
        add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        // --- Enrollment Button ---
        JButton enrollBtn = new JButton("Enroll in Selected Course");
        enrollBtn.setPreferredSize(new Dimension(200, 40));
        enrollBtn.setBackground(new Color(66, 133, 244));
        enrollBtn.setForeground(Color.WHITE);

        // Enroll Action (No Lambda)
        enrollBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEnrollment();
            }
        });

        add(enrollBtn, BorderLayout.SOUTH);
        
        performSearch(); // Load all by default
    }

    private void performSearch() {
        String query = searchField.getText();
        List<Course> results = courseDAO.searchCourses(query);
        tableModel.setRowCount(0);
        for (Course c : results) {
            tableModel.addRow(new Object[]{
                c.getCourseID(), c.getCourseCode(), c.getCourseName(), 
                c.getInstructorName(), c.getCredits()
            });
        }
    }

    private void handleEnrollment() {
        int row = catalogTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course first.");
            return;
        }

        int courseID = (int) tableModel.getValueAt(row, 0);
        if (courseDAO.enrollStudentInCourse(studentID, courseID)) {
            JOptionPane.showMessageDialog(this, "Successfully enrolled!");
        } else {
            JOptionPane.showMessageDialog(this, "Enrollment failed. You might already be registered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}