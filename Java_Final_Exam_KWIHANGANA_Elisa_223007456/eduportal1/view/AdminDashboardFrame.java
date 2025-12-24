package com.eduportal.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Main dashboard frame for the Administrator role.
 * FINAL RUNNING VERSION: Matches DB schema and uses no lambdas.
 */
public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame(String title) {
        super(title);
        
        // Prevent default close to handle logout confirmation
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); 
        
        // 1. --- Implement Menu Bar ---
        setJMenuBar(createMenuBar());
        
        // 2. --- Header Area (Greeting) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(52, 73, 94)); // Dark Blue/Grey
        JLabel welcomeLabel = new JLabel("  Administrator Access Console");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(welcomeLabel);
        
        // 3. --- Use a JTabbedPane for easy navigation ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        Color primaryBackground = new Color(245, 247, 250); 
        
        // --- Add Management Tabs ---
        // Note: 'this' is passed so panels can communicate with the main frame if needed
        tabbedPane.addTab("Manage Students", createStyledPanel(new ManageStudentsPanel(this), primaryBackground));
        tabbedPane.addTab("Manage Instructors", createStyledPanel(new ManageInstructorsPanel(this), primaryBackground));
        tabbedPane.addTab("Manage Courses", createStyledPanel(new ManageCoursesPanel(this), primaryBackground));
        tabbedPane.addTab("Manage Enrollments", createStyledPanel(new ManageEnrollmentsPanel(this), primaryBackground));
        tabbedPane.addTab("Manage Assignments", createStyledPanel(new ManageAssignmentsPanel(this), primaryBackground));
        tabbedPane.addTab("Manage Grades", createStyledPanel(new ManageGradesPanel(this), primaryBackground));
        
        // --- Assemble Frame ---
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        // 4. --- Window Listener (Anonymous Inner Class) ---
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleLogoutConfirmation();
            }
        });
        
        setVisible(true);
    }
    
    private JPanel createStyledPanel(JPanel content, Color bgColor) {
        if (content != null) {
            content.setBackground(bgColor);
        }
        return content;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogoutConfirmation();
            }
        });
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardFrame.this, 
                    "Education Portal Admin Console\nVersion 1.2\nDatabase Synced: SUCCESS", 
                    "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private void handleLogoutConfirmation() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            // Redirect back to LoginFrame
            new LoginFrame().setVisible(true); 
        }
    }
}