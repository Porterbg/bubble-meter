package com.bubblemeter

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
    private lateinit var levelView: LevelView
    private lateinit var statusText: TextView
    private lateinit var angleText: TextView
    private lateinit var sensorManager: LevelSensorManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        levelView = findViewById(R.id.levelView)
        statusText = findViewById(R.id.statusText)
        angleText = findViewById(R.id.angleText)
        
        // Initialize sensor manager
        sensorManager = LevelSensorManager(this)
        
        // Check if sensor is available
        if (!sensorManager.isSensorAvailable()) {
            statusText.text = getString(R.string.sensor_unavailable)
            statusText.setTextColor(getColor(R.color.tilted_red))
            angleText.text = ""
        } else {
            // Register sensor listener
            sensorManager.register(object : LevelSensorManager.OnTiltChangeListener {
                override fun onTiltChanged(pitch: Float, roll: Float) {
                    // Update level view
                    levelView.updateTilt(pitch, roll)
                    
                    // Update status text
                    updateStatus(pitch, roll)
                }
            })
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Re-register sensor listener when activity resumes
        if (::sensorManager.isInitialized && sensorManager.isSensorAvailable()) {
            sensorManager.register(object : LevelSensorManager.OnTiltChangeListener {
                override fun onTiltChanged(pitch: Float, roll: Float) {
                    levelView.updateTilt(pitch, roll)
                    updateStatus(pitch, roll)
                }
            })
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Unregister sensor to save battery
        if (::sensorManager.isInitialized) {
            sensorManager.unregister()
        }
    }
    
    /**
     * Update status text based on tilt angles
     */
    private fun updateStatus(pitch: Float, roll: Float) {
        val totalTilt = levelView.getTotalTiltAngle()
        
        // Update angle display
        angleText.text = String.format(
            Locale.getDefault(),
            getString(R.string.angle_display),
            totalTilt
        )
        
        // Update status text and color
        if (totalTilt < 2.0f) {
            statusText.text = getString(R.string.level_status)
            statusText.setTextColor(getColor(R.color.level_green))
        } else {
            statusText.text = getString(R.string.tilted_status)
            if (totalTilt < 10f) {
                statusText.setTextColor(getColor(R.color.tilted_orange))
            } else {
                statusText.setTextColor(getColor(R.color.tilted_red))
            }
        }
    }
}
