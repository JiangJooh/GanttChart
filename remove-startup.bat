@echo off
chcp 65001 >nul
title GanttChart Uninstall

echo ========================================
echo   Uninstall GanttChart Service
echo ========================================
echo.

sc query GanttChart >nul 2>&1
if errorlevel 1 (
    echo [..] GanttChart service not found.
) else (
    echo Stopping service...
    net stop GanttChart >nul 2>&1
    echo Uninstalling service...
    cd /d D:\Vibe-Coding\GanttChart
    ganttchart-service.exe uninstall >nul 2>&1
    echo [OK] Service removed.
)

echo.
echo Done. GanttChart will no longer auto-start on boot.
echo.
pause
