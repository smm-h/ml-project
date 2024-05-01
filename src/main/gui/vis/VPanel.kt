package src.main.gui.vis

import src.main.gui.GUI
import src.main.gui.GUIUtil
import src.main.gui.GUIUtil.action
import src.main.gui.GUIUtil.by
import src.main.gui.GUIUtil.mouseButton
import java.awt.Graphics
import java.awt.GridBagLayout
import java.awt.event.*
import java.util.*
import javax.swing.JPanel
import javax.swing.KeyStroke
import kotlin.math.roundToInt

class VPanel(override val gui: GUI) : VHost {

    override var padding: Float = 8f

    private val mouseButtonDown = BooleanArray(MouseButton.entries.size)

    override val isMouseLeftButtonDown: Boolean get() = mouseButtonDown[0]
    override val isMouseRightButtonDown: Boolean get() = mouseButtonDown[1]
    override val isMouseMiddleButtonDown: Boolean get() = mouseButtonDown[2]

    override var isControlDown = false
        private set
    override var isShiftDown = false
        private set
    override var isAltDown = false
        private set

    private val actionControlPressed = action("ctrl+") {
        isControlDown = true
        println(":)")
    }
    private val actionControlReleased = action("ctrl-") { isControlDown = false }
    private val actionShiftPressed = action("shift+") { isShiftDown = true }
    private val actionShiftReleased = action("shift-") { isShiftDown = false }
    private val actionAltPressed = action("alt+") { isAltDown = true }
    private val actionAltReleased = action("alt-") { isAltDown = false }

    private val mpl = mutableListOf<ListensTo.MousePress>()
    private val mrl = mutableListOf<ListensTo.MouseRelease>()
    private val mml = mutableListOf<ListensTo.MouseMove>()
    private val mdl = mutableListOf<ListensTo.MouseDrag>()
    private val kpl = mutableListOf<ListensTo.KeyPress>()
    private val krl = mutableListOf<ListensTo.KeyRelease>()

    override fun register(it: ListensTo) {
        if (it is ListensTo.MousePress) mpl.add(it)
        if (it is ListensTo.MouseRelease) mrl.add(it)
        if (it is ListensTo.MouseMove) mml.add(it)
        if (it is ListensTo.MouseDrag) mdl.add(it)
        if (it is ListensTo.KeyPress) kpl.add(it)
        if (it is ListensTo.KeyRelease) krl.add(it)
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

    private val keyListener = object : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            val keyCode = e.keyCode
            when (keyCode) {
                KeyEvent.VK_CONTROL -> isControlDown = true
                KeyEvent.VK_SHIFT -> isShiftDown = true
                KeyEvent.VK_ALT -> isAltDown = true
            }
            kpl.forEach { it.onKeyPress(keyCode) }
        }

        override fun keyReleased(e: KeyEvent) {
            val keyCode = e.keyCode
            krl.forEach { it.onKeyRelease(keyCode) }
            when (keyCode) {
                KeyEvent.VK_CONTROL -> isControlDown = false
                KeyEvent.VK_SHIFT -> isShiftDown = false
                KeyEvent.VK_ALT -> isAltDown = false
            }
        }
    }

    override var rootVisual: Visual? = null
        set(value) {
            field = value
            redraw()
        }

    // JInternalFrame("--", true, true, true, true) { isVisible = true
    val jPanel = object : JPanel(GridBagLayout()) {
        override fun paintComponent(g0: Graphics?) {
            super.paintComponent(g0)
            val g = GUIUtil.getSmoothGraphics(g0)
            rootVisual?.draw(g)
        }
    }.apply {
        addMouseListener(mouseListener)
        addMouseMotionListener(mouseMotionListener)
        inputMap.put(KeyStroke.getKeyStroke("pressed A"), "ctrl+")
        inputMap.put(KeyStroke.getKeyStroke("released CONTROL"), "ctrl-")
        inputMap.put(KeyStroke.getKeyStroke("pressed SHIFT"), "shift+")
        inputMap.put(KeyStroke.getKeyStroke("released SHIFT"), "shift-")
        inputMap.put(KeyStroke.getKeyStroke("pressed ALT"), "alt+")
        inputMap.put(KeyStroke.getKeyStroke("released ALT"), "alt-")
        actionMap.put("ctrl+", actionControlPressed)
        actionMap.put("ctrl-", actionControlReleased)
        actionMap.put("shift+", actionShiftPressed)
        actionMap.put("shift-", actionShiftReleased)
        actionMap.put("alt+", actionAltPressed)
        actionMap.put("alt-", actionAltReleased)
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