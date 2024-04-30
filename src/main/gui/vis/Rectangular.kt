package src.main.gui.vis

interface Rectangular : Visual {
    val w: Float
    val h: Float

    override fun contains(x: Float, y: Float, margin: Float): Boolean =
        x >= this.x - margin && y >= this.y - margin && x < this.x + w + margin && y < this.y + h + margin
}