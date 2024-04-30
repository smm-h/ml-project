package src.main.util

import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.math.roundToInt


object Util {
    fun compareFiles(f1: String, f2: String, algorithm: String = "SHA-256"): Boolean {
        val md = MessageDigest.getInstance(algorithm)
        val d1 = md.digest(Files.readAllBytes(Path.of(f1)))
        val d2 = md.digest(Files.readAllBytes(Path.of(f2)))
        return d1.contentEquals(d2)


//        model.writeToFile("model1")
//        val read = Perceptron.readFromFile("model1")
//        read.writeToFile("model2")
//        println(compareFiles("model1", "model2"))
    }

    private val fileSizeUnitsBase2 =
        arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")

    private val fileSizeUnitsBase10 =
        arrayOf("Bi", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB")

    /**
     * Units based on powers of 2: used by Windows, and generally by companies
     * for marketing and billing purposes
     */
    fun formatFileSizeBase2(size: Long): String {
        assert(size >= 0)
        var s = size.toDouble()
        var u = 0
        while (s > 1000) {
            s /= 1000
            u++
        }
        return "${String.format("%.2f", s)} ${fileSizeUnitsBase2[u]}"
    }

    /**
     * Units based on powers of 10: international standard; used for transfer
     * rate, storage capacity, and performance measuring.
     */
    fun formatFileSizeBase10(size: Long): String {
        assert(size >= 0)
        var s = size.toDouble()
        var u = 0
        while (s > 1024) {
            s /= 1024
            u++
        }
        return "${String.format("%.2f", s)} ${fileSizeUnitsBase10[u]}"
    }

    fun formatPercentage(x: Float): String =
        ((x * 100000).roundToInt() / 1000f).toString() + "%"

    infix fun Int.by(that: Int) =
        Dimension(this, that)

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
        it.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        it.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        it.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        it.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        it.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
    }
}