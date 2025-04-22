package com.studytracker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PomodoroPanel extends JPanel {
    private static final int POMODORO_MINUTES = 25;
    private static final int SHORT_BREAK_MINUTES = 5;
    private static final int LONG_BREAK_MINUTES = 15;

    private int timeRemaining; // in seconds
    private Timer timer;
    private boolean isRunning = false;

    private JLabel timerLabel;
    private JLabel modeLabel;
    private JButton startButton;
    private JButton resetButton;
    private JComboBox<String> modeSelector;

    public PomodoroPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Mode label at top
        modeLabel = new JLabel("Pomodoro Timer", SwingConstants.CENTER);
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        modeLabel.setForeground(new Color(50, 50, 50));
        modeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(modeLabel, BorderLayout.NORTH);

        // Timer label at center
        timerLabel = new JLabel("25:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 60));
        timerLabel.setForeground(new Color(40, 40, 40));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(timerLabel, BorderLayout.CENTER);

        // Controls panel
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        modeSelector = new JComboBox<>(new String[]{"Pomodoro", "Short Break", "Long Break"});
        modeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        startButton = new JButton("Start");
        styleButton(startButton, new Color(76, 175, 80));

        resetButton = new JButton("Reset");
        styleButton(resetButton, new Color(244, 67, 54));

        gbc.gridx = 0;
        controls.add(modeSelector, gbc);
        gbc.gridx = 1;
        controls.add(startButton, gbc);
        gbc.gridx = 2;
        controls.add(resetButton, gbc);

        add(controls, BorderLayout.SOUTH);

        setupListeners();
        setMode("Pomodoro");
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void setupListeners() {
        startButton.addActionListener(e -> toggleTimer());
        resetButton.addActionListener(e -> resetTimer());

        modeSelector.addActionListener(e -> {
            resetTimer();
            setMode((String) modeSelector.getSelectedItem());
        });
    }

    private void toggleTimer() {
        if (isRunning) {
            timer.stop();
            startButton.setText("Start");
        } else {
            timer = new Timer(1000, new TimerTick());
            timer.start();
            startButton.setText("Pause");
        }
        isRunning = !isRunning;
    }

    private void resetTimer() {
        if (timer != null) timer.stop();
        isRunning = false;
        startButton.setText("Start");
        setMode((String) modeSelector.getSelectedItem());
    }

    private void setMode(String mode) {
        switch (mode) {
            case "Pomodoro" -> {
                timeRemaining = POMODORO_MINUTES * 60;
                setBackground(new Color(255, 236, 227));
                modeLabel.setText("Pomodoro Focus Session");
            }
            case "Short Break" -> {
                timeRemaining = SHORT_BREAK_MINUTES * 60;
                setBackground(new Color(227, 255, 240));
                modeLabel.setText("Take a Short Break");
            }
            case "Long Break" -> {
                timeRemaining = LONG_BREAK_MINUTES * 60;
                setBackground(new Color(227, 232, 255));
                modeLabel.setText("Take a Long Break");
            }
        }
        updateTimerLabel();
    }

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private class TimerTick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (timeRemaining > 0) {
                timeRemaining--;
                updateTimerLabel();
            } else {
                timer.stop();
                Toolkit.getDefaultToolkit().beep(); // Alert when time is up
                JOptionPane.showMessageDialog(PomodoroPanel.this, "Time's up!");
                resetTimer();
            }
        }
    }

    // Optional: Make this accessible from other classes
    public void refreshData() {
        resetTimer();
    }

    public boolean isTimerRunning() {
        return isRunning;
    }

    public void startTimer() {
        if (!isRunning) {
            toggleTimer();
        }
    }
}
