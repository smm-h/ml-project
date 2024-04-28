package src.main.gui

import MultilayerPerceptron
import src.main.util.Util.QUARTER_BLACK
import src.main.util.Util.by
import java.awt.*
import javax.swing.JPanel
import kotlin.math.*

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
        WeightView(model, layerViews[i], layerViews[i + 1], 112f)
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

        var prevX: Float
        var currX = margin

        weightViews.forEachIndexed { index, weightView ->
            prevX = currX
            currX += weightView.prevLayer.hSize + weightView.hSize
            val prevY = (height - weightView.prevLayer.vSize) / 2
            val currY = (height - weightView.currLayer.vSize) / 2
            weightView.draw(g, index, prevX, currX, prevY, currY)
        }

        var x0 = margin
        layerViews.forEachIndexed { index, layer ->
            layer.draw(g, x0, (height - layer.vSize) / 2)
            x0 += layer.hSize
            if (index != n - 1)
                x0 += weightViews[index].hSize
        }
    }

    private class WeightView(
        val model: MultilayerPerceptron,
        val prevLayer: LayerView,
        val currLayer: LayerView,
        val hSize: Float = 112f,
    ) {
        val divisor = sqrt((prevLayer.cellCount * currLayer.cellCount).toFloat()) * 0.1f
        fun colorizeWeight(weight: Float): Color {
            val v = abs(weight).pow(2) / divisor
            return if (weight > 0f)
                Color(0f, 1f, 0f, v) // green
            else
                Color(1f, 0f, 0f, v) // red
        }

        fun draw(g: Graphics2D, layerIndex: Int, prevX: Float, currX: Float, prevY: Float, currY: Float) {
            currLayer.forEach { currNeuron ->
                prevLayer.forEach { prevNeuron ->
                    g.color = colorizeWeight(model.getWeight(layerIndex, currNeuron, prevNeuron))
                    g.drawLine(
                        (prevX + prevLayer.getCellCenterX(prevNeuron)).roundToInt(),
                        (prevY + prevLayer.getCellCenterY(prevNeuron)).roundToInt(),
                        (currX + currLayer.getCellCenterX(currNeuron)).roundToInt(),
                        (currY + currLayer.getCellCenterY(currNeuron)).roundToInt(),
                    )
                }
            }
        }
    }
}