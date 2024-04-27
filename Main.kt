import ActivationFunction.Companion.CAPPED_RELU
import ActivationFunction.Companion.RELU
import ExperimentDirectory.Companion.defaultDirectory
import MNIST.INPUT_SIZE
import MNIST.OUTPUT_SIZE
import MNIST.determineLabel
import MNIST.training
import MultilayerPerceptron.Blueprint
import MultilayerPerceptron.Structure

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val hiddenLayerSizes = listOf(400, 200, 100, 50)
        val experiment = Experiment(
            seed = (Math.random() * Long.MAX_VALUE).toLong(),
            directory = defaultDirectory,
            data = training.slice(0 until 1000),
            labeler = ::determineLabel,
            blueprint = Blueprint(
                structure = Structure(
                    inputSize = INPUT_SIZE,
                    outputSize = OUTPUT_SIZE,
                    hiddenLayerSizes = hiddenLayerSizes,
                ),
                hiddenLayerActivationFunctions = List(hiddenLayerSizes.size) { RELU },
                outputLayerActivationFunction = CAPPED_RELU,
            ),
            populationSize = 6,
            parentsCount = 3,
            mutationProbability = 0.001f,
            logEvery = 1,
            saveEvery = 30000L,
        )
//        println(experiment.getConfig())
        println("Press Enter to start...")
        readln()
        experiment.run()
    }
}