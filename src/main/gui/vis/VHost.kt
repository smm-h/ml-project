package src.main.gui.vis

import java.util.*

interface VHost {
    var padding: Float

    val width: Int
    val height: Int
    fun setSize(w: Float, h: Float)

    val mouseButtonDown: BooleanArray

    val atMouse: Queue<Visual>

    fun addLayer(layer: VLayer<*>)

    fun register(it: Visual)

    fun redraw()
}