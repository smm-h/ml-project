package src.main.gui.layerview

interface ColumnLayerView : LayerView {
    operator fun set(i: Int, v: Float) {
        data[i] = v.coerceIn(0f, 1f)
    }

    operator fun get(i: Int): Float = data[i]
}