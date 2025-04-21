package com.musarayy.watermark

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * A custom view that displays a diagonal watermark across the screen.
 * This can be used to visually indicate that the app is running in debug mode,
 * or to add a watermark to images or screens.
 */
class WatermarkView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * The text to display as watermark
     */
    var text: String = "Watermark"
        set(value) {
            field = value
            invalidate()
        }

    /**
     * The color of the watermark text
     */
    var textColor: Int = Color.argb(50, 255, 0, 0) // Semi-transparent red by default
        set(value) {
            field = value
            textPaint.color = value
            invalidate()
        }

    /**
     * The size of the watermark text
     */
    var textSize: Float = 40f
        set(value) {
            field = value
            textPaint.textSize = value
            invalidate()
        }

    /**
     * The spacing between watermark texts
     */
    var spacing: Int = 200
        set(value) {
            field = value
            invalidate()
        }

    /**
     * The angle of the watermark text in degrees
     */
    var angle: Float = -45f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * The bitmap to display as watermark instead of text
     */
    var watermarkBitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    /**
     * The alpha value for the bitmap (0-255)
     */
    var bitmapAlpha: Int = 50
        set(value) {
            field = value.coerceIn(0, 255)
            invalidate()
        }

    private val textPaint = Paint().apply {
        color = textColor
        textSize = this@WatermarkView.textSize
        isAntiAlias = true
    }

    private val bitmapPaint = Paint().apply {
        isAntiAlias = true
        alpha = bitmapAlpha
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val diagonal = Math.sqrt((width * width + height * height).toDouble()).toFloat()

        // Save the canvas state
        canvas.save()

        // Rotate the canvas to draw diagonally
        canvas.rotate(angle, width / 2, height / 2)

        if (watermarkBitmap != null) {
            drawBitmapWatermark(canvas, diagonal)
        } else {
            drawTextWatermark(canvas, diagonal)
        }

        // Restore the canvas to its original state
        canvas.restore()
    }

    private fun drawTextWatermark(canvas: Canvas, diagonal: Float) {
        // Calculate how many lines we need to cover the entire screen
        val numLines = (diagonal * 2 / spacing).toInt() + 2

        // Calculate the starting position to ensure full coverage
        val startX = -diagonal
        val startY = -diagonal

        // Draw the watermark text multiple times
        for (i in 0 until numLines) {
            val y = startY + i * spacing
            for (j in 0 until numLines) {
                val x = startX + j * spacing
                canvas.drawText(text, x, y, textPaint)
            }
        }
    }

    private fun drawBitmapWatermark(canvas: Canvas, diagonal: Float) {
        val bitmap = watermarkBitmap ?: return
        
        // Calculate the size of the bitmap to fit within the spacing
        val bitmapSize = min(spacing * 0.8f, min(bitmap.width, bitmap.height).toFloat())
        val scale = bitmapSize / bitmap.width
        
        val scaledWidth = bitmap.width * scale
        val scaledHeight = bitmap.height * scale
        
        // Calculate how many bitmaps we need to cover the entire screen
        val numLines = (diagonal * 2 / spacing).toInt() + 2

        // Calculate the starting position to ensure full coverage
        val startX = -diagonal
        val startY = -diagonal

        // Draw the watermark bitmap multiple times
        for (i in 0 until numLines) {
            val y = startY + i * spacing
            for (j in 0 until numLines) {
                val x = startX + j * spacing
                
                val destRect = Rect(
                    x.toInt(),
                    y.toInt(),
                    (x + scaledWidth).toInt(),
                    (y + scaledHeight).toInt()
                )
                
                canvas.drawBitmap(bitmap, null, destRect, bitmapPaint)
            }
        }
    }
}