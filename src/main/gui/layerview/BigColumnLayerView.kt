package src.main.gui.layerview

import src.main.gui.MultilayerPerceptronView

class BigColumnLayerView(
    multilayerPerceptronView: MultilayerPerceptronView,
    override val cellCount: Int,
    override var cellSize: Float = 8f,
) : AbstractLayerView(multilayerPerceptronView), BigLayerView, ColumnLayerView {
    override var data = FloatArray(cellCount)

    // TODO error
    override var vSep: Float
        get() = 0f
        set(_) {}

    override val w
        get() = cellSize
    override val h
        get() = cellCount * cellSize

    override fun getCellX(cellIndex: Int) = 0f
    override fun getCellY(cellIndex: Int) = cellIndex * cellSize
}