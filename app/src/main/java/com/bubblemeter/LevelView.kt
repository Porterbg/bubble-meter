package com.bubblemeter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Custom view that displays a bubble level indicator.
 * Shows a circle with a bubble that moves based on device tilt.
 */
class LevelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paint objects for drawing
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.circle_stroke_width)
        color = ContextCompat.getColor(context, R.color.circle_color)
    }
    
    private val circleBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.circle_stroke_width) * 0.5f
        color = ContextCompat.getColor(context, R.color.circle_border)
    }
    
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.bubble_color)
    }
    
    private val centerIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.center_indicator)
    }
    
    private val levelIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.circle_stroke_width) * 0.3f
    }
    
    // Dimensions
    private var centerX = 0f
    private var centerY = 0f
    private var circleRadius = 0f
    private var bubbleRadius = 0f
    private val maxBubbleRadiusRatio = 0.15f // Bubble is 15% of circle radius
    
    // Bubble position (normalized, -1 to 1)
    private var bubbleX = 0f
    private var bubbleY = 0f
    
    // Sensitivity for mapping angles to bubble position
    private var sensitivity = 0.015f // pixels per degree
    
    // Level threshold (degrees)
    private val levelThreshold = 2.0f
    
    // Current tilt angles
    private var pitch = 0f
    private var roll = 0f
    
    // Smoothing for bubble movement
    private var targetBubbleX = 0f
    private var targetBubbleY = 0f
    private val smoothingFactor = 0.15f
    
    init {
        setWillNotDraw(false)
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculate dimensions
        val size = min(width, height)
        centerX = width / 2f
        centerY = height / 2f
        
        // Circle takes up 80% of available space
        circleRadius = size * 0.4f
        bubbleRadius = circleRadius * maxBubbleRadiusRatio
        
        // Calculate sensitivity based on circle radius
        // Maximum displacement should be circleRadius - bubbleRadius
        val maxDisplacement = circleRadius - bubbleRadius
        sensitivity = maxDisplacement / 45f // 45 degrees max tilt
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw circle background
        drawCircle(canvas)
        
        // Draw center indicator (crosshair)
        drawCenterIndicator(canvas)
        
        // Draw degree markings (optional)
        drawDegreeMarkings(canvas)
        
        // Draw bubble
        drawBubble(canvas)
        
        // Update bubble position with smoothing
        updateBubblePosition()
        
        // Invalidate to trigger redraw for smooth animation
        invalidate()
    }
    
    /**
     * Draw the main circle
     */
    private fun drawCircle(canvas: Canvas) {
        // Draw outer border
        canvas.drawCircle(centerX, centerY, circleRadius, circleBorderPaint)
        
        // Draw main circle
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint)
    }
    
    /**
     * Draw center indicator (crosshair)
     */
    private fun drawCenterIndicator(canvas: Canvas) {
        val indicatorSize = resources.getDimension(R.dimen.center_indicator_size)
        val halfSize = indicatorSize / 2f
        
        // Draw crosshair lines
        val lineLength = circleRadius * 0.1f
        levelIndicatorPaint.color = ContextCompat.getColor(context, R.color.center_indicator)
        
        // Horizontal line
        canvas.drawLine(
            centerX - lineLength,
            centerY,
            centerX + lineLength,
            centerY,
            levelIndicatorPaint
        )
        
        // Vertical line
        canvas.drawLine(
            centerX,
            centerY - lineLength,
            centerX,
            centerY + lineLength,
            levelIndicatorPaint
        )
        
        // Center dot
        canvas.drawCircle(centerX, centerY, halfSize, centerIndicatorPaint)
    }
    
    /**
     * Draw degree markings at 0°, 45°, 90°
     */
    private fun drawDegreeMarkings(canvas: Canvas) {
        val markLength = circleRadius * 0.05f
        levelIndicatorPaint.color = ContextCompat.getColor(context, R.color.circle_border)
        
        val angles = floatArrayOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f)
        
        for (angle in angles) {
            val rad = Math.toRadians(angle.toDouble())
            val startX = centerX + (circleRadius - markLength) * cos(rad).toFloat()
            val startY = centerY + (circleRadius - markLength) * sin(rad).toFloat()
            val endX = centerX + circleRadius * cos(rad).toFloat()
            val endY = centerY + circleRadius * sin(rad).toFloat()
            
            canvas.drawLine(startX, startY, endX, endY, levelIndicatorPaint)
        }
    }
    
    /**
     * Draw the bubble indicator
     */
    private fun drawBubble(canvas: Canvas) {
        // Calculate bubble position
        val bubblePosX = centerX + bubbleX * sensitivity * circleRadius
        val bubblePosY = centerY + bubbleY * sensitivity * circleRadius
        
        // Ensure bubble stays within circle bounds
        val distanceFromCenter = sqrt(
            (bubblePosX - centerX) * (bubblePosX - centerX) +
            (bubblePosY - centerY) * (bubblePosY - centerY)
        )
        
        val maxDistance = circleRadius - bubbleRadius
        val clampedDistance = min(distanceFromCenter, maxDistance)
        
        val angle = if (distanceFromCenter > 0) {
            atan2(bubblePosY - centerY, bubblePosX - centerX)
        } else {
            0f
        }
        
        val finalX = centerX + clampedDistance * cos(angle)
        val finalY = centerY + clampedDistance * sin(angle)
        
        // Change bubble color based on level status
        val totalTilt = sqrt(pitch * pitch + roll * roll)
        if (totalTilt < levelThreshold) {
            // Level - green tint
            bubblePaint.color = ContextCompat.getColor(context, R.color.level_green)
        } else if (totalTilt < 10f) {
            // Slightly tilted - orange
            bubblePaint.color = ContextCompat.getColor(context, R.color.tilted_orange)
        } else {
            // Tilted - red
            bubblePaint.color = ContextCompat.getColor(context, R.color.tilted_red)
        }
        
        // Draw bubble with gradient effect (simplified)
        canvas.drawCircle(finalX, finalY, bubbleRadius, bubblePaint)
        
        // Draw inner highlight for 3D effect
        val highlightRadius = bubbleRadius * 0.6f
        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.white)
            alpha = 100
        }
        canvas.drawCircle(
            finalX - bubbleRadius * 0.2f,
            finalY - bubbleRadius * 0.2f,
            highlightRadius,
            highlightPaint
        )
    }
    
    /**
     * Update bubble position with smoothing
     */
    private fun updateBubblePosition() {
        // Smooth interpolation towards target
        bubbleX += (targetBubbleX - bubbleX) * smoothingFactor
        bubbleY += (targetBubbleY - bubbleY) * smoothingFactor
    }
    
    /**
     * Update tilt angles and calculate bubble position
     */
    fun updateTilt(pitch: Float, roll: Float) {
        this.pitch = pitch
        this.roll = roll
        
        // Map angles to normalized bubble position (-1 to 1)
        // Roll affects X position, Pitch affects Y position
        targetBubbleX = (roll / 45f).coerceIn(-1f, 1f)
        targetBubbleY = (pitch / 45f).coerceIn(-1f, 1f)
    }
    
    /**
     * Get current total tilt angle
     */
    fun getTotalTiltAngle(): Float {
        return sqrt(pitch * pitch + roll * roll)
    }
}
