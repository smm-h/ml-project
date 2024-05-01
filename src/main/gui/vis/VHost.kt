package src.main.gui.vis

interface VHost {
    var padding: Float

    fun setSize(w: Float, h: Float)
    fun redraw()
}