/*Code from https://github.com/yuriy-budiyev/code-scanner/blob/master/src/main/java/com/budiyev/android/codescanner/ViewFinderView.java
frame ui design component (shit didnt work with they xml so had to find a workaround)
translated from java into kotlin
may not be perfect since it was translated by gpt
 */

package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px

class ViewfinderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var frameAspectRatioWidth: Int = 2
        set(value) {
            field = value.coerceAtLeast(1)
            updateFrameRect()
            invalidate()
        }

    var frameAspectRatioHeight: Int = 1
        set(value) {
            field = value.coerceAtLeast(1)
            updateFrameRect()
            invalidate()
        }


    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private var frameRect: RectF? = null

    @FloatRange(from = 0.1, to = 1.0)
    var frameSize: Float = .75f
        set(value) {
            field = value.coerceIn(0.1f, 1f)
            updateFrameRect()
            invalidate()
        }

    @ColorInt
    var maskColor: Int = Color.parseColor("#99000000")
        set(value) {
            field = value
            maskPaint.color = value
            invalidate()
        }

    @ColorInt
    var frameColor: Int = Color.WHITE
        set(value) {
            field = value
            framePaint.color = value
            invalidate()
        }

    @ColorInt
    var cornerColor: Int = Color.WHITE
        set(value) {
            field = value
            cornerPaint.color = value
            invalidate()
        }

    @Px
    var frameThickness: Int = dpToPx(1)
        set(value) {
            field = value
            framePaint.strokeWidth = value.toFloat()
            invalidate()
        }

    @Px
    var cornerThickness: Int = dpToPx(4)
        set(value) {
            field = value
            cornerPaint.strokeWidth = value.toFloat()
            invalidate()
        }

    @Px
    var frameCornersSize: Int = dpToPx(48)
        set(value) {
            field = value
            invalidate()
        }

    @Px
    var frameCornersRadius: Int = dpToPx(10)
        set(value) {
            field = value
            invalidate()
        }

    init {
        setupAttributes(attrs)
        setupPaints()
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView)
        try {
            // Add these lines
            frameAspectRatioWidth = typedArray.getInt(
                R.styleable.ViewfinderView_FrameAspectRatioWidth,
                2
            )
            frameAspectRatioHeight = typedArray.getInt(
                R.styleable.ViewfinderView_FrameAspectRatioHeight,
                1
            )
            // Rest of the existing code...
        } finally {
            typedArray.recycle()
        }
    }

    private fun setupPaints() {
        maskPaint.style = Paint.Style.FILL
        maskPaint.color = maskColor

        framePaint.style = Paint.Style.STROKE
        framePaint.color = frameColor
        framePaint.strokeWidth = frameThickness.toFloat()

        cornerPaint.style = Paint.Style.STROKE
        cornerPaint.color = cornerColor
        cornerPaint.strokeWidth = cornerThickness.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        frameRect?.let { rect ->
            drawMask(canvas, rect)
            drawOuterExtrusion(canvas, rect)
            drawFrame(canvas, rect)
            drawCorners(canvas, rect)
        }
    }

    private fun drawMask(canvas: Canvas, frame: RectF) {
        path.reset()
        path.addRect(0f, -10f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        path.addRoundRect(frame, frameCornersRadius.toFloat(), frameCornersRadius.toFloat(), Path.Direction.CCW)

        canvas.drawPath(path, maskPaint)
    }

    private fun drawFrame(canvas: Canvas, frame: RectF) {
        canvas.drawRoundRect(
            frame,
            frameCornersRadius.toFloat(),
            frameCornersRadius.toFloat(),
            framePaint
        )
    }

    private fun drawCorners(canvas: Canvas, frame: RectF) {
        val cornerLength = frameCornersSize.toFloat()
        val cornerRadius = frameCornersRadius.toFloat() // Define the corner radius

        // Paint for arcs
        val arcPaint = Paint(cornerPaint).apply {
            style = Paint.Style.STROKE
            strokeWidth = cornerPaint.strokeWidth
        }

        // Top-left corner
        canvas.drawArc(
            frame.left,
            frame.top,
            frame.left + 2 * cornerRadius,
            frame.top + 2 * cornerRadius,
            180f,
            90f,
            false,
            arcPaint
        )
        canvas.drawLine(frame.left + cornerRadius, frame.top, frame.left + cornerLength, frame.top, cornerPaint)
        canvas.drawLine(frame.left, frame.top + cornerRadius, frame.left, frame.top + cornerLength, cornerPaint)

        // Top-right corner
        canvas.drawArc(
            frame.right - 2 * cornerRadius,
            frame.top,
            frame.right,
            frame.top + 2 * cornerRadius,
            270f,
            90f,
            false,
            arcPaint
        )
        canvas.drawLine(frame.right - cornerRadius, frame.top, frame.right - cornerLength, frame.top, cornerPaint)
        canvas.drawLine(frame.right, frame.top + cornerRadius, frame.right, frame.top + cornerLength, cornerPaint)

        // Bottom-right corner
        canvas.drawArc(
            frame.right - 2 * cornerRadius,
            frame.bottom - 2 * cornerRadius,
            frame.right,
            frame.bottom,
            0f,
            90f,
            false,
            arcPaint
        )
        canvas.drawLine(frame.right - cornerRadius, frame.bottom, frame.right - cornerLength, frame.bottom, cornerPaint)
        canvas.drawLine(frame.right, frame.bottom - cornerRadius, frame.right, frame.bottom - cornerLength, cornerPaint)

        // Bottom-left corner
        canvas.drawArc(
            frame.left,
            frame.bottom - 2 * cornerRadius,
            frame.left + 2 * cornerRadius,
            frame.bottom,
            90f,
            90f,
            false,
            arcPaint
        )
        canvas.drawLine(frame.left + cornerRadius, frame.bottom, frame.left + cornerLength, frame.bottom, cornerPaint)
        canvas.drawLine(frame.left, frame.bottom - cornerRadius, frame.left, frame.bottom - cornerLength, cornerPaint)
    }

    private fun drawOuterExtrusion(canvas: Canvas, frame: RectF) {
        val extrusionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFFF00") // Set the extrusion color
            style = Paint.Style.FILL
        }
        val gradientPaint = Paint().apply {
            shader = LinearGradient(
                0f, frame.centerY() - 0.3f,
                0f, frame.centerY() + 12f,
                Color.BLACK,
                Color.parseColor("#E0E1DD"),
                Shader.TileMode.CLAMP
            )
        }

        val extrusionWidth = 150f
        val bottomExtrusionHeight = 100f
        val smallArcRadius = 16f
        val cornerRadius = frameCornersRadius.toFloat() // Use the frame's corner radius

        // Left extrusion
        canvas.drawRect(
            frame.left - extrusionWidth,
            frame.centerY(),
            frame.left,
            frame.bottom,
            gradientPaint
        )

        // Right extrusion
        canvas.drawRect(
            frame.right,
            frame.centerY(),
            frame.right + extrusionWidth,
            frame.bottom,
            gradientPaint
        )

        // Bottom extrusion
        canvas.drawRect(
            frame.left - extrusionWidth,
            frame.bottom,
            frame.right + extrusionWidth,
            frame.bottom + bottomExtrusionHeight,
            gradientPaint
        )

        // Draw top arches for left and right extrusions
        // Left extrusion top arch
        canvas.drawArc(
            frame.left - extrusionWidth - cornerRadius, // Left bound
            frame.centerY() - cornerRadius,             // Top bound
            frame.left - extrusionWidth + cornerRadius, // Right bound
            frame.centerY() + cornerRadius,             // Bottom bound
            180f,                                       // Start angle (left)
            -90f,                                       // Sweep angle (counter-clockwise 90)
            false,
            extrusionPaint
        )

        // Right extrusion top arch
        canvas.drawArc(
            frame.right + extrusionWidth - cornerRadius, // Left bound
            frame.centerY() - cornerRadius,              // Top bound
            frame.right + extrusionWidth + cornerRadius, // Right bound
            frame.centerY() + cornerRadius,              // Bottom bound
            0f,                                          // Start angle (right)
            -90f,                                        // Sweep angle (counter-clockwise 90)
            false,
            extrusionPaint
        )

        // Existing bottom corner arcs
        canvas.drawArc(
            frame.left + 13f - smallArcRadius,
            frame.bottom - 19f - smallArcRadius,
            frame.left + 12f + smallArcRadius,
            frame.bottom + smallArcRadius,
            90f,
            90f,
            true,
            gradientPaint
        )

        canvas.drawArc(
            frame.right - 13f - smallArcRadius,
            frame.bottom - 19f - smallArcRadius,
            frame.right - 12f + smallArcRadius,
            frame.bottom + smallArcRadius,
            0f,
            90f,
            true,
            gradientPaint
        )
    }





    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateFrameRect()
    }

    private fun updateFrameRect() {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Convert to float explicitly
        val aspectRatio = frameAspectRatioWidth.toFloat() / frameAspectRatioHeight.toFloat()

        // Calculate frame dimensions
        val frameWidth = viewWidth * frameSize
        val frameHeight = frameWidth / aspectRatio

        // Ensure we cast to float explicitly
        val left = (viewWidth - frameWidth) / 2f
        val top = (viewHeight - frameHeight) / 2f

        frameRect = RectF(
            left,
            top,
            left + frameWidth,
            top + frameHeight
        )
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}