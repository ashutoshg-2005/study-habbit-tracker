package com.studytracker.ui;

import com.studytracker.model.DatabaseManager;
import com.studytracker.model.User;
import com.studytracker.util.UIConstants;
import com.studytracker.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;

/**
 * Panel for visualizing study statistics
 */
public class StatisticsPanel extends JPanel {
    private final DatabaseManager dbManager;
    private final User currentUser;
    
    // UI components
    private JPanel chartPanel;
    private JComboBox<String> timeRangeCombo;
    private JComboBox<String> chartTypeCombo;
    private JPanel summaryPanel;
    
    // Animation variables
    private javax.swing.Timer animationTimer;
    private double animationProgress = 0.0;
    private boolean isAnimating = false;
    
    // Tooltip tracking
    private String currentTooltip = null;
    private Point tooltipPoint = new Point();
    
    public StatisticsPanel(DatabaseManager dbManager, User currentUser) {
        this.dbManager = dbManager;
        this.currentUser = currentUser;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING_MEDIUM, 
            UIConstants.PADDING_MEDIUM, 
            UIConstants.PADDING_MEDIUM, 
            UIConstants.PADDING_MEDIUM
        ));
        setBackground(Color.WHITE);
        
        setupUI();
        setupAnimation();
        refreshData();
    }
    
    private void setupUI() {
        // Controls panel
        JPanel controlsPanel = createControlsPanel();
        
        // Chart panel
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Study Time Distribution"));
        chartPanel.setBackground(Color.WHITE);
        
        // Summary panel
        summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Study Summary"));
        summaryPanel.setBackground(Color.WHITE);
        
        JPanel summaryContent = new JPanel(new GridLayout(2, 2, 20, 20));
        summaryContent.setBackground(Color.WHITE);
        summaryPanel.add(summaryContent, BorderLayout.CENTER);
        
        // Add panels to stats tab
        JPanel upperPanel = new JPanel(new BorderLayout());
        upperPanel.setBackground(Color.WHITE);
        upperPanel.add(controlsPanel, BorderLayout.NORTH);
        upperPanel.add(chartPanel, BorderLayout.CENTER);
        
        JSplitPane statsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, summaryPanel);
        statsSplitPane.setDividerLocation(400);
        statsSplitPane.setOneTouchExpandable(true);
        
        add(statsSplitPane, BorderLayout.CENTER);
    }
    
    private void setupAnimation() {
        // Create animation timer
        animationTimer = new javax.swing.Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (animationProgress < 1.0) {
                    animationProgress += 0.05;
                    chartPanel.repaint();
                } else {
                    animationProgress = 1.0;
                    isAnimating = false;
                    animationTimer.stop();
                }
            }
        });
    }
    
    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Statistics Options"));
        
        // Time range selector
        JLabel timeRangeLabel = new JLabel("Time Range:");
        timeRangeCombo = new JComboBox<>(new String[]{
            "This Week", "Last Week", "This Month", "Last Month", "Last 3 Months", "This Year"
        });
        
        // Chart type selector
        JLabel chartTypeLabel = new JLabel("Chart Type:");
        chartTypeCombo = new JComboBox<>(new String[]{
            "Bar Chart", "Pie Chart", "Line Chart"
        });
        
        // Update button
        JButton updateButton = UIUtils.createStyledButton("Update", Color.WHITE, UIConstants.ACCENT_COLOR);
        updateButton.addActionListener(e -> {
            startAnimation();
            refreshData();
        });
        
        controlsPanel.add(timeRangeLabel);
        controlsPanel.add(timeRangeCombo);
        controlsPanel.add(chartTypeLabel);
        controlsPanel.add(chartTypeCombo);
        controlsPanel.add(updateButton);
        
        return controlsPanel;
    }
    
    private void startAnimation() {
        animationProgress = 0.0;
        isAnimating = true;
        animationTimer.restart();
    }
    
    /**
     * Refresh the statistics data and visualizations
     */
    public void refreshData() {
        // Get the selected time range and chart type
        String timeRange = (String) timeRangeCombo.getSelectedItem();
        String chartType = (String) chartTypeCombo.getSelectedItem();
        
        // Calculate date range
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        
        switch (timeRange) {
            case "This Week":
                startDate = endDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
                break;
            case "Last Week":
                startDate = endDate.minusWeeks(1).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
                endDate = endDate.minusWeeks(1).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7);
                break;
            case "This Month":
                startDate = endDate.withDayOfMonth(1);
                break;
            case "Last Month":
                startDate = endDate.minusMonths(1).withDayOfMonth(1);
                endDate = endDate.withDayOfMonth(1).minusDays(1);
                break;
            case "Last 3 Months":
                startDate = endDate.minusMonths(3);
                break;
            case "This Year":
                startDate = endDate.withDayOfYear(1);
                break;
            default:
                startDate = endDate.minusWeeks(1);
        }
        
        // Get statistics for the date range
        List<Map<String, Object>> statistics = dbManager.getStudySummary(
            Date.valueOf(startDate), 
            Date.valueOf(endDate)
        );
        
        // Create chart visualization
        JPanel chart = createChart(statistics, chartType);
        
        // Clear and update chart panel
        chartPanel.removeAll();
        chartPanel.add(chart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
        
        // Update summary panel
        updateSummaryPanel(statistics);
    }
    
    private JPanel createChart(List<Map<String, Object>> statistics, String chartType) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        if (statistics.isEmpty()) {
            JLabel noDataLabel = new JLabel("No study data available for the selected period", JLabel.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_MEDIUM));
            panel.add(noDataLabel, BorderLayout.CENTER);
            return panel;
        }
        
        // Create a simple visualization of the data
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int padding = 40;
                
                // Draw chart based on type
                if (chartType.equals("Bar Chart")) {
                    drawBarChart(g2d, statistics, width, height, padding);
                } else if (chartType.equals("Pie Chart")) {
                    drawPieChart(g2d, statistics, width, height);
                } else if (chartType.equals("Line Chart")) {
                    drawLineChart(g2d, statistics, width, height, padding);
                }
                
                // Draw tooltip if active
                if (currentTooltip != null) {
                    drawTooltip(g2d, currentTooltip, tooltipPoint.x, tooltipPoint.y);
                }
            }
        };
        
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setBackground(Color.WHITE);
        
        // Add mouse listeners for tooltips
        chartPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Get tooltip data based on position and chart type
                String tooltip = getTooltipAt(e.getPoint(), statistics, chartType, 
                                            chartPanel.getWidth(), chartPanel.getHeight());
                
                if (tooltip != null) {
                    currentTooltip = tooltip;
                    tooltipPoint.setLocation(e.getPoint());
                    chartPanel.repaint();
                } else if (currentTooltip != null) {
                    currentTooltip = null;
                    chartPanel.repaint();
                }
            }
        });
        
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                currentTooltip = null;
                chartPanel.repaint();
            }
        });
        
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String getTooltipAt(Point point, List<Map<String, Object>> statistics, 
                               String chartType, int width, int height) {
        int padding = 40;
        
        if (chartType.equals("Bar Chart")) {
            // Calculate bar positions and check if mouse is over a bar
            int chartWidth = width - 2 * padding;
            int barWidth = chartWidth / (statistics.size() * 2);
            
            int x = padding + barWidth;
            for (Map<String, Object> stat : statistics) {
                if (point.x >= x && point.x <= x + barWidth) {
                    String subject = (String) stat.get("subject");
                    double hours = (double) stat.get("totalHours");
                    int sessions = (int) stat.get("sessions");
                    
                    return String.format("%s: %.1f hours (%d sessions)", subject, hours, sessions);
                }
                
                x += barWidth * 2;
            }
        } else if (chartType.equals("Pie Chart")) {
            // Check if mouse is over a pie slice
            int diameter = Math.min(width, height) - 100;
            int x = (width - diameter) / 2;
            int y = (height - diameter) / 2;
            
            // Only check if within pie bounds
            int centerX = x + diameter / 2;
            int centerY = y + diameter / 2;
            
            // Check if point is within circle
            double distanceFromCenter = Math.sqrt(
                Math.pow(point.x - centerX, 2) + Math.pow(point.y - centerY, 2)
            );
            
            if (distanceFromCenter <= diameter / 2) {
                // Calculate angle
                double angle = Math.toDegrees(Math.atan2(point.y - centerY, point.x - centerX));
                if (angle < 0) angle += 360;
                
                // Calculate total hours
                double totalHours = 0;
                for (Map<String, Object> stat : statistics) {
                    totalHours += (double) stat.get("totalHours");
                }
                
                // Find which slice the angle falls into
                double currentAngle = 0;
                for (Map<String, Object> stat : statistics) {
                    String subject = (String) stat.get("subject");
                    double hours = (double) stat.get("totalHours");
                    int sessions = (int) stat.get("sessions");
                    
                    double arcAngle = 360.0 * (hours / totalHours);
                    
                    if (angle >= currentAngle && angle < currentAngle + arcAngle) {
                        return String.format("%s: %.1f hours (%d sessions)", subject, hours, sessions);
                    }
                    
                    currentAngle += arcAngle;
                }
            }
        } else if (chartType.equals("Line Chart")) {
            // Calculate points and check if mouse is near a point
            int chartWidth = width - 2 * padding;
            int pointSpacing = chartWidth / (statistics.size() - 1);
            int pointX = padding;
            
            for (Map<String, Object> stat : statistics) {
                if (Math.abs(point.x - pointX) <= 10) { // Within 10 pixels
                    String subject = (String) stat.get("subject");
                    double hours = (double) stat.get("totalHours");
                    int sessions = (int) stat.get("sessions");
                    
                    return String.format("%s: %.1f hours (%d sessions)", subject, hours, sessions);
                }
                
                pointX += pointSpacing;
            }
        }
        
        return null;
    }
    
    private void drawTooltip(Graphics2D g2d, String text, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D textBounds = fm.getStringBounds(text, g2d);
        
        int padding = 5;
        int width = (int) textBounds.getWidth() + padding * 2;
        int height = (int) textBounds.getHeight() + padding * 2;
        
        // Position tooltip to stay within panel bounds
        x = Math.max(x, padding);
        x = Math.min(x, getWidth() - width - padding);
        
        // Create tooltip background
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(x, y - height - 5, width, height, 8, 8);
        
        // Draw tooltip text
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x + padding, y - 10);
    }
    
    private void drawBarChart(Graphics2D g2d, List<Map<String, Object>> statistics, int width, int height, int padding) {
        // Find max value for scaling
        double maxHours = 0;
        for (Map<String, Object> stat : statistics) {
            double hours = (double) stat.get("totalHours");
            if (hours > maxHours) maxHours = hours;
        }
        
        // Chart dimensions
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        int barWidth = chartWidth / (statistics.size() * 2);
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X axis
        g2d.drawLine(padding, padding, padding, height - padding); // Y axis
        
        // Draw grid lines
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
        
        int gridLines = 5;
        for (int i = 1; i <= gridLines; i++) {
            int y = height - padding - (i * chartHeight / gridLines);
            g2d.drawLine(padding, y, width - padding, y);
            
            // Label the grid line
            g2d.setColor(Color.DARK_GRAY);
            String label = String.format("%.1f", (i * maxHours / gridLines));
            g2d.drawString(label, padding - 30, y + 5);
            g2d.setColor(new Color(200, 200, 200));
        }
        
        g2d.setStroke(new BasicStroke(1));
        
        // Draw bars
        int x = padding + barWidth;
        for (Map<String, Object> stat : statistics) {
            String subject = (String) stat.get("subject");
            double hours = (double) stat.get("totalHours");
            
            // Apply animation to bar height
            double animatedHours = hours * animationProgress;
            int barHeight = (int)((animatedHours / maxHours) * chartHeight);
            
            // Create gradient for bar
            GradientPaint gradient = new GradientPaint(
                x, height - padding, UIConstants.PRIMARY_COLOR,
                x, height - padding - barHeight, UIConstants.INFO_COLOR
            );
            g2d.setPaint(gradient);
            
            // Draw rounded bar
            RoundRectangle2D.Double bar = new RoundRectangle2D.Double(
                x, height - padding - barHeight, barWidth, barHeight, 
                barWidth/4, barWidth/4
            );
            g2d.fill(bar);
            
            // Draw outline
            g2d.setColor(new Color(60, 60, 60));
            g2d.draw(bar);
            
            // Draw label
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            String shortSubject = subject.length() > 10 ? subject.substring(0, 10) + "..." : subject;
            int textWidth = fm.stringWidth(shortSubject);
            g2d.drawString(shortSubject, x + (barWidth - textWidth)/2, height - padding + 15);
            
            // Draw value
            String value = String.format("%.1f", hours);
            textWidth = fm.stringWidth(value);
            g2d.drawString(value, x + (barWidth - textWidth)/2, height - padding - barHeight - 5);
            
            x += barWidth * 2;
        }
        
        // Draw title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_MEDIUM));
        g2d.drawString("Study Hours by Subject", width / 2 - 80, padding / 2);
    }
    
    private void drawPieChart(Graphics2D g2d, List<Map<String, Object>> statistics, int width, int height) {
        // Calculate total hours
        double totalHours = 0;
        for (Map<String, Object> stat : statistics) {
            totalHours += (double) stat.get("totalHours");
        }
        
        // Chart dimensions
        int diameter = Math.min(width, height) - 100;
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;
        
        // Colors for pie slices
        Color[] colors = {
            UIConstants.PRIMARY_COLOR,
            UIConstants.ACCENT_COLOR,
            UIConstants.WARNING_COLOR,
            UIConstants.DANGER_COLOR,
            UIConstants.INFO_COLOR,
            new Color(96, 92, 168)
        };
        
        // Draw pie slices
        double currentAngle = 0;
        int colorIndex = 0;
        
        // Draw legend
        int legendX = width - 160;
        int legendY = 50;
        
        g2d.setStroke(new BasicStroke(1.5f));
        
        for (Map<String, Object> stat : statistics) {
            String subject = (String) stat.get("subject");
            double hours = (double) stat.get("totalHours");
            
            // Apply animation to angle
            double animatedHours = hours * animationProgress;
            double arcAngle = 360.0 * (animatedHours / totalHours);
            
            Color pieColor = colors[colorIndex % colors.length];
            
            // Draw pie slice
            g2d.setColor(pieColor);
            
            // Create a slightly separated slice for 3D effect
            double radians = Math.toRadians(currentAngle + arcAngle/2);
            int offsetX = (int)(5 * Math.cos(radians));
            int offsetY = (int)(5 * Math.sin(radians));
            
            g2d.fillArc(
                x + offsetX, 
                y + offsetY, 
                diameter, 
                diameter, 
                (int)currentAngle, 
                (int)arcAngle
            );
            
            // Draw slice outline
            g2d.setColor(Color.WHITE);
            g2d.drawArc(
                x + offsetX, 
                y + offsetY, 
                diameter, 
                diameter, 
                (int)currentAngle, 
                (int)arcAngle
            );
            
            // Draw legend
            g2d.setColor(pieColor);
            g2d.fillRoundRect(legendX, legendY, 15, 15, 4, 4);
            g2d.setColor(Color.BLACK);
            g2d.drawString(subject + ": " + String.format("%.1f", hours) + " hrs", legendX + 20, legendY + 12);
            
            currentAngle += arcAngle;
            colorIndex++;
            legendY += 20;
        }
        
        // Draw a white circle in center for donut effect
        g2d.setColor(Color.WHITE);
        int innerDiameter = (int)(diameter * 0.4);
        g2d.fillOval(
            x + (diameter - innerDiameter)/2,
            y + (diameter - innerDiameter)/2,
            innerDiameter,
            innerDiameter
        );
        
        // Draw title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_MEDIUM));
        g2d.drawString("Study Time Distribution", width / 2 - 90, 30);
    }
    
    private void drawLineChart(Graphics2D g2d, List<Map<String, Object>> statistics, int width, int height, int padding) {
        // Find max value for scaling
        double maxHours = 0;
        for (Map<String, Object> stat : statistics) {
            double hours = (double) stat.get("totalHours");
            if (hours > maxHours) maxHours = hours;
        }
        
        // Chart dimensions
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        
        // Draw grid
        g2d.setColor(new Color(220, 220, 220));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
        
        int gridLines = 5;
        for (int i = 1; i <= gridLines; i++) {
            int y = height - padding - (i * chartHeight / gridLines);
            g2d.drawLine(padding, y, width - padding, y);
            
            // Label the grid line
            g2d.setColor(Color.DARK_GRAY);
            String label = String.format("%.1f", (i * maxHours / gridLines));
            g2d.drawString(label, padding - 30, y + 5);
            g2d.setColor(new Color(220, 220, 220));
        }
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X axis
        g2d.drawLine(padding, padding, padding, height - padding); // Y axis
        
        if (statistics.size() > 1) {
            // Calculate points
            int[] xPoints = new int[statistics.size()];
            int[] yPoints = new int[statistics.size()];
            
            int pointX = padding;
            int pointSpacing = chartWidth / (statistics.size() - 1);
            
            for (int i = 0; i < statistics.size(); i++) {
                Map<String, Object> stat = statistics.get(i);
                double hours = (double) stat.get("totalHours") * animationProgress; // Apply animation
                String subject = (String) stat.get("subject");
                
                xPoints[i] = pointX;
                yPoints[i] = height - padding - (int)((hours / maxHours) * chartHeight);
                
                // Draw label
                g2d.setColor(Color.BLACK);
                String shortSubject = subject.length() > 8 ? subject.substring(0, 8) + "..." : subject;
                g2d.drawString(shortSubject, pointX - 15, height - padding + 15);
                
                pointX += pointSpacing;
            }
            
            // Draw area under the line
            g2d.setColor(new Color(UIConstants.PRIMARY_COLOR.getRed(),
                                  UIConstants.PRIMARY_COLOR.getGreen(), 
                                  UIConstants.PRIMARY_COLOR.getBlue(), 50));
            
            GeneralPath area = new GeneralPath();
            area.moveTo(xPoints[0], height - padding);
            for (int i = 0; i < xPoints.length; i++) {
                area.lineTo(xPoints[i], yPoints[i]);
            }
            area.lineTo(xPoints[xPoints.length-1], height - padding);
            area.closePath();
            
            g2d.fill(area);
            
            // Draw curved line connecting points
            g2d.setColor(UIConstants.PRIMARY_COLOR);
            g2d.setStroke(new BasicStroke(3));
            
            GeneralPath path = new GeneralPath();
            path.moveTo(xPoints[0], yPoints[0]);
            
            // Use cubic curves for smooth lines between points
            for (int i = 0; i < xPoints.length - 1; i++) {
                int x1 = xPoints[i];
                int y1 = yPoints[i];
                int x2 = xPoints[i+1];
                int y2 = yPoints[i+1];
                
                int ctrlX1 = x1 + (x2 - x1) / 2;
                int ctrlY1 = y1;
                int ctrlX2 = x1 + (x2 - x1) / 2;
                int ctrlY2 = y2;
                
                path.curveTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x2, y2);
            }
            
            g2d.draw(path);
            
            // Draw points and values
            for (int i = 0; i < xPoints.length; i++) {
                Map<String, Object> stat = statistics.get(i);
                double hours = (double) stat.get("totalHours");
                
                // Draw point
                g2d.setColor(Color.WHITE);
                g2d.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
                
                g2d.setColor(UIConstants.DANGER_COLOR);
                g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
                
                // Draw value
                g2d.setColor(Color.BLACK);
                String value = String.format("%.1f", hours);
                g2d.drawString(value, xPoints[i] - 10, yPoints[i] - 10);
            }
        }
        
        // Draw title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_MEDIUM));
        g2d.drawString("Study Progress Trend", width / 2 - 80, padding / 2);
    }
    
    private void updateSummaryPanel(List<Map<String, Object>> statistics) {
        summaryPanel.removeAll();
        
        if (statistics.isEmpty()) {
            JLabel noDataLabel = new JLabel("No study data available for the selected period", JLabel.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_MEDIUM));
            summaryPanel.add(noDataLabel, BorderLayout.CENTER);
            summaryPanel.revalidate();
            summaryPanel.repaint();
            return;
        }
        
        // Calculate total study hours
        double totalHours = 0;
        int totalSessions = 0;
        for (Map<String, Object> stat : statistics) {
            totalHours += (double) stat.get("totalHours");
            totalSessions += (int) stat.get("sessions");
        }
        
        // Create summary panels
        JPanel summaryGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        summaryGrid.setBackground(Color.WHITE);
        summaryGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Total hours panel
        JPanel totalHoursPanel = createSummaryBox("Total Study Hours", 
                                                String.format("%.1f", totalHours), 
                                                UIConstants.PRIMARY_COLOR);
        
        // Total sessions panel
        JPanel totalSessionsPanel = createSummaryBox("Study Sessions", 
                                                  String.valueOf(totalSessions), 
                                                  UIConstants.ACCENT_COLOR);
        
        // Average hours per session
        double avgHoursPerSession = totalHours / (totalSessions > 0 ? totalSessions : 1);
        JPanel avgHoursPanel = createSummaryBox("Avg Hours/Session", 
                                             String.format("%.1f", avgHoursPerSession), 
                                             UIConstants.INFO_COLOR);
        
        // Most studied subject
        String mostStudiedSubject = "";
        double mostStudiedHours = 0;
        for (Map<String, Object> stat : statistics) {
            double hours = (double) stat.get("totalHours");
            if (hours > mostStudiedHours) {
                mostStudiedHours = hours;
                mostStudiedSubject = (String) stat.get("subject");
            }
        }
        
        JPanel mostStudiedPanel = createSummaryBox("Most Studied Subject", 
                                               mostStudiedSubject, 
                                               UIConstants.WARNING_COLOR);
        
        summaryGrid.add(totalHoursPanel);
        summaryGrid.add(totalSessionsPanel);
        summaryGrid.add(avgHoursPanel);
        summaryGrid.add(mostStudiedPanel);
        
        summaryPanel.add(summaryGrid, BorderLayout.CENTER);
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }
    
    private JPanel createSummaryBox(String label, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        
        // Create header with color stripe
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JPanel colorStripe = new JPanel();
        colorStripe.setBackground(color);
        colorStripe.setPreferredSize(new Dimension(panel.getWidth(), 5));
        headerPanel.add(colorStripe, BorderLayout.NORTH);
        
        JLabel labelComponent = new JLabel(label, JLabel.CENTER);
        labelComponent.setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_SMALL));
        labelComponent.setForeground(color);
        labelComponent.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        headerPanel.add(labelComponent, BorderLayout.CENTER);
        
        JLabel valueComponent = new JLabel(value, JLabel.CENTER);
        valueComponent.setFont(new Font("Arial", Font.BOLD, 24));
        valueComponent.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(valueComponent, BorderLayout.CENTER);
        
        return panel;
    }
}