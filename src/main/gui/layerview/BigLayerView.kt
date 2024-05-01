package src.main.gui.layerview

import src.main.gui.GUIUtil
import src.main.gui.GUIUtil.drawRectFloat
import src.main.gui.GUIUtil.fillRectFloat
import java.awt.Graphics2D

interface BigLayerView : LayerView {
    override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) {
        g.color = getValueColor(data[i])
        g.fillRectFloat(x, y, cellSize, cellSize)
        g.color = GUIUtil.HALF_BLACK
        g.drawRectFloat(x, y, cellSize, cellSize)
    }

    override fun drawDisabled(g: Graphics2D) {
        g.color = GUIUtil.HALF_GRAY
        g.fillRectFloat(x, y, w, h)
    }
}