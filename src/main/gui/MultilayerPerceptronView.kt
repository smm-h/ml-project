package src.main.gui

import MultilayerPerceptron
import src.main.util.Util.QUARTER_BLACK
import src.main.util.Util.by
import java.awt.*
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

class MultilayerPerceptronView(
    private val model: MultilayerPerceptron,
    vararg gridLayers: Pair<Int, Dimension>,
) : JPanel() {
    private val margin: Float = 32f
    private val gridLayersMap = gridLayers.toMap()

    private val n = model.structure.hiddenLayerSizes.size + 2

    private val layerViews = List(n) { i ->
        val s = when (i) {
            0 -> model.inputSize
            n - 1 -> model.outputSize
            else -> model.structure.hiddenLayerSizes[i - 1]
        }
        val d = gridLayersMap[i]
        if (d == null) {
            if (s > 16)
                LayerView.DenseColumn(s)
            else
                LayerView.Column(s)
        } else {
            val w = d.width
            val h = d.height
            if (max(w, h) > 16)
                LayerView.DenseGrid(w, h)
            else
                LayerView.Grid(w, h)
        }
    }

    private val weightViews = List(n - 1) { i ->
        WeightView(i, 2, 1f, 112f, 0.01f)
    }

    init {
        refresh()
    }

    private fun refresh() {
        var w = 0f
        var h = 0f

        layerViews.forEachIndexed { index, layer ->
            w += layer.hSize
            h = max(layer.vSize, h)
            if (index != n - 1)
                w += weightViews[index].hSize
        }

        size = (w + margin * 2).roundToInt() by (h + margin * 2).roundToInt()
        preferredSize = size
        minimumSize = size
        revalidate()
        repaint()
    }

    override fun paintComponent(g0: Graphics?) {
        super.paintComponent(g0)
        val g = g0 as Graphics2D

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val k = (margin / 2).roundToInt()
        val arc = (margin * 2).roundToInt()
        g.color = QUARTER_BLACK
        g.drawRoundRect(
            k, k,
            (width - margin).roundToInt(),
            (height - margin).roundToInt(),
            arc, arc,
        )

        var currX: Float
        var nextX = margin

        weightViews.forEachIndexed { index, weightView ->
            currX = nextX
            nextX += weightView.currLayerView.hSize + weightView.hSize
            val currY = (height - weightView.currLayerView.vSize) / 2
            val nextY = (height - weightView.nextLayerView.vSize) / 2
            weightView.draw(g, index, currX, nextX, currY, nextY)
        }

        var x0 = margin
        layerViews.forEachIndexed { index, layer ->
            layer.draw(g, x0, (height - layer.vSize) / 2)
            x0 += layer.hSize
            if (index != n - 1)
                x0 += weightViews[index].hSize
        }
    }

    private inner class WeightView(
        val layerIndex: Int,
        val alphaPower: Int,
        val alphaFactor: Float,
        val hSize: Float,
        val minimumVisibleAlpha: Float,
    ) {
        val currLayerView get() = layerViews[layerIndex]
        val nextLayerView get() = layerViews[layerIndex + 1]

        private val divisor: Float =
            (currLayerView.cellCount + nextLayerView.cellCount) / 32f

        fun draw(g: Graphics2D, layerIndex: Int, currX: Float, nextX: Float, currY: Float, nextY: Float) {
            nextLayerView.forEach { nextNeuron ->
                currLayerView.forEach { currNeuron ->
                    val weight = model.getWeight(layerIndex, nextNeuron, currNeuron)
                    val alpha = (abs(weight).pow(alphaPower) / divisor * alphaFactor).coerceIn(0f, 1f)
                    if (alpha >= minimumVisibleAlpha) {
                        g.color =
                            if (weight > 0f)
                                Color(0f, 1f, 0f, alpha) // green
                            else
                                Color(1f, 0f, 0f, alpha) // red
                        g.drawLine(
                            (currX + currLayerView.getCellCenterX(currNeuron)).roundToInt(),
                            (currY + currLayerView.getCellCenterY(currNeuron)).roundToInt(),
                            (nextX + nextLayerView.getCellCenterX(nextNeuron)).roundToInt(),
                            (nextY + nextLayerView.getCellCenterY(nextNeuron)).roundToInt(),
                        )
                    }
                }
            }
        }
    }
}