# Study Habit Tracker

A desktop application built with Java Swing to help students track, analyze, and improve their study habits. This application allows users to log study sessions, set study goals, and visualize progress over time.

## Features

- **User Authentication**: Secure login system with role-based access control
- **Study Session Logging**: Record subject, hours studied, date, and notes
- **Goal Setting**: Create and track daily, weekly, and monthly study goals
- **Statistics and Visualization**: View study data through various chart types (bar, pie, line)
- **Progress Tracking**: Monitor your progress toward meeting study goals
- **Data Analysis**: Get insights into your most studied subjects and time allocation

## System Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- Windows, macOS, or Linux operating system
- At least 100MB of free disk space

## Installation and Setup

### 1. Database Setup

1. Install MySQL Server if you don't have it already
2. Start MySQL Server service
3. Execute the SQL script located at `src/main/resources/database/study_tracker.sql` to create the database schema:

```bash
mysql -u root -p < src/main/resources/database/study_tracker.sql
```

*Note: The default database connection settings in the application use:*
- Database URL: jdbc:mysql://localhost:3306/study_tracker
- Username: root
- Password: 1234

If your MySQL setup uses different credentials, please modify the `DatabaseManager.java` file before compiling.

### 2. Running the Application

#### Windows:

1. Double-click on the `run.bat` file in the root directory, or
2. Open a command prompt in the project directory and run:
   ```
   run.bat
   ```

#### Manual Compilation and Execution:

If you prefer to compile and run the application manually:

1. Open a terminal/command prompt in the project directory
2. Compile the source code:
   ```
   javac -d target/classes -cp .;./lib/mysql-connector-java-8.0.17.jar src/main/java/com/studytracker/util/*.java src/main/java/com/studytracker/model/*.java src/main/java/com/studytracker/ui/*.java src/main/java/com/studytracker/StudyTrackerApp.java
   ```
3. Run the application:
   ```
   java -cp ./lib/mysql-connector-java-8.0.17.jar;target/classes com.studytracker.StudyTrackerApp
   ```
   
## Usage Guide

### First-time Setup

When you first run the application, you'll be presented with a login screen:

1. If you don't have an account, click the "Register" button to create one
(if u have executed the script file try username as rahul and password as password123 )
2. Fill in your username, password, email, and full name
3. Click "Register" to create your account
4. Log in with your new credentials

### Main Interface

The main window is divided into several tabs:

#### Study Log Tab

- Record your study sessions with details like subject, duration, and date
- View and manage your past study entries
- Delete incorrect entries if needed

#### Goals Tab

- Create new study goals for various subjects
- Set daily, weekly, or monthly targets for study hours
- Track your progress toward meeting goals
- Enable or disable goals as needed

#### Statistics Tab

- Visualize your study data using different chart types:
  - Bar charts for comparing study hours across subjects
  - Pie charts for seeing the proportion of time spent on each subject
  - Line charts for tracking progress over time
- Filter statistics by different time periods:
  - This week
  - Last week
  - This month
  - Last month
  - Last 3 months
  - This year
- View summary data including total study hours and sessions

## Best Practices for Use

1. **Regular Logging**: Try to log your study sessions daily for the most accurate tracking
2. **Realistic Goals**: Set achievable goals that you can consistently meet
3. **Review Statistics**: Check your statistics weekly to identify trends and areas for improvement
4. **Consistent Subjects**: Use consistent subject names to ensure accurate tracking

## Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Verify MySQL is running
   - Check that your database credentials match those in the application
   - Ensure the study_tracker database exists

2. **Application Won't Start**:
   - Make sure you have Java installed and properly configured
   - Verify the MySQL connector JAR file is in the lib directory

3. **Charts Not Displaying**:
   - Make sure you have logged some study sessions
   - Try selecting a different time period

## Technical Support

If you encounter issues not covered in the troubleshooting section, please:
- Check the console output for error messages
- Verify your Java and MySQL versions match the minimum requirements

## Development and Contribution

This application is built using:
- Java for the core application logic
- Swing for the user interface
- MySQL for data storage
- JDBC for database connectivity

## License

This project is distributed under the MIT License. See the LICENSE file for more details.

## Acknowledgements

- MySQL Connector/J for database connectivity
- Java Swing library for the user interface