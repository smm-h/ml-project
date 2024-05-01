package src.main.gui.layerview

import src.main.gui.GUIUtil
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.roundToInt

interface SmallLayerView : LayerView {
    override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) {
        val rx = x.roundToInt()
        val ry = y.roundToInt()
        val s = cellSize.roundToInt()
        g.color = GUIUtil.gray(1 - (data[i]).coerceIn(0f, 1f))
        g.fillOval(rx, ry, s, s)
        g.color = GUIUtil.HALF_BLACK
        g.drawOval(rx, ry, s, s)
    }

    override fun drawDisabled(g: Graphics2D) {
        g.color = Color.GRAY
        g.fillRect(
            x.roundToInt(),
            (y + cellSize / 2).roundToInt(),
            w.roundToInt(),
            (h - cellSize).roundToInt(),
        )
        g.fillOval(
            x.roundToInt(),
            y.roundToInt(),
            cellSize.roundToInt(),
            cellSize.roundToInt(),
        )
        g.fillOval(
            x.roundToInt(),
            (y + h - cellSize).roundToInt(),
            cellSize.roundToInt(),
            cellSize.roundToInt(),
        )
    }
}