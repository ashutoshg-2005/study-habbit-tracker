package com.studytracker.ui;

import com.studytracker.model.DatabaseManager;
import com.studytracker.model.User;
import com.studytracker.util.UIConstants;
import com.studytracker.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application window with tabbed interface for the study habit tracker
 */
public class MainWindow {
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private final DatabaseManager dbManager;
    private final User currentUser;
    
    // Tab panels
    private StudyLogPanel logPanel;
    private StudyGoalsPanel goalsPanel;
    private StatisticsPanel statsPanel;
    private PomodoroPanel pomodoroPanel;
    private StudyCalendarPanel calendarPanel;
    private ChatbotPanel chatbotPanel;
    private MotivationQuotePanel motivationPanel;
    
    public MainWindow(DatabaseManager dbManager, User currentUser) {
        this.dbManager = dbManager;
        this.currentUser = currentUser;
        initialize();
    }
    
    private void initialize() {
        // Create and set up the main window
        mainFrame = new JFrame("Study Habit Tracker - " + currentUser.getFullName());
        mainFrame.setSize(950, 700);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().setBackground(UIConstants.LIGHT_BG);
        
        // Create header panel with welcome message and user info
        JPanel headerPanel = createHeaderPanel();
        mainFrame.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane with custom styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_MEDIUM));
        tabbedPane.setForeground(UIConstants.PRIMARY_COLOR);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING_MEDIUM,
            UIConstants.PADDING_MEDIUM,
            UIConstants.PADDING_MEDIUM,
            UIConstants.PADDING_MEDIUM
        ));
        
        // Create tabs
        createTabs();
        
        // Add tabbed pane to frame
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        
        // Create status bar
        JPanel statusBar = createStatusBar();
        mainFrame.add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(
            UIConstants.PADDING_MEDIUM, 
            UIConstants.PADDING_MEDIUM,
            UIConstants.PADDING_MEDIUM,
            UIConstants.PADDING_MEDIUM
        ));
        
        JLabel titleLabel = new JLabel("Study Habit Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, UIConstants.FONT_MEDIUM));
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = UIUtils.createStyledButton("Logout", Color.WHITE, UIConstants.WARNING_COLOR);
        logoutButton.addActionListener(e -> logout());
        
        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(20));
        userPanel.add(logoutButton);
        
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void createTabs() {
        // Create Study Log tab
        logPanel = new StudyLogPanel(dbManager, currentUser);
        tabbedPane.addTab("Study Log", logPanel);
        
        // Create Study Goals tab
        goalsPanel = new StudyGoalsPanel(dbManager, currentUser);
        tabbedPane.addTab("Study Goals", goalsPanel);
        
        // Create Statistics tab
        statsPanel = new StatisticsPanel(dbManager, currentUser);
        tabbedPane.addTab("Statistics", statsPanel);

        // Create Pomodoro Timer tab
        pomodoroPanel = new PomodoroPanel();
        tabbedPane.addTab("Pomodoro Timer", pomodoroPanel);

        // Create Study Calendar tab
        calendarPanel = new StudyCalendarPanel(currentUser.getId(), dbManager);
        tabbedPane.addTab("Study Calendar", calendarPanel);

        
        
        // Create Motivation Quotes tab
        motivationPanel = new MotivationQuotePanel();
        tabbedPane.addTab("Motivation", motivationPanel);
        
        // Create Chatbot Assistant tab
        chatbotPanel = new ChatbotPanel(dbManager, currentUser);
        tabbedPane.addTab("Study Assistant", chatbotPanel);
        
        // Add change listener to refresh data when switching tabs
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 0) {
                logPanel.refreshData();
            } else if (selectedIndex == 1) {
                goalsPanel.refreshData();
            } else if (selectedIndex == 2) {
                statsPanel.refreshData();
            } else if (selectedIndex == 4) {
                calendarPanel.refreshData();
            } else if (selectedIndex == 5) {
                motivationPanel.refreshData();
            } else if (selectedIndex == 6) {
                chatbotPanel.refreshData();
            }
        });
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UIConstants.PRIMARY_COLOR);
        statusBar.setPreferredSize(new Dimension(mainFrame.getWidth(), 25));
        
        JLabel statusLabel = new JLabel("  Ready");
        statusLabel.setForeground(Color.WHITE);
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel versionLabel = new JLabel("Study Habit Tracker v1.0  ");
        versionLabel.setForeground(Color.WHITE);
        statusBar.add(versionLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private void logout() {
        // Close the current window
        mainFrame.dispose();
        
        // Clean up resources
        if (chatbotPanel != null) {
            chatbotPanel.cleanup();
        }
        if (motivationPanel != null) {
            motivationPanel.cleanup();
        }
        dbManager.close();
        
        // Start a new instance of the application
        // This will show the login dialog again
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.studytracker.StudyTrackerApp").getConstructor().newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    /**
     * Shows the main window
     */
    public void show() {
        mainFrame.pack(); // Ensure components are properly sized before displaying
        mainFrame.setSize(950, 700); // Set the preferred size
        mainFrame.setLocationRelativeTo(null); // Center on screen
        mainFrame.setVisible(true);
    }
}