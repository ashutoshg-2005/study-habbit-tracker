package com.studytracker.ui;

import com.studytracker.model.DatabaseManager;
import com.studytracker.model.User;
import com.studytracker.util.UIConstants;
import com.studytracker.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Panel for chatbot interactions to help with study recommendations and questions
 */
public class ChatbotPanel extends JPanel {
    private final DatabaseManager dbManager;
    private final User currentUser;
    
    // UI Components
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton clearButton;
    private JPanel suggestionPanel;
    private JComboBox<String> apiKeySelector;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    // Styling
    private final String SYSTEM_STYLE = "system";
    private final String USER_STYLE = "user";
    private final String BOT_STYLE = "bot";
    private final String TIME_STYLE = "time";
    
    // API key will be loaded from environment or configuration
    private String apiKey = "";
    
    public ChatbotPanel(DatabaseManager dbManager, User currentUser) {
        this.dbManager = dbManager;
        this.currentUser = currentUser;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setupUI();
        
        // Welcome message
        appendToChatArea("Study Assistant Bot", "Hi " + currentUser.getFullName() + 
                "! I'm your study assistant. I can help you with study recommendations, " +
                "answer questions about the app, or provide study tips. How can I help you today?", BOT_STYLE);
        
        // Suggested questions
        displaySuggestedQuestions();
    }
    
    private void setupUI() {
        // Chat area with styled document
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(250, 250, 250));
        
        StyledDocument doc = chatArea.getStyledDocument();
        addStylesToDocument(doc);
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Message input area
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Send on Enter key
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        sendButton = UIUtils.createStyledButton("Send", Color.WHITE, UIConstants.PRIMARY_COLOR);
        sendButton.addActionListener(e -> sendMessage());
        
        clearButton = UIUtils.createStyledButton("Clear", Color.WHITE, UIConstants.ACCENT_COLOR);
        clearButton.addActionListener(e -> clearChat());
        
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Settings panel
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        settingsPanel.setOpaque(false);
        
        JLabel apiKeyLabel = new JLabel("API Key Source:");
        apiKeySelector = new JComboBox<>(new String[]{"From Environment", "From Configuration File"});
        apiKeySelector.addActionListener(e -> loadApiKey());
        
        settingsPanel.add(apiKeyLabel);
        settingsPanel.add(apiKeySelector);
        settingsPanel.add(clearButton);
        
        // Suggestion chips
        suggestionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        suggestionPanel.setBackground(new Color(245, 245, 245));
        
        // Main panel structure
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(suggestionPanel, BorderLayout.NORTH);
        southPanel.add(inputPanel, BorderLayout.CENTER);
        southPanel.add(settingsPanel, BorderLayout.SOUTH);
        
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        
        // Load API key on startup
        loadApiKey();
    }
    
    private void loadApiKey() {
        try {
            if (apiKeySelector.getSelectedIndex() == 0) {
                // From environment variable
                apiKey = System.getenv("GEMINI_API_KEY");
                if (apiKey == null || apiKey.isEmpty()) {
                    appendToChatArea("System", "Warning: GEMINI_API_KEY environment variable not found. " +
                            "Please set it or use a configuration file.", SYSTEM_STYLE);
                } else {
                    appendToChatArea("System", "API key loaded from environment variables.", SYSTEM_STYLE);
                }
            } else {
                // From config file
                File configFile = new File("config.properties");
                if (configFile.exists()) {
                    java.util.Properties props = new java.util.Properties();
                    try (FileInputStream fis = new FileInputStream(configFile)) {
                        props.load(fis);
                    }
                    apiKey = props.getProperty("gemini.api.key");
                    if (apiKey == null || apiKey.isEmpty()) {
                        appendToChatArea("System", "Warning: API key not found in configuration file.", SYSTEM_STYLE);
                    } else {
                        appendToChatArea("System", "API key loaded from configuration file.", SYSTEM_STYLE);
                    }
                } else {
                    appendToChatArea("System", "Warning: Configuration file not found. " +
                            "Create a config.properties file with gemini.api.key=YOUR_API_KEY.", SYSTEM_STYLE);
                }
            }
        } catch (Exception e) {
            appendToChatArea("System", "Error loading API key: " + e.getMessage(), SYSTEM_STYLE);
        }
    }
    
    private void addStylesToDocument(StyledDocument doc) {
        // Regular text style
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        // System message style (gray italics)
        Style systemStyle = doc.addStyle(SYSTEM_STYLE, defaultStyle);
        StyleConstants.setForeground(systemStyle, new Color(120, 120, 120));
        StyleConstants.setItalic(systemStyle, true);
        StyleConstants.setFontSize(systemStyle, 12);
        
        // User message style (blue)
        Style userStyle = doc.addStyle(USER_STYLE, defaultStyle);
        StyleConstants.setForeground(userStyle, new Color(25, 118, 210));
        StyleConstants.setBold(userStyle, true);
        StyleConstants.setFontSize(userStyle, 14);
        
        // Bot message style (dark gray)
        Style botStyle = doc.addStyle(BOT_STYLE, defaultStyle);
        StyleConstants.setForeground(botStyle, new Color(40, 40, 40));
        StyleConstants.setFontSize(botStyle, 14);
        
        // Time stamp style (light gray, small)
        Style timeStyle = doc.addStyle(TIME_STYLE, defaultStyle);
        StyleConstants.setForeground(timeStyle, new Color(150, 150, 150));
        StyleConstants.setItalic(timeStyle, true);
        StyleConstants.setFontSize(timeStyle, 10);
    }
    
    private void appendToChatArea(String sender, String message, String style) {
        StyledDocument doc = chatArea.getStyledDocument();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        try {
            // Add line breaks between messages if not first message
            if (doc.getLength() > 0) {
                doc.insertString(doc.getLength(), "\n\n", doc.getStyle(style));
            }
            
            // Add timestamp
            doc.insertString(doc.getLength(), "(" + timestamp + ") ", doc.getStyle(TIME_STYLE));
            
            // Add sender
            doc.insertString(doc.getLength(), sender + ": ", doc.getStyle(style));
            
            // Add message
            doc.insertString(doc.getLength(), message, doc.getStyle(style));
            
            // Scroll to bottom
            chatArea.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void sendMessage() {
        String userMessage = messageField.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }
        
        // Display user message
        appendToChatArea("You", userMessage, USER_STYLE);
        messageField.setText("");
        
        // Disable UI while processing
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        
        // Process asynchronously
        CompletableFuture.supplyAsync(() -> {
            try {
                return processUserMessage(userMessage);
            } catch (Exception e) {
                return "I'm having trouble connecting to my brain right now. " +
                       "Please check your API key and internet connection. Error: " + e.getMessage();
            }
        }, executorService).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                appendToChatArea("Study Assistant Bot", response, BOT_STYLE);
                messageField.setEnabled(true);
                sendButton.setEnabled(true);
                messageField.requestFocus();
            });
        });
    }
    
    private String processUserMessage(String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Please set up your Gemini API key first through environment variables " +
                   "or a configuration file to enable the full capabilities of the chatbot.";
        }
        
        try {
            StringBuilder context = new StringBuilder();
            
            // Add user context
            context.append("User: ").append(currentUser.getFullName()).append("\n");
            
            // Add study session context
            int totalSessions = dbManager.getStudyLogCount(currentUser.getId());
            double totalHours = dbManager.getTotalStudyHours(currentUser.getId());
            context.append("Study statistics: ")
                   .append(totalSessions).append(" sessions, ")
                   .append(String.format("%.1f", totalHours)).append(" total hours\n");
            
            // Add goal context if available
            context.append("Recent study goals: ");
            // Would add code here to get the user's active goals
            
            // Create the prompt with context
            String prompt = "As a study assistant chatbot in a Study Habit Tracker application, " +
                            "I need to respond to this user query. Here's the context about the user:\n\n" +
                            context.toString() + "\n\n" +
                            "User question: " + userMessage + "\n\n" +
                            "Please provide a helpful, concise response focused on improving study habits, " +
                            "answering questions about the application features, or providing study tips. " +
                            "Keep your response under 200 words.";
            
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "I encountered an error processing your request: " + e.getMessage();
        }
    }
    
    private String callGeminiAPI(String prompt) {
        try {
            // URL remains the same
            URL url = new URL("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            
            // Create request body
            String jsonInput = "{\"contents\":[{\"parts\":[{\"text\":\"" + 
                               prompt.replace("\"", "\\\"").replace("\n", "\\n") + 
                               "\"}]}]}";
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            // Get response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    
                    // Improved JSON response parsing
                    String jsonResponse = response.toString();
                    System.out.println("Raw API response: " + jsonResponse); // Debug log
                    
                    // Check for different JSON structure patterns
                    if (jsonResponse.contains("\"text\"")) {
                        // Extract text from response using more reliable method
                        int candidatesIdx = jsonResponse.indexOf("\"candidates\"");
                        if (candidatesIdx != -1) {
                            int partsIdx = jsonResponse.indexOf("\"parts\"", candidatesIdx);
                            if (partsIdx != -1) {
                                int textIdx = jsonResponse.indexOf("\"text\"", partsIdx);
                                if (textIdx != -1) {
                                    int valueStartIdx = jsonResponse.indexOf(":", textIdx) + 1;
                                    // Find the opening quote
                                    int contentStartIdx = jsonResponse.indexOf("\"", valueStartIdx) + 1;
                                    // Find the closing quote (accounting for escaped quotes)
                                    int contentEndIdx = contentStartIdx;
                                    boolean foundEnd = false;
                                    while (!foundEnd && contentEndIdx < jsonResponse.length()) {
                                        contentEndIdx = jsonResponse.indexOf("\"", contentEndIdx);
                                        // Check if this quote is escaped
                                        if (jsonResponse.charAt(contentEndIdx - 1) == '\\') {
                                            contentEndIdx++; // Move past this quote and continue
                                        } else {
                                            foundEnd = true; // Found the unescaped closing quote
                                        }
                                    }
                                    
                                    if (foundEnd) {
                                        String extractedText = jsonResponse.substring(contentStartIdx, contentEndIdx)
                                            .replace("\\n", "\n")
                                            .replace("\\\"", "\"")
                                            .replace("\\\\", "\\");
                                        return extractedText;
                                    }
                                }
                            }
                        }
                    }
                    
                    // Fallback approach if the structure isn't as expected
                    if (jsonResponse.contains("\"text\":")) {
                        String[] parts = jsonResponse.split("\"text\":");
                        if (parts.length >= 2) {
                            String textPart = parts[1].trim();
                            if (textPart.startsWith("\"")) {
                                int endQuote = textPart.indexOf("\"", 1);
                                if (endQuote > 1) {
                                    return textPart.substring(1, endQuote)
                                        .replace("\\n", "\n")
                                        .replace("\\\"", "\"");
                                }
                            }
                        }
                    }
                    
                    // If we still couldn't extract the text properly
                    return "I received a response but couldn't parse it properly. This may be due " +
                           "to a change in the API response format. Please try asking a different question.";
                }
            } else {
                // Handle error response (unchanged)
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return "API error: " + response.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error connecting to Gemini API: " + e.getMessage();
        }
    }
    
    private void clearChat() {
        StyledDocument doc = chatArea.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            
            // Re-add welcome message
            appendToChatArea("Study Assistant Bot", "Chat cleared! How can I help you with your studies today?", BOT_STYLE);
            displaySuggestedQuestions();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void displaySuggestedQuestions() {
        suggestionPanel.removeAll();
        
        String[] suggestions = {
            "Study tips for focus",
            "How to use Pomodoro timer?",
            "Analyze my study pattern",
            "Recommend study goals",
            "How many hours did I study this week?"
        };
        
        for (String suggestion : suggestions) {
            JButton chip = createSuggestionChip(suggestion);
            suggestionPanel.add(chip);
        }
        
        suggestionPanel.revalidate();
        suggestionPanel.repaint();
    }
    
    private JButton createSuggestionChip(String text) {
        JButton chip = new JButton(text);
        chip.setFont(new Font("Arial", Font.PLAIN, 12));
        chip.setBackground(new Color(240, 240, 240));
        chip.setBorderPainted(false);
        chip.setFocusPainted(false);
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chip.addActionListener(e -> {
            messageField.setText(text);
            sendMessage();
        });
        return chip;
    }
    
    /**
     * Cleanup resources when panel is closed
     */
    public void cleanup() {
        executorService.shutdown();
    }
    
    /**
     * Public method to refresh data
     */
    public void refreshData() {
        // Refresh any data from database if needed
    }
}