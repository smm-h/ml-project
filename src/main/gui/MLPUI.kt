package src.main.gui

import MultilayerPerceptron
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class MLPUI(
    val structure: MultilayerPerceptron.Structure,
) : JPanel() {
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        g!!.apply {
            color = Color.BLACK
            for (i in 0 until structure.hiddenLayerSizes.size + 2) {
                val x = i * 64
                drawRect(x, 0, 32, 32)
            }
        }
    }
}