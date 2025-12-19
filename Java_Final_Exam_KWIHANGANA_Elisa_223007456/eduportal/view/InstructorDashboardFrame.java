package com.eduportal.view;

import com.eduportal.model.Instructor; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * Main frame for the Instructor Dashboard.
 * UPDATED: Added Menu Bar with File, Edit, and Help menus using Anonymous Inner Classes.
 */
public class InstructorDashboardFrame extends JFrame {
    
    private final Instructor loggedInInstructor; 
    private final int instructorID; 
    private final String instructorName;
    
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    private ViewCoursesInstructorPanel viewCoursesPanel;
    private GradeStudentsPanel gradeStudentsPanel; 
    private InstructorAssignmentPanel assignmentPanel; 
    private ManageRosterPanel rosterPanel; 
            
 // REFACTORED CONSTRUCTOR: Fixed the 'loggedInStudent' error
    public InstructorDashboardFrame(Instructor instructor) {
        // 1. Call parent constructor with the instructor's name
        super("Instructor Dashboard - " + instructor.getName());
        
        // 2. Assign the Instructor object to the class field
        this.loggedInInstructor = instructor;
        
        // 3. Extract IDs for easier use in panels
        this.instructorID = instructor.getInstructorID();
        this.instructorName = instructor.getName();

        // 4. Configure Frame Settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); 
        setLocationRelativeTo(null);
        
        // 5. Initialize Menu Bar (Added as requested previously)
        setJMenuBar(createMenuBar());
        
        // 6. Build the UI
        initializeComponents();
        layoutComponents();
        
        // 7. Show the first module
        cardLayout.show(mainContentPanel, "COURSES");
        setVisible(true);
    }

    /**
     * Creates the Menu Bar for the Instructor Portal.
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
        JMenuItem refreshItem = new JMenuItem("Refresh Dashboard");
        refreshItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContentPanel, "COURSES");
                JOptionPane.showMessageDialog(InstructorDashboardFrame.this, "Dashboard Refreshed.");
            }
        });
        editMenu.add(refreshItem);

        // 3. Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About Instructor Portal");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(InstructorDashboardFrame.this, 
                    "Instructor Management Module v1.0\nEducational Portal System", 
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
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout, " + instructorName + "?", 
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            // new LoginFrame().setVisible(true);
        }
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        
        viewCoursesPanel = new ViewCoursesInstructorPanel(instructorID); 
        gradeStudentsPanel = new GradeStudentsPanel(instructorID);
        assignmentPanel = new InstructorAssignmentPanel(instructorID); 
        rosterPanel = new ManageRosterPanel(instructorID);            
        
        mainContentPanel.add(viewCoursesPanel, "COURSES");
        mainContentPanel.add(assignmentPanel, "ASSIGNMENTS"); 
        mainContentPanel.add(gradeStudentsPanel, "GRADING");
        mainContentPanel.add(rosterPanel, "ROSTER"); 
    }
    
    private void layoutComponents() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(44, 62, 80)); 
        navPanel.setPreferredSize(new Dimension(220, 800)); 

        JLabel header = new JLabel("<html><b style='color:white;'>" + instructorName + "<br>PORTAL</b></html>", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton coursesButton = createNavButton("1. View Assigned Courses", "COURSES");
        JButton assignmentsButton = createNavButton("2. Manage Assignments", "ASSIGNMENTS");
        JButton gradingButton = createNavButton("3. Grade Management", "GRADING");
        JButton rosterButton = createNavButton("4. Manage Roster/Students", "ROSTER"); 
        JButton logoutButton = createNavButton("Logout", "LOGOUT"); 
        
        navPanel.add(header);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(coursesButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(assignmentsButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(gradingButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(rosterButton); 
        
        navPanel.add(Box.createVerticalGlue()); 
        navPanel.add(logoutButton);
        navPanel.add(Box.createVerticalStrut(20));

        add(navPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private JButton createNavButton(String text, final String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setMinimumSize(new Dimension(200, 45));
        button.setBackground(new Color(52, 73, 94)); 
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cardName.equals("LOGOUT")) {
                    handleLogout();
                } else {
                    cardLayout.show(mainContentPanel, cardName);
                }
            }
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Instructor mockInstructor = new Instructor(
                    101, "prof_alice", "pass123", "Instructor", "Professor Alice", 
                    "CS Dept.", "Active", "Office 404", "alice@edu.com", new Date()
                ) {
                    public int getInstructorID() { return 101; }
                    public String getName() { return "Professor Alice"; }
                };
                new InstructorDashboardFrame(mockInstructor);
            }
        });
    }
}