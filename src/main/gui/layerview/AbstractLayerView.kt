package src.main.gui.layerview

import src.main.gui.GUIUtil.createBoundCheckBoxMenuItem
import src.main.gui.MultilayerPerceptronView
import src.main.gui.vis.VHost
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JSeparator
import javax.swing.SwingConstants

abstract class AbstractLayerView(final override val multilayerPerceptronView: MultilayerPerceptronView) : LayerView {

    override val host: VHost by multilayerPerceptronView::host

    override var containsMouse: Boolean = false
    override var x: Float = 0f
    override var y: Float = 0f

    override var showCells: Boolean = true
        set(value) {
            println(value)
            field = value
            if (mnuShowCellValues.state != value)
                mnuShowCellValues.state = value
            host.redraw()
        }

    private val mnuShowCellValues =
        createBoundCheckBoxMenuItem("Show cell values", ::showCells)

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
            addActionListener {
                data = FloatArray(cellCount)
                host.redraw()
            }
        }

    private val mnuRandomize =
        JMenuItem("Randomize").apply {
            isEnabled = false
            addActionListener {
                data = FloatArray(cellCount) { Math.random().toFloat() }
                host.redraw()
            }
        }

    private val mnuProperties =
        JMenuItem("Properties").apply {
            addActionListener {
            }
        }

    override val popupMenu = JPopupMenu().apply {
        add(mnuShowCellValues)
        add(mnuEditing)
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(mnuClear)
        add(mnuRandomize)
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(mnuProperties)
    }
}