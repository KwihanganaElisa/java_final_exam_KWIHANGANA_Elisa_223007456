package com.eduportal.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.BorderLayout;

/**
 * The main window/dashboard of the application after successful login.
 */
public class MainFrame extends JFrame {

    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 700;

    public MainFrame(String title) {
        super(title);
        // Set basic frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        
        // Add a temporary welcome label to the dashboard
        JLabel welcomeLabel = new JLabel("Welcome to the " + title.split(" - ")[0] + "!", SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null); 
    }
}