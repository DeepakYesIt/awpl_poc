package com.bussiness.awpl.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View

class ShimmerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : View(context, attrs) {



    private val paint = Paint()
    private val gradient: LinearGradient
    private val gradientMatrix = Matrix()
    private var translateX = 0f
    private var viewWidth = 0
    private var shimmerSpeed = 10f

    private val shimmerColor = intArrayOf(
        Color.LTGRAY,
        Color.WHITE,
        Color.LTGRAY
    )

    private val handler = Handler(Looper.getMainLooper())
    private var shimmerRunnable: Runnable? = null

    init {
        paint.isAntiAlias = true

        // Set up a default gradient
        gradient = LinearGradient(
            -viewWidth.toFloat(), 0f, 0f, 0f,
            shimmerColor, floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP
        )
        paint.shader = gradient
    }

    // Start shimmer animation
    fun startShimmer() {
        // Ensure that no existing animation is running
        stopShimmer()

        shimmerRunnable = object : Runnable {
            override fun run() {
                translateX += shimmerSpeed
                if (viewWidth > 0) {
                    if (translateX > 2 * viewWidth) {
                        translateX = -viewWidth.toFloat()
                    }
                    gradientMatrix.setTranslate(translateX, 0f)
                    gradient.setLocalMatrix(gradientMatrix)
                    invalidate()
                }
                handler.postDelayed(this, 16) // Run every frame (60fps)
            }
        }

        handler.post(shimmerRunnable!!)
    }

    // Stop shimmer animation
    fun stopShimmer() {
        shimmerRunnable?.let {
            handler.removeCallbacks(it)
        }
        shimmerRunnable = null
    }

    // Update on size change to configure the gradient properly
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        gradient.setLocalMatrix(gradientMatrix)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

}