package src.main.gui.layerview

import src.main.gui.GUIUtil.createBoundCheckBoxMenuItem
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JSeparator
import javax.swing.SwingConstants

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
        createBoundCheckBoxMenuItem("Show cells", ::showCells)

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
        createBoundCheckBoxMenuItem("Enable editing", ::editing)

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