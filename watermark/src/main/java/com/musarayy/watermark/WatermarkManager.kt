package com.musarayy.watermark

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * A utility class to easily add watermarks to activities or views.
 */
object WatermarkManager {

    /**
     * Adds a text watermark to an activity.
     *
     * @param activity The activity to add the watermark to
     * @param text The text to display as watermark
     * @param textColor The color of the watermark text (default: semi-transparent red)
     * @param textSize The size of the watermark text (default: 40f)
     * @param spacing The spacing between watermark texts (default: 200)
     * @param angle The angle of the watermark text in degrees (default: -45f)
     * @return The created WatermarkView instance
     */
    @JvmOverloads
    fun addTextWatermark(
        activity: Activity,
        text: String,
        textColor: Int = Color.argb(50, 255, 0, 0),
        textSize: Float = 40f,
        spacing: Int = 200,
        angle: Float = -45f
    ): WatermarkView {
        return addWatermarkToActivity(activity) { context ->
            WatermarkView(context).apply {
                this.text = text
                this.textColor = textColor
                this.textSize = textSize
                this.spacing = spacing
                this.angle = angle
            }
        }
    }

    /**
     * Adds a bitmap watermark to an activity.
     *
     * @param activity The activity to add the watermark to
     * @param bitmap The bitmap to display as watermark
     * @param alpha The alpha value for the bitmap (0-255) (default: 50)
     * @param spacing The spacing between watermark bitmaps (default: 200)
     * @param angle The angle of the watermark in degrees (default: -45f)
     * @return The created WatermarkView instance
     */
    @JvmOverloads
    fun addBitmapWatermark(
        activity: Activity,
        bitmap: Bitmap,
        alpha: Int = 50,
        spacing: Int = 200,
        angle: Float = -45f
    ): WatermarkView {
        return addWatermarkToActivity(activity) { context ->
            WatermarkView(context).apply {
                this.watermarkBitmap = bitmap
                this.bitmapAlpha = alpha
                this.spacing = spacing
                this.angle = angle
            }
        }
    }

    /**
     * Adds a text watermark to a view.
     *
     * @param view The view to add the watermark to
     * @param text The text to display as watermark
     * @param textColor The color of the watermark text (default: semi-transparent red)
     * @param textSize The size of the watermark text (default: 40f)
     * @param spacing The spacing between watermark texts (default: 200)
     * @param angle The angle of the watermark text in degrees (default: -45f)
     * @return The created WatermarkView instance
     */
    @JvmOverloads
    fun addTextWatermark(
        view: View,
        text: String,
        textColor: Int = Color.argb(50, 255, 0, 0),
        textSize: Float = 40f,
        spacing: Int = 200,
        angle: Float = -45f
    ): WatermarkView {
        return addWatermarkToView(view) { context ->
            WatermarkView(context).apply {
                this.text = text
                this.textColor = textColor
                this.textSize = textSize
                this.spacing = spacing
                this.angle = angle
            }
        }
    }

    /**
     * Adds a bitmap watermark to a view.
     *
     * @param view The view to add the watermark to
     * @param bitmap The bitmap to display as watermark
     * @param alpha The alpha value for the bitmap (0-255) (default: 50)
     * @param spacing The spacing between watermark bitmaps (default: 200)
     * @param angle The angle of the watermark in degrees (default: -45f)
     * @return The created WatermarkView instance
     */
    @JvmOverloads
    fun addBitmapWatermark(
        view: View,
        bitmap: Bitmap,
        alpha: Int = 50,
        spacing: Int = 200,
        angle: Float = -45f
    ): WatermarkView {
        return addWatermarkToView(view) { context ->
            WatermarkView(context).apply {
                this.watermarkBitmap = bitmap
                this.bitmapAlpha = alpha
                this.spacing = spacing
                this.angle = angle
            }
        }
    }

    /**
     * Adds a debug watermark to an activity.
     * This is a convenience method for adding a "Dev Only" watermark in debug builds.
     *
     * @param activity The activity to add the watermark to
     * @return The created WatermarkView instance
     */
    fun addDebugWatermark(activity: Activity): WatermarkView {
        return addTextWatermark(
            activity = activity,
            text = "Dev Only",
            textColor = Color.argb(50, 255, 0, 0),
            textSize = 40f,
            spacing = 200,
            angle = -45f
        )
    }

    /**
     * Removes a watermark from a view.
     *
     * @param parent The parent view that contains the watermark
     * @param watermarkView The watermark view to remove
     */
    fun removeWatermark(parent: View, watermarkView: WatermarkView) {
        // Find the parent of the watermark view
        val watermarkParent = watermarkView.parent as? ViewGroup ?: return

        // If the parent is a FrameLayout and has exactly 2 children (the original view and the watermark)
        if (watermarkParent is FrameLayout && watermarkParent.childCount == 2) {
            // Find the original view (the one that's not the watermark)
            val originalView = if (watermarkParent.getChildAt(0) == watermarkView) {
                watermarkParent.getChildAt(1)
            } else {
                watermarkParent.getChildAt(0)
            }

            // Find the parent of the FrameLayout
            val frameLayoutParent = watermarkParent.parent as? ViewGroup ?: return
            val index = frameLayoutParent.indexOfChild(watermarkParent)

            // Remove the FrameLayout from its parent
            frameLayoutParent.removeView(watermarkParent)

            // Remove the original view from the FrameLayout
            watermarkParent.removeView(originalView)

            // Add the original view back to the parent at the same position
            frameLayoutParent.addView(originalView, index)
        } else {
            // Just remove the watermark view from its parent
            watermarkParent.removeView(watermarkView)
        }
    }

    /**
     * Helper method to add a watermark to an activity.
     */
    private fun addWatermarkToActivity(
        activity: Activity,
        createWatermarkView: (Context) -> WatermarkView
    ): WatermarkView {
        // Find the content view
        val content = activity.findViewById<ViewGroup>(android.R.id.content)
        val watermarkView = createWatermarkView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Add the watermark view on top of the content
        if (content is FrameLayout) {
            content.addView(watermarkView)
        } else {
            // If content is not a FrameLayout, wrap it in one
            val rootView = content.getChildAt(0)
            content.removeView(rootView)

            val frameLayout = FrameLayout(activity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            frameLayout.addView(rootView)
            frameLayout.addView(watermarkView)
            content.addView(frameLayout)
        }

        return watermarkView
    }

    /**
     * Helper method to add a watermark to a view.
     */
    private fun addWatermarkToView(
        view: View,
        createWatermarkView: (Context) -> WatermarkView
    ): WatermarkView {
        val context = view.context
        val parent = view.parent as? ViewGroup ?: throw IllegalArgumentException("View must have a parent")

        // Create a FrameLayout to hold the view and watermark
        val frameLayout = FrameLayout(context).apply {
            layoutParams = view.layoutParams
        }

        // Remove the view from its parent
        val index = parent.indexOfChild(view)
        parent.removeView(view)

        // Add the view to the FrameLayout
        frameLayout.addView(view.apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        })

        // Create and add the watermark view
        val watermarkView = createWatermarkView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        frameLayout.addView(watermarkView)

        // Add the FrameLayout to the parent
        parent.addView(frameLayout, index)

        return watermarkView
    }
}
