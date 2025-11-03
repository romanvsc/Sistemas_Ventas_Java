@echo off
title Sistema de Ventas
cls
echo ========================================
echo    SISTEMA DE VENTAS
echo ========================================
echo.
echo Iniciando aplicacion...
echo.
cd /d "%~dp0"
cd src
java -cp ".;../lib/mysql-connector-j-8.2.0.jar" presentacion.LoginVentana
cd ..
pause
