package src.main.gui.layerview

import javax.swing.JComponent

class SmallColumnLayerView(
    override val host: JComponent,
    override val cellCount: Int,
    override var cellSize: Float = 32f,
    var vSep: Float = cellSize * 0.5f,
) : AbstractLayerView(), SmallLayerView, ColumnLayerView {
    override var data = FloatArray(cellCount)

    override val w
        get() = cellSize
    override val h
        get() = cellCount * (cellSize + vSep) - vSep

    override fun getCellX(cellIndex: Int) = 0f
    override fun getCellY(cellIndex: Int) = cellIndex * (cellSize + vSep)
}