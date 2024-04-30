package src.main.gui.vis

interface Visual {
    val x: Float
    val y: Float

    fun contains(x: Float, y: Float): Boolean
}