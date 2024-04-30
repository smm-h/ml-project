package src.main.gui.vis

import src.main.gui.GUIUtil.PI
import src.main.util.Util
import src.main.util.Util.sqr
import kotlin.math.sqrt

interface Circular : Visual {
    val radius: Float

    override val area: Float
        get() = sqr(radius) * PI

    override fun contains(x: Float, y: Float, margin: Float): Boolean =
        sqrt(Util.sqr(this.x - x) + Util.sqr(this.y - y)) <= radius + margin
}