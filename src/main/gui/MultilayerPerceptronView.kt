package src.main.gui

import src.main.gui.layerview.BigColumnLayerView
import src.main.gui.layerview.BigGridLayerView
import src.main.gui.layerview.SmallColumnLayerView
import src.main.gui.layerview.SmallGridLayerView
import src.main.gui.vis.AbstractRectangularVisual
import src.main.gui.vis.VHost
import src.main.mlp.MultilayerPerceptron
import src.main.mnist.MNIST
import java.awt.Dimension
import java.awt.Graphics2D
import javax.swing.JCheckBoxMenuItem
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JSeparator
import kotlin.math.max
import kotlin.math.round

class MultilayerPerceptronView(
    override val host: VHost,
    private val filename: String,
    vararg gridLayers: Pair<Int, Dimension>,
) : AbstractRectangularVisual() {

    override var x: Float = 0f
    override var y: Float = 0f

    private val structure = MultilayerPerceptron.readStructure(filename)
    private val model by lazy { MultilayerPerceptron.readModel(filename) }

    private val n = structure.hiddenLayerSizes.size + 2

    private var showHiddenLayers = true
        set(value) {
            field = value
            layerViews.forEachIndexed { i, it -> it.showCells = value || i == 0 || i == n - 1 }
            host.redraw()
        }
    private var showWeights = true
        set(value) {
            field = value
            weightsViews.forEach { it.enabled = value }
            host.redraw()
        }
    private var showBorder = true
        set(value) {
            field = value
            host.redraw()
        }

    var input: FloatArray
        get() = layerViews.first().data
        set(value) {
            layerViews.first().data = value
            forwardPropagate()
        }

    val output: FloatArray
        get() = layerViews.last().data

    private fun forwardPropagate() {
        model.forwardPropagateAlsoRecord(input).forEachIndexed { index, data ->
            layerViews[index + 1].data = data
        }
        host.redraw()
    }

    private val gridLayersMap = gridLayers.toMap()
    private val layerViews = List(n) { i ->
        val s = when (i) {
            0 -> structure.inputSize
            n - 1 -> structure.outputSize
            else -> structure.hiddenLayerSizes[i - 1]
        }
        val d = gridLayersMap[i]
        if (d == null) {
            val mh = 200f
            if (s > 16) {
                val cellSize = round(mh / s).coerceAtLeast(1f)
                BigColumnLayerView(host, s, cellSize)
            } else {
                val vSep = round(mh / 4 / s).coerceAtLeast(1f)
                val cellSize = vSep * 3
                SmallColumnLayerView(host, s, cellSize, vSep)
            }
        } else {
            val w = d.width
            val h = d.height
            if (max(w, h) > 16) {
                BigGridLayerView(host, w, h)
            } else {
                SmallGridLayerView(host, w, h)
            }
        }
    }

    private val weightsViews = List(n - 1) { i ->
        val h = h
        WeightsView(
            weights = model.getWeights(i),
            l = layerViews[i],
            r = layerViews[i + 1],
            lY = (h - layerViews[i].h) / 2,
            rY = (h - layerViews[i + 1].h) / 2,
            gapSize = 112f,
            vSize = h,
            alphaPower = 2,
            alphaFactor = 1f,
            minimumVisibleAlpha = 0.01f,
        )
    }

    private val popUp = JPopupMenu().apply {
        add(JMenuItem("Clear input").apply {
            addActionListener {
                input = FloatArray(structure.inputSize)
            }
        })
        add(JMenuItem("Choose random datapoint as input").apply {
            addActionListener {
                input = MNIST.training[(Math.random() * 1000).toInt()].data
            }
        })
        add(JMenuItem("Randomize input cells").apply {
            addActionListener {
                input = FloatArray(structure.inputSize) { Math.random().toFloat() }
            }
        })
        add(JSeparator())
        add(JCheckBoxMenuItem("Show border").apply {
            state = showBorder
            addActionListener { showBorder = state }
        })
        add(JCheckBoxMenuItem("Show hidden layers").apply {
            state = showHiddenLayers
            addActionListener { showHiddenLayers = state }
        })
        add(JCheckBoxMenuItem("Show weights").apply {
            state = showWeights
            addActionListener { showWeights = state }
        })
    }

    init {
        updateSize()
        input = FloatArray(structure.inputSize)
    }

    override val w: Float
        get() {
            var w = 0f
            layerViews.forEachIndexed { index, layer ->
                w += layer.w
                if (index != n - 1)
                    w += weightsViews[index].gapSize
            }
            return w + host.padding * 2
        }

    override val h: Float
        get() {
            var h = 0f
            layerViews.forEach { layer ->
                h = max(layer.h, h)
            }
            return h + host.padding * 2
        }

    override fun draw(g: Graphics2D) {
    }

    private fun updateSize() {
        host.setSize(w, h)
    }
}