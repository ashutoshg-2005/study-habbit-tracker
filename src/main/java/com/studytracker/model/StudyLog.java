package com.studytracker.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a study log entry in the Study Habit Tracker system
 */
public class StudyLog {
    private int id;
    private int userId;
    private String subject;
    private double hours;
    private Date date;
    private String notes;
    private Timestamp createdAt;

    // Constructor for new study logs
    public StudyLog(int userId, String subject, double hours, Date date, String notes) {
        this.userId = userId;
        this.subject = subject;
        this.hours = hours;
        this.date = date;
        this.notes = notes;
    }

    // Constructor for logs retrieved from database
    public StudyLog(int id, int userId, String subject, double hours, 
                   Date date, String notes, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.subject = subject;
        this.hours = hours;
        this.date = date;
        this.notes = notes;
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

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("%s - %.1f hours on %s", subject, hours, date);
    }
}