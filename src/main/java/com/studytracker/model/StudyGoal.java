package com.studytracker.model;

import java.sql.Timestamp;

/**
 * Represents a study goal in the Study Habit Tracker system
 */
public class StudyGoal {
    private int id;
    private int userId;
    private String subject;
    private double targetHours;
    private PeriodType periodType;
    private boolean isActive;
    private Timestamp createdAt;

    public enum PeriodType {
        DAILY, WEEKLY, MONTHLY
    }

    // Constructor for new study goals
    public StudyGoal(int userId, String subject, double targetHours, PeriodType periodType) {
        this.userId = userId;
        this.subject = subject;
        this.targetHours = targetHours;
        this.periodType = periodType;
        this.isActive = true;
    }

    // Constructor for goals retrieved from database
    public StudyGoal(int id, int userId, String subject, double targetHours, 
                    PeriodType periodType, boolean isActive, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.subject = subject;
        this.targetHours = targetHours;
        this.periodType = periodType;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getTargetHours() {
        return targetHours;
    }

    public void setTargetHours(double targetHours) {
        this.targetHours = targetHours;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("%s - %.1f hours %s", subject, targetHours, periodType.toString().toLowerCase());
    }
}