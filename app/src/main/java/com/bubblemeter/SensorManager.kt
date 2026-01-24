package com.bubblemeter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager as AndroidSensorManager
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Manages sensor data for the bubble level app.
 * Handles gravity sensor reading and calculates tilt angles.
 */
class LevelSensorManager(private val context: Context) : SensorEventListener {
    
    private val sensorManager: AndroidSensorManager = 
        context.getSystemService(Context.SENSOR_SERVICE) as AndroidSensorManager
    
    private var gravitySensor: Sensor? = null
    private var listener: OnTiltChangeListener? = null
    
    // Low-pass filter constants
    private val alpha = 0.2f // Smoothing factor (0.1 to 0.3 recommended)
    
    // Filtered gravity values
    private var filteredGravityX = 0f
    private var filteredGravityY = 0f
    private var filteredGravityZ = 0f
    
    // Tilt angles in degrees
    var pitch: Float = 0f // Forward/backward tilt (rotation around X-axis)
        private set
    var roll: Float = 0f  // Left/right tilt (rotation around Y-axis)
        private set
    
    interface OnTiltChangeListener {
        fun onTiltChanged(pitch: Float, roll: Float)
    }
    
    init {
        // Try to get gravity sensor first (preferred)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        
        // Fallback to accelerometer if gravity sensor is not available
        if (gravitySensor == null) {
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
    }
    
    /**
     * Register the sensor listener
     */
    fun register(listener: OnTiltChangeListener) {
        this.listener = listener
        gravitySensor?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                AndroidSensorManager.SENSOR_DELAY_UI // ~60Hz update rate
            )
        }
    }
    
    /**
     * Unregister the sensor listener
     */
    fun unregister() {
        sensorManager.unregisterListener(this)
        listener = null
    }
    
    /**
     * Check if sensor is available
     */
    fun isSensorAvailable(): Boolean {
        return gravitySensor != null
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_GRAVITY -> {
                    // Gravity sensor provides filtered gravity vector
                    processGravityData(it.values[0], it.values[1], it.values[2])
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    // Accelerometer needs low-pass filtering
                    applyLowPassFilter(it.values[0], it.values[1], it.values[2])
                    processGravityData(filteredGravityX, filteredGravityY, filteredGravityZ)
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }
    
    /**
     * Apply low-pass filter to accelerometer data
     */
    private fun applyLowPassFilter(x: Float, y: Float, z: Float) {
        filteredGravityX = alpha * x + (1 - alpha) * filteredGravityX
        filteredGravityY = alpha * y + (1 - alpha) * filteredGravityY
        filteredGravityZ = alpha * z + (1 - alpha) * filteredGravityZ
    }
    
    /**
     * Process gravity data and calculate tilt angles
     */
    private fun processGravityData(gravityX: Float, gravityY: Float, gravityZ: Float) {
        // Calculate tilt angles in degrees
        // Pitch: rotation around X-axis (forward/backward tilt)
        // Roll: rotation around Y-axis (left/right tilt)
        
        val magnitude = sqrt(gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ)
        
        if (magnitude > 0.1f) { // Avoid division by zero
            // Normalize gravity vector
            val normX = gravityX / magnitude
            val normY = gravityY / magnitude
            val normZ = gravityZ / magnitude
            
            // Calculate angles using atan2
            // Pitch: forward/backward tilt (rotation around X-axis)
            pitch = Math.toDegrees(atan2(normY, normZ).toDouble()).toFloat()
            
            // Roll: left/right tilt (rotation around Y-axis)
            roll = Math.toDegrees(atan2(normX, normZ).toDouble()).toFloat()
            
            // Notify listener
            listener?.onTiltChanged(pitch, roll)
        }
    }
    
    /**
     * Calculate the total tilt angle (magnitude of tilt)
     */
    fun getTotalTiltAngle(): Float {
        return sqrt(pitch * pitch + roll * roll)
    }
}
