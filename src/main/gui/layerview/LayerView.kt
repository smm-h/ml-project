package src.main.gui.layerview

import src.main.gui.GUIUtil
import src.main.gui.GUIUtil.drawOutline
import src.main.gui.GUIUtil.fillOutline
import src.main.gui.vis.MouseButton
import src.main.gui.vis.Visual
import java.awt.Color
import java.awt.Graphics2D
import javax.swing.JPopupMenu
import kotlin.math.roundToInt

/**
 * A one or two-dimensional view of a layer and its cells.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
interface LayerView :
    Visual.Movable,
    Visual.Hoverable,
    Visual.ListensToMouseDrag,
    Visual.ListensToMouseRelease,
    Iterable<Int> {

    val cellCount: Int
    var cellSize: Float

    override val w: Float
    override val h: Float

    var data: FloatArray
    var showCells: Boolean
    var editing: Boolean

    val popupMenu: JPopupMenu

    override fun iterator(): Iterator<Int> = (0 until cellCount).iterator()

    fun getCellX(cellIndex: Int): Float
    fun getCellY(cellIndex: Int): Float

    fun getCellCenterX(cellIndex: Int) = getCellX(cellIndex) + cellSize / 2f
    fun getCellCenterY(cellIndex: Int) = getCellY(cellIndex) + cellSize / 2f

    override fun draw(g: Graphics2D) {
        if (containsMouse) {
            g.color = GUIUtil.QUARTER_GRAY
            g.fillOutline(x, y, w, h, 4f)
        }
        if (showCells) {
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
        if (containsMouse) {
            g.color = Color.GRAY
            g.drawOutline(x, y, w, h, 4f)
        }
    }

    fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float)

    override fun onMouseRelease(x: Float, y: Float, b: MouseButton) {
        if (b == MouseButton.LEFT) {
            forwardPropagateStartingFrom()
        }
    }
}