@echo off
chcp 65001 >nul
title 排期助手 - 取消自启

echo [排期助手] 正在移除开机自启...

del /f "%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup\GanttChart.lnk" 2>nul

if exist "%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup\GanttChart.lnk" (
    echo 取消失败，请手动删除：%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup\GanttChart.lnk
) else (
    echo [排期助手] 已取消开机自启！
)

echo.
echo 如需重新启用，请双击项目目录下的 startup.bat
pause
