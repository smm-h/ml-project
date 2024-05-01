package src.main.gui.layerview

import src.main.gui.vis.VHost

class BigGridLayerView(
    override val host: VHost,
    override val hCellCount: Int,
    override val vCellCount: Int,
    override var cellSize: Float = 8f,
) : AbstractLayerView(), BigLayerView, GridLayerView {
    override var data = FloatArray(cellCount)

    // TODO error
    override var hSep: Float
        get() = 0f
        set(_) {}
    override var vSep: Float
        get() = 0f
        set(_) {}

    override val w
        get() = hCellCount * cellSize
    override val h
        get() = vCellCount * cellSize

    override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * cellSize
    override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * cellSize
}