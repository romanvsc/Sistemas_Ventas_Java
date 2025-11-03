@echo off
title Compilar Sistema de Ventas
cls
echo ========================================
echo    COMPILANDO SISTEMA DE VENTAS
echo ========================================
echo.
cd /d "%~dp0"
cd src
javac -encoding UTF-8 -cp ".;../lib/mysql-connector-j-8.2.0.jar" modelo/*.java datos/*.java servicios/*.java presentacion/*.java
if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo    COMPILACION EXITOSA
    echo ========================================
) else (
    echo.
    echo ========================================
    echo    ERROR EN LA COMPILACION
    echo ========================================
    pause
    exit /b 1
)
cd ..
pause
