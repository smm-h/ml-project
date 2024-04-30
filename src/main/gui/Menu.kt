package src.main.gui

import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.JOptionPane.showMessageDialog


@Suppress("MemberVisibilityCanBePrivate", "PropertyName", "unused")
class Menu(val gui: GUI) : JMenuBar() {

    // TODO keyboard shortcuts

    val File = JMenu("File").also {
        add(it)
        it.mnemonic = KeyEvent.VK_F
    }

    val New = JMenuItem("New").also {
        File.add(it)
        it.mnemonic = KeyEvent.VK_N
        it.addActionListener { gui.new() }
    }

    val Open = JMenuItem("Open").also {
        File.add(it)
        it.mnemonic = KeyEvent.VK_O
        it.addActionListener { gui.open() }
    }

    val Save = JMenuItem("Save").also {
        File.add(it)
        it.mnemonic = KeyEvent.VK_S
        it.addActionListener { gui.save() }
    }

    val SaveAs = JMenuItem("Save As").also {
        File.add(it)
        it.mnemonic = KeyEvent.VK_A
        it.addActionListener { gui.saveAs() }
    }

    val Exit = JMenuItem("Exit").also {
        File.add(JSeparator(SwingConstants.HORIZONTAL))
        File.add(it)
        it.addActionListener { gui.exit() }
    }

    val View = JMenu("View").also {
        add(it)
        it.mnemonic = KeyEvent.VK_V
    }

    val EnableDarkMode = JCheckBoxMenuItem("Enable dark mode").also {
        View.add(it)
        it.state = gui.darkMode
        it.addActionListener { _ -> gui.darkMode = it.state }
    }

    val Help = JMenu("Help").also {
        add(it)
        it.mnemonic = KeyEvent.VK_H
    }

    val About = JMenuItem("About").also {
        Help.add(it)
        it.addActionListener { showMessageDialog(null, "By SMMH") }
    }
}