package src.main.gui

import MultilayerPerceptron
import src.main.util.Util.by
import java.awt.GridLayout
import javax.swing.*


@Suppress("MemberVisibilityCanBePrivate", "unused")
class GUI {
    val tree = JTree().apply {

    }
    val treePanel = JPanel(GridLayout()).apply {
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

    var skin: String = DEFAULT_SKIN_NAME
        set(value) {
            try {
                UIManager.setLookAndFeel(installedSkins[value])
                field = value
            } catch (_: Exception) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
                    field = DEFAULT_SKIN_NAME
                } catch (_: Exception) {
                }
            }
            SwingUtilities.updateComponentTreeUI(frame);
        }

    companion object {
        private const val DEFAULT_SKIN_NAME = "Default"

        val INSTANCE by lazy { GUI() }

        @JvmStatic
        fun main(args: Array<String>) {
            INSTANCE.apply {
                skin = "Nimbus"
                tabs.addTab("new-tab", MLPUI(MultilayerPerceptron.Structure(1, 1, listOf(1))))
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