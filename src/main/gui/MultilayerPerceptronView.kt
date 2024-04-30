package src.main.gui

import src.main.gui.GUIUtil.by
import src.main.gui.layerview.BigColumnLayerView
import src.main.gui.layerview.BigGridLayerView
import src.main.gui.layerview.SmallColumnLayerView
import src.main.gui.layerview.SmallGridLayerView
import src.main.gui.vis.Visual
import src.main.mlp.MultilayerPerceptron
import src.main.mnist.MNIST
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*
import kotlin.math.max
import kotlin.math.round
import kotlin.math.roundToInt

class MultilayerPerceptronView(
    private val filename: String,
    vararg gridLayers: Pair<Int, Dimension>,
) : JPanel() {

    private val structure = MultilayerPerceptron.readStructure(filename)
    private val model by lazy { MultilayerPerceptron.readModel(filename) }

    private val padding: Float = 8f
    private val gridLayersMap = gridLayers.toMap()

    private val n = structure.hiddenLayerSizes.size + 2

    private var showHiddenLayers = true
        set(value) {
            field = value
            redraw()
        }
    private var showWeights = true
        set(value) {
            field = value
            redraw()
        }
    private var showBorder = true
        set(value) {
            field = value
            redraw()
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
        redraw()
    }

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
                BigColumnLayerView(s, cellSize)
            } else {
                val vSep = round(mh / 4 / s).coerceAtLeast(1f)
                val cellSize = vSep * 3
                SmallColumnLayerView(s, cellSize, vSep)
            }
        } else {
            val w = d.width
            val h = d.height
            if (max(w, h) > 16) {
                BigGridLayerView(w, h)
            } else {
                SmallGridLayerView(w, h)
            }
        }
    }

    private val weightsViews = List(n - 1) { i ->
        val h = vSize
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

    private val visualsContainingMouse = PriorityQueue<Visual> { v1, v2 -> (v1.area - v2.area).roundToInt() }

    private val mouseHandler = object : MouseAdapter() {
        private var isLeftMouseButtonDown = false

        override fun mousePressed(e: MouseEvent) {
            if (e.isPopupTrigger) {
                triggerPopup(e)
            } else when (e.button) {
                MouseEvent.BUTTON1 -> {
                    val l = layerViews.first()
                    val rx: Float = e.x - padding
                    val ry: Float = e.y - (height - l.h) / 2
                    if (rx >= 0 && rx <= l.w &&
                        ry >= 0 && ry <= l.h
                    ) isLeftMouseButtonDown = true
                }

                else -> Unit
            }
        }

        override fun mouseReleased(e: MouseEvent) {
            if (e.isPopupTrigger) {
                triggerPopup(e)
            } else if (isLeftMouseButtonDown && e.button == MouseEvent.BUTTON1) {
                isLeftMouseButtonDown = false
                forwardPropagate()
            }
        }

        override fun mouseMoved(e: MouseEvent) {
            layerViews.forEach {
                val x = it.contains(e.x.toFloat(), e.y.toFloat(), 2f)
                if (it.containsMouse != x) {
                    it.containsMouse = x
                    if (x) {
                        visualsContainingMouse.add(it)
                    } else {
                        visualsContainingMouse.remove(it)
                    }
                    redraw()
                }
            }
        }

        override fun mouseDragged(e: MouseEvent) {
            if (isLeftMouseButtonDown) {
                val l = layerViews.first()
                val rx: Float = e.x - padding
                val ry: Float = e.y - (height - l.h) / 2
                when (l) {
                    is BigGridLayerView -> {
                        val i = (rx / l.cellSize).toInt()
                        val j = (ry / l.cellSize).toInt()
                        if (
                            i >= 0 &&
                            j >= 0 &&
                            i <= l.hCellCount &&
                            j <= l.vCellCount
                        ) {
                            val sgn = if (e.isControlDown) -1 else +1
                            val iM = i > 0
                            val jM = j > 0
                            val iP = i < l.hCellCount - 1
                            val jP = j < l.vCellCount - 1
                            if (iM && jM) l[i - 1, j - 1] += sgn * 0.1f
                            if (iP && jM) l[i + 1, j - 1] += sgn * 0.1f
                            if (iM && jP) l[i - 1, j + 1] += sgn * 0.1f
                            if (iP && jP) l[i + 1, j + 1] += sgn * 0.1f
                            if (iM) l[i - 1, j] += sgn * 0.2f
                            if (iP) l[i + 1, j] += sgn * 0.2f
                            if (jM) l[i, j - 1] += sgn * 0.2f
                            if (jP) l[i, j + 1] += sgn * 0.2f
                            l[i, j] += sgn * 1f
                            redraw()
                        }
                    }

                    else -> Unit
                }
            }
        }
    }.also {
        addMouseListener(it)
        addMouseMotionListener(it)
    }

    private fun triggerPopup(e: MouseEvent) {
        popUp.show(e.component, e.x, e.y)
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
            this.state = showBorder
            this.addActionListener {
                showBorder = state
            }
        })
        add(JCheckBoxMenuItem("Show hidden layers").apply {
            this.state = showHiddenLayers
            this.addActionListener {
                showHiddenLayers = state
            }
        })
        add(JCheckBoxMenuItem("Show weights").apply {
            this.state = showWeights
            this.addActionListener {
                showWeights = state
            }
        })
    }

    init {
        updateSize()
        input = FloatArray(structure.inputSize)
    }

    private val hSize: Float
        get() {
            var w = 0f
            layerViews.forEachIndexed { index, layer ->
                w += layer.w
                if (index != n - 1)
                    w += weightsViews[index].gapSize
            }
            return w + padding * 2
        }

    private val vSize: Float
        get() {
            var h = 0f
            layerViews.forEach { layer ->
                h = max(layer.h, h)
            }
            return h + padding * 2
        }

    private fun updateSize() {
        size = hSize.roundToInt() by vSize.roundToInt()
        preferredSize = size
        minimumSize = size
        redraw()
    }

    private fun redraw() {
        revalidate()
        repaint()
    }

    override fun paintComponent(g0: Graphics?) {
        super.paintComponent(g0)
        val g = GUIUtil.getSmoothGraphics(g0)

        if (showBorder) {
            GUIUtil.drawOutline(g, 0f, 0f, width.toFloat(), height.toFloat(), -padding / 2)
        }

        var x: Float

        x = padding
        weightsViews.forEach {
            it.x = x
            it.y = ((height - vSize) / 2)
            it.enabled = showWeights
            it.draw(g)
            //GUIUtil.drawOutline(g, x, y, it.hSize, it.vSize, 0f)
            x += it.l.w + it.gapSize
        }

        x = padding
        layerViews.forEachIndexed { i, it ->
            val y = (height - it.h) / 2
            it.x = x
            it.y = y
            it.enabled = showHiddenLayers || i == 0 || i == n - 1
            it.draw(g)
//            GUIUtil.drawOutline(g, x, y, it.w, it.h, 4f)
            x += it.w
            if (i != n - 1)
                x += weightsViews[i].gapSize
        }
    }
}