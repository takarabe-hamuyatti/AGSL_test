package com.example.agsl_test

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class RotateText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val ANIMATED_TEXT = "Color"
        private const val DURATION = 4000f
        private const val COLOR_SHADER_SRC = """
            uniform float2 iResolution;
            uniform float iTime;
            uniform float iDuration;
            half4 main(in float2 fragCoord) {
                float2 scaled = abs(1.0-mod(fragCoord/iResolution.xy+iTime/(iDuration/2.0),2.0));
                return half4(scaled, 0, 1.0);
            }
        """
    }

    private val animatedShader = RuntimeShader(COLOR_SHADER_SRC)
    private val paint = Paint().apply {
        textSize = 260f
        shader = animatedShader
    }
    private val camera = Camera()
    private val rotationMatrix = Matrix()
    private val bounds = Rect()

    // declare the ValueAnimator
    private val shaderAnimator = ValueAnimator.ofFloat(0f, DURATION)

    init {
        // use it to animate the time uniform
        shaderAnimator.duration = DURATION.toLong()
        shaderAnimator.repeatCount = ValueAnimator.INFINITE
        shaderAnimator.repeatMode = ValueAnimator.RESTART
        shaderAnimator.interpolator = LinearInterpolator()

        animatedShader.setFloatUniform("iDuration", DURATION)
        shaderAnimator.addUpdateListener { animation ->
            animatedShader.setFloatUniform("iTime", animation.animatedValue as Float)
            camera.rotate(0.0f, animation.animatedValue as Float / DURATION * 360f / 100f, 0.0f)
            invalidate()
        }
        shaderAnimator.start()
    }

    override fun onDrawForeground(canvas: Canvas?) {
        canvas?.let {
            animatedShader.setFloatUniform("iResolution", width.toFloat(), height.toFloat())

            camera.getMatrix(rotationMatrix)
            paint.getTextBounds(ANIMATED_TEXT, 0, ANIMATED_TEXT.length, bounds)

            val centerX = (bounds.width().toFloat()) / 2
            val centerY = (bounds.height().toFloat()) / 2

            rotationMatrix.preTranslate(-centerX, -centerY)
            rotationMatrix.postTranslate(centerX, centerY)

            canvas.save()
            canvas.concat(rotationMatrix)
            canvas.drawText(ANIMATED_TEXT, 0f, 0f + bounds.height(), paint)
            canvas.restore()
        }
    }
}