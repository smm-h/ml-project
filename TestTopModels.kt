import java.nio.file.Files
import java.nio.file.Path

object TestTopModels {
    @JvmStatic
    fun main(args: Array<String>) {

        val data = MNIST.TESTING_DATASET

        for ((experiment, model) in topModels()) {

            println("Experiment: $experiment")

            var total = 0
            var successful = 0
            var loss = 0f

            for (datapoint in data) {
                val output = model.forwardPropagate(datapoint.asInput)
                val expectedLabel = datapoint.label
                val determinedLabel = MNIST.determineLabel(output)
                if (expectedLabel == determinedLabel) {
                    loss += model.calculateLoss(output, datapoint.asExpectedOutput)
                    successful++
                }
                total++
            }

            println("$successful/$total = ${successful * 100f / total}%")
            println("Average successful loss: ${loss / successful}")
        }
    }

    fun topModels(): Map<String, MultilayerPerceptron> {
        return Files.readString(Path.of("experiments/list.txt")).trim().split("\n")
            .filter { Files.exists(Path.of((modelFilename(it)))) }
            .associateWith { MultilayerPerceptron.readFromFile(modelFilename(it)) }
    }

    fun modelFilename(experimentTimestamp: String) =
        "experiments/$experimentTimestamp/0.${MultilayerPerceptron.FILE_EXT}"
}