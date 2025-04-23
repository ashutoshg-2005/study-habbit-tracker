package com.studytracker.ui;

import com.studytracker.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Panel for displaying motivational quotes to inspire study habits
 */
public class MotivationQuotePanel extends JPanel {
    private final List<String> quotes;
    private final Random random = new Random();
    private JLabel quoteLabel;
    private JLabel authorLabel;
    private Timer quoteTimer;
    
    public MotivationQuotePanel() {
        // Initialize quotes collection
        quotes = initializeQuotes();
        
        // Set up panel layout
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Create decorative quote image on the left
        JLabel quoteImageLabel = new JLabel("❝");
        quoteImageLabel.setFont(new Font("Arial", Font.BOLD, 70));
        quoteImageLabel.setForeground(UIConstants.PRIMARY_COLOR);
        quoteImageLabel.setVerticalAlignment(JLabel.TOP);
        quoteImageLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        // Create panel for quote text and author
        JPanel quotePanel = new JPanel(new BorderLayout(5, 15));
        quotePanel.setOpaque(false);
        
        // Create quote display
        quoteLabel = new JLabel();
        quoteLabel.setFont(new Font("Georgia", Font.ITALIC, 20));
        quoteLabel.setForeground(new Color(60, 60, 60));
        
        // Create author display
        authorLabel = new JLabel();
        authorLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        authorLabel.setForeground(new Color(100, 100, 100));
        authorLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        // Add components to quote panel
        quotePanel.add(quoteLabel, BorderLayout.CENTER);
        quotePanel.add(authorLabel, BorderLayout.SOUTH);
        
        // Create panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        
        // New quote button
        JButton newQuoteButton = new JButton("New Quote");
        newQuoteButton.setFocusPainted(false);
        newQuoteButton.addActionListener(e -> displayRandomQuote());
        
        // Auto refresh toggle
        JCheckBox autoRefreshCheckbox = new JCheckBox("Auto-refresh quotes");
        autoRefreshCheckbox.setOpaque(false);
        autoRefreshCheckbox.addActionListener(e -> toggleAutoRefresh(autoRefreshCheckbox.isSelected()));
        
        controlPanel.add(autoRefreshCheckbox);
        controlPanel.add(newQuoteButton);
        
        // Add all components to main panel
        add(quoteImageLabel, BorderLayout.WEST);
        add(quotePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Display initial random quote
        displayRandomQuote();
    }
    
    private List<String> initializeQuotes() {
        List<String> quoteList = new ArrayList<>();
        
        // Format: "Quote text|Author name"
        quoteList.add("The secret of getting ahead is getting started.|Mark Twain");
        quoteList.add("Education is not the filling of a pot but the lighting of a fire.|W.B. Yeats");
        quoteList.add("The beautiful thing about learning is that no one can take it away from you.|B.B. King");
        quoteList.add("Learning is not attained by chance, it must be sought for with ardor and attended to with diligence.|Abigail Adams");
        quoteList.add("The more that you read, the more things you will know. The more that you learn, the more places you'll go.|Dr. Seuss");
        quoteList.add("Education is the passport to the future, for tomorrow belongs to those who prepare for it today.|Malcolm X");
        quoteList.add("The expert in anything was once a beginner.|Helen Hayes");
        quoteList.add("The only person who is educated is the one who has learned how to learn and change.|Carl Rogers");
        quoteList.add("Genius is 1% inspiration and 99% perspiration.|Thomas Edison");
        quoteList.add("Study hard what interests you the most in the most undisciplined, irreverent and original manner possible.|Richard Feynman");
        quoteList.add("Learn from yesterday, live for today, hope for tomorrow.|Albert Einstein");
        quoteList.add("The mind is not a vessel to be filled, but a fire to be kindled.|Plutarch");
        quoteList.add("Education is the most powerful weapon which you can use to change the world.|Nelson Mandela");
        quoteList.add("The difference between ordinary and extraordinary is that little extra.|Jimmy Johnson");
        quoteList.add("Success is no accident. It is hard work, perseverance, learning, studying, sacrifice and most of all, love of what you are doing.|Pelé");
        quoteList.add("The roots of education are bitter, but the fruit is sweet.|Aristotle");
        quoteList.add("The capacity to learn is a gift; the ability to learn is a skill; the willingness to learn is a choice.|Brian Herbert");
        quoteList.add("I am still learning.|Michelangelo (at age 87)");
        quoteList.add("Never let formal education get in the way of your learning.|Mark Twain");
        quoteList.add("Live as if you were to die tomorrow. Learn as if you were to live forever.|Mahatma Gandhi");
        
        return quoteList;
    }
    
    private void displayRandomQuote() {
        if (quotes.isEmpty()) {
            quoteLabel.setText("No quotes available.");
            authorLabel.setText("");
            return;
        }
        
        String randomQuote = quotes.get(random.nextInt(quotes.size()));
        String[] parts = randomQuote.split("\\|");
        
        if (parts.length >= 2) {
            quoteLabel.setText("<html><div style='width: 400px;'>" + parts[0] + "</div></html>");
            authorLabel.setText("— " + parts[1]);
        } else {
            quoteLabel.setText("<html><div style='width: 400px;'>" + parts[0] + "</div></html>");
            authorLabel.setText("— Unknown");
        }
    }
    
    private void toggleAutoRefresh(boolean enabled) {
        if (enabled) {
            // Create timer to refresh quote every 30 seconds
            if (quoteTimer == null) {
                quoteTimer = new Timer(30000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        displayRandomQuote();
                    }
                });
                quoteTimer.start();
            }
        } else {
            // Stop timer
            if (quoteTimer != null) {
                quoteTimer.stop();
                quoteTimer = null;
            }
        }
    }
    
    /**
     * Public method to refresh data
     */
    public void refreshData() {
        // Display a new quote when the tab is activated
        displayRandomQuote();
    }
    
    /**
     * Clean up resources used by this panel
     */
    public void cleanup() {
        if (quoteTimer != null) {
            quoteTimer.stop();
            quoteTimer = null;
        }
    }
}