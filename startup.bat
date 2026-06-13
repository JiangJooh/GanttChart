@echo off
chcp 65001 >nul
title GanttChart
color 0A

echo ========================================
echo   GanttChart Starting...
echo ========================================
echo.

:: Step 1: Start MySQL
echo [1/2] Checking MySQL...
sc query MySQL80 | find "RUNNING" >nul
if errorlevel 1 (
    echo Starting MySQL...
    net start MySQL80 >nul 2>&1
    if errorlevel 1 (
        echo [!!] MySQL failed to start, but continuing...
    ) else (
        echo [OK] MySQL started
    )
) else (
    echo [OK] MySQL is running
)

:: Step 2: Start backend
echo [2/2] Starting backend server...
echo.

set JAR=D:\Vibe-Coding\GanttChart\backend\target\gantt-chart-1.0.0.jar
if not exist "%JAR%" (
    echo [!!] JAR not found at %JAR%
    echo      Build it: cd backend ^&^& mvn package -DskipTests
    pause
    exit /b 1
)

start "GanttChart-Backend" java -jar "%JAR%"

echo URL: http://localhost:8080
echo.
echo Waiting for backend to start (15s)...
ping -n 15 127.0.0.1 >nul

start http://localhost:8080

echo.
echo Browser opened. If blank, refresh after a few seconds.
echo You can close this window, service runs in background.
echo To stop, close the "GanttChart-Backend" window.
timeout /t 5 >nul
