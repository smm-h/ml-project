package src.main.gui.vis

import src.main.gui.GUI
import java.util.*

interface VHost {

    val gui: GUI

    var padding: Float

    val width: Int
    val height: Int
    fun setSize(w: Float, h: Float)

    val isMouseLeftButtonDown: Boolean
    val isMouseRightButtonDown: Boolean
    val isMouseMiddleButtonDown: Boolean

    val isControlDown: Boolean
    val isShiftDown: Boolean
    val isAltDown: Boolean

    val atMouse: Queue<Visual>

    var rootVisual: Visual?

    fun register(it: ListensTo)

    fun redraw()
}