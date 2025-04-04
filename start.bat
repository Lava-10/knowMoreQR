@echo off

echo Starting the knowMoreQR application...
echo:

echo Starting MySQL...
REM Ensure MySQL is running - add your MySQL start command here if needed
echo:

echo Starting the Spring Boot server...
start cmd /c "cd server && mvnw.cmd spring-boot:run"
echo:

echo Starting the React client...
start cmd /c "cd client && npm start"
echo:

echo The application is now starting up!
echo Server: http://localhost:8080
echo Client: http://localhost:3000 