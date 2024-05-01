package src.main.gui.layerview

import src.main.gui.MultilayerPerceptronView

class SmallColumnLayerView(
    multilayerPerceptronView: MultilayerPerceptronView,
    override val cellCount: Int,
    override var cellSize: Float = 32f,
    override var vSep: Float = cellSize * 0.5f,
) : AbstractLayerView(multilayerPerceptronView), SmallLayerView, ColumnLayerView {
    override var data = FloatArray(cellCount)

    override val w
        get() = cellSize
    override val h
        get() = cellCount * (cellSize + vSep) - vSep

    override fun getCellX(cellIndex: Int) = 0f
    override fun getCellY(cellIndex: Int) = cellIndex * (cellSize + vSep)
}