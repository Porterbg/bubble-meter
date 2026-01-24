# PowerShell script to set up Gradle Wrapper
# This script helps you set up the Gradle wrapper for building the Android app

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Gradle Wrapper Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Gradle is installed
$gradleInstalled = $false
try {
    $gradleVersion = gradle -v 2>&1
    if ($LASTEXITCODE -eq 0) {
        $gradleInstalled = $true
        Write-Host "Gradle is installed!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Generating Gradle wrapper..." -ForegroundColor Yellow
        gradle wrapper --gradle-version 8.4
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "Success! You can now run: .\gradlew.bat build" -ForegroundColor Green
        } else {
            Write-Host "Error generating wrapper" -ForegroundColor Red
        }
    }
} catch {
    $gradleInstalled = $false
}

if (-not $gradleInstalled) {
    Write-Host "Gradle is not installed on your system." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "RECOMMENDED SOLUTION" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Open the project in Android Studio - it will automatically:" -ForegroundColor Green
    Write-Host "  • Download and set up Gradle wrapper" -ForegroundColor White
    Write-Host "  • Sync project dependencies" -ForegroundColor White
    Write-Host "  • Allow you to build with one click" -ForegroundColor White
    Write-Host ""
    Write-Host "Steps:" -ForegroundColor Yellow
    Write-Host "  1. Open Android Studio" -ForegroundColor White
    Write-Host "  2. File → Open → Select this project folder" -ForegroundColor White
    Write-Host "  3. Wait for Gradle sync (first time may take a few minutes)" -ForegroundColor White
    Write-Host "  4. Build → Make Project (or press Ctrl+F9)" -ForegroundColor White
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "ALTERNATIVE: Install Gradle" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "If you prefer command line:" -ForegroundColor Yellow
    Write-Host "  1. Install Gradle: https://gradle.org/install/" -ForegroundColor White
    Write-Host "  2. Run this script again, or run:" -ForegroundColor White
    Write-Host "     gradle wrapper --gradle-version 8.4" -ForegroundColor Cyan
    Write-Host ""
}
