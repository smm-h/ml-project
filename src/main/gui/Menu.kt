package src.main.gui

import java.awt.event.KeyEvent
import javax.swing.*


object Menu {
    val jMenuBar = JMenuBar().apply {
        add(JMenu("File").apply {
            this.mnemonic = KeyEvent.VK_F
            add(JMenu("Create New...").apply {
                this.mnemonic = KeyEvent.VK_N
                add(JMenuItem("Multilayer Perceptron").apply {
                    addActionListener {}
                })
                add(JMenuItem("Genetic Experiment").apply {
                    addActionListener {}
                })
            })
            add(JMenu("Open").apply {
                this.mnemonic = KeyEvent.VK_O
                add(JMenuItem("Multilayer Perceptron").apply {
                    addActionListener {
                        val fileChooser = JFileChooser()
                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            val file = fileChooser.selectedFile
                            // load from file
                        }
                    }
                })
            })
            add(JMenu("Save").apply {
                this.mnemonic = KeyEvent.VK_S
            })
            add(JMenu("Save As").apply {
            })
            add(JSeparator())
            add(JMenu("Add Source File...").apply {
                this.mnemonic = KeyEvent.VK_A
                add(JMenuItem("Multilayer Perceptron").apply {
                    addActionListener {}
                })
                add(JMenuItem("Genetic Experiment Configuration").apply {
                    addActionListener {}
                })
                add(JMenuItem("Plot").apply {
                    addActionListener {}
                })
                add(JMenuItem("Dataset").apply {
                    addActionListener {}
                })
            })
            add(JMenu("Add Source Directory...").apply {
                this.mnemonic = KeyEvent.VK_D
            })
            add(JMenuItem("Remove Source").apply {
                this.mnemonic = KeyEvent.VK_DELETE
            })
            add(JSeparator())
            add(JMenu("Exit").apply {
                this.mnemonic = KeyEvent.VK_E
                addActionListener {

                }
            })
        })
        add(JMenu("Help").apply {
            this.mnemonic = KeyEvent.VK_H
            add(JMenuItem("About").apply {
                addActionListener {
                    JOptionPane.showMessageDialog(null, "By SMMH");
                }
            })
        })
    }
}