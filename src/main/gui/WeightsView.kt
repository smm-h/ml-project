package src.main.gui

import src.main.gui.GUIUtil.createStill
import src.main.gui.GUIUtil.drawLineFloat
import src.main.gui.GUIUtil.drawStill
import src.main.gui.layerview.LayerView
import src.main.gui.vis.VHost
import src.main.gui.vis.Visual
import src.main.mlp.Weights
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.abs
import kotlin.math.pow

class WeightsView(
    override val host: VHost,
    val weights: Weights,
    val l: LayerView,
    val r: LayerView,
    val lY: Float,
    val rY: Float,
    val gapSize: Float,
    override val h: Float,
    val alphaPower: Int,
    val alphaFactor: Float,
    val minimumVisibleAlpha: Float,
) : Visual.Movable {

    override var x: Float = 0f
    override var y: Float = 0f

    var enabled: Boolean = true

    //    TODO Area.Circular
//    override val radius: Float
//        get() = gapSize

    override val w get() = l.w + gapSize + r.w
//       override val h get() = max(l.vSize, r.vSize)

    private val divisor: Float =
        (l.cellCount + r.cellCount) / 32f

    override fun draw(g: Graphics2D) {
        if (enabled)
            drawStill(g, enabledImage)
    }

    private val enabledImage by lazy {
        createStill { g ->
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
                        g.drawLineFloat(
                            l.getCellCenterX(currNeuron),
                            l.getCellCenterY(currNeuron) + lY,
                            r.getCellCenterX(nextNeuron) + l.w + gapSize,
                            r.getCellCenterY(nextNeuron) + rY,
                        )
                    }
                }
            }
        }
    }

    private val disabledImage by lazy {
        createStill { g ->
            val alpha = (1f * alphaFactor / divisor).coerceIn(0f, 1f)
            g.color = GUIUtil.gray(0.5f, alpha)
            r.forEach { nextNeuron ->
                l.forEach { currNeuron ->
                    g.drawLineFloat(
                        l.getCellCenterX(currNeuron),
                        l.getCellCenterY(currNeuron) + lY,
                        r.getCellCenterX(nextNeuron) + l.w + gapSize,
                        r.getCellCenterY(nextNeuron) + rY,
                    )
                }
            }
        }
    }
}