# Android Bubble Level App - Development Plan

## Project Overview
A bubble level app that uses the device's sensors to display a visual level indicator. The app shows a circle with a bubble that moves based on the phone's orientation, indicating whether the device is level or tilted.

## Technology Stack

### Core Technologies
- **Language**: Kotlin (recommended) or Java
- **Minimum SDK**: API 21 (Android 5.0 Lollipop)
- **Target SDK**: API 34 (Android 14)
- **Build Tool**: Gradle
- **IDE**: Android Studio

### Key Libraries/Dependencies
- AndroidX libraries (AppCompat, Core KTX)
- Material Design Components (optional, for modern UI)
- No external dependencies required for core functionality

## Project Structure

```
bubble-meter/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bubblemeter/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── LevelView.kt (Custom View)
│   │   │   │   └── SensorManager.kt (Sensor handling)
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── dimens.xml
│   │   │   │   └── drawable/ (optional icons)
│   │   │   └── AndroidManifest.xml
│   │   └── test/ (Unit tests)
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts (Project level)
├── settings.gradle.kts
└── README.md
```

## Core Components

### 1. Sensor Management
**Sensor Type**: `TYPE_GRAVITY` or `TYPE_ACCELEROMETER` (with low-pass filter)
- **Why**: Gyroscope measures rotation rate, not tilt. For level detection, we need gravity/acceleration data.
- **Implementation**: Use `SensorManager` to register gravity sensor listener
- **Data Processing**: 
  - Extract X and Y components of gravity vector
  - Calculate tilt angles (pitch and roll)
  - Apply smoothing/filtering to reduce jitter

### 2. Custom View (LevelView)
**Purpose**: Draw the circle and bubble indicator
- **Circle**: Fixed size, centered on screen
- **Bubble**: Moves based on sensor data
- **Rendering**: Custom `onDraw()` method using Canvas
- **Features**:
  - Responsive sizing (adapts to screen)
  - Smooth bubble animation
  - Visual feedback (color changes when level)

### 3. Main Activity
**Responsibilities**:
- Initialize sensor manager
- Set up custom view
- Handle lifecycle (register/unregister sensors)
- Manage permissions if needed

## Implementation Steps

### Phase 1: Project Setup
1. **Create Android Project**
   - New Project in Android Studio
   - Choose "Empty Activity" template
   - Set package name: `com.bubblemeter`
   - Language: Kotlin
   - Minimum SDK: API 21

2. **Configure Gradle**
   - Update `build.gradle.kts` with dependencies
   - Enable view binding or data binding (optional)

3. **Set Up Basic UI**
   - Create `activity_main.xml` layout
   - Add custom view placeholder
   - Define color resources

### Phase 2: Sensor Integration
1. **Create SensorManager Class**
   - Implement `SensorEventListener`
   - Register gravity sensor
   - Process sensor data
   - Calculate tilt angles (pitch/roll)
   - Apply low-pass filter for smoothing

2. **Handle Lifecycle**
   - Register sensor in `onResume()`
   - Unregister in `onPause()`
   - Handle sensor availability checks

### Phase 3: Custom View Implementation
1. **Create LevelView Class**
   - Extend `View` or `SurfaceView` (for smoother animation)
   - Implement `onDraw()` method
   - Draw circle background
   - Draw bubble indicator
   - Handle touch events (optional: calibration)

2. **Bubble Position Calculation**
   - Map sensor tilt angles to bubble position
   - Convert angles to pixel coordinates
   - Apply constraints (keep bubble within circle)
   - Implement smooth interpolation

3. **Visual Enhancements**
   - Add center indicator (crosshair or dot)
   - Color coding (green when level, red when tilted)
   - Optional: Grid lines or degree markings

### Phase 4: UI/UX Polish
1. **Layout Design**
   - Center the level view
   - Add optional controls (reset, sensitivity)
   - Status text (angle display, level indicator)

2. **Animations**
   - Smooth bubble movement
   - Transition animations
   - Haptic feedback (optional)

3. **Responsive Design**
   - Support different screen sizes
   - Handle orientation changes
   - Landscape/portrait modes

### Phase 5: Testing & Optimization
1. **Testing**
   - Test on multiple devices
   - Verify sensor accuracy
   - Test edge cases (rapid movement, extreme angles)
   - Battery consumption testing

2. **Optimization**
   - Optimize drawing performance
   - Reduce sensor update frequency if needed
   - Memory leak checks

## Technical Details

### Sensor Data Processing
```kotlin
// Pseudo-code for sensor processing
gravityX = sensorEvent.values[0]
gravityY = sensorEvent.values[1]
gravityZ = sensorEvent.values[2]

// Calculate tilt angles
pitch = Math.toDegrees(Math.atan2(gravityY, gravityZ))  // Forward/backward tilt (rotation around X-axis)
roll = Math.toDegrees(Math.atan2(gravityX, gravityZ))   // Left/right tilt (rotation around Y-axis)

// Map to bubble position
bubbleX = centerX + (roll * sensitivity)
bubbleY = centerY + (pitch * sensitivity)
```

### Bubble Position Constraints
- Maximum displacement = circle radius - bubble radius
- Clamp bubble position to stay within circle bounds
- Calculate distance from center for visual feedback

### Smoothing Algorithm
- Use low-pass filter: `filteredValue = α * newValue + (1 - α) * oldValue`
- Typical α value: 0.1 to 0.3
- Reduces jitter from sensor noise

## Permissions
- **No special permissions required** for basic functionality
- Gravity sensor is available without permissions
- Optional: `VIBRATE` permission for haptic feedback

## UI Design Considerations

### Visual Elements
1. **Circle**
   - Outer ring (border)
   - Center point indicator
   - Optional: Degree markings (0°, 45°, 90°)

2. **Bubble**
   - Circular shape
   - Smaller than circle radius
   - Smooth movement animation
   - Color gradient (optional)

3. **Status Indicators**
   - Angle display (degrees)
   - Level indicator (text or icon)
   - Optional: History graph

### Color Scheme
- **Level**: Green (#4CAF50)
- **Tilted**: Red (#F44336) or Orange (#FF9800)
- **Background**: Dark theme recommended for visibility
- **Circle**: White or light gray
- **Bubble**: Blue or contrasting color

## Advanced Features (Optional)

1. **Calibration**
   - Allow user to set zero point
   - Compensate for device-specific offsets

2. **Multiple Views**
   - Horizontal level
   - Vertical level
   - 3D view

3. **Measurement Tools**
   - Angle measurement
   - Incline percentage
   - History/recording

4. **Settings**
   - Sensitivity adjustment
   - Update frequency
   - Theme selection
   - Sound/haptic feedback

## Testing Checklist

- [ ] Sensor data is received correctly
- [ ] Bubble moves smoothly with device tilt
- [ ] Bubble stays within circle bounds
- [ ] App works in portrait and landscape
- [ ] Battery consumption is reasonable
- [ ] No memory leaks
- [ ] Works on different Android versions (API 21+)
- [ ] Handles sensor unavailability gracefully
- [ ] Smooth animations without lag
- [ ] Accurate level detection

## Next Steps

1. Set up Android Studio project
2. Create basic project structure
3. Implement sensor reading
4. Create custom view
5. Connect sensor data to view
6. Test and refine

## Resources

- [Android Sensor Overview](https://developer.android.com/guide/topics/sensors/sensors_overview)
- [SensorManager Documentation](https://developer.android.com/reference/android/hardware/SensorManager)
- [Custom View Drawing](https://developer.android.com/training/custom-views/custom-drawing)
- [Material Design Components](https://material.io/components)

