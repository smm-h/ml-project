package src.main.gui

import src.main.util.Util.HALF_BLACK
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.roundToInt

/**
 * A one or two-dimensional view of a layer and its cells.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
interface LayerView : Iterable<Int> {
    val cellCount: Int
    val cellSize: Float
    val hSize: Float
    val vSize: Float

    override fun iterator(): Iterator<Int> = (0 until cellCount).iterator()

    fun getCellX(cellIndex: Int): Float
    fun getCellY(cellIndex: Int): Float

    fun getCellCenterX(cellIndex: Int) = getCellX(cellIndex) + cellSize / 2f
    fun getCellCenterY(cellIndex: Int) = getCellY(cellIndex) + cellSize / 2f

    fun draw(g: Graphics2D, atX: Float, atY: Float) {
        forEach { i -> drawCell(g, i, atX + getCellX(i), atY + getCellY(i)) }
    }

    fun drawCell(g: Graphics2D, cellIndex: Int, atX: Float, atY: Float)

    interface CircleCells : LayerView {
        override fun drawCell(g: Graphics2D, cellIndex: Int, atX: Float, atY: Float) {
            val x = atX.roundToInt()
            val y = atY.roundToInt()
            val s = cellSize.roundToInt()
            g.color = Color.WHITE
            g.fillOval(x, y, s, s)
            g.color = HALF_BLACK
            g.drawOval(x, y, s, s)
        }
    }

    interface SquareCells : LayerView {
        override fun drawCell(g: Graphics2D, cellIndex: Int, atX: Float, atY: Float) {
            val x = atX.roundToInt()
            val y = atY.roundToInt()
            val s = cellSize.roundToInt()
            g.color = Color.WHITE
            g.fillRect(x, y, s, s)
            g.color = HALF_BLACK
            g.drawRect(x, y, s, s)
        }
    }

    class Column(
        override val cellCount: Int,
        override val cellSize: Float = 16f,
    ) : CircleCells {

        val vSep = cellSize * 0.5f

        override val hSize
            get() = cellSize
        override val vSize
            get() = cellCount * (cellSize + vSep) - vSep

        override fun getCellX(cellIndex: Int) = 0f
        override fun getCellY(cellIndex: Int) = cellIndex * (cellSize + vSep)

    }

    class DenseColumn(
        override val cellCount: Int,
        override val cellSize: Float = 4f,
    ) : SquareCells {

        override val hSize
            get() = cellSize
        override val vSize
            get() = cellCount * cellSize

        override fun getCellX(cellIndex: Int) = 0f
        override fun getCellY(cellIndex: Int) = cellIndex * cellSize
    }

    class Grid(
        val hCellCount: Int,
        val vCellCount: Int,
        override val cellSize: Float = 16f,
    ) : CircleCells {
        override val cellCount get() = hCellCount * vCellCount

        val hSep = cellSize * 0.5f
        val vSep = cellSize * 0.5f

        override val hSize
            get() = hCellCount * (cellSize + hSep) - hSep
        override val vSize
            get() = vCellCount * (cellSize + vSep) - vSep

        override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * (cellSize + hSep)
        override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * (cellSize + vSep)
    }

    class DenseGrid(
        val hCellCount: Int,
        val vCellCount: Int,
        override val cellSize: Float = 4f,
    ) : SquareCells {
        override val cellCount get() = hCellCount * vCellCount

        override val hSize
            get() = hCellCount * cellSize
        override val vSize
            get() = vCellCount * cellSize

        override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * cellSize
        override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * cellSize
    }
}