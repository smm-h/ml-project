package src.main.gui

import src.main.util.Util
import java.awt.Graphics2D
import kotlin.math.roundToInt

/**
 * A one or two-dimensional view of a layer and its cells.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
sealed class LayerView(
    val cellCount: Int,
    val cellSize: Float,
) : Iterable<Int> {
    abstract val hSize: Float
    abstract val vSize: Float

    var data: FloatArray = FloatArray(cellCount)

    override fun iterator(): Iterator<Int> = (0 until cellCount).iterator()

    abstract fun getCellX(cellIndex: Int): Float
    abstract fun getCellY(cellIndex: Int): Float

    fun getCellCenterX(cellIndex: Int) = getCellX(cellIndex) + cellSize / 2f
    fun getCellCenterY(cellIndex: Int) = getCellY(cellIndex) + cellSize / 2f

    fun draw(g: Graphics2D, x: Float, y: Float) {
        forEach { i -> drawCell(g, i, x + getCellX(i), y + getCellY(i)) }
    }

    abstract fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float)

    fun drawSquareCell(g: Graphics2D, i: Int, x: Float, y: Float) {
        val rx = x.roundToInt()
        val ry = y.roundToInt()
        val s = cellSize.roundToInt()
        g.color = Util.gray(1 - data[i])
        g.fillRect(rx, ry, s, s)
        g.color = Util.HALF_BLACK
        g.drawRect(rx, ry, s, s)
    }

    fun drawCircleCell(g: Graphics2D, i: Int, x: Float, y: Float) {
        val rx = x.roundToInt()
        val ry = y.roundToInt()
        val s = cellSize.roundToInt()
        g.color = Util.gray(1 - data[i])
        g.fillOval(rx, ry, s, s)
        g.color = Util.HALF_BLACK
        g.drawOval(rx, ry, s, s)
    }

    class Column(
        cellCount: Int,
        cellSize: Float = 32f,
    ) : LayerView(cellCount, cellSize) {
        val vSep = cellSize * 0.5f

        override val hSize
            get() = cellSize
        override val vSize
            get() = cellCount * (cellSize + vSep) - vSep

        override fun getCellX(cellIndex: Int) = 0f
        override fun getCellY(cellIndex: Int) = cellIndex * (cellSize + vSep)

        override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) =
            drawCircleCell(g, i, x, y)
    }

    class DenseColumn(
        cellCount: Int,
        cellSize: Float = 8f,
    ) : LayerView(cellCount, cellSize) {
        override val hSize
            get() = cellSize
        override val vSize
            get() = cellCount * cellSize

        override fun getCellX(cellIndex: Int) = 0f
        override fun getCellY(cellIndex: Int) = cellIndex * cellSize

        override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) =
            drawSquareCell(g, i, x, y)
    }

    class Grid(
        val hCellCount: Int,
        val vCellCount: Int,
        cellSize: Float = 32f,
    ) : LayerView(hCellCount * vCellCount, cellSize) {
        val hSep = cellSize * 0.5f
        val vSep = cellSize * 0.5f

        override val hSize
            get() = hCellCount * (cellSize + hSep) - hSep
        override val vSize
            get() = vCellCount * (cellSize + vSep) - vSep

        override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * (cellSize + hSep)
        override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * (cellSize + vSep)

        override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) =
            drawCircleCell(g, i, x, y)
    }

    class DenseGrid(
        val hCellCount: Int,
        val vCellCount: Int,
        cellSize: Float = 8f,
    ) : LayerView(hCellCount * vCellCount, cellSize) {
        override val hSize
            get() = hCellCount * cellSize
        override val vSize
            get() = vCellCount * cellSize

        override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * cellSize
        override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * cellSize

        override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) =
            drawSquareCell(g, i, x, y)
    }
}