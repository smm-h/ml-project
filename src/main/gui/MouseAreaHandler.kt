package src.main.gui

import src.main.gui.vis.Visual
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter

class MouseAreaHandler : MouseMotionAdapter() {
    val visuals = mutableListOf<Visual>()
    override fun mouseMoved(e: MouseEvent) {
        for (it in visuals) {
            if (it.contains(e.x.toFloat(), e.y.toFloat())) {
                it.invoke()
            }
        }
    }
}