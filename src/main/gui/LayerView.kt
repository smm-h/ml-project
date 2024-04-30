package src.main.gui

import src.main.util.Util
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

    var data: FloatArray

    override fun iterator(): Iterator<Int> = (0 until cellCount).iterator()

    fun getCellX(cellIndex: Int): Float
    fun getCellY(cellIndex: Int): Float

    fun getCellCenterX(cellIndex: Int) = getCellX(cellIndex) + cellSize / 2f
    fun getCellCenterY(cellIndex: Int) = getCellY(cellIndex) + cellSize / 2f

    fun draw(
        g: Graphics2D,
        x: Float,
        y: Float,
        enabled: Boolean,
    ) {
        if (enabled) {
            forEach { i ->
                drawCell(g, i, x + getCellX(i), y + getCellY(i))
            }
        } else {
            g.color = Color.GRAY
            g.fillRect(
                x.roundToInt(),
                y.roundToInt(),
                hSize.roundToInt(),
                vSize.roundToInt(),
            )
        }
    }

    fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float)

    interface Small : LayerView {
        override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) {
            val rx = x.roundToInt()
            val ry = y.roundToInt()
            val s = cellSize.roundToInt()
            g.color = Util.gray(1 - (data[i]).coerceIn(0f, 1f))
            g.fillOval(rx, ry, s, s)
            g.color = Util.HALF_BLACK
            g.drawOval(rx, ry, s, s)
        }
    }

    interface Big : LayerView {
        override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) {
            val rx = x.roundToInt()
            val ry = y.roundToInt()
            val s = cellSize.roundToInt()
            g.color = Util.gray(1 - (data[i]).coerceIn(0f, 1f))
            g.fillRect(rx, ry, s, s)
            g.color = Util.HALF_BLACK
            g.drawRect(rx, ry, s, s)
        }
    }

    interface Column : LayerView {
        operator fun set(i: Int, v: Float) {
            data[i] = v.coerceIn(0f, 1f)
        }

        operator fun get(i: Int): Float = data[i]
    }

    interface Grid : LayerView {
        val hCellCount: Int
        val vCellCount: Int
        override val cellCount: Int
            get() = hCellCount * vCellCount

        operator fun set(i: Int, j: Int, v: Float) {
            data[i + j * hCellCount] = v.coerceIn(0f, 1f)
        }

        operator fun get(i: Int, j: Int): Float = data[i + j * hCellCount]
    }

    class SmallColumn(
        override val cellCount: Int,
        override val cellSize: Float = 32f,
    ) : Small, Column {
        override var data = FloatArray(cellCount)

        val vSep = cellSize * 0.5f

        override val hSize
            get() = cellSize
        override val vSize
            get() = cellCount * (cellSize + vSep) - vSep

        override fun getCellX(cellIndex: Int) = 0f
        override fun getCellY(cellIndex: Int) = cellIndex * (cellSize + vSep)
    }

    class BigColumn(
        override val cellCount: Int,
        override val cellSize: Float = 8f,
    ) : Big, Column {
        override var data = FloatArray(cellCount)

        override val hSize
            get() = cellSize
        override val vSize
            get() = cellCount * cellSize

        override fun getCellX(cellIndex: Int) = 0f
        override fun getCellY(cellIndex: Int) = cellIndex * cellSize
    }

    class SmallGrid(
        override val hCellCount: Int,
        override val vCellCount: Int,
        override val cellSize: Float = 32f,
    ) : Small, Grid {
        override var data = FloatArray(cellCount)

        val hSep = cellSize * 0.5f
        val vSep = cellSize * 0.5f

        override val hSize
            get() = hCellCount * (cellSize + hSep) - hSep
        override val vSize
            get() = vCellCount * (cellSize + vSep) - vSep

        override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * (cellSize + hSep)
        override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * (cellSize + vSep)
    }

    class BigGrid(
        override val hCellCount: Int,
        override val vCellCount: Int,
        override val cellSize: Float = 8f,
    ) : Big, Grid {
        override var data = FloatArray(cellCount)

        override val hSize
            get() = hCellCount * cellSize
        override val vSize
            get() = vCellCount * cellSize

        override fun getCellX(cellIndex: Int) = cellIndex.mod(hCellCount) * cellSize
        override fun getCellY(cellIndex: Int) = cellIndex.div(hCellCount) * cellSize
    }
}