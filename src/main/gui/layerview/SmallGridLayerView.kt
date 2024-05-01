package src.main.gui.layerview

import src.main.gui.MultilayerPerceptronView

class SmallGridLayerView(
    multilayerPerceptronView: MultilayerPerceptronView,
    override val hCellCount: Int,
    override val vCellCount: Int,
    override var cellSize: Float = 32f,
    override var hSep: Float = cellSize * 0.5f,
    override var vSep: Float = cellSize * 0.5f,
) : AbstractLayerView(multilayerPerceptronView), SmallLayerView, GridLayerView {
    override var data = FloatArray(cellCount)

    override val w
        get() = hCellCount * (cellSize + hSep) - hSep
    override val h
        get() = vCellCount * (cellSize + vSep) - vSep

    override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * (cellSize + hSep)
    override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * (cellSize + vSep)
}