import ActivationFunction.Companion.CAPPED_RELU
import ActivationFunction.Companion.RELU
import ExperimentDirectory.Companion.defaultDirectory
import MNIST.INPUT_SIZE
import MNIST.OUTPUT_SIZE
import MNIST.determineLabel
import MNIST.testing
import MultilayerPerceptron.Blueprint
import MultilayerPerceptron.Structure

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Experiment(
            directory = defaultDirectory,
            data = testing.slice(0 until 1000),
            labeler = ::determineLabel,
            blueprint = Blueprint(
                structure = Structure(
                    inputSize = INPUT_SIZE,
                    outputSize = OUTPUT_SIZE,
                    hiddenLayerSizes = listOf(400, 200, 100, 50, 25), // listOf(16, 16),
                ),
                hiddenLayerActivationFunctions = List(5) { RELU },
                outputLayerActivationFunction = CAPPED_RELU,
            ),
            populationSize = 100,
            mutationProbability = 0.01f,
            saveEvery = 5000L,
        ).run()
    }
}