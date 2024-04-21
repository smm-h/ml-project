import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.pathString

open class Experiment {
    companion object {
        private val dateFormat = SimpleDateFormat("YYYYMMDD_hhmmss")
    }

    val startDate = Date()
    val timestamp: String = dateFormat.format(startDate)
    val path: Path = Path("experiments/$timestamp")
    val logFile: File = File("${path.pathString}/log.txt")

    // TODO last experiment

    init {
        Files.createDirectory(path)
        File("experiments/list.txt").appendText("$timestamp\n")
        log("EXPERIMENT STARTED AT: $timestamp")
        logSeparator()
    }

    fun log(string: String) {
        println(string)
        logFile.appendText("[${dateFormat.format(Date())}] \t $string\n")
    }

    fun logSeparator() =
        log("--------------------------------")


    fun finish() {
        logSeparator()
        log("EXPERIMENT FINISHED IN $timeElapsed ms")
    }

    val timeElapsed: Long get() = Date().time - startDate.time
}