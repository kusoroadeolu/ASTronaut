@echo off
setlocal

:: ============================================================
::  CONFIGURE THESE PATHS BEFORE RUNNING
:: ============================================================
set FRONTEND_DIR=C:\Users\eastw\Git Projects\Personal\astronaut-ui\astronaut
set BACKEND_DIR=C:\Users\eastw\Git Projects\Personal\ASTronaut
:: ============================================================

set STATIC_DIR=%BACKEND_DIR%\src\main\resources\static

echo [1/4] Building frontend...
cd /d "%FRONTEND_DIR%"
call npm run build
if errorlevel 1 (
    echo ERROR: Frontend build failed.
    exit /b 1
)

echo [2/4] Clearing old static files...
if exist "%STATIC_DIR%" (
    rd /s /q "%STATIC_DIR%"
)
mkdir "%STATIC_DIR%"

echo [3/4] Copying dist to Spring Boot static folder...
xcopy /e /i /q "%FRONTEND_DIR%\dist\*" "%STATIC_DIR%\"
if errorlevel 1 (
    echo ERROR: Failed to copy frontend build.
    exit /b 1
)

echo [4/4] Packaging backend JAR...
cd /d "%BACKEND_DIR%"
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ERROR: Maven build failed.
    exit /b 1
)

echo.
echo BUILD SUCCESSFUL!
echo Your JAR is in: %BACKEND_DIR%\target\
echo Run it with: start.bat

endlocal