package src.main.gui.layerview

interface GridLayerView : LayerView {
    val hCellCount: Int
    val vCellCount: Int
    override val cellCount: Int
        get() = hCellCount * vCellCount

    operator fun set(i: Int, j: Int, v: Float) {
        data[i + j * hCellCount] = v.coerceIn(0f, 1f)
    }

    operator fun get(i: Int, j: Int): Float = data[i + j * hCellCount]
}