package src.main.gui

import java.awt.*
import kotlin.math.roundToInt

object GUIUtil {

    val PI = kotlin.math.PI.toFloat()

    infix fun Int.by(that: Int) =
        Dimension(this, that)

    fun gray(value: Float, alpha: Float = 1f) =
        Color(value, value, value, alpha)

    val HALF_BLACK =
        gray(0f, 0.5f)
    val QUARTER_BLACK =
        gray(0f, 0.25f)
    val HALF_GRAY =
        gray(0.5f, 0.5f)
    val QUARTER_GRAY =
        gray(0.5f, 0.25f)

    fun getSmoothGraphics(g: Graphics?) = (g as Graphics2D).also {
        it.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        it.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )
        it.setRenderingHint(
            RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
        )
        it.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
        it.setRenderingHint(
            RenderingHints.KEY_STROKE_CONTROL,
            RenderingHints.VALUE_STROKE_PURE
        )
    }

    fun drawOutline(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, margin: Float) {
        val arc = (margin * 2).roundToInt().coerceAtLeast(1)
        g.color = Color.BLUE
        g.drawRoundRect(
            (x - margin).roundToInt(),
            (y - margin).roundToInt(),
            (w + margin * 2).roundToInt(),
            (h + margin * 2).roundToInt(),
            arc, arc,
        )
    }
}