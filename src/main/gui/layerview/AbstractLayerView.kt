package src.main.gui.layerview

abstract class AbstractLayerView : LayerView {
    override var containsMouse: Boolean = true
    override var enabled: Boolean = true
    override var x: Float = 0f
    override var y: Float = 0f
}