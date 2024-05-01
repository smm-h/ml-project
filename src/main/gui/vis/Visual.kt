package src.main.gui.vis

import java.awt.Graphics2D
import javax.swing.JComponent

interface Visual {
    val x: Float
    val y: Float

    val area: Float

    var containsMouse: Boolean

    fun contains(x: Float, y: Float, margin: Float): Boolean

    fun draw(g: Graphics2D)

    val host: JComponent
}