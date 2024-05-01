package src.main.gui.vis

import java.awt.Graphics2D

interface Visual {
    val host: VHost
    var x: Float
    var y: Float

    val area: Float

    var containsMouse: Boolean

    fun contains(x: Float, y: Float, margin: Float): Boolean

    fun draw(g: Graphics2D)
}