package src.main.gui

import MultilayerPerceptron
import src.main.util.Util.QUARTER_BLACK
import src.main.util.Util.by
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class MultilayerPerceptronView(
    val model: MultilayerPerceptron,
    val layers: List<LayerView>,
    val hSep: Float = 56f,
    val margin: Float = 16f,
) : JPanel() {

    init {
        assert(layers.isNotEmpty())
        refresh()
    }

    fun refresh() {
        var w = 0f
        var h = 0f
        for (i in layers) {
            w += i.hSize + hSep
            h = max(i.vSize, h)
        }
        w -= hSep
        size = (w + margin * 2).roundToInt() by (h + margin * 2).roundToInt()
        preferredSize = size
        minimumSize = size
        revalidate()
        repaint()
    }

    fun colorizeWeight(weight: Float): Color =
        if (weight > 0f)
            Color(0.2f, 0.9f, 0.1f, weight) // green
        else
            Color(0.9f, 0.1f, 0.2f, abs(weight)) // red

    override fun paintComponent(g0: Graphics?) {
        super.paintComponent(g0)
        val g = g0 as Graphics2D

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g.color = QUARTER_BLACK
        g.drawRoundRect(
            0,
            0,
            width - 1,
            height - 1,
            (margin * 2).roundToInt(),
            (margin * 2).roundToInt(),
        )

        var prevX: Float
        var currX = margin

        for (currIndex in 1 until layers.size) {
            val prevIndex = currIndex - 1
            val prevLayer = layers[prevIndex]
            val currLayer = layers[currIndex]

            prevX = currX
            currX += prevLayer.hSize + hSep

            val prevY = (height - prevLayer.vSize) / 2
            val currY = (height - currLayer.vSize) / 2

            currLayer.forEach { currNeuron ->
                prevLayer.forEach { prevNeuron ->
                    g.color = colorizeWeight(model.getWeight(prevIndex, currNeuron, prevNeuron))
                    g.drawLine(
                        (currX + currLayer.getCellCenterX(currNeuron)).roundToInt(),
                        (currY + currLayer.getCellCenterY(currNeuron)).roundToInt(),
                        (prevX + prevLayer.getCellCenterX(prevNeuron)).roundToInt(),
                        (prevY + prevLayer.getCellCenterY(prevNeuron)).roundToInt(),
                    )
                }
            }
        }

        var x0 = margin
        for (layer in layers) {
            layer.draw(g, x0, (height - layer.vSize) / 2)
            x0 += layer.hSize + hSep
        }
    }
}