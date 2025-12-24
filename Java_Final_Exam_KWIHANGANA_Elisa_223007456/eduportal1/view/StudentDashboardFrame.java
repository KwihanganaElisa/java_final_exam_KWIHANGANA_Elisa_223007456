package com.eduportal.view;

import com.eduportal.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * GUI for the Student Dashboard.
 * FULL RUNNING CODE - No lambdas used.
 */
public class StudentDashboardFrame extends JFrame {

    private Student loggedInStudent;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // View Panels
    private StudentProfilePanel profilePanel;
    private StudentCourseViewerPanel courseViewerPanel;
    private StudentEnrollmentPanel enrollmentPanel;
    private StudentGradesPanel gradesPanel;
    
    // UI Constants
    private static final Color PRIMARY_COLOR = new Color(0, 150, 136); // Teal

    public StudentDashboardFrame(Student student) {
        super("Student Dashboard - " + student.getUsername());
        this.loggedInStudent = student;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750); 
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // Build UI Components
        setJMenuBar(createMenuBar());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.WEST);

        initializeContentPanels();
        add(mainContentPanel, BorderLayout.CENTER);

        // Show default view
        cardLayout.show(mainContentPanel, "PROFILE");
    }

    private void initializeContentPanels() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create instances of sub-panels passing student context
        profilePanel = new StudentProfilePanel(loggedInStudent);
        courseViewerPanel = new StudentCourseViewerPanel(loggedInStudent.getStudentID());
        enrollmentPanel = new StudentEnrollmentPanel(loggedInStudent.getStudentID());
        gradesPanel = new StudentGradesPanel(loggedInStudent.getStudentID());

        // Add to CardLayout
        mainContentPanel.add(profilePanel, "PROFILE");
        mainContentPanel.add(courseViewerPanel, "COURSES");
        mainContentPanel.add(enrollmentPanel, "ENROLLMENT");
        mainContentPanel.add(gradesPanel, "GRADES");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        
        // Traditional ActionListener for menu item
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        return menuBar;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", "Logout", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            // Redirect back to login
            new LoginFrame().setVisible(true);
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInStudent.getName() + "!", SwingConstants.LEFT);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        // Traditional ActionListener for button
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        panel.add(logoutBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adding Navigation Links
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
        
        // Traditional ActionListener for navigation switching
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContentPanel, cardName);
                
                // Logic to refresh data when the user navigates to specific tabs
                if (cardName.equals("COURSES")) {
                    courseViewerPanel.refreshData();
                } else if (cardName.equals("GRADES")) {
                    gradesPanel.refreshData();
                } else if (cardName.equals("ENROLLMENT")) {
                    enrollmentPanel.refreshData();
                }
            }
        });

        panel.add(button);
        panel.add(Box.createVerticalStrut(10));
    }

    /**
     * Main method to launch the frame standalone for testing.
     */
    public static void main(String[] args) {
        // Create mock student for testing
        final Student mockStudent = new Student(
            1, "student1", "password123", "Student", 
            "John Doe", "john@edu.com", "Computer Science", new Date()
        );

        // Traditional Runnable for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StudentDashboardFrame(mockStudent).setVisible(true);
            }
        });
    }
}