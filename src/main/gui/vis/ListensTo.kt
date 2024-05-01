package src.main.gui.vis

import java.awt.event.KeyEvent

interface ListensTo {

    interface MouseDrag : Visual, ListensTo {
        fun onMouseDrag(x: Float, y: Float)
    }

    interface MouseMove : Visual, ListensTo {
        fun onMouseMove(x: Float, y: Float)
    }

    interface Hover : MouseMove {
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

    interface MousePress : Hover {
        fun onMousePress(x: Float, y: Float, b: MouseButton)
    }

    interface MouseRelease : Hover {
        fun onMouseRelease(x: Float, y: Float, b: MouseButton)
    }

    interface KeyPress : ListensTo {
        fun onKeyPress(keyCode: Int)
    }

    interface KeyRelease : ListensTo {
        fun onKeyRelease(keyCode: Int)
    }

    class KeyCombination(
        private val ctrl: Boolean = false,
        private val shift: Boolean = false,
        private val alt: Boolean = false,
        private val keyCode: Int,
        private val onKeyCombination: () -> Unit,
    ) : KeyPress, KeyRelease {

        private var ctrlIsDown = false
        private var shiftIsDown = false
        private var altIsDown = false

        override fun onKeyPress(keyCode: Int) {
            when (keyCode) {
                KeyEvent.VK_CONTROL -> ctrlIsDown = true
                KeyEvent.VK_SHIFT -> shiftIsDown = true
                KeyEvent.VK_ALT -> altIsDown = true
                else -> {
                    if (keyCode == this.keyCode
                        && ctrl == ctrlIsDown
                        && shift == shiftIsDown
                        && alt == altIsDown
                    ) onKeyCombination()
                }
            }
        }

        override fun onKeyRelease(keyCode: Int) {
            when (keyCode) {
                KeyEvent.VK_CONTROL -> ctrlIsDown = false
                KeyEvent.VK_SHIFT -> shiftIsDown = false
                KeyEvent.VK_ALT -> altIsDown = false
            }
        }
    }
}