package src.main.gui

import src.main.mlp.MultilayerPerceptron
import src.main.mlp.ReadableLayer
import src.main.mnist.MNIST
import src.main.util.Util
import src.main.util.Util.by
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.JCheckBoxMenuItem
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

class MultilayerPerceptronView(
    private val model: MultilayerPerceptron,
    vararg gridLayers: Pair<Int, Dimension>,
) : JPanel() {
    private val margin: Float = 2f
    private val gridLayersMap = gridLayers.toMap()

    private val n = model.structure.hiddenLayerSizes.size + 2

    private var showHiddenLayers = true

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
            0 -> model.inputSize
            n - 1 -> model.outputSize
            else -> model.structure.hiddenLayerSizes[i - 1]
        }
        val d = gridLayersMap[i]
        if (d == null) {
            if (s > 16)
                LayerView.BigColumn(s, (320f / s).toInt() + 1f)
            else
                LayerView.SmallColumn(s)
        } else {
            val w = d.width
            val h = d.height
            if (max(w, h) > 16)
                LayerView.BigGrid(w, h)
            else
                LayerView.SmallGrid(w, h)
        }
    }

    private val weightViews = List(n - 1) { i ->
        WeightView(
            readableLayer = model.getReadableLayer(i),
            layerIndex = i,
            alphaPower = 2,
            alphaFactor = 1f,
            hSize = 112f,
            minimumVisibleAlpha = 0.01f,
            layerViews[i],
            layerViews[i + 1],
        )
    }

    private val mouseHandler = object : MouseAdapter() {

        private var isLeftMouseButtonDown = false

        override fun mousePressed(e: MouseEvent) {
            if (e.isPopupTrigger) {
                popUp.show(e.component, e.x, e.y)
            } else when (e.button) {
                MouseEvent.BUTTON1 -> {
                    val l = layerViews.first()
                    val rx: Float = e.x - margin
                    val ry: Float = e.y - (height - l.vSize) / 2
                    if (rx >= 0 && rx <= l.hSize &&
                        ry >= 0 && ry <= l.vSize
                    ) isLeftMouseButtonDown = true
                }

                else -> Unit
            }
        }

        override fun mouseReleased(e: MouseEvent) {
            if (e.isPopupTrigger) {
                popUp.show(e.component, e.x, e.y)
            } else if (isLeftMouseButtonDown && e.button == MouseEvent.BUTTON1) {
                isLeftMouseButtonDown = false
                forwardPropagate()
            }
        }

        override fun mouseDragged(e: MouseEvent) {
            if (isLeftMouseButtonDown) {
                val l = layerViews.first()
                val rx: Float = e.x - margin
                val ry: Float = e.y - (height - l.vSize) / 2
                when (l) {
                    is LayerView.BigGrid -> {
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
    }

    val popUp = JPopupMenu().apply {
        add(JMenuItem("Clear input").apply {
            addActionListener {
                input = FloatArray(this@MultilayerPerceptronView.model.inputSize)
            }
        })
        add(JMenuItem("Choose random datapoint as input").apply {
            addActionListener {
                input = MNIST.training[(Math.random() * 1000).toInt()].data
            }
        })
        add(JMenuItem("Randomize input cells").apply {
            addActionListener {
                input = FloatArray(this@MultilayerPerceptronView.model.inputSize) { Math.random().toFloat() }
            }
        })
        add(JCheckBoxMenuItem("Show hidden layers").apply {
            this.state = true
            this.addActionListener {
                showHiddenLayers = state
                redraw()
            }
        })
    }

    init {
        updateSize()
        input = FloatArray(model.inputSize)
        addMouseListener(mouseHandler)
        addMouseMotionListener(mouseHandler)
    }

    private fun updateSize() {
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
        redraw()
    }

    private fun redraw() {
        revalidate()
        repaint()
    }

    override fun paintComponent(g0: Graphics?) {
        super.paintComponent(g0)
        val g = g0 as Graphics2D

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

//        val k = (margin / 2).roundToInt()
//        val arc = (margin * 2).roundToInt()
//        g.color = QUARTER_BLACK
//        g.drawRoundRect(
//            k, k,
//            (width - margin).roundToInt(),
//            (height - margin).roundToInt(),
//            arc, arc,
//        )

        var currX: Float
        var nextX = margin

        weightViews.forEach {
            currX = nextX
            nextX += it.currLayerView.hSize + it.hSize
            it.draw(
                g = g,
                currX = currX,
                nextX = nextX,
                currY = (height - it.currLayerView.vSize) / 2,
                nextY = (height - it.nextLayerView.vSize) / 2,
            )
        }

        var x0 = margin
        layerViews.forEachIndexed { index, layer ->
            layer.draw(g, x0, (height - layer.vSize) / 2, showHiddenLayers || index == 0 || index == n - 1)
            x0 += layer.hSize
            if (index != n - 1)
                x0 += weightViews[index].hSize
        }
    }

    private class WeightView(
        val readableLayer: ReadableLayer,
        val layerIndex: Int,
        val alphaPower: Int,
        val alphaFactor: Float,
        val hSize: Float,
        val minimumVisibleAlpha: Float,
        val currLayerView: LayerView,
        val nextLayerView: LayerView,
    ) {
        val vSize get() = max(currLayerView.vSize, nextLayerView.vSize)

        private val divisor: Float =
            (currLayerView.cellCount + nextLayerView.cellCount) / 32f

        fun draw(
            g: Graphics2D,
            currX: Float,
            currY: Float,
            nextX: Float,
            nextY: Float,
        ) {
            nextLayerView.forEach { nextNeuron ->
                currLayerView.forEach { currNeuron ->
                    val weight = readableLayer.getWeight(nextNeuron, currNeuron)
                    val alpha = (abs(weight).pow(alphaPower) * alphaFactor / divisor).coerceIn(0f, 1f)
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

        fun drawDisabled(
            currX: Float,
            height: Float,
        ) {
            val currY = (height - currLayerView.vSize) / 2
            val nextY = (height - nextLayerView.vSize) / 2
            val image = BufferedImage(
                hSize.roundToInt(),
                vSize.roundToInt(),
                BufferedImage.TYPE_INT_ARGB
            )
            val g = image.graphics
            nextLayerView.forEach { nextNeuron ->
                currLayerView.forEach { currNeuron ->
                    val alpha = (0.5f * alphaFactor / divisor).coerceIn(0f, 1f)
                    g.color = Util.gray(0.5f, alpha)
                    g.drawLine(
                        (currX + currLayerView.getCellCenterX(currNeuron)).roundToInt(),
                        (currY + currLayerView.getCellCenterY(currNeuron)).roundToInt(),
                        (currX + nextLayerView.getCellCenterX(nextNeuron) + currLayerView.hSize + hSize).roundToInt(),
                        (nextY + nextLayerView.getCellCenterY(nextNeuron)).roundToInt(),
                    )
                }
            }
        }
    }
}