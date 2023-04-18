package com.example.agsl_test

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RuntimeShader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator


private const val COLOR_SHADER_SRC =
    """uniform float2 iResolution;
   half4 main(float2 fragCoord) {
      float2 scaled = fragCoord/iResolution.xy;
      return half4(scaled, 0, 1);
   }"""


private const val COLOR_SHADER_SRC2 = """
            uniform float2 iResolution;
            uniform float iTime;
            uniform float iDuration;
            half4 main(in float2 fragCoord) {
                float2 scaled = abs(1.0-mod(fragCoord/iResolution.xy+iTime/(iDuration/2.0),2.0));
                return half4(scaled, 0, 1.0);
            }
        """

private const val DURATION = 4000f

class AnimateText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    // private val fixedColorShader = RuntimeShader(COLOR_SHADER_SRC)
    private val animatedShader = RuntimeShader(COLOR_SHADER_SRC2)
    private val paint = Paint()

    init {
        animatedShader.setFloatUniform("iDuration", DURATION)

        val valueCreator = ValueAnimator.ofFloat(0f, DURATION)
        valueCreator.duration = DURATION.toLong()
        valueCreator.repeatCount = ValueAnimator.INFINITE
        valueCreator.repeatMode = ValueAnimator.RESTART
        valueCreator.interpolator = LinearInterpolator()
        valueCreator.addUpdateListener { animation ->
            animatedShader.setFloatUniform("iTime", animation.animatedValue as Float)
            invalidate()
        }
        paint.shader = animatedShader
        paint.textSize = 220f

        valueCreator.start()
    }

    override fun onDrawForeground(canvas: Canvas?) {
        canvas?.let {
            animatedShader.setFloatUniform("iResolution", width.toFloat(), height.toFloat())
            canvas.drawText("hello", 300f, 300f, paint)
        }
    }
}