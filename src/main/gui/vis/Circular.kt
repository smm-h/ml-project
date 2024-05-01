package src.main.gui.vis

import src.main.gui.GUIUtil.PI
import src.main.util.Util.sqr
import kotlin.math.sqrt

interface Circular : Visual {
    val radius: Float

    override val area: Float
        get() = sqr(radius) * PI

    override fun contains(x: Float, y: Float, margin: Float): Boolean =
        sqrt(sqr(this.x - x) + sqr(this.y - y)) <= radius + margin
}