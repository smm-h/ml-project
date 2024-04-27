package src.main.genex

import LabeledData
import MultilayerPerceptron
import util.Stats
import util.Util
import util.Util.formatPercentage
import java.io.File
import java.nio.file.Files
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path

@Suppress("unused", "CanBeParameter")
class Experiment(
    private val directory: ExperimentDirectory,
    private val data: List<LabeledData>,
    private val blueprint: MultilayerPerceptron.Blueprint,
    private val labeler: (FloatArray) -> Int,
    private val seed: Long,
    private val logToFile: Boolean = true,
    private val printLog: Boolean = true,
    logFilename: String = "log.txt",
    plotFilename: String = "plot.csv",
    private val populationSize: Int,
    private val parentsCount: Int,
    private val mutationProbability: Float,
    private val maxGeneration: Int = -1,
    private val saveEvery: Long = 30000L,
    private val logEvery: Int = 1,
    private val plotEvery: Int = 1,
) {

    companion object {
        val dateFormat: DateFormat = SimpleDateFormat("YYYYMMdd_hhmmss")
    }

    private val random = Random(seed)
    private val startDate = Date()
    private val timestamp = dateFormat.format(startDate)
    private val path = directory.path + "/" + timestamp + "/"
    private val logFile = File(path + logFilename)
    private val plotFile = File(path + plotFilename)

    init {
        directory.indexFile.appendText("$timestamp\n")
        Files.createDirectory(Path(path))
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
        log("Welcome to a new experiment!")
        log("Random number generator seed: $seed")
        log("Evolution will continue until ${if (maxGeneration == -1) "it is stopped" else "generation #$maxGeneration"}")

        val dataSize = data.size
        log("Testing data size: $dataSize")
        log("Population size: $populationSize")

        log("MLP structure: ${blueprint.structure}")
        log("MLP file size: ${Util.formatFileSizeBase2(blueprint.fileSizeExact)}")

        assert(parentsCount >= 1)
        val parentsSlice = 0 until parentsCount
        log("Only the top $parentsCount get to reproduce")

        log("Mutation probability: ${formatPercentage(mutationProbability)}")

//        val previousBest = MultilayerPerceptron.readFromFile(modelFilename("202404111_070518"))
        val population = Array(populationSize) {
//            RankedModel(previousBest)
            RankedModel(createRandomModel())
        }
        log("Starting models are random")


        var lastSavedAt = 0L
        log("The top model will be saved every ${saveEvery / 1000L} seconds")
        log("Messages will be logged every $logEvery generation(s)")
        log("Plot information will be saved every $plotEvery generation(s)")

        val stats = Stats.Floats()


        logSeparator()
        log("EXPERIMENT STARTED")

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