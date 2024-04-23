import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path
import kotlin.math.pow

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter", "unused", "KotlinConstantConditions")
class Genetics(
    val dateFormat: DateFormat = SimpleDateFormat("YYYYMMDD_hhmmss"),
    val seed: Long = (Math.random() * Long.MAX_VALUE).toLong(),
) {

    val startDate = Date()
    val timestamp: String = dateFormat.format(startDate)
    val path = "experiments/$timestamp"
    val logFile = File("$path/log.txt")
    fun timeElapsed() = Date().time - startDate.time

    fun log(string: String) {
        println(string)
        logFile.appendText("[${dateFormat.format(Date())}] \t $string\n")
    }

    fun logSeparator() =
        log("--------------------------------")

    init {
        Files.createDirectory(Path(path))
        File("experiments/list.txt").appendText("$timestamp\n")
        log("EXPERIMENT STARTED AT: $timestamp")
        logSeparator()
    }

    private val hiddenLayerSizes = listOf(16, 16) // (800) //
    private val random = Random(seed)

    fun createEmptyModel(): MultilayerPerceptron =
        MultilayerPerceptron.create(
            inputSize = MNIST.INPUT_SIZE,
            outputSize = MNIST.OUTPUT_SIZE,
            hiddenLayerSizes = hiddenLayerSizes,
            hiddenLayerAF = ActivationFunction.RELU,
            outputAF = ActivationFunction.CAPPED_RELU,
        )

    fun createRandomModel(): MultilayerPerceptron =
        createEmptyModel().apply { randomize(random) }

    fun topModels(): Map<String, MultilayerPerceptron> {
        return Files.readString(Path.of("experiments/list.txt")).trim().split("\n")
            .filter { Files.exists(Path.of((modelFilename(it)))) }
            .associateWith { MultilayerPerceptron.readFromFile(modelFilename(it)) }
    }

    private fun modelFilename(experimentTimestamp: String) =
        "experiments/$experimentTimestamp/0.${MultilayerPerceptron.FILE_EXT}"

    private data class RankedModel(
        var model: MultilayerPerceptron,
        var rank: Float = 0f
    )

    fun run() {
        log("Random number generator seed: $seed")

        val data = MNIST.testing.slice(0 until 1000)
        val dataSize = data.size
        log("Data: MNIST.TESTING_DATASET, size: $dataSize")

        val populationSize = (2.0).pow(8).toInt()
        log("Population: $populationSize")

        val mutationProbability = 0.01f
        log("Mutation probability: $mutationProbability")

//        val noMutationRate = 0.1f
//        val noMutationSize = (noMutationRate * populationSize).toInt()
//        val mutationSize = populationSize - noMutationSize
//        log("No mutation: ${noMutationRate * 100}% = $noMutationSize")

//        val previousBest = MultilayerPerceptron.readFromFile(modelFilename("202404111_070518"))

        val population = Array(populationSize) {
//            RankedModel(previousBest)
            RankedModel(createRandomModel())
        }

        val selectionRate = 0.0f
        val selectionSize = (populationSize * selectionRate).toInt().coerceAtLeast(2)
        val selectionSlice = 0 until selectionSize
        log("Parents: ${selectionRate * 100}% = $selectionSize")

        val saveEvery = 30 * 1000L
        var lastSavedAt = 0L
        log("Save: every ${saveEvery / 1000L} seconds")

        val logEvery = 1
        log("Log: every $logEvery generations")

        val plotEvery = 1
        log("Plot: every $plotEvery generations")

        fun save() {
//            population.slice(selectionSlice).forEachIndexed { i, p ->
//                p.model.writeToFile(path + "/$i." + MultilayerPerceptron.FILE_EXT)
//            }
            population[0].model.writeToFile(path + "/0." + MultilayerPerceptron.FILE_EXT)
            logSeparator()
            log("Saved!")
            lastSavedAt = timeElapsed()
        }

        val stats = Stats.Floats()

        println("Press Enter to continue...")
        readln()

        val plotData = File("experiments/$timestamp/plot.csv")
        plotData.appendText("Generation,TimeElapsed,Average,Min,Max\n")

        var generation = 0

        while (true) {

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
                    if (MNIST.determineLabel(output) == datapoint.label) {
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
                plotData.appendText("$generation,${timeElapsed()},${stats.average},${stats.min},${stats.max}\n")
                stats.clear()
            }

            population.sortBy(RankedModel::rank)

            if (timeElapsed() - lastSavedAt >= saveEvery) save()

            val selection = population.slice(selectionSlice).map(RankedModel::model)

            population.forEachIndexed { i, p ->
                if (i > 0)
                    p.model = createEmptyModel().apply { populate(selection, random, mutationProbability) }
            }
        }

        // TODO save()
        // store this and seed
//        log("EXPERIMENT FINISHED IN {$timeElapsed()} ms")
    }
}