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
import javax.swing.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

class MultilayerPerceptronView(
    private val filename: String,
    vararg gridLayers: Pair<Int, Dimension>,
) : JPanel() {

    private val structure = MultilayerPerceptron.readStructure(filename)
    private val model by lazy { MultilayerPerceptron.readModel(filename) }

    private val margin: Float = 2f
    private val gridLayersMap = gridLayers.toMap()

    private val n = structure.hiddenLayerSizes.size + 2

    private var showHiddenLayers = true
    private var showWeights = true

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
        val h = vSize
        WeightView(
            readableLayer = model.getReadableLayer(i),
            l = layerViews[i],
            r = layerViews[i + 1],
            lY = (h - layerViews[i].vSize) / 2,
            rY = (h - layerViews[i + 1].vSize) / 2,
            gapSize = 112f,
            vSize = h,
            alphaPower = 2,
            alphaFactor = 1f,
            minimumVisibleAlpha = 0.01f,
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
        add(JCheckBoxMenuItem("Show hidden layers").apply {
            this.state = true
            this.addActionListener {
                showHiddenLayers = state
                redraw()
            }
        })
        add(JCheckBoxMenuItem("Show weights").apply {
            this.state = true
            this.addActionListener {
                showWeights = state
                redraw()
            }
        })
    }

    init {
        updateSize()
        input = FloatArray(structure.inputSize)
        addMouseListener(mouseHandler)
        addMouseMotionListener(mouseHandler)
    }

    private val hSize: Float
        get() {
            var w = margin * 2
            layerViews.forEachIndexed { index, layer ->
                w += layer.hSize
                if (index != n - 1)
                    w += weightViews[index].gapSize
            }
            return w
        }

    private val vSize: Float
        get() {
            var h = margin * 2
            layerViews.forEach { layer ->
                h = max(layer.vSize, h)
            }
            return h
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
        val g = g0 as Graphics2D

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

//        val k = (margin / 2).roundToInt()
//        val arc = (margin * 2).roundToInt()
//        g.color = QUARTER_BLACK
//        g.drawRoundRect(
//            k, k,
//            (width - margin).roundToInt(),
//            (height - margin).roundToInt(),
//            arc, arc,
//        )

        var x: Float

        x = margin
        weightViews.forEach {
            val y = ((height - vSize) / 2)
            it.draw(g, x, y, showWeights)
            x += it.l.hSize + it.gapSize
        }

        x = margin
        layerViews.forEachIndexed { index, layer ->
            val y = (height - layer.vSize) / 2
            layer.draw(g, x, y, showHiddenLayers || index == 0 || index == n - 1)
            x += layer.hSize
            if (index != n - 1)
                x += weightViews[index].gapSize
        }
    }

    private class WeightView(
        val readableLayer: ReadableLayer,
        val l: LayerView,
        val r: LayerView,
        val lY: Float,
        val rY: Float,
        val gapSize: Float,
        val vSize: Float,
        val alphaPower: Int,
        val alphaFactor: Float,
        val minimumVisibleAlpha: Float,
    ) {
        val hSize get() = l.hSize + gapSize + r.hSize
//        val vSize get() = max(layerViewL.vSize, layerViewR.vSize)

        private val divisor: Float =
            (l.cellCount + r.cellCount) / 32f

        fun draw(g: Graphics2D, x: Float, y: Float, enabled: Boolean) {
            g.drawImage(if (enabled) enabledImage else disabledImage, x.roundToInt(), y.roundToInt(), null)
        }

        private val enabledImage by lazy {
            BufferedImage(
                hSize.roundToInt(),
                vSize.roundToInt(),
                BufferedImage.TYPE_INT_ARGB,
            ).also {
                val g = it.graphics as Graphics2D
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                r.forEach { nextNeuron ->
                    l.forEach { currNeuron ->
                        val weight = readableLayer.getWeight(nextNeuron, currNeuron)
                        val alpha = (abs(weight).pow(alphaPower) * alphaFactor / divisor).coerceIn(0f, 1f)
                        if (alpha >= minimumVisibleAlpha) {
                            g.color =
                                if (weight > 0f)
                                    Color(0f, 1f, 0f, alpha) // green
                                else
                                    Color(1f, 0f, 0f, alpha) // red
                            g.drawLine(
                                (l.getCellCenterX(currNeuron)).roundToInt(),
                                (l.getCellCenterY(currNeuron) + lY).roundToInt(),
                                (r.getCellCenterX(nextNeuron) + l.hSize + gapSize).roundToInt(),
                                (r.getCellCenterY(nextNeuron) + rY).roundToInt(),
                            )
                        }
                    }
                }
            }
        }

        private val disabledImage by lazy {
            BufferedImage(
                hSize.roundToInt(),
                vSize.roundToInt(),
                BufferedImage.TYPE_INT_ARGB,
            ).also {
                val g = it.graphics as Graphics2D
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                val alpha = (1f * alphaFactor / divisor).coerceIn(0f, 1f)
                g.color = Util.gray(0.5f, alpha)
                r.forEach { nextNeuron ->
                    l.forEach { currNeuron ->
                        g.drawLine(
                            (l.getCellCenterX(currNeuron)).roundToInt(),
                            (l.getCellCenterY(currNeuron) + lY).roundToInt(),
                            (r.getCellCenterX(nextNeuron) + l.hSize + gapSize).roundToInt(),
                            (r.getCellCenterY(nextNeuron) + rY).roundToInt(),
                        )
                    }
                }
            }
        }
    }
}