import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.math.pow

object Genetics {
    private val dateFormat = SimpleDateFormat("YYYYMMDD_hhmmss")

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

    private val hiddenLayerSizes = listOf(16, 16)
    private val random = Random()

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

    @JvmStatic
    fun main(args: Array<String>) {

        val seed = (Math.random() * Long.MAX_VALUE).toLong()
        val random = Random(seed)
        log("Random number generator seed: $seed")

        val data = MNIST.TESTING_DATASET
        val dataSize = data.size
        log("Data: MNIST.TESTING_DATASET, size: $dataSize")

        val populationSize = (2.0).pow(8).toInt()
        log("Population: $populationSize")

        val mutationProbability = 0.02f
        log("Mutation probability: $mutationProbability")

//        val noMutationRate = 0.1f
//        val noMutationSize = (noMutationRate * populationSize).toInt()
//        val mutationSize = populationSize - noMutationSize
//        log("No mutation: ${noMutationRate * 100}% = $noMutationSize")

        val previousBest = MultilayerPerceptron.readFromFile(modelFilename("202404111_070518"))

        val population = Array(populationSize) { RankedModel(previousBest) } // createRandomModel()

        val selectionRate = 0.2f
        val selectionSize = (populationSize * selectionRate).toInt().coerceAtLeast(2)
        val selectionSlice = 0 until selectionSize
        log("Parents: ${selectionRate * 100}% = $selectionSize")

        val saveEvery = 30 * 1000L
        var lastSavedAt = 0L
        log("Save: every ${saveEvery / 1000L} seconds")

        fun save() {
//            population.slice(selectionSlice).forEachIndexed { i, p ->
//                p.model.writeToFile(path.pathString + "/$i." + MultilayerPerceptron.FILE_EXT)
//            }
            population[0].model.writeToFile(path.pathString + "/0." + MultilayerPerceptron.FILE_EXT)
            logSeparator()
            log("Saved!")
            lastSavedAt = timeElapsed
        }

        val stats = Stats(
            findMin = true,
            findMax = true,
            findAverage = true,
        )

        println("Press Enter to continue...")
        readln()

        val plotData = File("experiments/$timestamp/plot.csv")
        plotData.appendText("Generation,TimeElapsed,Average,Min,Max\n")

        var generation = 0

        while (true) {

            logSeparator()
            log("Generation: #${generation++}")

            for (p in population) {
                val model = p.model
                var successful = 0
                for (datapoint in data) {
                    val input = datapoint.asInput
                    val output = model.forwardPropagate(input)
                    if (MNIST.determineLabel(output) == datapoint.label) {
                        successful++
                    }
                }

                val accuracy = successful * 100f / dataSize

                stats.check(accuracy.toDouble())

                p.rank = -accuracy
            }

            log(stats.getSummary())
            plotData.appendText("$generation,$timeElapsed,${stats.average},${stats.min},${stats.max}\n")
            stats.clear()

            population.sortBy(RankedModel::rank)

            if (timeElapsed - lastSavedAt >= saveEvery) save()

            val selection = population.slice(selectionSlice).map(RankedModel::model)

            population.forEachIndexed { i, p ->
                if (i > 0)
//                    val m = if (i > noMutationSize) (i - noMutationSize).toFloat() / mutationSize else 0f
                    p.model = createEmptyModel().apply { populate(selection, random, mutationProbability) }
            }
        }

        // TODO save() finish()
    }
}