package src.main.gui.layerview

import src.main.gui.GUIUtil
import src.main.gui.GUIUtil.drawOutline
import src.main.gui.GUIUtil.fillOutline
import src.main.gui.GUIUtil.showPopupMenu
import src.main.gui.MultilayerPerceptronView
import src.main.gui.vis.ListensTo
import src.main.gui.vis.MouseButton
import src.main.gui.vis.Visual
import java.awt.Color
import java.awt.Graphics2D
import javax.swing.JPopupMenu

/**
 * A one or two-dimensional view of a layer and its cells.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
interface LayerView :
    Visual.Movable,
    ListensTo.Hover,
    ListensTo.MouseDrag,
    ListensTo.MouseRelease,
    Iterable<Int> {

    val multilayerPerceptronView: MultilayerPerceptronView

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
            forEach { i -> drawCell(g, i, x + getCellX(i), y + getCellY(i)) }
        } else {
            drawDisabled(g)
        }
        if (containsMouse) {
            g.color = Color.GRAY
            g.drawOutline(x, y, w, h, 4f)
        }
    }

    fun drawDisabled(g: Graphics2D)

    fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float)

    override fun onMouseRelease(x: Float, y: Float, b: MouseButton) {
        when (b) {
            MouseButton.LEFT -> {
                if (editing) {
                    multilayerPerceptronView.lastChangedLayer = this
                    host.redraw()
                }
            }

            MouseButton.RIGHT -> {
                showPopupMenu(popupMenu, x, y)
            }

            MouseButton.MIDDLE -> {}
        }
    }
}