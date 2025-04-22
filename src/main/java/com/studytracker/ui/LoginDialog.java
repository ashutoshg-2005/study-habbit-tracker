package com.studytracker.ui;

import com.studytracker.model.DatabaseManager;
import com.studytracker.model.User;
import com.studytracker.util.UIConstants;
import com.studytracker.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for user login and registration
 */
public class LoginDialog {
    private final DatabaseManager dbManager;
    private User currentUser;
    
    public LoginDialog(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    /**
     * Shows the login dialog and returns the authenticated user
     * @return User object if login successful, null otherwise
     */
    public User showLoginDialog() {
        JDialog loginDialog = new JDialog((Frame)null, "Study Habit Tracker - Login", true);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginDialog.setLayout(new BorderLayout());
        
        // Set dialog background color
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIConstants.LIGHT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setBackground(UIConstants.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        
        JLabel titleLabel = new JLabel("Study Habit Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Welcome message panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        welcomePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 60));
        
        JLabel welcomeLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "Welcome to Study Habit Tracker!<br>" +
            "Please login or register to start tracking your study habits." +
            "</div></html>"
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomePanel.add(welcomeLabel);
        
        // Create the tabbed pane with login and register tabs
        JTabbedPane loginTabs = new JTabbedPane();
        loginTabs.setFont(new Font("Arial", Font.BOLD, 12));
        loginTabs.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Login panel
        JPanel loginPanel = createLoginPanel();
        
        // Register panel
        JPanel registerPanel = createRegisterPanel();
        
        loginTabs.addTab("Login", loginPanel);
        loginTabs.addTab("Register", registerPanel);
        
        // Add components to the main panel
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(welcomePanel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(loginTabs);
        
        loginDialog.add(mainPanel);
        
        // Pack the dialog to ensure proper sizing based on components
        loginDialog.pack();
        
        // Set minimum size to prevent components from being cut off
        loginDialog.setMinimumSize(new Dimension(350, 450));
        
        // Set the dialog's location to center of screen
        loginDialog.setLocationRelativeTo(null);
        
        // Make it visible
        loginDialog.setVisible(true);
        
        return currentUser;
    }
    
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Login button
        JButton loginButton = UIUtils.createStyledButton("Login", Color.WHITE, UIConstants.ACCENT_COLOR);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to panel with spacing
        loginPanel.add(usernameLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);
        
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                UIUtils.showError(loginButton, "Please enter both username and password!");
                return;
            }
            
            if (dbManager.login(username, password)) {
                currentUser = dbManager.getCurrentUser();
                Window window = SwingUtilities.getWindowAncestor(loginButton);
                window.dispose();
            } else {
                UIUtils.showError(loginButton, "Invalid username or password!");
            }
        });
        
        return loginPanel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField regUsernameField = new JTextField(20);
        regUsernameField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        regUsernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPasswordField regPasswordField = new JPasswordField(20);
        regPasswordField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        regPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField regEmailField = new JTextField(20);
        regEmailField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        regEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Full name field
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField regFullNameField = new JTextField(20);
        regFullNameField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        regFullNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Register button
        JButton registerButton = UIUtils.createStyledButton("Register", Color.WHITE, UIConstants.ACCENT_COLOR);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to panel with spacing
        registerPanel.add(usernameLabel);
        registerPanel.add(Box.createVerticalStrut(5));
        registerPanel.add(regUsernameField);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(passwordLabel);
        registerPanel.add(Box.createVerticalStrut(5));
        registerPanel.add(regPasswordField);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(emailLabel);
        registerPanel.add(Box.createVerticalStrut(5));
        registerPanel.add(regEmailField);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(fullNameLabel);
        registerPanel.add(Box.createVerticalStrut(5));
        registerPanel.add(regFullNameField);
        registerPanel.add(Box.createVerticalStrut(15));
        registerPanel.add(registerButton);
        
        registerButton.addActionListener(e -> {
            String username = regUsernameField.getText();
            String password = new String(regPasswordField.getPassword());
            String email = regEmailField.getText();
            String fullName = regFullNameField.getText();
            
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || fullName.isEmpty()) {
                UIUtils.showError(registerButton, "All fields are required!");
                return;
            }
            
            User newUser = new User(username, password, email, fullName);
            
            if (dbManager.registerUser(newUser)) {
                UIUtils.showInfo(registerButton, "Registration successful! Please login.");
                
                // Get the parent tabbed pane and switch to login tab
                Component comp = registerButton.getParent();
                while (comp != null && !(comp instanceof JTabbedPane)) {
                    comp = comp.getParent();
                }
                if (comp != null) {
                    ((JTabbedPane) comp).setSelectedIndex(0); // Switch to login tab
                }
                
                // Clear registration fields
                regUsernameField.setText("");
                regPasswordField.setText("");
                regEmailField.setText("");
                regFullNameField.setText("");
            } else {
                UIUtils.showError(registerButton, "Registration failed! Username or email might already exist.");
            }
        });
        
        return registerPanel;
    }
}