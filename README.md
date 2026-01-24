# Bubble Meter - Android Bubble Level App

A bubble level app for Android that uses the device's sensors to display a visual level indicator. The app shows a circle with a bubble that moves based on the phone's orientation, indicating whether the device is level or tilted.

## Features

- **Real-time Level Detection**: Uses gravity sensor to detect device tilt
- **Visual Bubble Indicator**: Animated bubble moves smoothly based on device orientation
- **Color-coded Feedback**: 
  - Green when level (< 2°)
  - Orange when slightly tilted (2° - 10°)
  - Red when significantly tilted (> 10°)
- **Angle Display**: Shows current tilt angle in degrees
- **Smooth Animations**: Low-pass filtering for smooth bubble movement
- **No Permissions Required**: Works without any special permissions

## Requirements

- Android 5.0 (API 21) or higher
- Device with gravity sensor or accelerometer

## Installation

1. Clone this repository
2. Open the project in Android Studio
3. Build and run on your device or emulator

## Building and Running

### Option 1: Using Android Studio (Recommended)
1. Open Android Studio
2. Select **File → Open** and navigate to this project directory
3. Wait for Gradle sync to complete
4. **Set up a device:**
   - **Physical Device**: Connect via USB and enable USB debugging
   - **Emulator**: Tools → Device Manager → Create/Start an emulator
5. Click **Run** (green play button) or press `Shift+F10` (Windows) / `Ctrl+R` (Mac)
6. Select your device/emulator when prompted

### Option 2: Using Gradle Command Line

**⚠️ Important:** If you have Java 25 installed, you may encounter compatibility issues. Use one of these solutions:

**Solution A: Use the Java 17 wrapper script (if Java 17 is installed):**
```powershell
.\build-with-java17.bat build
```

**Solution B: Use Android Studio** (recommended) - it handles Java version compatibility automatically

**Solution C: Install Java 17 and set JAVA_HOME:**
1. Download Java 17 from: https://adoptium.net/temurin/releases/?version=17
2. Install it
3. Set JAVA_HOME to point to Java 17
4. Then run: `.\gradlew.bat build`

**On Windows (standard build):**
```powershell
.\gradlew.bat build
```

**On macOS/Linux:**
```bash
./gradlew build
```

**To build and install on connected device/emulator:**
```powershell
# Make sure device is connected or emulator is running first
.\gradlew.bat installDebug    # Windows
./gradlew installDebug         # macOS/Linux
```

**To run the app after installation:**
```powershell
# The app will be automatically launched after installDebug
# Or manually launch it:
adb shell am start -n com.bubblemeter/.MainActivity
```

**To build a release APK:**
```powershell
.\gradlew.bat assembleRelease  # Windows
./gradlew assembleRelease       # macOS/Linux
```

The APK will be located at: `app/build/outputs/apk/release/app-release.apk`

## Project Structure

```
bubble-meter/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bubblemeter/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── LevelView.kt
│   │   │   │   └── SensorManager.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── dimens.xml
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## How It Works

1. **Sensor Management**: The app uses Android's `SensorManager` to read gravity sensor data (or accelerometer as fallback)
2. **Angle Calculation**: Calculates pitch (forward/backward tilt) and roll (left/right tilt) angles from gravity vector
3. **Smoothing**: Applies low-pass filtering to reduce sensor noise and jitter
4. **Visual Display**: Maps tilt angles to bubble position within a circle indicator
5. **Feedback**: Changes colors and displays angle based on level status

## Technical Details

- **Language**: Kotlin
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34 (Android 14)
- **Sensor**: TYPE_GRAVITY (preferred) or TYPE_ACCELEROMETER (fallback)
- **Update Rate**: ~60Hz (SENSOR_DELAY_UI)

## License

See LICENSE file for details.
