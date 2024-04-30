package src.main.gui.layerview

import src.main.gui.vis.Rectangular
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.roundToInt

/**
 * A one or two-dimensional view of a layer and its cells.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
interface LayerView : Iterable<Int>, Rectangular {
    val cellCount: Int
    var cellSize: Float

    override val w: Float
    override val h: Float

    var data: FloatArray
    var enabled: Boolean

    override fun iterator(): Iterator<Int> = (0 until cellCount).iterator()

    fun getCellX(cellIndex: Int): Float
    fun getCellY(cellIndex: Int): Float

    fun getCellCenterX(cellIndex: Int) = getCellX(cellIndex) + cellSize / 2f
    fun getCellCenterY(cellIndex: Int) = getCellY(cellIndex) + cellSize / 2f

    fun draw(g: Graphics2D) {
        if (enabled) {
            forEach { i ->
                drawCell(g, i, x + getCellX(i), y + getCellY(i))
            }
        } else {
            g.color = Color.GRAY
            g.fillRect(
                x.roundToInt(),
                y.roundToInt(),
                w.roundToInt(),
                h.roundToInt(),
            )
        }
    }

    fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float)
}