package com.musarayy.watermarkdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.musarayy.watermark.WatermarkManager
import com.musarayy.watermark.WatermarkView

class MainActivity : AppCompatActivity() {

    private lateinit var previewImageView: ImageView
    private lateinit var watermarkView: WatermarkView
    private lateinit var textWatermarkInput: EditText
    private lateinit var textSizeSeekBar: SeekBar
    private lateinit var textAlphaSeekBar: SeekBar
    private lateinit var spacingSeekBar: SeekBar
    private lateinit var angleSeekBar: SeekBar
    private lateinit var textSizeValue: TextView
    private lateinit var textAlphaValue: TextView
    private lateinit var spacingValue: TextView
    private lateinit var angleValue: TextView
    private lateinit var watermarkTypeRadioGroup: RadioGroup
    private lateinit var applyButton: Button

    private var demoBitmap: Bitmap? = null
    private var watermarkBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        previewImageView = findViewById(R.id.previewImageView)
        textWatermarkInput = findViewById(R.id.textWatermarkInput)
        textSizeSeekBar = findViewById(R.id.textSizeSeekBar)
        textAlphaSeekBar = findViewById(R.id.textAlphaSeekBar)
        spacingSeekBar = findViewById(R.id.spacingSeekBar)
        angleSeekBar = findViewById(R.id.angleSeekBar)
        textSizeValue = findViewById(R.id.textSizeValue)
        textAlphaValue = findViewById(R.id.textAlphaValue)
        spacingValue = findViewById(R.id.spacingValue)
        angleValue = findViewById(R.id.angleValue)
        watermarkTypeRadioGroup = findViewById(R.id.watermarkTypeRadioGroup)
        applyButton = findViewById(R.id.applyButton)

        // Create a placeholder demo image
        val demoBitmapWidth = 500
        val demoBitmapHeight = 300
        demoBitmap = Bitmap.createBitmap(demoBitmapWidth, demoBitmapHeight, Bitmap.Config.ARGB_8888)
        val demoCanvas = Canvas(demoBitmap!!)

        // Fill background
        val bgPaint = Paint()
        bgPaint.color = Color.rgb(200, 200, 255) // Light blue
        bgPaint.style = Paint.Style.FILL
        demoCanvas.drawRect(0f, 0f, demoBitmapWidth.toFloat(), demoBitmapHeight.toFloat(), bgPaint)

        // Draw some shapes
        val shapePaint = Paint()
        shapePaint.color = Color.rgb(100, 100, 200) // Darker blue
        shapePaint.style = Paint.Style.FILL
        demoCanvas.drawCircle(demoBitmapWidth / 3f, demoBitmapHeight / 2f, 50f, shapePaint)
        demoCanvas.drawRect(
            demoBitmapWidth / 2f, 
            demoBitmapHeight / 3f, 
            demoBitmapWidth * 0.8f, 
            demoBitmapHeight * 0.7f, 
            shapePaint
        )

        previewImageView.setImageBitmap(demoBitmap)

        // Create a placeholder watermark logo
        val logoBitmapSize = 100
        watermarkBitmap = Bitmap.createBitmap(logoBitmapSize, logoBitmapSize, Bitmap.Config.ARGB_8888)
        val logoCanvas = Canvas(watermarkBitmap!!)

        // Fill background
        val logoBgPaint = Paint()
        logoBgPaint.color = Color.rgb(255, 100, 100) // Light red
        logoBgPaint.style = Paint.Style.FILL
        logoCanvas.drawRect(0f, 0f, logoBitmapSize.toFloat(), logoBitmapSize.toFloat(), logoBgPaint)

        // Draw a simple logo
        val logoPaint = Paint()
        logoPaint.color = Color.WHITE
        logoPaint.style = Paint.Style.STROKE
        logoPaint.strokeWidth = 5f
        logoCanvas.drawCircle(logoBitmapSize / 2f, logoBitmapSize / 2f, 40f, logoPaint)
        logoCanvas.drawLine(
            logoBitmapSize * 0.3f, 
            logoBitmapSize * 0.3f, 
            logoBitmapSize * 0.7f, 
            logoBitmapSize * 0.7f, 
            logoPaint
        )
        logoCanvas.drawLine(
            logoBitmapSize * 0.3f, 
            logoBitmapSize * 0.7f, 
            logoBitmapSize * 0.7f, 
            logoBitmapSize * 0.3f, 
            logoPaint
        )

        // Set up seek bars
        setupSeekBars()

        // Set up radio group for watermark type
        watermarkTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            updateUIForWatermarkType(checkedId)
        }

        // Set up apply button
        applyButton.setOnClickListener {
            applyWatermark()
        }

        // Apply a default watermark
        applyDefaultWatermark()
    }

    private fun setupSeekBars() {
        // Text size: 20-100
        textSizeSeekBar.max = 80
        textSizeSeekBar.progress = 20 // Default: 40
        textSizeValue.text = "40"

        // Alpha: 10-100%
        textAlphaSeekBar.max = 90
        textAlphaSeekBar.progress = 40 // Default: 50%
        textAlphaValue.text = "50%"

        // Spacing: 100-500
        spacingSeekBar.max = 400
        spacingSeekBar.progress = 100 // Default: 200
        spacingValue.text = "200"

        // Angle: -90 to 90
        angleSeekBar.max = 180
        angleSeekBar.progress = 90 // Default: 0 (horizontal)
        angleValue.text = "0°"

        // Set up listeners
        textSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 20
                textSizeValue.text = value.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        textAlphaSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 10
                textAlphaValue.text = "$value%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        spacingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 100
                spacingValue.text = value.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        angleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress - 90
                angleValue.text = "$value°"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateUIForWatermarkType(checkedId: Int) {
        if (checkedId == R.id.radioText) {
            textWatermarkInput.visibility = View.VISIBLE
            textSizeSeekBar.visibility = View.VISIBLE
            textSizeValue.visibility = View.VISIBLE
            findViewById<TextView>(R.id.textSizeLabel).visibility = View.VISIBLE
        } else {
            textWatermarkInput.visibility = View.GONE
            textSizeSeekBar.visibility = View.GONE
            textSizeValue.visibility = View.GONE
            findViewById<TextView>(R.id.textSizeLabel).visibility = View.GONE
        }
    }

    private fun applyWatermark() {
        // Remove previous watermark if exists
        if (::watermarkView.isInitialized) {
            (previewImageView.parent as? View)?.let { parent ->
                WatermarkManager.removeWatermark(parent, watermarkView)
            }
        }

        val isTextWatermark = watermarkTypeRadioGroup.checkedRadioButtonId == R.id.radioText

        if (isTextWatermark) {
            // Apply text watermark
            val text = textWatermarkInput.text.toString().takeIf { it.isNotEmpty() } ?: "WATERMARK"
            val textSize = textSizeSeekBar.progress + 20f
            val alpha = textAlphaSeekBar.progress + 10
            val textColor = Color.argb((alpha * 255 / 100), 255, 0, 0) // Red with variable alpha
            val spacing = spacingSeekBar.progress + 100
            val angle = angleSeekBar.progress - 90f

            watermarkView = WatermarkManager.addTextWatermark(
                view = previewImageView,
                text = text,
                textColor = textColor,
                textSize = textSize,
                spacing = spacing,
                angle = angle
            )
        } else {
            // Apply bitmap watermark
            watermarkBitmap?.let { bitmap ->
                val alpha = textAlphaSeekBar.progress + 10
                val spacing = spacingSeekBar.progress + 100
                val angle = angleSeekBar.progress - 90f

                watermarkView = WatermarkManager.addBitmapWatermark(
                    view = previewImageView,
                    bitmap = bitmap,
                    alpha = (alpha * 255 / 100),
                    spacing = spacing,
                    angle = angle
                )
            }
        }
    }

    private fun applyDefaultWatermark() {
        // Apply a default text watermark
        textWatermarkInput.setText("SAMPLE")
        watermarkTypeRadioGroup.check(R.id.radioText)
        updateUIForWatermarkType(R.id.radioText)
        applyWatermark()
    }
}
