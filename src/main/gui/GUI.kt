package src.main.gui

import src.main.util.Util.by
import javax.swing.*


@Suppress("MemberVisibilityCanBePrivate", "unused")
class GUI {
    val tree = JTree()
    val treePanel = JPanel()
    val tabs = JTabbedPane()
    val tabsPanel = JPanel()
    val split = JSplitPane()
    val panel = JPanel()
    val frame = JFrame("Neurarium").apply {
        isLocationByPlatform = true
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        jMenuBar = Menu.jMenuBar
    }

    private val installedSkins: Map<String, String> by lazy {
        UIManager.getInstalledLookAndFeels()
            .map { it.className }
            .associateBy { it.slice(it.lastIndexOf(".") + 1..it.length - 12) }
    }

    init {
        try {
            UIManager.setLookAndFeel(installedSkins["Nimbus"])
        } catch (_: Exception) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (_: Exception) {
            }
        }
        frame.contentPane = panel
    }

    companion object {
        val INSTANCE by lazy { GUI() }

        @JvmStatic
        fun main(args: Array<String>) {
            INSTANCE.frame.apply {
                size = 640 by 480
                isVisible = true
                pack()
            }
        }
    }
}
