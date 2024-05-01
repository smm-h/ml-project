package src.main.gui.vis

interface VPositioner {
    fun <T : Visual.Movable> position(x0: Float, y0: Float, m: Iterable<T>, spacing: Float = 0f)
    sealed class Linear : VPositioner {
        data object Horizontal : Linear() {
            override fun <T : Visual.Movable> position(x0: Float, y0: Float, m: Iterable<T>, spacing: Float) {
                var x = x0
                for (visual in m) {
                    visual.x = x
                    visual.y = y0
                    x += visual.w + spacing
                }
            }
        }

        data object Vertical : Linear() {
            override fun <T : Visual.Movable> position(x0: Float, y0: Float, m: Iterable<T>, spacing: Float) {
                var y = y0
                for (visual in m) {
                    visual.x = x0
                    visual.y = y
                    y += visual.w + spacing
                }
            }
        }
    }
}