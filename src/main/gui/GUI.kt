package src.main.gui

import ActivationFunction.Companion.RELU
import MultilayerPerceptron
import com.formdev.flatlaf.FlatLightLaf
import src.main.util.Util.by
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
//        FlatLightLaf.initIconColors()
//        tree.putClientProperty("Tree.background", "#ff0000")
//        tree.putClientProperty("Tree.background", Color.RED)
//        tree.putClientProperty("background", "#ff0000")
//        tree.putClientProperty("background", Color.RED)
//        tree.putClientProperty("JTree.paintSelection", "true")
//        tree.putClientProperty("JTree.paintSelection", true)
//        tree.putClientProperty("Tree.paintSelection", "true")
//        tree.putClientProperty("Tree.paintSelection", true)
//        tree.isRootVisible = false
//        tree.addTreeSelectionListener {
//            if (it.isAddedPath) {
//                showMessageDialog(null, it.path)
//            }
//        }
//        tree.showsRootHandles = true
//        tree.putClientProperty("Tree.showDefaultIcons", true)
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
        this.dividerLocation = 192
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

        private val installedSkins: Map<String, String> by lazy {
            UIManager.getInstalledLookAndFeels()
                .map { it.className }
                .associateBy { it.slice(it.lastIndexOf(".") + 1..it.length - 12) }
        }

        val INSTANCE by lazy { GUI() }

        @JvmStatic
        fun main(args: Array<String>) {

            val structure = MultilayerPerceptron.Structure(10, 10, listOf(10))
            val blueprint = MultilayerPerceptron.Blueprint(structure, RELU, listOf(RELU))
            val model = blueprint.instantiate().also { it.randomize(Random()) }

            FlatLightLaf.setup()
//            UIManager.setLookAndFeel(installedSkins["Nimbus"])
            UIManager.setLookAndFeel(FlatLightLaf())

            INSTANCE.apply {
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