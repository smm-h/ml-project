package src.main.gui.layerview

interface ColumnLayerView : LayerView {
    var vSep: Float

    operator fun set(i: Int, v: Float) {
        data[i] = v.coerceIn(0f, 1f)
    }

    operator fun get(i: Int): Float = data[i]

    override fun onMouseDrag(x: Float, y: Float) {
        if (editing && containsMouse && host.isMouseLeftButtonDown) {
            val sgn = if (host.isControlDown) -1 else +1
            val i = ((x - this.x) / (cellSize)).toInt()
            val j = ((y - this.y) / (cellSize + vSep)).toInt()
            if (i in 0..1 && j in 0..cellCount) {
                this[j] += sgn * 1f
                host.redraw()
            }
        }
    }
}