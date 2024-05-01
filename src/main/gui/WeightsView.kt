package src.main.gui

import src.main.gui.GUIUtil.component1
import src.main.gui.GUIUtil.component2
import src.main.gui.GUIUtil.component3
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
            drawStill(g, if (host.gui.darkMode) stillDarkMode else stillLightMode)
    }

    private val stillLightMode by lazy {
        createStill(
            Color(0f, 1.0f, 0f),
            Color(1.0f, 0f, 0f),
        )
    }
    private val stillDarkMode by lazy {
        createStill(
            Color(0f, 0.5f, 0f),
            Color(0.5f, 0f, 0f),
        )
    }

    private fun createStill(colorPositive: Color, colorNegative: Color) =
        createStill { g ->
            val (pR, pG, pB) = colorPositive
            val (nR, nG, nB) = colorNegative
            r.forEach { nextNeuron ->
                l.forEach { currNeuron ->
                    val weight = weights.getWeight(nextNeuron, currNeuron)
                    val alpha = (abs(weight).pow(alphaPower) * alphaFactor / divisor).coerceIn(0f, 1f)
                    if (alpha >= minimumVisibleAlpha) {
                        g.color =
                            if (weight > 0f)
                                Color(pR, pG, pB, alpha) // green
                            else
                                Color(nR, nG, nB, alpha) // red
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