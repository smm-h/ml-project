package src.main.gui

import ActivationFunction.Companion.RELU
import MultilayerPerceptron
import com.formdev.flatlaf.FlatLightLaf
import src.main.util.Util.by
import java.awt.Component
import java.awt.GridLayout
import java.util.*
import javax.swing.*
import javax.swing.JOptionPane.showMessageDialog
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel


@Suppress("MemberVisibilityCanBePrivate", "unused")
class GUI {
    private object CustomCellRenderer : DefaultTreeCellRenderer() {
        override fun getTreeCellRendererComponent(
            tree: JTree?,
            value: Any?,
            sel: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
        ): Component {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
            icon = UIManager.getIcon("FileView.directoryIcon")
            return this
        }
    }

    val root = DefaultMutableTreeNode("Root").apply {
        this.add(DefaultMutableTreeNode("Example"))
    }
    val treeModel: TreeModel = DefaultTreeModel(root)
    val tree = JTree(treeModel).apply {
        cellRenderer = CustomCellRenderer
    }
    val treePanel = JPanel(GridLayout()).apply {
//        tree.isRootVisible = false
        tree.addTreeSelectionListener {
            if (it.isAddedPath) {
                showMessageDialog(null, it.path)
            }
        }
        add(tree)
    }
    val tabs = JTabbedPane().apply {

    }
    val tabsPanel = JPanel(GridLayout()).apply {
        add(tabs)
    }
    val split = JSplitPane().apply {
        leftComponent = treePanel
        rightComponent = tabsPanel
    }
    val panel = JPanel(GridLayout()).apply {
        add(split)
    }
    val frame = JFrame("Neurarium").apply {
        isLocationByPlatform = true
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        jMenuBar = Menu.jMenuBar
        contentPane = panel
    }

    private val installedSkins: Map<String, String> by lazy {
        UIManager.getInstalledLookAndFeels()
            .map { it.className }
            .associateBy { it.slice(it.lastIndexOf(".") + 1..it.length - 12) }
    }

    companion object {
        private const val DEFAULT_SKIN_NAME = "Default"

        val INSTANCE by lazy { GUI() }

        @JvmStatic
        fun main(args: Array<String>) {

            FlatLightLaf.setup()

            val structure = MultilayerPerceptron.Structure(10, 10, listOf(10))
            val blueprint = MultilayerPerceptron.Blueprint(structure, RELU, listOf(RELU))
            val model = blueprint.instantiate().also { it.randomize(Random()) }

            INSTANCE.apply {
                UIManager.setLookAndFeel(FlatLightLaf())
                tabs.addTab("new-tab", MLPUI(model))
                frame.apply {
                    preferredSize = 640 by 480
                    size = preferredSize
                    isVisible = true
                    pack()
                }
            }
        }
    }
}