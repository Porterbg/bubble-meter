package com.bubblemeter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.min

/**
 * Custom view that displays a spirit level tube (like traditional bubble levels).
 * Shows a curved tube with a bubble that moves based on tilt angle.
 * 
 * @param orientation VERTICAL for Y-axis (pitch) or HORIZONTAL for X-axis (roll)
 */
class SpiritLevelTubeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Orientation {
        VERTICAL,   // For Y-axis (pitch/forward-backward tilt)
        HORIZONTAL  // For X-axis (roll/left-right tilt)
    }

    var orientation: Orientation = Orientation.VERTICAL
        set(value) {
            field = value
            // Recalculate sensitivity when orientation changes
            if (width > 0 && height > 0) {
                recalculateDimensions()
            }
            invalidate()
        }

    // Paint objects
    private val tubePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.circle_stroke_width) * 1.5f
        color = ContextCompat.getColor(context, R.color.circle_color)
    }
    
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.bubble_color)
    }
    
    private val centerLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = ContextCompat.getColor(context, R.color.center_indicator)
    }
    
    private val levelIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // Dimensions
    private var tubeWidth = 0f
    private var tubeHeight = 0f
    private var bubbleRadius = 0f
    private var centerX = 0f
    private var centerY = 0f
    
    // Current tilt angle (in degrees)
    private var tiltAngle = 0f
    
    // Smoothing for bubble movement
    private var targetBubblePosition = 0f
    private var currentBubblePosition = 0f
    private val smoothingFactor = 0.2f // Increased for more responsive movement
    
    // Level threshold (degrees)
    private val levelThreshold = 1.0f
    
    // Sensitivity: pixels per degree
    private var sensitivity = 0f

    init {
        setWillNotDraw(false)
    }
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Start animation loop when view is attached
        invalidate()
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Stop animation when view is detached
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalculateDimensions()
    }
    
    /**
     * Recalculate dimensions and sensitivity based on current size and orientation
     */
    private fun recalculateDimensions() {
        centerX = width / 2f
        centerY = height / 2f
        
        if (orientation == Orientation.VERTICAL) {
            // Vertical tube: width is smaller, height fills the view
            tubeWidth = width * 0.6f
            tubeHeight = height * 0.9f
            // Sensitivity: full tube height for ±45 degrees range
            // So each degree moves bubble by tubeHeight / 90 pixels
            sensitivity = tubeHeight / 90f
        } else {
            // Horizontal tube: height is smaller, width fills the view
            tubeWidth = width * 0.9f
            tubeHeight = height * 0.6f
            // Sensitivity: full tube width for ±45 degrees range
            sensitivity = tubeWidth / 90f
        }
        
        bubbleRadius = minOf(tubeWidth, tubeHeight) * 0.15f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Update bubble position with smoothing first
        updateBubblePosition()
        
        // Draw the curved tube
        drawTube(canvas)
        
        // Draw center indicator line
        drawCenterLine(canvas)
        
        // Draw bubble
        drawBubble(canvas)
        
        // Invalidate to trigger redraw for smooth animation
        // Always invalidate to keep animation loop running (same as LevelView)
        invalidate()
    }

    /**
     * Draw the curved spirit level tube
     */
    private fun drawTube(canvas: Canvas) {
        val path = Path()
        
        if (orientation == Orientation.VERTICAL) {
            // Vertical tube: curved from top to bottom
            val left = centerX - tubeWidth / 2f
            val right = centerX + tubeWidth / 2f
            val top = centerY - tubeHeight / 2f
            val bottom = centerY + tubeHeight / 2f
            
            // Create curved tube path (bulging outward)
            val curveHeight = tubeWidth * 0.3f // How much the tube bulges
            
            path.moveTo(left, top)
            // Left side curve
            path.quadTo(
                left - curveHeight,
                centerY,
                left,
                bottom
            )
            // Bottom curve
            path.quadTo(
                centerX,
                bottom + curveHeight * 0.5f,
                right,
                bottom
            )
            // Right side curve
            path.quadTo(
                right + curveHeight,
                centerY,
                right,
                top
            )
            // Top curve
            path.quadTo(
                centerX,
                top - curveHeight * 0.5f,
                left,
                top
            )
            path.close()
        } else {
            // Horizontal tube: curved from left to right
            val left = centerX - tubeWidth / 2f
            val right = centerX + tubeWidth / 2f
            val top = centerY - tubeHeight / 2f
            val bottom = centerY + tubeHeight / 2f
            
            // Create curved tube path (bulging upward)
            val curveWidth = tubeHeight * 0.3f
            
            path.moveTo(left, top)
            // Top curve
            path.quadTo(
                centerX,
                top - curveWidth,
                right,
                top
            )
            // Right side curve
            path.quadTo(
                right + curveWidth * 0.5f,
                centerY,
                right,
                bottom
            )
            // Bottom curve
            path.quadTo(
                centerX,
                bottom + curveWidth,
                left,
                bottom
            )
            // Left side curve
            path.quadTo(
                left - curveWidth * 0.5f,
                centerY,
                left,
                top
            )
            path.close()
        }
        
        canvas.drawPath(path, tubePaint)
    }

    /**
     * Draw center indicator line
     */
    private fun drawCenterLine(canvas: Canvas) {
        if (orientation == Orientation.VERTICAL) {
            // Vertical line in the center
            canvas.drawLine(
                centerX,
                centerY - tubeHeight / 2f,
                centerX,
                centerY + tubeHeight / 2f,
                centerLinePaint
            )
        } else {
            // Horizontal line in the center
            canvas.drawLine(
                centerX - tubeWidth / 2f,
                centerY,
                centerX + tubeWidth / 2f,
                centerY,
                centerLinePaint
            )
        }
    }

    /**
     * Draw the bubble indicator
     */
    private fun drawBubble(canvas: Canvas) {
        if (orientation == Orientation.VERTICAL) {
            // Vertical: bubble moves up/down based on pitch
            // Positive pitch (forward tilt) -> bubble moves DOWN (away from user)
            // Negative pitch (backward tilt) -> bubble moves UP (toward user)
            // Calculate bubble position directly from currentBubblePosition
            val maxOffset = tubeHeight / 2f - bubbleRadius
            val offset = currentBubblePosition * maxOffset // Use maxOffset instead of sensitivity
            val bubbleY = centerY + offset
            // Clamp to ensure bubble stays within tube bounds
            val clampedY = bubbleY.coerceIn(
                centerY - maxOffset,
                centerY + maxOffset
            )
            
            // Change color based on level status
            updateBubbleColor(abs(tiltAngle))
            canvas.drawCircle(centerX, clampedY, bubbleRadius, bubblePaint)
            
            // Draw highlight for 3D effect
            val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = ContextCompat.getColor(context, R.color.white)
                alpha = 100
            }
            canvas.drawCircle(
                centerX - bubbleRadius * 0.2f,
                clampedY - bubbleRadius * 0.2f,
                bubbleRadius * 0.6f,
                highlightPaint
            )
        } else {
            // Horizontal: bubble moves left/right based on roll
            // Positive roll (right tilt) -> bubble moves RIGHT
            // Negative roll (left tilt) -> bubble moves LEFT
            // Calculate bubble position directly from currentBubblePosition
            val maxOffset = tubeWidth / 2f - bubbleRadius
            val offset = currentBubblePosition * maxOffset // Use maxOffset instead of sensitivity
            val bubbleX = centerX + offset
            // Clamp to ensure bubble stays within tube bounds
            val clampedX = bubbleX.coerceIn(
                centerX - maxOffset,
                centerX + maxOffset
            )
            
            // Change color based on level status
            updateBubbleColor(abs(tiltAngle))
            canvas.drawCircle(clampedX, centerY, bubbleRadius, bubblePaint)
            
            // Draw highlight for 3D effect
            val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = ContextCompat.getColor(context, R.color.white)
                alpha = 100
            }
            canvas.drawCircle(
                clampedX - bubbleRadius * 0.2f,
                centerY - bubbleRadius * 0.2f,
                bubbleRadius * 0.6f,
                highlightPaint
            )
        }
    }

    /**
     * Update bubble color based on level status
     */
    private fun updateBubbleColor(angle: Float) {
        bubblePaint.color = when {
            angle < levelThreshold -> ContextCompat.getColor(context, R.color.level_green)
            angle < 10f -> ContextCompat.getColor(context, R.color.tilted_orange)
            else -> ContextCompat.getColor(context, R.color.tilted_red)
        }
    }

    /**
     * Update bubble position with smoothing
     */
    private fun updateBubblePosition() {
        currentBubblePosition += (targetBubblePosition - currentBubblePosition) * smoothingFactor
    }

    /**
     * Update tilt angle and calculate bubble position
     * @param angle Tilt angle in degrees (positive/negative indicates direction)
     * 
     * For vertical tube (pitch):
     *   - Positive angle (forward tilt) -> bubble moves DOWN (positive Y offset)
     *   - Negative angle (backward tilt) -> bubble moves UP (negative Y offset)
     * 
     * For horizontal tube (roll):
     *   - Positive angle (right tilt) -> bubble moves RIGHT (positive X offset)
     *   - Negative angle (left tilt) -> bubble moves LEFT (negative X offset)
     */
    fun updateTilt(angle: Float) {
        this.tiltAngle = angle
        // Map angle to normalized bubble position (-1 to +1)
        // -45 degrees maps to -1 (top/left), +45 degrees maps to +1 (bottom/right)
        // 0 degrees maps to 0 (center)
        targetBubblePosition = (angle / 45f).coerceIn(-1f, 1f)
        // Invalidate to trigger redraw with new target position
        invalidate()
    }
}
