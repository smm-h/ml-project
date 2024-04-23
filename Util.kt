import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest


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
}