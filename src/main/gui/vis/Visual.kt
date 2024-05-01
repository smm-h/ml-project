package src.main.gui.vis

import src.main.gui.GUIUtil.PI
import src.main.util.Util.sqr
import java.awt.Graphics2D
import kotlin.math.sqrt

interface Visual {
    val host: VHost
    val x: Float
    val y: Float
    val w: Float
    val h: Float

    interface Movable : Visual {
        override var x: Float
        override var y: Float
    }

    interface Resizable : Visual {
        override var w: Float
        override var h: Float
    }

    val area: Float
        get() = w * h

    fun contains(x: Float, y: Float, margin: Float): Boolean =
        x >= this.x - margin && y >= this.y - margin && x < this.x + w + margin && y < this.y + h + margin

    fun draw(g: Graphics2D)

    interface Circular : Visual {
        val radius: Float

        override val w: Float get() = radius * 2
        override val h: Float get() = radius * 2

        override val area: Float
            get() = sqr(radius) * PI

        override fun contains(x: Float, y: Float, margin: Float): Boolean =
            sqrt(sqr(this.x - x) + sqr(this.y - y)) <= radius + margin
    }

    interface ListensToMouseDrag : Visual {
        fun onMouseDrag(x: Float, y: Float)
    }

    interface ListensToMouseMove : Visual {
        fun onMouseMove(x: Float, y: Float)
    }

    interface Hoverable : ListensToMouseMove {
        var containsMouse: Boolean

        override fun onMouseMove(x: Float, y: Float) {
            val s = contains(x, y, 2f)
            if (containsMouse != s) {
                containsMouse = s
                if (s) {
                    host.atMouse.add(this)
                } else {
                    host.atMouse.remove(this)
                }
                host.redraw()
            }
        }
    }

    interface ListensToMousePress : Hoverable {
        fun onMousePress(x: Float, y: Float, b: MouseButton)
    }

    interface ListensToMouseRelease : Hoverable {
        fun onMouseRelease(x: Float, y: Float, b: MouseButton)
    }
}