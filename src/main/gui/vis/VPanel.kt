package src.main.gui.vis

import src.main.gui.GUIUtil
import src.main.gui.GUIUtil.by
import src.main.gui.layerview.BigGridLayerView
import src.main.gui.layerview.LayerView
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.util.*
import javax.swing.JPanel
import kotlin.math.roundToInt

class VPanel : VHost {

    override var padding: Float = 8f

    private val mouseListener = object : MouseListener {
        private var isLeftMouseButtonDown = false

        private val visualsContainingMouse = PriorityQueue<Visual> { v1, v2 -> (v1.area - v2.area).roundToInt() }

        override fun mousePressed(e: MouseEvent) {
            when (e.button) {
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
            when (e.button) {
                MouseEvent.BUTTON1 -> {
                    if (isLeftMouseButtonDown) {
                        isLeftMouseButtonDown = false
                        forwardPropagate()
                    }
                }

                MouseEvent.BUTTON3 -> {
                    val v = visualsContainingMouse.elementAtOrNull(0)
                    val p =
                        if (v == null)
                            popUp
                        else
                            (v as LayerView).popupMenu
                    p.show(e.component, e.x, e.y)
                }

                else -> Unit
            }
        }

        override fun mouseClicked(e: MouseEvent?) {
            TODO("Not yet implemented")
        }

        override fun mouseEntered(e: MouseEvent?) {
            TODO("Not yet implemented")
        }

        override fun mouseExited(e: MouseEvent?) {
            TODO("Not yet implemented")
        }
    }

    private val mouseMotionListener = object : MouseMotionListener {
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
    }

    private val jPanel = object : JPanel() {

        override fun paintComponent(g0: Graphics?) {
            super.paintComponent(g0)
            val g = GUIUtil.getSmoothGraphics(g0)

//        if (showBorder)
//            GUIUtil.drawOutline(g, 0f, 0f, width.toFloat(), height.toFloat(), -padding / 2)

            var x: Float

            x = padding
            weightsViews.forEach {
                it.x = x
                it.y = ((height - vSize) / 2)
                it.draw(g)
                x += it.l.w + it.gapSize
            }

            x = padding
            layerViews.forEachIndexed { i, it ->
                val y = (height - it.h) / 2
                it.x = x
                it.y = y
                it.draw(g)
                x += it.w
                if (i != n - 1)
                    x += weightsViews[i].gapSize
            }
        }
    }.apply {
        addMouseListener(mouseListener)
        addMouseMotionListener(mouseMotionListener)
    }

    override fun setSize(w: Float, h: Float) {
        val size = w by h
        jPanel.size = size
        jPanel.preferredSize = size
        jPanel.minimumSize = size
        redraw()
    }

    override fun redraw() {
        jPanel.revalidate()
        jPanel.repaint()
    }
}