package com.eduportal.main;

import com.eduportal.dao.DatabaseConnector;
import com.eduportal.util.DataInitializer; // Import the new seeder
import com.eduportal.view.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The main application starter class for the Education Portal System.
 * UPDATED: Performs a full database reset and data seeding on startup.
 */
public class AppStarter {

    public static void main(String[] args) {
        
        // 1. Rebuild and Seed Database
        // This ensures the new columns (full_name, credits) exist before the UI opens.
        System.out.println("Initializing System Components...");
        DataInitializer.seedData();

        // 2. Set the Look and Feel for a modern look (Nimbus).
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

        // 3. Launch the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 4. Instantiate the Login frame and make it visible
                LoginFrame login = new LoginFrame();
                login.setLocationRelativeTo(null); // Centers the window on screen
                login.setVisible(true);
                
                System.out.println("System Ready. Login with 'student1' / 'password123'");
            }
        });
    }
}