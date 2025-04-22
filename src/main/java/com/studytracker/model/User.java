package com.studytracker.model;

import java.sql.Timestamp;

/**
 * Represents a user in the Study Habit Tracker system
 * with role-based access capabilities
 */
public class User {
    private int id;
    private String username;
    private String password; // In a real app, this would be hashed
    private String email;
    private String fullName;
    private int roleId;
    private String roleName;
    private Timestamp createdAt;

    // Constructor for new users
    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        // Default role is ROLE_USER (1)
        this.roleId = 1;
    }

    // Constructor for users from database
    public User(int id, String username, String password, String email, 
                String fullName, int roleId, String roleName, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.roleId = roleId;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(roleName);
    }

    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}