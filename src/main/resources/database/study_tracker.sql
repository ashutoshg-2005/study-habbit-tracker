-- Study Habit Tracker Database Schema
CREATE DATABASE study_tracker;
USE study_tracker;
-- Drop tables if they exist to allow clean recreation
DROP TABLE IF EXISTS study_goals;
DROP TABLE IF EXISTS study_logs;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- Create roles table
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Create users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,  -- Should store hashed passwords in a real app
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Create study_logs table
CREATE TABLE study_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    hours DECIMAL(5,2) NOT NULL,  -- Allows for precision in time tracking
    date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_date (user_id, date)  -- Index for faster queries filtered by user and date
);

-- Create study goals table
CREATE TABLE study_goals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    target_hours DECIMAL(5,2) NOT NULL,
    period_type ENUM('DAILY', 'WEEKLY', 'MONTHLY') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create stored procedure to add a new user
DELIMITER //
CREATE PROCEDURE create_user(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(100),
    IN p_email VARCHAR(100),
    IN p_full_name VARCHAR(100)
)
BEGIN
    INSERT INTO users (username, password, email, full_name, role_id)
    VALUES (p_username, p_password, p_email, p_full_name, 
           (SELECT id FROM roles WHERE name = 'ROLE_USER'));
END //
DELIMITER ;

-- Create view for user's study logs - enforces role-based access
DELIMITER //
CREATE PROCEDURE get_user_study_logs(IN p_user_id INT)
BEGIN
    SELECT 
        sl.id,
        sl.subject,
        sl.hours,
        sl.date,
        sl.notes
    FROM 
        study_logs sl
    WHERE 
        sl.user_id = p_user_id
    ORDER BY 
        sl.date DESC, sl.created_at DESC;
END //
DELIMITER ;

-- Create stored procedure to add a study log entry
DELIMITER //
CREATE PROCEDURE add_study_log(
    IN p_user_id INT,
    IN p_subject VARCHAR(100),
    IN p_hours DECIMAL(5,2),
    IN p_date DATE,
    IN p_notes TEXT
)
BEGIN
    INSERT INTO study_logs (user_id, subject, hours, date, notes)
    VALUES (p_user_id, p_subject, p_hours, p_date, p_notes);
END //
DELIMITER ;

-- Create stored procedure to get summary statistics
DELIMITER //
CREATE PROCEDURE get_user_study_summary(
    IN p_user_id INT,
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        subject,
        SUM(hours) as total_hours,
        COUNT(*) as sessions,
        AVG(hours) as avg_hours_per_session,
        MIN(date) as first_date,
        MAX(date) as last_date
    FROM 
        study_logs
    WHERE 
        user_id = p_user_id
        AND date BETWEEN p_start_date AND p_end_date
    GROUP BY 
        subject
    ORDER BY 
        total_hours DESC;
END //
DELIMITER ;

CALL create_user('rahul', 'password123', 'rahul@example.com', 'Rahul Sharma');
CALL create_user('priya', 'password456', 'priya@example.com', 'Priya Patel');
CALL create_user('amit', 'password789', 'amit@example.com', 'Amit Singh');
CALL create_user('divya', 'divya123', 'divya@example.com', 'Divya Agarwal');
CALL create_user('ananya', 'ananya123', 'ananya@example.com', 'Ananya Reddy');

-- Get user IDs for other students
SET @rahul_id = (SELECT id FROM users WHERE username = 'rahul');
SET @priya_id = (SELECT id FROM users WHERE username = 'priya');
SET @amit_id = (SELECT id FROM users WHERE username = 'amit');
SET @divya_id = (SELECT id FROM users WHERE username = 'divya');
SET @ananya_id = (SELECT id FROM users WHERE username = 'ananya');

-- Add study logs for other students
-- For Rahul (Medical student)
INSERT INTO study_logs (user_id, subject, hours, date, notes) VALUES
(@rahul_id, 'Anatomy', 3.5, CURDATE() - INTERVAL 1 DAY, 'Studied muscular system'),
(@rahul_id, 'Physiology', 2.5, CURDATE() - INTERVAL 2 DAY, 'Cardiovascular system'),
(@rahul_id, 'Biochemistry', 2.0, CURDATE() - INTERVAL 3 DAY, 'Metabolism pathways'),
(@rahul_id, 'Pathology', 3.0, CURDATE() - INTERVAL 4 DAY, 'Neoplasia chapter');

-- For Priya (Commerce student)
INSERT INTO study_logs (user_id, subject, hours, date, notes) VALUES
(@priya_id, 'Accountancy', 3.0, CURDATE() - INTERVAL 1 DAY, 'Partnership accounts'),
(@priya_id, 'Business Studies', 2.5, CURDATE() - INTERVAL 2 DAY, 'Marketing strategies'),
(@priya_id, 'Economics', 2.0, CURDATE() - INTERVAL 3 DAY, 'Demand analysis'),
(@priya_id, 'Statistics', 1.5, CURDATE() - INTERVAL 4 DAY, 'Probability distributions');

-- For Amit (Engineering student)
INSERT INTO study_logs (user_id, subject, hours, date, notes) VALUES
(@amit_id, 'Engineering Drawing', 3.0, CURDATE() - INTERVAL 1 DAY, 'Isometric projections'),
(@amit_id, 'Electrical Engineering', 2.5, CURDATE() - INTERVAL 2 DAY, 'Circuit analysis'),
(@amit_id, 'Mechanical Engineering', 2.0, CURDATE() - INTERVAL 3 DAY, 'Thermodynamics'),
(@amit_id, 'Computer Programming', 4.0, CURDATE() - INTERVAL 4 DAY, 'C++ object-oriented programming');

-- For Divya (Arts student)
INSERT INTO study_logs (user_id, subject, hours, date, notes) VALUES
(@divya_id, 'Psychology', 2.5, CURDATE() - INTERVAL 1 DAY, 'Cognitive psychology'),
(@divya_id, 'Sociology', 2.0, CURDATE() - INTERVAL 2 DAY, 'Social institutions'),
(@divya_id, 'Political Science', 3.0, CURDATE() - INTERVAL 3 DAY, 'Indian political thought'),
(@divya_id, 'History', 2.5, CURDATE() - INTERVAL 4 DAY, 'Modern Indian history');

-- For Ananya (IAS aspirant)
INSERT INTO study_logs (user_id, subject, hours, date, notes) VALUES
(@ananya_id, 'General Studies', 4.0, CURDATE() - INTERVAL 1 DAY, 'Current affairs'),
(@ananya_id, 'Indian Polity', 3.0, CURDATE() - INTERVAL 2 DAY, 'Constitutional amendments'),
(@ananya_id, 'Geography', 2.5, CURDATE() - INTERVAL 3 DAY, 'Indian economic geography'),
(@ananya_id, 'History', 3.0, CURDATE() - INTERVAL 4 DAY, 'Ancient Indian history');

-- Add study goals for other students
INSERT INTO study_goals (user_id, subject, target_hours, period_type, is_active) VALUES
(@rahul_id, 'Anatomy', 12.0, 'WEEKLY', TRUE),
(@rahul_id, 'Physiology', 10.0, 'WEEKLY', TRUE),
(@priya_id, 'Accountancy', 10.0, 'WEEKLY', TRUE),
(@priya_id, 'Business Studies', 8.0, 'WEEKLY', TRUE),
(@amit_id, 'Electrical Engineering', 15.0, 'WEEKLY', TRUE),
(@amit_id, 'Computer Programming', 12.0, 'WEEKLY', TRUE),
(@divya_id, 'Psychology', 8.0, 'WEEKLY', TRUE),
(@divya_id, 'Sociology', 6.0, 'WEEKLY', TRUE),
(@ananya_id, 'General Studies', 20.0, 'WEEKLY', TRUE),
(@ananya_id, 'Indian Polity', 15.0, 'WEEKLY', TRUE);