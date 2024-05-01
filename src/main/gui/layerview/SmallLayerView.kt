package src.main.gui.layerview

import src.main.gui.GUIUtil
import src.main.gui.GUIUtil.drawOvalFloat
import src.main.gui.GUIUtil.fillOvalFloat
import src.main.gui.GUIUtil.fillRectFloat
import java.awt.Graphics2D

interface SmallLayerView : LayerView {
    override fun drawCell(g: Graphics2D, i: Int, x: Float, y: Float) {
        g.color = getValueColor(data[i])
        g.fillOvalFloat(x, y, cellSize, cellSize)
        g.color = GUIUtil.HALF_BLACK
        g.drawOvalFloat(x, y, cellSize, cellSize)
    }

    override fun drawDisabled(g: Graphics2D) {
        g.color = GUIUtil.HALF_GRAY
        g.fillRectFloat(x, y + cellSize / 2, w, h - cellSize)
        g.fillOvalFloat(x, y, cellSize, cellSize)
        g.fillOvalFloat(x, y + h - cellSize, cellSize, cellSize)
    }
}