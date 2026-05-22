@echo off
REM Finance Tracker - Auto-Build and Run

setlocal enabledelayedexpansion

echo Building Finance Tracker...

cd /d "%~dp0"
call "%~dp0apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd" clean package -DskipTests -q

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)

if exist "%~dp0target\java-finance-tracker-1.0.0.jar" (
    start javaw --enable-native-access=ALL-UNNAMED -jar "%~dp0target\java-finance-tracker-1.0.0.jar"
    exit /b 0
) else (
    echo JAR file not found!
    pause
    exit /b 1
)


