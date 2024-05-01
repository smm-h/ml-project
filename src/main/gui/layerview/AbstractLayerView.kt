package src.main.gui.layerview

import javax.swing.*

abstract class AbstractLayerView : LayerView {
    override var containsMouse: Boolean = false
    override var x: Float = 0f
    override var y: Float = 0f

    override var showCells: Boolean = true
        set(value) {
            println(value)
            field = value
            if (mnuShowCells.state != value)
                mnuShowCells.state = value
            host.redraw()
        }

    private val mnuShowCells =
        JCheckBoxMenuItem("Show cells").apply {
            state = showCells
            addActionListener { showCells = state }
        }

    override var editing: Boolean = false
        set(value) {
            field = value
            if (mnuEditing.state != value)
                mnuEditing.state = value
            mnuClear.isEnabled = value
            mnuRandomize.isEnabled = value
            host.redraw()
        }

    private val mnuEditing =
        JCheckBoxMenuItem("Enable editing").apply {
            state = editing
            addActionListener { editing = state }
        }

    private val mnuClear =
        JMenuItem("Clear").apply {
            isEnabled = false
            addActionListener { data = FloatArray(cellCount) }
        }

    private val mnuRandomize =
        JMenuItem("Randomize").apply {
            isEnabled = false
            addActionListener { data = FloatArray(cellCount) { Math.random().toFloat() } }
        }

    private val mnuProperties =
        JMenuItem("Properties").apply {
            addActionListener {
            }
        }

    override val popupMenu = JPopupMenu().apply {
        add(mnuShowCells)
        add(mnuEditing)
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(mnuClear)
        add(mnuRandomize)
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(mnuProperties)
    }
}