package com.eduportal.view;

import com.eduportal.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * GUI for the Student Dashboard, displayed after successful login.
 * UPDATED: Includes a Menu Bar using Anonymous Inner Classes (No Lambdas).
 */
public class StudentDashboardFrame extends JFrame {

    private Student loggedInStudent;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    private StudentProfilePanel profilePanel;
    private StudentCourseViewerPanel courseViewerPanel;
    private StudentEnrollmentPanel enrollmentPanel;
    private StudentGradesPanel gradesPanel;
    
    private static final Color PRIMARY_COLOR = new Color(0, 150, 136); // Teal
    private static final Color ACCENT_COLOR = new Color(255, 193, 7); // Amber

    public StudentDashboardFrame(Student student) {
        super("Student Dashboard - " + student.getUsername());
        this.loggedInStudent = student;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750); 
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // --- ADDED: Initialize Menu Bar ---
        setJMenuBar(createMenuBar());

        // 1. Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Navigation Panel (Sidebar)
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.WEST);

        // 3. Content Panel (CardLayout)
        initializeContentPanels();
        add(mainContentPanel, BorderLayout.CENTER);

        cardLayout.show(mainContentPanel, "PROFILE");
        setVisible(true);
    }

    /**
     * Creates the Menu Bar with File, Edit, and Help menus using Anonymous Inner Classes.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 1. File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 2. Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem profileItem = new JMenuItem("Update Profile");
        
        profileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContentPanel, "PROFILE");
            }
        });
        editMenu.add(profileItem);

        // 3. Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About Portal");
        
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(StudentDashboardFrame.this, 
                    "Education Portal System v1.0\nStudent Management Module", 
                    "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            // new LoginFrame().setVisible(true); 
        }
    }
    
    private void initializeContentPanels() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        profilePanel = new StudentProfilePanel(loggedInStudent);
        courseViewerPanel = new StudentCourseViewerPanel(loggedInStudent.getStudentID());
        enrollmentPanel = new StudentEnrollmentPanel(loggedInStudent.getStudentID());
        gradesPanel = new StudentGradesPanel(loggedInStudent.getStudentID());

        mainContentPanel.add(profilePanel, "PROFILE");
        mainContentPanel.add(courseViewerPanel, "COURSES");
        mainContentPanel.add(enrollmentPanel, "ENROLLMENT");
        mainContentPanel.add(gradesPanel, "GRADES");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInStudent.getName() + "!", SwingConstants.LEFT);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(ACCENT_COLOR);
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        panel.add(logoutButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(230, 230, 230));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addNavLink(panel, "My Profile", "PROFILE");
        addNavLink(panel, "View Courses", "COURSES");
        addNavLink(panel, "Enrollment", "ENROLLMENT");
        addNavLink(panel, "Grades & Results", "GRADES");

        return panel;
    }

    private void addNavLink(JPanel panel, String title, final String cardName) {
        JButton button = new JButton(title);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setMinimumSize(new Dimension(200, 40));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContentPanel, cardName);
                System.out.println("Switched view to: " + cardName);
            }
        });

        panel.add(button);
        panel.add(Box.createVerticalStrut(10));
    }

    public static void main(String[] args) {
        final Student mockStudent = new Student(
            300, "john_doe", "hashedpass", "Student", 
            "John Doe", "johndoe@email.com", "CS Major", new Date()
        );

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StudentDashboardFrame(mockStudent);
            }
        });
    }
}