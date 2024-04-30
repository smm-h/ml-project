package src.main.gui.vis

interface Rectangular : Visual {
    val w: Float
    val h: Float

    override fun contains(x: Float, y: Float): Boolean =
        x >= this.x && y >= this.y && x < this.x + w && y < this.y + h
}