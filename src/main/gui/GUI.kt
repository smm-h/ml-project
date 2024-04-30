package src.main.gui

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLightLaf
import src.main.gui.GUIUtil.by
import src.main.mnist.MNIST
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.io.File
import javax.swing.*
import javax.swing.JFileChooser.APPROVE_OPTION
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel
import kotlin.system.exitProcess


@Suppress("MemberVisibilityCanBePrivate", "unused")
class GUI {

    var darkMode: Boolean = false
        set(value) {
            if (value) {
                assert(FlatDarkLaf.setup())
            } else {
                assert(FlatLightLaf.setup())
            }
            SwingUtilities.updateComponentTreeUI(frame)
            field = value
        }

    val models = DefaultMutableTreeNode("Models").apply {
    }
    val root = DefaultMutableTreeNode().apply {
//        val filename = "D:/CE/ML/project/experiments/20240428_094012/0.mlp"
        val filename = "D:/CE/ML/project/experiments/best.mlp"
//        add(models)
//        addFileToTree(filename)
        add(DefaultMutableTreeNode(TreeItem.FileItem(File(filename))))

    }
    val treeModel: TreeModel = DefaultTreeModel(root)
    val tree = JTree(treeModel).apply {
    }
    val treePanel = JPanel(GridLayout()).apply {
        tree.isRootVisible = false
        tree.addTreeSelectionListener {
            if (it.isAddedPath) {
                val x = (it.path.lastPathComponent as DefaultMutableTreeNode).userObject
                if (x is TreeItem) {
                    when (x) {
                        is TreeItem.FileItem -> {
                            val file = x.file
                            tabs.addTab(file.name, JPanel(GridBagLayout()).apply {
                                add(MultilayerPerceptronView(file.absolutePath, 0 to (28 by 28)).apply {
                                    input = MNIST.training[(Math.random() * 1000).toInt()].data
                                })
                            })
                        }
                    }
                }
            }
        }
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
        dividerLocation = 192
    }
    val panel = JPanel(GridLayout()).apply {
        add(split)
    }
    val frame = JFrame("Neurarium").apply {
        isLocationByPlatform = true
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        jMenuBar = Menu(this@GUI)
        contentPane = panel
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            GUI().apply {
                darkMode = false
                frame.apply {
                    preferredSize = 800 by 600
                    size = preferredSize
                    isVisible = true
                    pack()
                }
            }
        }
    }

    fun addFileToTree(filename: String) {
        root.add(DefaultMutableTreeNode(TreeItem.FileItem(File(filename))))
        tree.revalidate()
        tree.repaint()
    }

    fun new() {

    }

    private val mlpExtensionFilter =
        FileNameExtensionFilter("Multilayer Perceptron (*.mlp)", "mlp")

    fun open() {
        JFileChooser().apply {
            fileFilter = mlpExtensionFilter
            if (showOpenDialog(frame) == APPROVE_OPTION) {
                val file = selectedFile
                addFileToTree(file.absolutePath)
                // tabs.addTab(file.name, MultilayerPerceptronView(file.absolutePath))
            }
        }
    }

    fun save() {

    }

    fun saveAs() {

    }

    fun exit() {
        frame.isVisible = false
        exitProcess(0)
    }
}