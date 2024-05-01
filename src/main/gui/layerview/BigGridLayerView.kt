package src.main.gui.layerview

import javax.swing.JComponent

class BigGridLayerView(
    override val host: JComponent,
    override val hCellCount: Int,
    override val vCellCount: Int,
    override var cellSize: Float = 8f,
) : AbstractLayerView(), BigLayerView, GridLayerView {
    override var data = FloatArray(cellCount)

    override val w
        get() = hCellCount * cellSize
    override val h
        get() = vCellCount * cellSize

    override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * cellSize
    override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * cellSize
}