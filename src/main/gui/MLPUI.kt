package src.main.gui

import MultilayerPerceptron
import src.main.gui.MLPUI.WeightColorizer
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.roundToInt

class MLPUI(
    val model: MultilayerPerceptron,
) : JPanel() {

    var options: Options = Options()
        set(value) {
            field = value
            refresh()
        }
    private val structure = model.structure
    private val hiddenLayers = structure.hiddenLayerSizes
    private val n = hiddenLayers.size + 2
    private val layerHeights = IntArray(n)
    private val layerSizes = IntArray(n) {
        when (it) {
            0 -> structure.inputSize
            n - 1 -> structure.outputSize
            else -> hiddenLayers[it - 1]
        }
    }
    private var myWidth: Int = 0
    private var myHeight: Int = 0

    init {
        refresh()
    }

    fun refresh() {
        options.apply {
            myWidth = (neuronSize + horizontalSep) * n - horizontalSep
            var maxHeight = 0
            for (i in 0 until n) {
                val s = layerSizes[i]
                val h = if (s > maxShownLayerSize) {
                    (maxShownLayerSize + log10(s.toFloat()) * (neuronSize + verticalSep)).toInt()
                        .coerceAtMost(maxShownLayerHeight)
                } else s * (neuronSize + verticalSep)
                layerHeights[i] = h
                maxHeight = max(h, maxHeight)
            }
            myHeight = maxHeight
        }
        revalidate()
        repaint()
    }

    class Options {
        val maxShownLayerSize: Int = 16
        val neuronSize: Int = 16
        val horizontalSep: Int = (neuronSize * 3.5).roundToInt()
        val verticalSep: Int = (neuronSize * 0.5).roundToInt()
        val maxShownLayerHeight: Int = ((neuronSize + verticalSep) * maxShownLayerSize * 1.5).roundToInt()
        val weightColorizer: WeightColorizer = WeightColorizer.redGreen
    }

    fun interface WeightColorizer {
        fun colorizeWeight(weight: Float): Color

        companion object {
            val redGreen = WeightColorizer { w ->
                if (w > 0f)
                    Color(0f, 1f, 0f, w)
                else
                    Color(1f, 0f, 0f, abs(w))
            }
        }
    }

    fun interface NeuronPainter {
        fun paintAtNeurons(neuronIndex: Int, x: Int, y: Int)
    }

    fun paintAtNeurons(layerIndex: Int, neuronPainter: NeuronPainter) {
        val o = options
        val layerSize = layerSizes[layerIndex]
        val x0 = (width - myWidth) / 2
        val y0 = (height - myHeight) / 2
        val x = x0 + layerIndex * (o.neuronSize + o.horizontalSep)
        val curr_y0 = y0 + (myHeight - layerHeights[layerIndex]) / 2
        for (neuronIndex in 0 until layerSize) {
            val y = curr_y0 + neuronIndex * (o.neuronSize + o.verticalSep)
            neuronPainter.paintAtNeurons(neuronIndex, x, y)
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        with(options) {
            (g as Graphics2D).apply {
                setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )
                for (layerIndex in 1 until n) {
                    paintAtNeurons(layerIndex) { i1, x1, y1 ->
                        paintAtNeurons(layerIndex - 1) { i2, x2, y2 ->
                            val weight = model.getWeight(layerIndex - 1, i1, i2)
                            color = weightColorizer.colorizeWeight(weight)
                            drawLine(x1 + neuronSize / 2, y1 + neuronSize / 2, x2 + neuronSize / 2, y2 + neuronSize / 2)
                        }
                    }
                }
                for (layerIndex in 0 until n) {
                    paintAtNeurons(layerIndex) { _, x, y ->
                        color = Color.WHITE
                        fillOval(x, y, neuronSize, neuronSize)
                        color = Color.GRAY
                        drawOval(x, y, neuronSize, neuronSize)
                    }
                }
            }
        }
    }
}