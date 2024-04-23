import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

@Suppress("unused", "KotlinConstantConditions")
class Experiment(
    private val directory: ExperimentDirectory,
    private val data: List<LabeledData>,
    private val blueprint: MultilayerPerceptron.Blueprint,
    private val labeler: (FloatArray) -> Int,
    private val seed: Long = (Math.random() * Long.MAX_VALUE).toLong(),
    private val logToFile: Boolean = true,
    private val printLog: Boolean = true,
    logFilename: String = "log.txt",
    plotFilename: String = "plot.csv",
) {

    companion object {
        val dateFormat: DateFormat = SimpleDateFormat("YYYYMMdd_hhmmss")
    }

    private val random = Random(seed)
    private val startDate = Date()
    private val timestamp = dateFormat.format(startDate)
    private val path = directory.path + "/" + timestamp
    private val logFile = File(path + logFilename)
    private val plotFile = File(path + plotFilename)

    init {
        directory.indexFile.appendText("$timestamp\n")
    }

    private fun log(string: String) {
        if (printLog) println(string)
        if (logToFile) logFile.appendText("[${dateFormat.format(Date())}] \t $string\n")
    }

    private fun logSeparator() =
        log("--------------------------------")

    private fun getElapsedTime(): Long = Date().time - startDate.time

    private fun createRandomModel(): MultilayerPerceptron =
        blueprint.instantiate().apply { randomize(random) }

    private data class RankedModel(
        var model: MultilayerPerceptron,
        var rank: Float = 0f
    )

    fun run() {

        log("EXPERIMENT STARTED")
        logSeparator()

        log("Random number generator seed: $seed")

        val maxGeneration = -1
        log("Evolution will continue until ${if (maxGeneration == -1) "it is stopped" else "generation #$maxGeneration"}")

        val dataSize = data.size
        log("Testing data size: $dataSize")

        val populationSize = (2.0).pow(7).toInt()
        log("Population size: $populationSize")

        val parentsRatio = 0.2
        val parentsCount = (parentsRatio * populationSize).toInt().coerceAtLeast(1)
        val parentsSlice = 0 until parentsCount
        log("Only the top ${parentsRatio * 100}% (=$parentsCount) get to reproduce")

        val mutationProbability = 0.05f
        log("Mutation probability: ${mutationProbability * 100}%")

//        val previousBest = MultilayerPerceptron.readFromFile(modelFilename("202404111_070518"))
        val population = Array(populationSize) {
//            RankedModel(previousBest)
            RankedModel(createRandomModel())
        }

        val saveEvery = 30 * 1000L
        var lastSavedAt = 0L
        log("Save model every ${saveEvery / 1000L} seconds")

        log("Model structure: ${blueprint.structure}")
        log("Model file size: ${blueprint.fileSizeExact}")

        val logEvery = 1
        log("Log information every $logEvery generation(s)")

        val plotEvery = 1
        log("Plot information every $plotEvery generation(s)")

        val stats = Stats.Floats()

        println("Press Enter to start...")
        readln()

        plotFile.appendText("Generation,TimeElapsed,Average,Min,Max\n")

        var generation = 0

        while (maxGeneration == -1 || generation < maxGeneration) {

            val logging = generation % logEvery == 0

            if (logging) {
                logSeparator()
                log("Generation: #$generation")
            }

            generation++

            for (p in population) {
                val model = p.model
                var successful = 0
                for (datapoint in data) {
                    val input = datapoint.data
                    val output = model.forwardPropagate(input)
                    if (labeler(output) == datapoint.label) {
                        successful++
                    }
                }

                val accuracy = successful * 100f / dataSize

                stats.check(accuracy)

                p.rank = -accuracy
            }

            if (logging) {
                log("[Average: ${stats.average}, Min: ${stats.min}, Max: ${stats.max}]")
            }

            if (generation % plotEvery == 0) {
                plotFile.appendText("$generation,${getElapsedTime()},${stats.average},${stats.min},${stats.max}\n")
                stats.clear()
            }

            population.sortBy(RankedModel::rank)

            val topModel = population[0].model

            if (getElapsedTime() - lastSavedAt >= saveEvery) {
                topModel.writeToFile(path + "/0." + MultilayerPerceptron.FILE_EXT)
                logSeparator()
                log("Saved!")
                lastSavedAt = getElapsedTime()
            }

            val parents = population.slice(parentsSlice).map(RankedModel::model)

            population.forEach {
                it.model = blueprint.instantiate().apply { populate(parents, random, mutationProbability) }
            }

            population[populationSize - 1].model = topModel
        }

        logSeparator()
        log("Elapsed time: ${getElapsedTime()}")
        log("EXPERIMENT FINISHED")
    }
}