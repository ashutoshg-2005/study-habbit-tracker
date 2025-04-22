package com.studytracker.ui;

import com.studytracker.model.DatabaseManager;
import com.studytracker.model.StudyGoal;
import com.studytracker.model.User;
import com.studytracker.util.UIConstants;
import com.studytracker.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Panel for managing study goals
 */
public class StudyGoalsPanel extends JPanel {
    private final DatabaseManager dbManager;
    private final User currentUser;
    
    // UI components
    private JTextField goalSubjectField;
    private JTextField targetHoursField;
    private JComboBox<String> periodTypeCombo;
    private DefaultTableModel goalTableModel;
    private JTable goalTable;
    private JPanel goalProgressPanel;
    
    public StudyGoalsPanel(DatabaseManager dbManager, User currentUser) {
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
        refreshData();
    }
    
    private void setupUI() {
        // Input panel for adding new goals
        JPanel inputPanel = createInputPanel();
        
        // Goals table
        JPanel tablePanel = createTablePanel();
        
        // Progress visualization
        goalProgressPanel = createProgressPanel();
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // Create split pane for input and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputPanel, tablePanel);
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(goalProgressPanel, BorderLayout.SOUTH);
        
        // Add to the main panel
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInputPanel() {
        JPanel goalInputPanel = new JPanel(new GridBagLayout());
        goalInputPanel.setBorder(BorderFactory.createTitledBorder("Add Study Goal"));
        goalInputPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(
            UIConstants.PADDING_SMALL,
            UIConstants.PADDING_SMALL,
            UIConstants.PADDING_SMALL,
            UIConstants.PADDING_SMALL
        );
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Subject field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        goalInputPanel.add(new JLabel("Subject:"), gbc);
        
        goalSubjectField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        goalInputPanel.add(goalSubjectField, gbc);
        
        // Target hours field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        goalInputPanel.add(new JLabel("Target Hours:"), gbc);
        
        targetHoursField = new JTextField(5);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        goalInputPanel.add(targetHoursField, gbc);
        
        // Period type combo
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        goalInputPanel.add(new JLabel("Period:"), gbc);
        
        periodTypeCombo = new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly"});
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        goalInputPanel.add(periodTypeCombo, gbc);
        
        // Save button
        JButton saveGoalButton = UIUtils.createStyledButton("Save Goal", Color.WHITE, UIConstants.ACCENT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        goalInputPanel.add(saveGoalButton, gbc);
        
        saveGoalButton.addActionListener(e -> saveStudyGoal());
        
        return goalInputPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Current Goals"));
        tablePanel.setBackground(Color.WHITE);
        
        // Goals table
        String[] columnNames = {"Subject", "Target Hours", "Period", "Active", "ID"};
        goalTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) return Boolean.class; // For checkbox in Active column
                return super.getColumnClass(column);
            }
        };
        
        goalTable = new JTable(goalTableModel);
        goalTable.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        goalTable.setFont(new Font("Arial", Font.PLAIN, UIConstants.FONT_SMALL));
        goalTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_SMALL));
        
        // Set custom rendering for active column
        goalTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected((Boolean) value);
                checkBox.setHorizontalAlignment(JLabel.CENTER);
                checkBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                return checkBox;
            }
        });
        
        // Hide the ID column
        goalTable.getColumnModel().getColumn(4).setMinWidth(0);
        goalTable.getColumnModel().getColumn(4).setMaxWidth(0);
        goalTable.getColumnModel().getColumn(4).setWidth(0);
        
        // Set better column widths
        goalTable.getColumnModel().getColumn(0).setPreferredWidth(200);  // Subject
        goalTable.getColumnModel().getColumn(1).setPreferredWidth(80);   // Target Hours
        goalTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Period
        goalTable.getColumnModel().getColumn(3).setPreferredWidth(60);   // Active
        
        JScrollPane goalScrollPane = new JScrollPane(goalTable);
        tablePanel.add(goalScrollPane, BorderLayout.CENTER);
        
        // Add button toolbar for goal actions
        JPanel goalToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        goalToolbar.setBackground(Color.WHITE);
        
        JButton toggleButton = UIUtils.createStyledButton("Toggle Active", Color.WHITE, UIConstants.PRIMARY_COLOR);
        toggleButton.addActionListener(e -> toggleSelectedGoal());
        
        JButton refreshButton = UIUtils.createStyledButton("Refresh", Color.WHITE, UIConstants.PRIMARY_COLOR);
        refreshButton.addActionListener(e -> refreshData());
        
        goalToolbar.add(toggleButton);
        goalToolbar.add(refreshButton);
        tablePanel.add(goalToolbar, BorderLayout.SOUTH);
        
        return tablePanel;
    }
    
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Goal Progress"));
        panel.setBackground(Color.WHITE);
        
        // Panel will be populated in refreshData()
        
        return panel;
    }
    
    private void saveStudyGoal() {
        try {
            String subject = goalSubjectField.getText().trim();
            double targetHours = Double.parseDouble(targetHoursField.getText().trim());
            String periodTypeStr = (String)periodTypeCombo.getSelectedItem();
            
            if (subject.isEmpty() || targetHours <= 0) {
                UIUtils.showError(this, "Please enter a valid subject and target hours!");
                return;
            }
            
            StudyGoal.PeriodType periodType = StudyGoal.PeriodType.valueOf(
                periodTypeStr.toUpperCase());
                
            StudyGoal goal = new StudyGoal(
                currentUser.getId(),
                subject,
                targetHours,
                periodType
            );
            
            if (dbManager.addStudyGoal(goal)) {
                goalSubjectField.setText("");
                targetHoursField.setText("");
                
                refreshData(); // Refresh goals display
                UIUtils.showInfo(this, "Study goal saved successfully!");
            } else {
                UIUtils.showError(this, "Error saving study goal!");
            }
            
        } catch (NumberFormatException e) {
            UIUtils.showError(this, "Target hours must be a valid number!");
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError(this, "An error occurred: " + e.getMessage());
        }
    }
    
    private void toggleSelectedGoal() {
        int selectedRow = goalTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showError(this, "Please select a goal to toggle!");
            return;
        }
        
        // Get the goal ID from the selected row
        // Since we're using getAllStudyGoals() now, we need to store the ID in the model
        int goalId = (Integer) goalTable.getValueAt(selectedRow, 4); // Hidden column for ID
        
        // Call the database method to toggle the goal's active status
        if (dbManager.toggleGoalActiveStatus(goalId)) {
            UIUtils.showInfo(this, "Goal status toggled successfully!");
            refreshData(); // Refresh the display to show updated status
        } else {
            UIUtils.showError(this, "Failed to toggle goal status!");
        }
    }
    
    /**
     * Updates the goal progress visualization panel
     */
    private void updateProgressPanel(List<StudyGoal> goals) {
        goalProgressPanel.removeAll();
        
        if (goals.isEmpty()) {
            JLabel emptyLabel = new JLabel("No active goals to display");
            emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
            goalProgressPanel.add(emptyLabel);
        } else {
            // Filter for active goals only for the progress display
            List<StudyGoal> activeGoals = goals.stream()
                .filter(StudyGoal::isActive)
                .toList();
            
            if (activeGoals.isEmpty()) {
                JLabel noActiveLabel = new JLabel("No active goals. Toggle a goal to activate it.");
                noActiveLabel.setAlignmentX(CENTER_ALIGNMENT);
                goalProgressPanel.add(noActiveLabel);
            } else {
                // Create progress bars for each active goal
                for (StudyGoal goal : activeGoals) {
                    // Get the current progress for this goal
                    double currentHours = dbManager.getStudyHoursForGoal(goal);
                    double targetHours = goal.getTargetHours();
                    double progressPercent = Math.min(currentHours / targetHours * 100, 100);
                    
                    // Create a title with subject and period
                    String title = goal.getSubject() + " (" + goal.getPeriodType().toString().toLowerCase() + ")";
                    JLabel titleLabel = new JLabel(title);
                    titleLabel.setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_SMALL));
                    
                    // Create a progress description
                    DecimalFormat df = new DecimalFormat("0.00");
                    String progressText = df.format(currentHours) + " / " + df.format(targetHours) + 
                                          " hours (" + df.format(progressPercent) + "%)";
                    JLabel progressLabel = new JLabel(progressText);
                    
                    // Create a progress bar
                    JProgressBar progressBar = new JProgressBar(0, 100);
                    progressBar.setValue((int)progressPercent);
                    progressBar.setStringPainted(true);
                    progressBar.setPreferredSize(new Dimension(400, 20));
                    
                    // Set color based on progress
                    if (progressPercent >= 100) {
                        progressBar.setForeground(UIConstants.SUCCESS_COLOR);
                    } else if (progressPercent >= 50) {
                        progressBar.setForeground(UIConstants.PRIMARY_COLOR);
                    } else if (progressPercent >= 25) {
                        progressBar.setForeground(UIConstants.WARNING_COLOR);
                    } else {
                        progressBar.setForeground(UIConstants.DANGER_COLOR);
                    }
                    
                    // Add components to panel
                    JPanel goalPanel = new JPanel();
                    goalPanel.setLayout(new BoxLayout(goalPanel, BoxLayout.Y_AXIS));
                    goalPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
                    goalPanel.setBackground(Color.WHITE);
                    
                    titleLabel.setAlignmentX(LEFT_ALIGNMENT);
                    progressLabel.setAlignmentX(LEFT_ALIGNMENT);
                    progressBar.setAlignmentX(LEFT_ALIGNMENT);
                    
                    goalPanel.add(titleLabel);
                    goalPanel.add(Box.createVerticalStrut(3));
                    goalPanel.add(progressLabel);
                    goalPanel.add(Box.createVerticalStrut(3));
                    goalPanel.add(progressBar);
                    
                    goalProgressPanel.add(goalPanel);
                }
            }
        }
        
        goalProgressPanel.revalidate();
        goalProgressPanel.repaint();
    }
    
    /**
     * Refresh the data in the table
     */
    public void refreshData() {
        // Switch to using getAllStudyGoals to include both active and inactive goals
        List<StudyGoal> goals = dbManager.getAllStudyGoals();
        
        // Clear the table
        goalTableModel.setRowCount(0);
        
        // Add rows to the table
        for (StudyGoal goal : goals) {
            goalTableModel.addRow(new Object[]{
                goal.getSubject(),
                new DecimalFormat("0.00").format(goal.getTargetHours()),
                goal.getPeriodType().toString().toLowerCase(),
                goal.isActive(),
                goal.getId() // Hidden column for the goal ID
            });
        }
        
        // Update progress visualization
        updateProgressPanel(goals);
    }
}