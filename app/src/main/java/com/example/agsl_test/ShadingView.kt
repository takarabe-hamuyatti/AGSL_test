package com.example.agsl_test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RuntimeShader
import android.util.AttributeSet
import android.view.View

private const val COLOR_SHADER_SRC =
    """uniform float2 iResolution;
   half4 main(float2 fragCoord) {
      float2 scaled = fragCoord/iResolution.xy;
      return half4(scaled, 0, 1);
   }"""


class ShadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val fixedColorShader = RuntimeShader(COLOR_SHADER_SRC)
    private val paint = Paint()

    override fun onDrawForeground(canvas: Canvas?) {
        canvas?.let {
            fixedColorShader.setFloatUniform("iResolution", width.toFloat(), height.toFloat())
            canvas.drawPaint(paint)
        }
    }

}