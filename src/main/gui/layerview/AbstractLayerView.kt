package src.main.gui.layerview

import src.main.gui.GUIUtil.redraw
import javax.swing.*

abstract class AbstractLayerView : LayerView {
    override var containsMouse: Boolean = true
    override var x: Float = 0f
    override var y: Float = 0f

    override var showCells: Boolean = true
        set(value) {
            println(value)
            field = value
            if (showCellsCheckBoxMenuItem.state != value)
                showCellsCheckBoxMenuItem.state = value
            host.redraw()
        }

    private val showCellsCheckBoxMenuItem = JCheckBoxMenuItem("Show cells").apply {
        state = showCells
        addActionListener { showCells = state }
    }

    override var editing: Boolean = false
        set(value) {
            field = value
            if (editingJCheckBoxMenuItem.state != value)
                editingJCheckBoxMenuItem.state = value
            host.redraw()
        }

    private val editingJCheckBoxMenuItem = JCheckBoxMenuItem("Enable editing").apply {
        state = editing
        addActionListener { editing = state }
    }

    override val popupMenu = JPopupMenu().apply {
        add(showCellsCheckBoxMenuItem)
        add(editingJCheckBoxMenuItem)
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(JMenuItem("Change type"))
    }
}