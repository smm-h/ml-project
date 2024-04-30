package src.main.gui.vis

import src.main.util.Util
import kotlin.math.sqrt

interface Circular : Visual {
    val radius: Float

    override fun contains(x: Float, y: Float, margin: Float): Boolean =
        sqrt(Util.sqr(this.x - x) + Util.sqr(this.y - y)) <= radius + margin
}