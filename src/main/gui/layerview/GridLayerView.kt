package src.main.gui.layerview

import src.main.gui.vis.MouseButton

interface GridLayerView : LayerView {
    val hCellCount: Int
    val vCellCount: Int
    override val cellCount: Int
        get() = hCellCount * vCellCount

    operator fun set(i: Int, j: Int, v: Float) {
        data[i + j * hCellCount] = v.coerceIn(0f, 1f)
    }

    operator fun get(i: Int, j: Int): Float = data[i + j * hCellCount]

    override fun onMouseDrag(x: Float, y: Float) {
        if (containsMouse && host.mouseButtonDown[MouseButton.LEFT.ordinal]) {
            val sgn = 1 // TODO -1
            val i = (x / cellSize).toInt()
            val j = (y / cellSize).toInt()
            if (i in 0..hCellCount && j in 0..vCellCount) {
                val iM = i > 0
                val jM = j > 0
                val iP = i < hCellCount - 1
                val jP = j < vCellCount - 1
                if (iM && jM) this[i - 1, j - 1] += sgn * 0.1f
                if (iP && jM) this[i + 1, j - 1] += sgn * 0.1f
                if (iM && jP) this[i - 1, j + 1] += sgn * 0.1f
                if (iP && jP) this[i + 1, j + 1] += sgn * 0.1f
                if (iM) this[i - 1, j] += sgn * 0.2f
                if (iP) this[i + 1, j] += sgn * 0.2f
                if (jM) this[i, j - 1] += sgn * 0.2f
                if (jP) this[i, j + 1] += sgn * 0.2f
                this[i, j] += sgn * 1f
                host.redraw()
            }
        }
    }
}