package com.eduportal.main;

import com.eduportal.dao.DatabaseConnector;
import com.eduportal.view.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The main application starter class for the Education Portal System.
 * It initializes the Look and Feel, CREATES DATABASE TABLES, and launches the GUI.
 */
public class AppStarter {

    public static void main(String[] args) {
        
        // 1. Initialize Database Tables before starting the application
        DatabaseConnector.getInstance().createTablesIfNotExist();

        // 2. Set the Look and Feel for a modern look (e.g., Nimbus).
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Could not set any custom LookAndFeel.");
            }
        }

        // 3. Ensure GUI code runs on the Event Dispatch Thread (EDT) for thread safety.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 4. Instantiate the Login frame and make it visible
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            }
        });
    }
}