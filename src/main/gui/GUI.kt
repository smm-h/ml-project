package src.main.gui

import ActivationFunction.Companion.CAPPED_RELU
import ActivationFunction.Companion.RELU
import MultilayerPerceptron
import com.formdev.flatlaf.FlatLightLaf
import src.main.util.Util.by
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.util.*
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel


@Suppress("MemberVisibilityCanBePrivate", "unused")
class GUI {
    val root = DefaultMutableTreeNode().apply {
        this.add(DefaultMutableTreeNode("Example"))
    }
    val treeModel: TreeModel = DefaultTreeModel(root)
    val tree = JTree().apply {
    }
    val treePanel = JPanel(GridLayout()).apply {
        tree.isRootVisible = false
//        tree.addTreeSelectionListener {
//            if (it.isAddedPath) {
//                showMessageDialog(null, it.path)
//            }
//        }
        tree.showsRootHandles = true
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
        this.dividerLocation = 0 // 192
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

    companion object {

        val INSTANCE by lazy { GUI() }

        @JvmStatic
        fun main(args: Array<String>) {

            FlatLightLaf.setup()
            UIManager.setLookAndFeel(FlatLightLaf())

            val structure = MultilayerPerceptron.Structure(784, 10, listOf(64, 64))
            val blueprint = MultilayerPerceptron.Blueprint(structure, CAPPED_RELU, listOf(RELU, RELU))
            val model = blueprint.instantiate()
                .also { it.randomize(Random()) }

            INSTANCE.apply {
                tabs.addTab("new-tab", JPanel(GridBagLayout()).apply {
                    add(MultilayerPerceptronView(model, 0 to (28 by 28)))
                })
                frame.apply {
//                    preferredSize = 640 by 480
                    size = preferredSize
                    isVisible = true
                    pack()
                }
            }
        }
    }
}