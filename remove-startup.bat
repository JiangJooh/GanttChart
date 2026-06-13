@echo off
chcp 65001 >nul
title GanttChart - 取消开机自启
color 0E

echo ========================================
echo    取消 GanttChart 开机自启
echo ========================================
echo.

:: 从 Windows 启动文件夹删除快捷方式
set STARTUP_DIR=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup
set SHORTCUT=%STARTUP_DIR%\GanttChart.lnk

if exist "%SHORTCUT%" (
    del "%SHORTCUT%"
    echo [OK] 已移除开机自启项
) else (
    echo [..] 未找到开机自启项 (可能已被移除)
)

echo.
echo 完成！下次开机将不再自动启动 GanttChart。
echo.
pause
