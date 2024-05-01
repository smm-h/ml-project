package src.main.gui

import src.main.gui.layerview.LayerView
import src.main.mlp.Weights
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class WeightsView(
    val weights: Weights,
    val l: LayerView,
    val r: LayerView,
    val lY: Float,
    val rY: Float,
    val gapSize: Float,
    val vSize: Float,
    val alphaPower: Int,
    val alphaFactor: Float,
    val minimumVisibleAlpha: Float,
) {

    var x: Float = 0f
    var y: Float = 0f

    var enabled: Boolean = true

    //    Area.Circular
//    override val radius: Float
//        get() = gapSize

    val hSize get() = l.w + gapSize + r.w
//        val vSize get() = max(l.vSize, r.vSize)

    private val divisor: Float =
        (l.cellCount + r.cellCount) / 32f

    fun draw(g: Graphics2D) {
        if (enabled)
            g.drawImage(enabledImage, x.roundToInt(), y.roundToInt(), null)
    }

    private val enabledImage by lazy {
        BufferedImage(
            hSize.roundToInt(),
            vSize.roundToInt(),
            BufferedImage.TYPE_INT_ARGB,
        ).also {
            val g = GUIUtil.getSmoothGraphics(it.graphics)
            r.forEach { nextNeuron ->
                l.forEach { currNeuron ->
                    val weight = weights.getWeight(nextNeuron, currNeuron)
                    val alpha = (abs(weight).pow(alphaPower) * alphaFactor / divisor).coerceIn(0f, 1f)
                    if (alpha >= minimumVisibleAlpha) {
                        g.color =
                            if (weight > 0f)
                                Color(0f, 1f, 0f, alpha) // green
                            else
                                Color(1f, 0f, 0f, alpha) // red
                        g.drawLine(
                            (l.getCellCenterX(currNeuron)).roundToInt(),
                            (l.getCellCenterY(currNeuron) + lY).roundToInt(),
                            (r.getCellCenterX(nextNeuron) + l.w + gapSize).roundToInt(),
                            (r.getCellCenterY(nextNeuron) + rY).roundToInt(),
                        )
                    }
                }
            }
        }
    }

    private val disabledImage by lazy {
        BufferedImage(
            hSize.roundToInt(),
            vSize.roundToInt(),
            BufferedImage.TYPE_INT_ARGB,
        ).also {
            val g = GUIUtil.getSmoothGraphics(it.graphics)
            val alpha = (1f * alphaFactor / divisor).coerceIn(0f, 1f)
            g.color = GUIUtil.gray(0.5f, alpha)
            r.forEach { nextNeuron ->
                l.forEach { currNeuron ->
                    g.drawLine(
                        (l.getCellCenterX(currNeuron)).roundToInt(),
                        (l.getCellCenterY(currNeuron) + lY).roundToInt(),
                        (r.getCellCenterX(nextNeuron) + l.w + gapSize).roundToInt(),
                        (r.getCellCenterY(nextNeuron) + rY).roundToInt(),
                    )
                }
            }
        }
    }
}