package com.studytracker.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for UI components
 */
public class UIUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Map<String, Icon> iconCache = new HashMap<>();
    
    /**
     * Creates or retrieves a cached icon
     */
    public static Icon createIcon(String name, int size) {
        String key = name + "_" + size;
        
        if (!iconCache.containsKey(key)) {
            // Create a fallback icon if the resource is not available
            Icon icon = createFallbackIcon(name, size);
            
            try {
                // Try to load from resources first
                String path = "/icons/" + name + ".png";
                ImageIcon imageIcon = new ImageIcon(UIUtils.class.getResource(path));
                
                if (imageIcon.getIconWidth() > 0) {
                    // Resize if needed
                    if (imageIcon.getIconWidth() != size || imageIcon.getIconHeight() != size) {
                        Image img = imageIcon.getImage().getScaledInstance(
                            size, size, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(img);
                    } else {
                        icon = imageIcon;
                    }
                }
            } catch (Exception e) {
                // Use fallback icon already created
            }
            
            iconCache.put(key, icon);
        }
        
        return iconCache.get(key);
    }
    
    /**
     * Creates a simple fallback icon when a resource is not available
     */
    private static Icon createFallbackIcon(String name, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Use a color based on the name to differentiate icons
                int hash = name.hashCode();
                Color color = new Color(
                    Math.abs(hash) % 200 + 55, 
                    Math.abs(hash / 256) % 200 + 55, 
                    Math.abs(hash / 65536) % 200 + 55
                );
                
                g2d.setColor(color);
                g2d.fillOval(x, y, size, size);
                
                // Add first character of name
                if (name.length() > 0) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, size * 2/3));
                    g2d.drawString(
                        name.substring(0, 1).toUpperCase(), 
                        x + size/4, 
                        y + size * 3/4
                    );
                }
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
    
    /**
     * Creates a styled button with custom colors and hover effect
     */
    public static JButton createStyledButton(String text, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Shows an error message dialog
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an information message dialog
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Formats a date for display
     */
    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }
    
    /**
     * Creates a date chooser component
     */
    public static JDateChooser createDateChooser(java.sql.Date initialDate) {
        return new JDateChooser(initialDate);
    }
    
    /**
     * Date chooser component
     */
    public static class JDateChooser extends JPanel {
        private JTextField dateField;
        private JButton dateButton;
        private java.sql.Date selectedDate;
        
        public JDateChooser(java.sql.Date initialDate) {
            setLayout(new BorderLayout());
            
            selectedDate = initialDate;
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            
            dateField = new JTextField(dateFormat.format(selectedDate));
            dateField.setEditable(false);
            
            dateButton = new JButton("📅");
            dateButton.addActionListener(e -> showDatePopup());
            
            add(dateField, BorderLayout.CENTER);
            add(dateButton, BorderLayout.EAST);
        }
        
        private void showDatePopup() {
            // In a real implementation, this would show a date picker
            // For now, we'll use a simple dialog
            String dateStr = JOptionPane.showInputDialog(
                this,
                "Enter date (YYYY-MM-DD):",
                dateField.getText()
            );
            
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(dateStr);
                    selectedDate = new java.sql.Date(parsedDate.getTime());
                    dateField.setText(dateFormat.format(selectedDate));
                } catch (Exception ex) {
                    showError(null, "Invalid date format. Please use YYYY-MM-DD.");
                }
            }
        }
        
        public java.sql.Date getDate() {
            return selectedDate;
        }
    }
}