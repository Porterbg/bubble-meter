@echo off
REM Build script that uses Java 21 if available
REM This works around Java 25 compatibility issues with Kotlin

REM Check if Java 21 is available
set JAVA21_HOME=
if exist "C:\Program Files\Java\jdk-21" (
    set JAVA21_HOME=C:\Program Files\Java\jdk-21
) else if exist "C:\Program Files\Java\jdk-21.0" (
    set JAVA21_HOME=C:\Program Files\Java\jdk-21.0
) else if exist "C:\Program Files\Eclipse Adoptium\jdk-21.0" (
    set JAVA21_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0
) else if exist "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-21.0" (
    set JAVA21_HOME=%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-21.0
) else if exist "C:\Java\jdk-21" (
    set JAVA21_HOME=C:\Java\jdk-21
)

if defined JAVA21_HOME (
    echo Using Java 21 from: %JAVA21_HOME%
    set JAVA_HOME=%JAVA21_HOME%
    set PATH=%JAVA_HOME%\bin;%PATH%
    call gradlew.bat %*
) else (
    echo.
    echo WARNING: Java 21 not found. The build may fail due to Java 25 compatibility issues.
    echo.
    echo RECOMMENDED: Use Android Studio to build the project instead.
    echo.
    echo To install Java 21:
    echo   1. Download from: https://adoptium.net/temurin/releases/?version=21
    echo   2. Install it
    echo   3. Run this script again
    echo.
    pause
    call gradlew.bat %*
)
