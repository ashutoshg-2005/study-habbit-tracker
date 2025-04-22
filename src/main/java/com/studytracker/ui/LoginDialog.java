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
        loginDialog.setLayout(new BorderLayout());
        loginDialog.setSize(400, 350);
        loginDialog.setLocationRelativeTo(null);
        
        // Set dialog background color
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.LIGHT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Study Habit Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Add a small icon/logo if available
        try {
            Icon appIcon = UIUtils.createIcon("study", 32);
            JLabel iconLabel = new JLabel(appIcon);
            titlePanel.add(iconLabel, BorderLayout.WEST);
            titlePanel.add(Box.createHorizontalStrut(10), BorderLayout.CENTER);
            titlePanel.add(titleLabel, BorderLayout.EAST);
        } catch (Exception e) {
            titlePanel.add(titleLabel, BorderLayout.CENTER);
        }
        
        // Welcome message panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "Welcome to Study Habit Tracker!<br>" +
            "Please login or register to start tracking your study habits." +
            "</div></html>"
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomePanel.add(welcomeLabel);
        
        JTabbedPane loginTabs = new JTabbedPane();
        loginTabs.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add icons to tabs
        Icon loginIcon = UIUtils.createIcon("user", 16);
        Icon registerIcon = UIUtils.createIcon("add-user", 16);
        
        // Login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(usernameField.getPreferredSize().width, 30));
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(passwordField.getPreferredSize().width, 30));
        
        JButton loginButton = UIUtils.createStyledButton("Login", Color.WHITE, UIConstants.ACCENT_COLOR);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        loginPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 5, 5, 5);
        loginPanel.add(loginButton, gbc);
        
        // Register panel
        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints rgbc = new GridBagConstraints();
        rgbc.insets = new Insets(5, 5, 5, 5);
        rgbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField regUsernameField = new JTextField(20);
        regUsernameField.setPreferredSize(new Dimension(regUsernameField.getPreferredSize().width, 30));
        
        JPasswordField regPasswordField = new JPasswordField(20);
        regPasswordField.setPreferredSize(new Dimension(regPasswordField.getPreferredSize().width, 30));
        
        JTextField regEmailField = new JTextField(20);
        regEmailField.setPreferredSize(new Dimension(regEmailField.getPreferredSize().width, 30));
        
        JTextField regFullNameField = new JTextField(20);
        regFullNameField.setPreferredSize(new Dimension(regFullNameField.getPreferredSize().width, 30));
        
        JButton registerButton = UIUtils.createStyledButton("Register", Color.WHITE, UIConstants.ACCENT_COLOR);
        
        rgbc.gridx = 0;
        rgbc.gridy = 0;
        registerPanel.add(new JLabel("Username:"), rgbc);
        
        rgbc.gridx = 0;
        rgbc.gridy = 1;
        registerPanel.add(regUsernameField, rgbc);
        
        rgbc.gridx = 0;
        rgbc.gridy = 2;
        registerPanel.add(new JLabel("Password:"), rgbc);
        
        rgbc.gridx = 0;
        rgbc.gridy = 3;
        registerPanel.add(regPasswordField, rgbc);
        
        rgbc.gridx = 0;
        rgbc.gridy = 4;
        registerPanel.add(new JLabel("Email:"), rgbc);
        
        rgbc.gridx = 0;
        rgbc.gridy = 5;
        registerPanel.add(regEmailField, rgbc);
        
        rgbc.gridx = 0;
        rgbc.gridy = 6;
        registerPanel.add(new JLabel("Full Name:"), rgbc);
        
        rgbc.gridx = 0;
        rgbc.gridy = 7;
        registerPanel.add(regFullNameField, rgbc);
        
        rgbc.gridx = 0;
        rgbc.gridy = 8;
        rgbc.insets = new Insets(20, 5, 5, 5);
        registerPanel.add(registerButton, rgbc);
        
        loginTabs.addTab("Login", loginIcon, loginPanel);
        loginTabs.addTab("Register", registerIcon, registerPanel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(welcomePanel, BorderLayout.CENTER);
        mainPanel.add(loginTabs, BorderLayout.SOUTH);
        
        loginDialog.add(mainPanel);
        
        final boolean[] success = {false};
        
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                UIUtils.showError(loginDialog, "Please enter both username and password!");
                return;
            }
            
            if (dbManager.login(username, password)) {
                currentUser = dbManager.getCurrentUser();
                success[0] = true;
                loginDialog.dispose();
            } else {
                UIUtils.showError(loginDialog, "Invalid username or password!");
            }
        });
        
        registerButton.addActionListener(e -> {
            String username = regUsernameField.getText();
            String password = new String(regPasswordField.getPassword());
            String email = regEmailField.getText();
            String fullName = regFullNameField.getText();
            
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || fullName.isEmpty()) {
                UIUtils.showError(loginDialog, "All fields are required!");
                return;
            }
            
            User newUser = new User(username, password, email, fullName);
            
            if (dbManager.registerUser(newUser)) {
                UIUtils.showInfo(loginDialog, "Registration successful! Please login.");
                loginTabs.setSelectedIndex(0); // Switch to login tab
                
                // Clear registration fields
                regUsernameField.setText("");
                regPasswordField.setText("");
                regEmailField.setText("");
                regFullNameField.setText("");
            } else {
                UIUtils.showError(loginDialog, "Registration failed! Username or email might already exist.");
            }
        });
        
        loginDialog.setVisible(true);
        return currentUser;
    }
}