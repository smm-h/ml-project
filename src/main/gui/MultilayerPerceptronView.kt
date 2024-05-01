package src.main.gui

import src.main.gui.GUIUtil.createBoundCheckBoxMenuItem
import src.main.gui.GUIUtil.drawOutline
import src.main.gui.GUIUtil.showPopupMenu
import src.main.gui.layerview.*
import src.main.gui.vis.MouseButton
import src.main.gui.vis.VHost
import src.main.gui.vis.Visual
import src.main.mlp.MultilayerPerceptron
import java.awt.Dimension
import java.awt.Graphics2D
import javax.swing.JPopupMenu
import javax.swing.JSeparator
import javax.swing.SwingConstants
import kotlin.math.max
import kotlin.math.round

class MultilayerPerceptronView(
    override val host: VHost,
    private val filename: String,
    vararg gridLayers: Pair<Int, Dimension>,
) : Visual.ListensToMouseRelease {

    override var containsMouse: Boolean = false

    override val x: Float = 0f
    override val y: Float = 0f

    private val structure = MultilayerPerceptron.readStructure(filename)
    private val model by lazy { MultilayerPerceptron.readModel(filename) }

    var lastChangedLayer: LayerView? = null

    private val n = structure.hiddenLayerSizes.size + 2

    private var automaticallyForwardPropagate = true
        set(value) {
            field = value
            host.redraw()
        }

    private val mnuAutomaticallyForwardPropagate =
        createBoundCheckBoxMenuItem("Automatically forward-propagate", ::automaticallyForwardPropagate)

    private var showHiddenLayers = true
        set(value) {
            field = value
            layerViews.forEachIndexed { i, it -> if (i != 0 && i != n - 1) it.showCells = value }
            host.redraw()
        }

    private val mnuShowHiddenLayers =
        createBoundCheckBoxMenuItem("Show hidden layers", ::showHiddenLayers)

    private var showWeights = true
        set(value) {
            field = value
            weightsViews.forEach { it.enabled = value }
            host.redraw()
        }
    private val mnuShowWeights =
        createBoundCheckBoxMenuItem("Show weights", ::showWeights)

    private var showBorder = false
        set(value) {
            field = value
            host.redraw()
        }

    private val mnuShowBorder =
        createBoundCheckBoxMenuItem("Show border", ::showBorder)

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
        val v: LayerView = if (d == null) {
            val mh = 200f
            if (s > 16) {
                val cellSize = round(mh / s).coerceAtLeast(1f)
                BigColumnLayerView(this, s, cellSize)
            } else {
                val vSep = round(mh / 4 / s).coerceAtLeast(1f)
                val cellSize = vSep * 3
                SmallColumnLayerView(this, s, cellSize, vSep)
            }
        } else {
            val w = d.width
            val h = d.height
            if (max(w, h) > 16) {
                BigGridLayerView(this, w, h)
            } else {
                SmallGridLayerView(this, w, h)
            }
        }
        host.register(v)
        v
    }

    private val weightsViews = List(n - 1) { i ->
        val h = h
        val v = WeightsView(
            host = host,
            weights = model.getWeights(i),
            l = layerViews[i],
            r = layerViews[i + 1],
            lY = (h - layerViews[i].h) / 2,
            rY = (h - layerViews[i + 1].h) / 2,
            gapSize = 112f,
            h = h,
            alphaPower = 2,
            alphaFactor = 1f,
            minimumVisibleAlpha = 0.01f,
        )
        host.register(v)
        v
    }

    private fun reposition() {
        var dx: Float
        var dy: Float

        dx = x + host.padding
        dy = y

        weightsViews.forEach {
            it.x = dx
            it.y = dy
            dx += it.l.w + it.gapSize
        }

        dx = x + host.padding

        layerViews.forEachIndexed { i, it ->
            dy = y + (h - it.h) / 2
            it.x = dx
            it.y = dy
            dx += it.w
            if (i != n - 1)
                dx += weightsViews[i].gapSize
        }
    }

    init {
        host.register(this)
        reposition()
        host.setSize(w, h)
        input = FloatArray(structure.inputSize)
    }

    private val popUpMenu = JPopupMenu().apply {
        add(mnuShowBorder)
        add(mnuShowHiddenLayers)
        add(mnuShowWeights)
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(mnuAutomaticallyForwardPropagate)
    }

    override fun onMouseRelease(x: Float, y: Float, b: MouseButton) {
        if (b == MouseButton.RIGHT) {
            showPopupMenu(popUpMenu, x, y)
        }
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
        if (automaticallyForwardPropagate) {
            val l = lastChangedLayer
            if (l != null) {
                val k = layerViews.indexOf(l)
                val r = model.forwardPropagateStartingFromAlsoRecord(l.data, k)
                for (i in k + 1 until n) {
                    layerViews[i].data = r[i - k - 1]
                }
                lastChangedLayer = null
            }
        }
        weightsViews.forEach { it.draw(g) }
        layerViews.forEach { it.draw(g) }
        if (showBorder) {
            g.color = GUIUtil.HALF_GRAY
            g.drawOutline(0f, 0f, w, h, -host.padding / 2)
        }
    }
}