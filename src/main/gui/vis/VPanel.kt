package src.main.gui.vis

import src.main.gui.GUIUtil
import src.main.gui.GUIUtil.by
import src.main.gui.GUIUtil.mouseButton
import java.awt.Graphics
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.util.*
import javax.swing.JPanel
import kotlin.math.roundToInt

class VPanel : VHost {

    override var padding: Float = 8f

    override val mouseButtonDown = BooleanArray(MouseButton.entries.size)

    private val mpl = mutableListOf<Visual.ListensToMousePress>()
    private val mrl = mutableListOf<Visual.ListensToMouseRelease>()
    private val mml = mutableListOf<Visual.ListensToMouseMove>()
    private val mdl = mutableListOf<Visual.ListensToMouseDrag>()

    override fun register(it: Visual) {
        if (it is Visual.ListensToMousePress) mpl.add(it)
        if (it is Visual.ListensToMouseRelease) mrl.add(it)
        if (it is Visual.ListensToMouseMove) mml.add(it)
        if (it is Visual.ListensToMouseDrag) mdl.add(it)
    }

    override val atMouse = PriorityQueue<Visual> { v1, v2 -> (v1.area - v2.area).roundToInt() }

    private val mouseListener = object : MouseAdapter() {

        override fun mousePressed(e: MouseEvent) {
            val b = e.mouseButton
            if (b != null) {
                mouseButtonDown[b.ordinal] = true
                val v = atMouse.elementAtOrNull(0)
                if (v != null) {
                    mpl.forEach {
                        if (it == v) {
                            it.onMousePress(e.x.toFloat(), e.y.toFloat(), b)
                        }
                    }
                }
            }
        }

        override fun mouseReleased(e: MouseEvent) {
            val b = e.mouseButton
            if (b != null) {
                if (mouseButtonDown[b.ordinal]) {
                    mouseButtonDown[b.ordinal] = false
                    val v = atMouse.elementAtOrNull(0)
                    if (v != null) {
                        mrl.forEach {
                            if (it == v) {
                                it.onMouseRelease(e.x.toFloat(), e.y.toFloat(), b)
                            }
                        }
                    }
                }
            }
        }
    }

    private val mouseMotionListener = object : MouseMotionListener {
        override fun mouseMoved(e: MouseEvent) {
            mml.forEach { it.onMouseMove(e.x.toFloat(), e.y.toFloat()) }
        }

        override fun mouseDragged(e: MouseEvent) {
            mdl.forEach { it.onMouseDrag(e.x.toFloat(), e.y.toFloat()) }
        }
    }

    override var rootVisual: Visual? = null
        set(value) {
            field = value
            redraw()
        }

    val jPanel = object : JPanel(GridBagLayout()) {
        override fun paintComponent(g0: Graphics?) {
            super.paintComponent(g0)
            val g = GUIUtil.getSmoothGraphics(g0)
            rootVisual?.draw(g)
        }
    }.apply {
        addMouseListener(mouseListener)
        addMouseMotionListener(mouseMotionListener)
    }

    override val width: Int get() = jPanel.width
    override val height: Int get() = jPanel.height

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