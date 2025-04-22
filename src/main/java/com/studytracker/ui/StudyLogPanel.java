package com.studytracker.ui;

import com.studytracker.model.DatabaseManager;
import com.studytracker.model.StudyLog;
import com.studytracker.model.User;
import com.studytracker.util.UIConstants;
import com.studytracker.util.UIUtils;
import com.studytracker.util.UIUtils.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for managing study logs
 */
public class StudyLogPanel extends JPanel {
    private final DatabaseManager dbManager;
    private final User currentUser;
    
    // UI components
    private JTextField subjectField;
    private JTextField hoursField;
    private JTextArea notesField;
    private JDateChooser dateChooser;
    private DefaultTableModel logTableModel;
    private JTable logTable;
    private JSplitPane splitPane;
    
    public StudyLogPanel(DatabaseManager dbManager, User currentUser) {
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
        // Input panel for adding new logs
        JPanel inputPanel = createInputPanel();
        
        // Log table
        JPanel tablePanel = createTablePanel();
        
        // Create split pane for input and table
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputPanel, tablePanel);
        splitPane.setResizeWeight(0.35); // Give 35% of space to top component
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        
        // Add to the main panel
        add(splitPane, BorderLayout.CENTER);
        
        // Add component listener to adjust split pane divider location when panel resizes
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Adjust divider location proportionally when the panel is resized
                SwingUtilities.invokeLater(() -> {
                    splitPane.setDividerLocation(0.35);
                });
            }
        });
    }
    
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Study Session"));
        inputPanel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
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
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Subject:"), gbc);
        
        subjectField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(subjectField, gbc);
        
        // Hours field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Hours:"), gbc);
        
        hoursField = new JTextField(5);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(hoursField, gbc);
        
        // Date field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Date:"), gbc);
        
        dateChooser = UIUtils.createDateChooser(new Date(System.currentTimeMillis()));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(dateChooser, gbc);
        
        // Notes field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Notes:"), gbc);
        
        notesField = new JTextArea(3, 20);
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesField);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(notesScroll, gbc);
        
        inputPanel.add(formPanel, BorderLayout.CENTER);
        
        // Save button
        JButton saveButton = UIUtils.createStyledButton("Save Session", Color.WHITE, UIConstants.ACCENT_COLOR);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        saveButton.addActionListener(e -> saveStudyLog());
        
        return inputPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Study History"));
        tablePanel.setBackground(Color.WHITE);
        
        // Log table
        String[] columnNames = {"Subject", "Hours", "Date", "Notes"};
        logTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        logTable = new JTable(logTableModel);
        logTable.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        logTable.setFont(new Font("Arial", Font.PLAIN, UIConstants.FONT_SMALL));
        logTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, UIConstants.FONT_SMALL));
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logTable.setFillsViewportHeight(true);
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Set relative column widths
        logTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Subject
        logTable.getColumnModel().getColumn(1).setPreferredWidth(50);  // Hours
        logTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date
        logTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Notes
        
        // Custom renderer for Date column to format dates consistently
        logTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Date) {
                    setText(UIUtils.formatDate((Date)value));
                }
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button toolbar for table actions
        JPanel tableToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableToolbar.setBackground(Color.WHITE);
        
        JButton deleteButton = UIUtils.createStyledButton("Delete", Color.WHITE, UIConstants.DANGER_COLOR);
        deleteButton.addActionListener(e -> deleteSelectedLog());
        
        JButton refreshButton = UIUtils.createStyledButton("Refresh", Color.WHITE, UIConstants.PRIMARY_COLOR);
        refreshButton.addActionListener(e -> refreshData());
        
        tableToolbar.add(deleteButton);
        tableToolbar.add(refreshButton);
        tablePanel.add(tableToolbar, BorderLayout.SOUTH);
        
        return tablePanel;
    }
    
    private void saveStudyLog() {
        try {
            String subject = subjectField.getText().trim();
            double hours = Double.parseDouble(hoursField.getText().trim());
            String notes = notesField.getText().trim();
            Date selectedDate = dateChooser.getDate();
            
            if (subject.isEmpty() || hours <= 0) {
                UIUtils.showError(this, "Please enter a valid subject and hours!");
                return;
            }
            
            StudyLog log = new StudyLog(
                currentUser.getId(),
                subject,
                hours,
                selectedDate,
                notes
            );
            
            if (dbManager.addStudyLog(log)) {
                subjectField.setText("");
                hoursField.setText("");
                notesField.setText("");
                
                refreshData(); // Refresh the logs display
                UIUtils.showInfo(this, "Study session logged successfully!");
            } else {
                UIUtils.showError(this, "Error saving study log!");
            }
            
        } catch (NumberFormatException e) {
            UIUtils.showError(this, "Hours must be a valid number!");
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError(this, "An error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Delete the selected log entry
     */
    private void deleteSelectedLog() {
        int selectedRow = logTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showError(this, "Please select a log entry to delete!");
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this study log entry?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Get the log ID or use row information to identify the log
            String subject = (String) logTable.getValueAt(selectedRow, 0);
            String hours = (String) logTable.getValueAt(selectedRow, 1);
            Date date = (Date) logTable.getValueAt(selectedRow, 2);
            
            // Call database method to delete the log
            if (dbManager.deleteStudyLog(subject, date)) {
                refreshData(); // Refresh the display
                UIUtils.showInfo(this, "Study log entry deleted successfully!");
            } else {
                UIUtils.showError(this, "Failed to delete study log!");
            }
        }
    }
    
    /**
     * Refresh the data in the table
     */
    public void refreshData() {
        List<StudyLog> logs = dbManager.getStudyLogs();
        
        // Clear the table
        logTableModel.setRowCount(0);
        
        // Add rows to the table
        for (StudyLog log : logs) {
            logTableModel.addRow(new Object[]{
                log.getSubject(),
                new DecimalFormat("0.00").format(log.getHours()),
                log.getDate(),
                log.getNotes()
            });
        }
    }
}