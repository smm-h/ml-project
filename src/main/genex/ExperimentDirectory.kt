package src.main.genex

import LabeledData
import MultilayerPerceptron
import src.main.mnist.MNIST
import src.main.util.Util.formatPercentage
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.exists

@Suppress("MemberVisibilityCanBePrivate")
class ExperimentDirectory(
    val path: String,
    createIfNotExists: Boolean = false,
    indexFilename: String = "index.txt",
) {
    val indexFile = File("$path/$indexFilename")

    init {
        if (createIfNotExists)
            Path(path).also { if (!it.exists()) Files.createDirectory(it) }
    }

    fun getExperiments(): List<String> {
        return indexFile.readText().trim().split("\n")
    }

    fun getModelFilename(experimentTimestamp: String, id: Int) =
        "$path/$experimentTimestamp/$id.${MultilayerPerceptron.FILE_EXT}"

    fun getTopModels(): Map<String, MultilayerPerceptron> {
        return getExperiments()
            .filter { File(getModelFilename(it, 0)).exists() }
            .associateWith { MultilayerPerceptron.readModel(getModelFilename(it, 0)) }
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

        println("$successful/$total = ${formatPercentage(successful.toFloat() / total)}")
        println("Average successful loss: ${loss / successful}")
    }

    companion object {

        val defaultDirectory = ExperimentDirectory("experiments", true)

        @JvmStatic
        fun main(args: Array<String>) {
            val data = MNIST.testing
            defaultDirectory.apply {
                getTopModels().forEach { (timestamp, model) ->
                    println("Experiment: $timestamp")
                    testModel(model, data)
                }
            }
        }
    }
}