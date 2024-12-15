@echo off
:: Start Spring Boot application in a new window
start cmd /k ".\run-spring-boot.bat"

:: Start frontend application in a new window
start cmd /k "cd .\front-end\ && npm run serve"

echo Services are starting...