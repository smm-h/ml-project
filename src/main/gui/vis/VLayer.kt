package src.main.gui.vis

import java.awt.Graphics2D

class VLayer<T : Visual>(val host: VHost) {

    val visuals = mutableListOf<T>()
    var x: Float = 0f
    var y: Float = 0f

    fun draw(g: Graphics2D) {
        for (visual in visuals) {
            visual.draw(g)
        }
    }
}
