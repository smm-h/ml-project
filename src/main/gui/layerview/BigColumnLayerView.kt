package src.main.gui.layerview

import src.main.gui.vis.VHost

class BigColumnLayerView(
    override val host: VHost,
    override val cellCount: Int,
    override var cellSize: Float = 8f,
) : AbstractLayerView(), BigLayerView, ColumnLayerView {
    override var data = FloatArray(cellCount)

    override val w
        get() = cellSize
    override val h
        get() = cellCount * cellSize

    override fun getCellX(cellIndex: Int) = 0f
    override fun getCellY(cellIndex: Int) = cellIndex * cellSize
}