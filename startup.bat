@echo off
chcp 65001 >nul
title GanttChart

echo ========================================
echo   GanttChart Launcher
echo ========================================
echo.

sc query GanttChart | find "RUNNING" >nul
if errorlevel 1 (
    echo Service is not running. Starting...
    net start GanttChart >nul 2>&1
    if errorlevel 1 (
        echo [!!] Failed to start service.
        echo      Try: cd /d D:\Vibe-Coding\GanttChart ^&^& ganttchart-service.exe start
        pause
        exit /b 1
    )
    echo Waiting for backend...
    ping -n 10 127.0.0.1 >nul
) else (
    echo [OK] GanttChart service is running
)

echo Opening browser...
start http://localhost:8080
timeout /t 3 >nul
