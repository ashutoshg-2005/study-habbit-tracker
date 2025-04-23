package com.studytracker.ui;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MotivationQuotePanel extends JPanel {

    private JLabel quoteLabel;
    private JLabel dayLabel;

    public MotivationQuotePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 248, 225)); // Light pastel background

        // Day label
        dayLabel = new JLabel(getCurrentDay(), SwingConstants.CENTER);
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dayLabel.setForeground(new Color(85, 60, 120));
        dayLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));
        add(dayLabel, BorderLayout.NORTH);

        // Quote label
        quoteLabel = new JLabel(getQuoteForToday(), SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        quoteLabel.setForeground(new Color(50, 50, 50));
        quoteLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(quoteLabel, BorderLayout.CENTER);
    }

    private String getQuoteForToday() {
        Map<DayOfWeek, String> quotes = new HashMap<>();
        quotes.put(DayOfWeek.MONDAY, "New week, new goals! Let's crush it! 💪");
        quotes.put(DayOfWeek.TUESDAY, "Progress is progress, no matter how small. 🚶‍♂️");
        quotes.put(DayOfWeek.WEDNESDAY, "Halfway there! Keep pushing. 🌟");
        quotes.put(DayOfWeek.THURSDAY, "Stay strong. You’re closer than you think. 🧗");
        quotes.put(DayOfWeek.FRIDAY, "Finish strong! The weekend is your reward. 🏁");
        quotes.put(DayOfWeek.SATURDAY, "Rest, recharge, and grow. 🌱");
        quotes.put(DayOfWeek.SUNDAY, "Plan, reflect, and get ready to rise again. 🔄");

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return quotes.getOrDefault(today, "You’re doing great. Keep going!");
    }

    private String getCurrentDay() {
        return "Today is " + LocalDate.now().getDayOfWeek().toString().replace("_", " ");
    }

    // Optional method for external refresh
    public void refreshQuote() {
        dayLabel.setText(getCurrentDay());
        quoteLabel.setText(getQuoteForToday());
    }
}
