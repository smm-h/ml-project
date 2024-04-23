import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

@Suppress("MemberVisibilityCanBePrivate")
class ExperimentDirectory(
    val path: String,
    createIfNotExists: Boolean = false,
    indexFilename: String = "index.txt",
) {
    val indexFile = File("$path/$indexFilename")

    init {
        if (createIfNotExists) {
            Files.createDirectory(Path(path))
        }
    }

    fun getExperiments(): List<String> {
        return indexFile.readText().trim().split("\n")
    }

    fun getModelFilename(experimentTimestamp: String, id: Int) =
        "$path/$experimentTimestamp/$id.${MultilayerPerceptron.FILE_EXT}"

    fun getTopModels(): Map<String, MultilayerPerceptron> {
        return getExperiments()
            .filter { File(getModelFilename(it, 0)).exists() }
            .associateWith { MultilayerPerceptron.readFromFile(getModelFilename(it, 0)) }
    }

    fun testModel(model: MultilayerPerceptron, data: List<LabeledData>) {
        var total = 0
        var successful = 0
        var loss = 0f

        for (datapoint in data) {
            val output = model.forwardPropagate(datapoint.data)
            val expectedLabel = datapoint.label
            val determinedLabel = MNIST.determineLabel(output)
            if (expectedLabel == determinedLabel) {
                loss += model.calculateLoss(output, MNIST.outputArrays[datapoint.label])
                successful++
            }
            total++
        }

        println("$successful/$total = ${successful * 100f / total}%")
        println("Average successful loss: ${loss / successful}")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val data = MNIST.testing
            ExperimentDirectory("experiments", true).apply {
                getTopModels().forEach { (timestamp, model) ->
                    println("Experiment: $timestamp")
                    testModel(model, data)
                }
            }
        }
    }
}