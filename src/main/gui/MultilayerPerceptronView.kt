package src.main.gui

import src.main.gui.GUIUtil.createBoundCheckBoxMenuItem
import src.main.gui.GUIUtil.drawOutline
import src.main.gui.GUIUtil.showPopupMenu
import src.main.gui.layerview.*
import src.main.gui.vis.MouseButton
import src.main.gui.vis.VHost
import src.main.gui.vis.VLayer
import src.main.gui.vis.Visual
import src.main.mlp.MultilayerPerceptron
import java.awt.Dimension
import java.awt.Graphics2D
import javax.swing.JPopupMenu
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

    private val n = structure.hiddenLayerSizes.size + 2

    private var showHiddenLayers = true
        set(value) {
            field = value
            layerViews.visuals.forEachIndexed { i, it -> it.showCells = value || i == 0 || i == n - 1 }
            host.redraw()
        }
    private var showWeights = true
        set(value) {
            field = value
            weightsViews.visuals.forEach { it.enabled = value }
            host.redraw()
        }
    private var showBorder = false
        set(value) {
            field = value
            host.redraw()
        }

    var input: FloatArray
        get() = layerViews.visuals.first().data
        set(value) {
            layerViews.visuals.first().data = value
            forwardPropagate()
        }

    val output: FloatArray
        get() = layerViews.visuals.last().data

    private fun forwardPropagate() {
        model.forwardPropagateAlsoRecord(input).forEachIndexed { index, data ->
            layerViews.visuals[index + 1].data = data
        }
        host.redraw()
    }

    private val gridLayersMap = gridLayers.toMap()
    private val layerViews = VLayer<LayerView>(host).apply {
        for (i in 0 until n) {
            val s = when (i) {
                0 -> structure.inputSize
                n - 1 -> structure.outputSize
                else -> structure.hiddenLayerSizes[i - 1]
            }
            val d = gridLayersMap[i]
            val v = if (d == null) {
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
            host.register(v)
            visuals.add(v)
        }
    }

    private val weightsViews = VLayer<WeightsView>(host).apply {
        for (i in 0 until n - 1) {
            val h = h
            val v = WeightsView(
                host = host,
                weights = model.getWeights(i),
                l = layerViews.visuals[i],
                r = layerViews.visuals[i + 1],
                lY = (h - layerViews.visuals[i].h) / 2,
                rY = (h - layerViews.visuals[i + 1].h) / 2,
                gapSize = 112f,
                h = h,
                alphaPower = 2,
                alphaFactor = 1f,
                minimumVisibleAlpha = 0.01f,
            )
            visuals.add(v)
            host.register(v)
        }
    }

    private fun reposition() {
        var dx: Float
        var dy: Float

        dx = x + host.padding
        dy = y

        weightsViews.visuals.forEach {
            it.x = dx
            it.y = dy
            dx += it.l.w + it.gapSize
        }

        dx = x + host.padding

        layerViews.visuals.forEachIndexed { i, it ->
            dy = y + (h - it.h) / 2
            it.x = dx
            it.y = dy
            dx += it.w
            if (i != n - 1)
                dx += weightsViews.visuals[i].gapSize
        }
    }

    init {
        host.addLayer(weightsViews)
        host.addLayer(layerViews)
        host.register(this)
        reposition()
        host.setSize(w, h)
        input = FloatArray(structure.inputSize)
    }

    private val mnuShowBorder =
        createBoundCheckBoxMenuItem("Show border", ::showBorder)

    private val mnuShowHiddenLayers =
        createBoundCheckBoxMenuItem("Show hidden layers", ::showHiddenLayers)

    private val mnuShowWeights =
        createBoundCheckBoxMenuItem("Show weights", ::showWeights)

    private val popUpMenu = JPopupMenu().apply {
        add(mnuShowBorder)
        add(mnuShowHiddenLayers)
        add(mnuShowWeights)
    }

    override fun onMouseRelease(x: Float, y: Float, b: MouseButton) {
        if (b == MouseButton.RIGHT) {
            showPopupMenu(popUpMenu, x, y)
        }
    }

    override val w: Float
        get() {
            var w = 0f
            layerViews.visuals.forEachIndexed { index, layer ->
                w += layer.w
                if (index != n - 1)
                    w += weightsViews.visuals[index].gapSize
            }
            return w + host.padding * 2
        }

    override val h: Float
        get() {
            var h = 0f
            layerViews.visuals.forEach { layer ->
                h = max(layer.h, h)
            }
            return h + host.padding * 2
        }

    override fun draw(g: Graphics2D) {
        if (showBorder) {
            g.color = GUIUtil.HALF_GRAY
            g.drawOutline(0f, 0f, w, h, -host.padding / 2)
        }
    }
}