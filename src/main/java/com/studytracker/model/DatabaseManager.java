package com.studytracker.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles database operations for the Study Habit Tracker application
 * with role-based access control
 */
public class DatabaseManager {
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/study_tracker";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "1234"; 
    
    // Current logged-in user
    private User currentUser;

    public DatabaseManager() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection to the database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Validates user credentials and sets the current user if valid
     */
    public boolean login(String username, String password) {
        String sql = "SELECT u.id, u.username, u.password, u.email, u.full_name, " +
                     "u.role_id, r.name as role_name, u.created_at " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.id " +
                     "WHERE u.username = ? AND u.password = ?";
                     
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In a real app, use password hashing
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Create user object from database result
                currentUser = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("full_name"),
                    rs.getInt("role_id"),
                    rs.getString("role_name"),
                    rs.getTimestamp("created_at")
                );
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Registers a new user in the system
     */
    public boolean registerUser(User user) {
        try {
            CallableStatement cstmt = connection.prepareCall("{call create_user(?, ?, ?, ?)}");
            cstmt.setString(1, user.getUsername());
            cstmt.setString(2, user.getPassword()); // In a real app, hash the password
            cstmt.setString(3, user.getEmail());
            cstmt.setString(4, user.getFullName());
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds a new study log entry for the current user
     */
    public boolean addStudyLog(StudyLog log) {
        // Ensure user can only add logs for themselves
        if (currentUser == null || log.getUserId() != currentUser.getId()) {
            return false;
        }
        
        try {
            CallableStatement cstmt = connection.prepareCall("{call add_study_log(?, ?, ?, ?, ?)}");
            cstmt.setInt(1, log.getUserId());
            cstmt.setString(2, log.getSubject());
            cstmt.setDouble(3, log.getHours());
            cstmt.setDate(4, log.getDate());
            cstmt.setString(5, log.getNotes());
            
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves study logs for the current user
     */
    public List<StudyLog> getStudyLogs() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<StudyLog> logs = new ArrayList<>();
        
        try {
            CallableStatement cstmt = connection.prepareCall("{call get_user_study_logs(?)}");
            cstmt.setInt(1, currentUser.getId());
            
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                StudyLog log = new StudyLog(
                    rs.getInt("id"),
                    currentUser.getId(),
                    rs.getString("subject"),
                    rs.getDouble("hours"),
                    rs.getDate("date"),
                    rs.getString("notes"),
                    null // We don't return created_at in the stored procedure
                );
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return logs;
    }
    
    /**
     * Gets study summary statistics for the current user
     */
    public List<Map<String, Object>> getStudySummary(Date startDate, Date endDate) {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> summaries = new ArrayList<>();
        
        try {
            CallableStatement cstmt = connection.prepareCall("{call get_user_study_summary(?, ?, ?)}");
            cstmt.setInt(1, currentUser.getId());
            cstmt.setDate(2, startDate);
            cstmt.setDate(3, endDate);
            
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> summary = new HashMap<>();
                summary.put("subject", rs.getString("subject"));
                summary.put("totalHours", rs.getDouble("total_hours"));
                summary.put("sessions", rs.getInt("sessions"));
                summary.put("avgHoursPerSession", rs.getDouble("avg_hours_per_session"));
                summary.put("firstDate", rs.getDate("first_date"));
                summary.put("lastDate", rs.getDate("last_date"));
                
                summaries.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return summaries;
    }
    
    /**
     * Adds a new study goal for the current user
     */
    public boolean addStudyGoal(StudyGoal goal) {
        // Ensure user can only add goals for themselves
        if (currentUser == null || goal.getUserId() != currentUser.getId()) {
            return false;
        }
        
        String sql = "INSERT INTO study_goals (user_id, subject, target_hours, period_type) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, goal.getUserId());
            pstmt.setString(2, goal.getSubject());
            pstmt.setDouble(3, goal.getTargetHours());
            pstmt.setString(4, goal.getPeriodType().toString());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets all active study goals for the current user
     */
    public List<StudyGoal> getStudyGoals() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<StudyGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM study_goals WHERE user_id = ? AND is_active = TRUE";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                StudyGoal.PeriodType periodType = StudyGoal.PeriodType.valueOf(rs.getString("period_type"));
                
                StudyGoal goal = new StudyGoal(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("subject"),
                    rs.getDouble("target_hours"),
                    periodType,
                    rs.getBoolean("is_active"),
                    rs.getTimestamp("created_at")
                );
                goals.add(goal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return goals;
    }
    
    /**
     * Toggles the active status of a study goal
     * 
     * @param goalId the ID of the goal to toggle
     * @return true if the operation was successful, false otherwise
     */
    public boolean toggleGoalActiveStatus(int goalId) {
        // Ensure user can only modify their own goals
        if (currentUser == null) {
            return false;
        }
        
        String sql = "UPDATE study_goals SET is_active = NOT is_active " +
                     "WHERE id = ? AND user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, goalId);
            pstmt.setInt(2, currentUser.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets all study goals (both active and inactive) for the current user
     */
    public List<StudyGoal> getAllStudyGoals() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<StudyGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM study_goals WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                StudyGoal.PeriodType periodType = StudyGoal.PeriodType.valueOf(rs.getString("period_type"));
                
                StudyGoal goal = new StudyGoal(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("subject"),
                    rs.getDouble("target_hours"),
                    periodType,
                    rs.getBoolean("is_active"),
                    rs.getTimestamp("created_at")
                );
                goals.add(goal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return goals;
    }
    
    /**
     * Deletes a study log entry for a given subject and date
     * 
     * @param subject the subject of the study log
     * @param date the date of the study log
     * @return true if the delete was successful, false otherwise
     */
    public boolean deleteStudyLog(String subject, Date date) {
        if (currentUser == null) {
            return false;
        }
        
        String sql = "DELETE FROM study_logs WHERE user_id = ? AND subject = ? AND date = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            pstmt.setString(2, subject);
            pstmt.setDate(3, date);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the accumulated study hours for a specific goal
     * 
     * @param goal the study goal to check progress for
     * @return the total hours studied for the goal's subject in the current period
     */
    public double getStudyHoursForGoal(StudyGoal goal) {
        if (currentUser == null) {
            return 0.0;
        }
        
        String sql;
        Date startDate;
        Date endDate = new Date(System.currentTimeMillis());
        
        // Calculate the date range based on the goal period
        switch (goal.getPeriodType()) {
            case DAILY:
                // Just today
                startDate = new Date(endDate.getTime());
                break;
                
            case WEEKLY:
                // Current week (last 7 days)
                startDate = new Date(endDate.getTime() - 7 * 24 * 60 * 60 * 1000);
                break;
                
            case MONTHLY:
                // Current month (last 30 days)
                startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000);
                break;
                
            default:
                return 0.0;
        }
        
        sql = "SELECT SUM(hours) as total_hours FROM study_logs " +
              "WHERE user_id = ? AND subject = ? AND date BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            pstmt.setString(2, goal.getSubject());
            pstmt.setDate(3, startDate);
            pstmt.setDate(4, endDate);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double totalHours = rs.getDouble("total_hours");
                return totalHours;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    /**
     * Closes the database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
}