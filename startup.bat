@echo off
setlocal

:: ============================================================
::  CONFIGURE THESE PATHS BEFORE RUNNING
:: ============================================================
set BACKEND_DIR=%~dp0
set JAR_NAME=ASTronaut-0.0.1-SNAPSHOT.jar
:: ============================================================

set JAR_PATH=%BACKEND_DIR%target\%JAR_NAME%

if not exist "%JAR_PATH%" (
    echo ERROR: JAR not found at %JAR_PATH%
    echo Did you run build.bat first?
    exit /b 1
)

echo Starting ASTronaut...
echo Visit http://localhost:9093 in your browser
echo Press Ctrl+C to stop.
echo.

java --enable-preview -jar "%JAR_PATH%"

endlocal