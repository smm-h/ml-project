package src.main.gui

import src.main.gui.vis.MouseButton
import src.main.gui.vis.MouseButton.*
import src.main.gui.vis.VPanel
import src.main.gui.vis.Visual
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.AbstractAction
import javax.swing.JCheckBox
import javax.swing.JCheckBoxMenuItem
import javax.swing.JPopupMenu
import kotlin.math.roundToInt
import kotlin.reflect.KMutableProperty0

object GUIUtil {

    val PI = kotlin.math.PI.toFloat()

    infix fun Int.by(that: Int) =
        Dimension(this, that)

    infix fun Float.by(that: Float) =
        this.roundToInt() by that.roundToInt()

    fun gray(value: Float, alpha: Float = 1f) =
        Color(value, value, value, alpha)

    val HALF_BLACK =
        gray(0f, 0.5f)
    val QUARTER_BLACK =
        gray(0f, 0.25f)
    val HALF_GRAY =
        gray(0.5f, 0.5f)
    val QUARTER_GRAY =
        gray(0.5f, 0.25f)

    fun getSmoothGraphics(g: Graphics?) = (g as Graphics2D).also {
        it.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        it.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )
        it.setRenderingHint(
            RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
        )
        it.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )
        it.setRenderingHint(
            RenderingHints.KEY_STROKE_CONTROL,
            RenderingHints.VALUE_STROKE_PURE
        )
    }

    fun Graphics2D.drawOutline(x: Float, y: Float, w: Float, h: Float, margin: Float) {
        val arc = (margin * 2).roundToInt().coerceAtLeast(1)
        drawRoundRect(
            (x - margin).roundToInt(),
            (y - margin).roundToInt(),
            (w + margin * 2).roundToInt(),
            (h + margin * 2).roundToInt(),
            arc, arc,
        )
    }

    fun Graphics2D.fillOutline(x: Float, y: Float, w: Float, h: Float, margin: Float) {
        val arc = (margin * 2).roundToInt().coerceAtLeast(1)
        fillRoundRect(
            (x - margin).roundToInt(),
            (y - margin).roundToInt(),
            (w + margin * 2).roundToInt(),
            (h + margin * 2).roundToInt(),
            arc, arc,
        )
    }

    fun Graphics2D.drawRectFloat(x: Float, y: Float, w: Float, h: Float) {
        drawRect(
            x.roundToInt(),
            y.roundToInt(),
            w.roundToInt(),
            h.roundToInt(),
        )
    }

    fun Graphics2D.fillRectFloat(x: Float, y: Float, w: Float, h: Float) {
        fillRect(
            x.roundToInt(),
            y.roundToInt(),
            w.roundToInt(),
            h.roundToInt(),
        )
    }

    fun Graphics2D.drawOvalFloat(x: Float, y: Float, w: Float, h: Float) {
        drawOval(
            x.roundToInt(),
            y.roundToInt(),
            w.roundToInt(),
            h.roundToInt(),
        )
    }

    fun Graphics2D.fillOvalFloat(x: Float, y: Float, w: Float, h: Float) {
        fillOval(
            x.roundToInt(),
            y.roundToInt(),
            w.roundToInt(),
            h.roundToInt(),
        )
    }

    fun Graphics2D.drawLineFloat(x1: Float, y1: Float, x2: Float, y2: Float) {
        drawLine(
            x1.roundToInt(),
            y1.roundToInt(),
            x2.roundToInt(),
            y2.roundToInt(),
        )
    }

    val MouseEvent.mouseButton: MouseButton?
        get() = when (button) {
            MouseEvent.BUTTON1 -> LEFT
            MouseEvent.BUTTON2 -> MIDDLE
            MouseEvent.BUTTON3 -> RIGHT
            else -> null
        }

    fun Visual.createStill(draw: (Graphics2D) -> Unit): BufferedImage =
        BufferedImage(w.roundToInt(), h.roundToInt(), BufferedImage.TYPE_INT_ARGB)
            .also { draw(getSmoothGraphics(it.graphics)) }

    fun Visual.drawStill(g: Graphics2D, still: BufferedImage) {
        g.drawImage(still, x.roundToInt(), y.roundToInt(), null)
    }

    fun Visual.showPopupMenu(m: JPopupMenu, x: Float, y: Float) =
        m.show((host as VPanel).jPanel, x.roundToInt(), y.roundToInt())

    fun createBoundCheckBox(text: String, property: KMutableProperty0<Boolean>) =
        JCheckBox(text).apply {
            isSelected = property.get()
            addActionListener { property.set(isSelected) }
        }.apply {
            isSelected = property.get()
        }

    fun createBoundCheckBoxMenuItem(text: String, property: KMutableProperty0<Boolean>) =
        JCheckBoxMenuItem(text).apply {
            state = property.get()
            addActionListener { property.set(state) }
        }.apply {
            state = property.get()
        }

    fun action(name: String, act: () -> Unit) =
        object : AbstractAction(name) {
            override fun actionPerformed(e: ActionEvent) {
                act()
            }
        }

    operator fun Color.component1(): Float = red / 255f
    operator fun Color.component2(): Float = green / 255f
    operator fun Color.component3(): Float = blue / 255f
}