@echo off
chcp 65001 >nul
title 排期助手 - 启动中...

echo [排期助手] 正在启动后端服务...
start "排期助手-后端" cmd /c "cd /d "%~dp0backend" && mvn spring-boot:run"

echo 等待后端启动 (约30秒)...
ping -n 31 127.0.0.1 >nul

echo [排期助手] 正在启动前端服务...
start "排期助手-前端" cmd /c "cd /d "%~dp0frontend" && npm run dev"

echo [排期助手] 启动完成！
echo   后端: http://localhost:8080
echo   前端: http://localhost:5173
echo.
echo 关闭本窗口不会影响后端和前端运行。
echo 如需关闭服务，请关闭对应的"排期助手-后端"和"排期助手-前端"命令行窗口。
pause
