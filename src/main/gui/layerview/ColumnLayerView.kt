package src.main.gui.layerview

import src.main.gui.vis.MouseButton

interface ColumnLayerView : LayerView {
    operator fun set(i: Int, v: Float) {
        data[i] = v.coerceIn(0f, 1f)
    }

    operator fun get(i: Int): Float = data[i]

    override fun onMouseDrag(x: Float, y: Float) {
        if (containsMouse && host.mouseButtonDown[MouseButton.LEFT.ordinal]) {
            val sgn = 1 // TODO -1
            val i = (x / cellSize).toInt()
            val j = (y / cellSize).toInt()
            if (i in 0..1 && j in 0..cellCount) {
                this[j] += sgn * 1f
                host.redraw()
            }
        }
    }
}