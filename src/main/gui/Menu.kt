package src.main.gui

import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.JOptionPane.showMessageDialog
import javax.swing.filechooser.FileNameExtensionFilter


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
    }

    val Open = JMenuItem("Open").also {
        File.add(it)
        it.mnemonic = KeyEvent.VK_O
        it.addActionListener {
            val fileChooser = JFileChooser()
            fileChooser.fileFilter = FileNameExtensionFilter("Multilayer Perceptron (*.mlp)", "mlp")
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                val file = fileChooser.selectedFile
                gui.tabs.addTab(file.name, MultilayerPerceptronView(file.absolutePath))
            }
        }
    }

    val Save = JMenuItem("Save").also {
        File.add(it)
        it.mnemonic = KeyEvent.VK_S
    }

    val SaveAs = JMenuItem("Save As").also {
        File.add(it)
        it.mnemonic = KeyEvent.VK_A
    }

    val Exit = JMenuItem("Exit").also {
        File.add(JSeparator(SwingConstants.HORIZONTAL))
        File.add(it)
        it.addActionListener {

        }
    }

    val View = JMenu("View").also {
        add(it)
        it.mnemonic = KeyEvent.VK_V
    }

    val EnableDarkMode = JCheckBoxMenuItem("Enable dark mode").also {
        View.add(it)
        it.state = gui.darkMode
        it.addActionListener { _ ->
            gui.darkMode = it.state
        }
    }

    val Help = JMenu("Help").also {
        add(it)
        it.mnemonic = KeyEvent.VK_H
    }

    val About = JMenuItem("About").also {
        Help.add(it)
        it.addActionListener {
            showMessageDialog(null, "By SMMH")
        }
    }
}