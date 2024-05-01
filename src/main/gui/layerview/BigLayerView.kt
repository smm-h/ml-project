package src.main.gui.layerview

import src.main.gui.GUIUtil
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.roundToInt

interface BigLayerView : LayerView {
    override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) {
        val rx = x.roundToInt()
        val ry = y.roundToInt()
        val s = cellSize.roundToInt()
        g.color = GUIUtil.gray(1 - (data[i]).coerceIn(0f, 1f))
        g.fillRect(rx, ry, s, s)
        g.color = GUIUtil.HALF_BLACK
        g.drawRect(rx, ry, s, s)
    }

    override fun drawDisabled(g: Graphics2D) {
        g.color = Color.GRAY
        g.fillRect(
            x.roundToInt(),
            y.roundToInt(),
            w.roundToInt(),
            h.roundToInt(),
        )
    }
}