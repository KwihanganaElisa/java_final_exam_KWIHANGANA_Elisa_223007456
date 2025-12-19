package com.eduportal.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Main dashboard frame for the Administrator role.
 * UPDATED: 
 * 1. Correctly initializes ManageGradesPanel.
 * 2. Adds aesthetic colors for better visual separation.
 * 3. Integrates a Menu Bar (File, Edit, Help).
 * 4. ***REMOVED ALL LAMBDA EXPRESSIONS*** using Anonymous Inner Classes.
 * 5. ***FIXED COMPILATION ERROR*** by moving createStyledPanel to be a valid method.
 */
public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame(String title) {
        super(title);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // Center the frame
        
        // 1. --- Implement Menu Bar ---
        setJMenuBar(createMenuBar());
        
        // 2. --- Use a JTabbedPane for easy navigation ---
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Set colors for tabs and components
        Color primaryBackground = new Color(240, 248, 255); // Alice Blue
        
        tabbedPane.setBackground(primaryBackground);
        
        // --- Add Management Tabs (Fully Implemented Panels) ---
        
        // 1. Manage Students
        tabbedPane.addTab("Manage Students", createStyledPanel(new ManageStudentsPanel(this), primaryBackground));
        
        // 2. Manage Instructors
        tabbedPane.addTab("Manage Instructors", createStyledPanel(new ManageInstructorsPanel(this), primaryBackground));
        
        // 3. Manage Courses
        tabbedPane.addTab("Manage Courses", createStyledPanel(new ManageCoursesPanel(this), primaryBackground));

        // 4. Manage Enrollments
        tabbedPane.addTab("Manage Enrollments", createStyledPanel(new ManageEnrollmentsPanel(this), primaryBackground));

        // 5. Manage Assignments
        tabbedPane.addTab("Manage Assignments", createStyledPanel(new ManageAssignmentsPanel(this), primaryBackground));

        // 6. Manage Grades (FIXED)
        tabbedPane.addTab("Manage Grades", createStyledPanel(new ManageGradesPanel(this), primaryBackground));
        
        // --- Assemble Frame ---
        
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        
        // 3. --- Add a logout confirmation on close (Anonymous Inner Class) ---
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleLogoutConfirmation();
            }
        });
        
        setVisible(true);
    }
    
    /**
     * Helper method to wrap a content panel and apply background color.
     * FIX: Moved outside the constructor.
     */
    private JPanel createStyledPanel(JPanel content, Color bgColor) {
        content.setBackground(bgColor);
        return content;
    }

    /**
     * Creates and configures the application menu bar.
     * @return JMenuBar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // --- File Menu ---
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        // Replaced Lambda with Anonymous Inner Class
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogoutConfirmation();
            }
        });
        
        JMenuItem exitItem = new JMenuItem("Exit");
        // Replaced Lambda with Anonymous Inner Class
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // --- Edit Menu (Placeholder) ---
        JMenu editMenu = new JMenu("Edit");
        JMenuItem preferencesItem = new JMenuItem("Preferences");
        // Replaced Lambda with Anonymous Inner Class
        preferencesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardFrame.this, "Edit Preferences feature not yet implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        editMenu.add(preferencesItem);

        // --- Help Menu ---
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        // Replaced Lambda with Anonymous Inner Class
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardFrame.this, 
                    "Education Portal Admin Dashboard\nVersion 1.0\nDeveloped by the Educational Tech Team", 
                    "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Handles the dialog for logout confirmation.
     */
    private void handleLogoutConfirmation() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to logout and exit?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            // Assumes a LoginFrame class exists to return to the login screen
            new LoginFrame().setVisible(true); 
        }
    }
}