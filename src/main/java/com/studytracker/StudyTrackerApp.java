package com.studytracker;

import com.studytracker.ui.LoginDialog;
import com.studytracker.ui.MainWindow;
import com.studytracker.model.DatabaseManager;
import com.studytracker.model.User;

import javax.swing.*;

public class StudyTrackerApp {
    private DatabaseManager dbManager;
    private User currentUser;

    public StudyTrackerApp() {
        dbManager = new DatabaseManager();
        initialize();
    }

    private void initialize() {
        // Show login dialog first
        LoginDialog loginDialog = new LoginDialog(dbManager);
        currentUser = loginDialog.showLoginDialog();

        if (currentUser == null) {
            System.exit(0);
        }

        // Launch the main application window
        MainWindow mainWindow = new MainWindow(dbManager, currentUser);
        mainWindow.show();
    }

    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the application
        SwingUtilities.invokeLater(StudyTrackerApp::new);
    }
}