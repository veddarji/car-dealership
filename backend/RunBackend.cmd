@echo off
cd /d "%~dp0"
echo Killing old Java processes...
taskkill /f /im java.exe >nul 2>&1
taskkill /f /im javaw.exe >nul 2>&1
timeout /t 3 /nobreak >nul
echo Starting Car Dealership API...
call mvn spring-boot:run
pause
