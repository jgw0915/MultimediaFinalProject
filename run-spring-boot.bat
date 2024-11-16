@echo off
cd /d %~dp0
cd back-end
echo Running mvn clean install...
call mvn clean install
if %errorlevel% neq 0 (
    echo Build failed. Exiting...
    exit /b %errorlevel%
)
echo Build successful.
echo Running mvn spring-boot:run...
call mvn spring-boot:run
