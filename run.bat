@echo off
echo Study Habit Tracker - Compiling and Running
echo ---------------------------------------

set CLASSPATH=.;.\lib\mysql-connector-java-8.0.17.jar
set SRC_DIR=src\main\java
set OUT_DIR=target\classes

REM Clean the target directory to avoid compilation issues
echo Cleaning target directory...
rmdir /S /Q %OUT_DIR%
mkdir %OUT_DIR%

echo Compiling...
javac -d %OUT_DIR% -cp %CLASSPATH% %SRC_DIR%\com\studytracker\util\*.java %SRC_DIR%\com\studytracker\model\*.java %SRC_DIR%\com\studytracker\ui\*.java %SRC_DIR%\com\studytracker\*.java

if %ERRORLEVEL% NEQ 0 (
  echo Compilation failed!
  pause
  exit /b 1
)

echo Running Study Habit Tracker...
java -cp %CLASSPATH%;%OUT_DIR% com.studytracker.StudyTrackerApp

pause