package com.studytracker.ui;

import com.studytracker.model.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class StudyCalendarPanel extends JPanel {
    private final JButton startButton;
    private final JButton refreshButton;
    private final JPanel calendarGrid;
    private final int userId;
    private final DatabaseManager db;

    private final Set<LocalDate> studyDates = new HashSet<>();

    public StudyCalendarPanel(int userId, DatabaseManager db) {
        this.userId = userId;
        this.db     = db;

        setLayout(new BorderLayout());

        // Control panel for buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // "Started Studying" button
        startButton = new JButton("Started Studying");
        startButton.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            db.logStudySession(userId, today);     // use injected db
            studyDates.add(today);
            refreshCalendar();
        });
        
        // "Refresh Calendar" button
        refreshButton = new JButton("Refresh Calendar");
        refreshButton.addActionListener(e -> {
            fetchStudyDates();
            refreshCalendar();
            JOptionPane.showMessageDialog(this, "Calendar refreshed successfully!", 
                                          "Refresh", JOptionPane.INFORMATION_MESSAGE);
        });

        controlPanel.add(startButton);
        controlPanel.add(refreshButton);

        // calendar grid (7 columns for Sun→Sat)
        calendarGrid = new JPanel(new GridLayout(0, 7));
        add(controlPanel, BorderLayout.NORTH);
        add(calendarGrid, BorderLayout.CENTER);

        fetchStudyDates();
        refreshCalendar();
    }

    private void fetchStudyDates() {
        studyDates.clear();
        studyDates.addAll(db.getStudyDates(userId));
    }

    private void refreshCalendar() {
        calendarGrid.removeAll();

        LocalDate today     = LocalDate.now();
        LocalDate firstDay  = today.withDayOfMonth(1);
        int startOffset     = firstDay.getDayOfWeek().getValue() % 7; // Sun=0, Mon=1...

        // empty cells up to the first weekday
        for (int i = 0; i < startOffset; i++) {
            calendarGrid.add(new JLabel(""));
        }

        // day buttons
        int daysInMonth = today.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = firstDay.withDayOfMonth(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setEnabled(false);   // read‑only buttons
            dayBtn.setOpaque(true);
            dayBtn.setBorderPainted(false);

            if (studyDates.contains(date)) {
                dayBtn.setBackground(Color.GREEN);
            } else {
                dayBtn.setBackground(Color.LIGHT_GRAY);
            }

            calendarGrid.add(dayBtn);
        }

        revalidate();
        repaint();
    }
    
    /**
     * Public method to refresh data from the database and update the calendar
     */
    public void refreshData() {
        fetchStudyDates();
        refreshCalendar();
    }
}
