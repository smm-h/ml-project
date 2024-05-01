package src.main.gui

import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.JOptionPane.showMessageDialog


@Suppress("MemberVisibilityCanBePrivate", "PropertyName", "unused")
class Menu(val gui: GUI) : JMenuBar() {

    // TODO keyboard shortcuts

    val mnuFile =
        JMenu("File").also {
            add(it)
            it.mnemonic = KeyEvent.VK_F
        }

    val mnuNew =
        JMenuItem("New").also {
            mnuFile.add(it)
            it.mnemonic = KeyEvent.VK_N
            it.addActionListener { gui.new() }
        }

    val mnuOpen =
        JMenuItem("Open").also {
            mnuFile.add(it)
            it.mnemonic = KeyEvent.VK_O
            it.addActionListener { gui.open() }
        }

    val mnuSave =
        JMenuItem("Save").also {
            mnuFile.add(it)
            it.mnemonic = KeyEvent.VK_S
            it.addActionListener { gui.save() }
        }

    val mnuSaveAs =
        JMenuItem("Save As").also {
            mnuFile.add(it)
            it.mnemonic = KeyEvent.VK_A
            it.addActionListener { gui.saveAs() }
        }

    val mnuExit =
        JMenuItem("Exit").also {
            mnuFile.add(JSeparator(SwingConstants.HORIZONTAL))
            mnuFile.add(it)
            it.addActionListener { gui.exit() }
        }

    val mnuView =
        JMenu("View").also {
            add(it)
            it.mnemonic = KeyEvent.VK_V
        }

    val mnuEnableDarkMode =
        JCheckBoxMenuItem("Enable dark mode").also {
            mnuView.add(it)
            it.state = gui.darkMode
            it.addActionListener { _ -> gui.darkMode = it.state }
        }

    val mnuHelp =
        JMenu("Help").also {
            add(it)
            it.mnemonic = KeyEvent.VK_H
        }

    val mnuAbout =
        JMenuItem("About").also {
            mnuHelp.add(it)
            it.addActionListener { showMessageDialog(null, "By SMMH") }
        }
}