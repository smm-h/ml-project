package src.main.gui

import src.main.gui.GUIUtil.createBoundCheckBoxMenuItem
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.JOptionPane.showMessageDialog


@Suppress("MemberVisibilityCanBePrivate", "PropertyName", "unused")
class Menu(val gui: GUI) : JMenuBar() {

    // TODO keyboard shortcuts

    val mnuFile =
        JMenu("File").also {
            it.mnemonic = KeyEvent.VK_F
        }

    val mnuNew =
        JMenuItem("New").also {
            it.mnemonic = KeyEvent.VK_N
            it.addActionListener { gui.new() }
        }

    val mnuOpen =
        JMenuItem("Open").also {
            it.mnemonic = KeyEvent.VK_O
            it.addActionListener { gui.open() }
        }

    val mnuSave =
        JMenuItem("Save").also {
            it.mnemonic = KeyEvent.VK_S
            it.addActionListener { gui.save() }
        }

    val mnuSaveAs =
        JMenuItem("Save As").also {
            it.mnemonic = KeyEvent.VK_A
            it.addActionListener { gui.saveAs() }
        }

    val mnuExit =
        JMenuItem("Exit").also {
            it.addActionListener { gui.exit() }
        }

    val mnuView =
        JMenu("View").also {
            it.mnemonic = KeyEvent.VK_V
        }

    val mnuEnableDarkMode =
        createBoundCheckBoxMenuItem("Enable dark mode", gui::darkMode)

    val mnuHelp =
        JMenu("Help").also {
            it.mnemonic = KeyEvent.VK_H
        }

    val mnuAbout =
        JMenuItem("About").also {
            it.addActionListener { showMessageDialog(null, "By SMMH") }
        }

    init {
        add(mnuFile.apply {
            add(mnuNew)
            add(mnuOpen)
            add(mnuSave)
            add(mnuSaveAs)
            add(JSeparator(SwingConstants.HORIZONTAL))
            add(mnuExit)
        })
        add(mnuView.apply {
            add(mnuEnableDarkMode)
        })
        add(mnuHelp.apply {
            add(mnuAbout)
        })
    }
}