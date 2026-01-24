@echo off
REM Build script that uses Java 17 if available
REM This works around Java 25 compatibility issues with Kotlin

REM Check if Java 17 is available
set JAVA17_HOME=
if exist "C:\Program Files\Java\jdk-17" (
    set JAVA17_HOME=C:\Program Files\Java\jdk-17
) else if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0" (
    set JAVA17_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0
) else if exist "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-17.0" (
    set JAVA17_HOME=%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-17.0
)

if defined JAVA17_HOME (
    echo Using Java 17 from: %JAVA17_HOME%
    set JAVA_HOME=%JAVA17_HOME%
    set PATH=%JAVA_HOME%\bin;%PATH%
    call gradlew.bat %*
) else (
    echo.
    echo WARNING: Java 17 not found. The build may fail due to Java 25 compatibility issues.
    echo.
    echo RECOMMENDED: Use Android Studio to build the project instead.
    echo.
    echo To install Java 17:
    echo   1. Download from: https://adoptium.net/temurin/releases/?version=17
    echo   2. Install it
    echo   3. Run this script again
    echo.
    pause
    call gradlew.bat %*
)
