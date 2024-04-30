package src.main.gui

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLightLaf
import src.main.mnist.MNIST
import src.main.util.Util.by
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.io.File
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel


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
        this.dividerLocation = 192
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
                    preferredSize = 640 by 480
                    size = preferredSize
                    isVisible = true
                    pack()
                }
            }
        }
    }
}