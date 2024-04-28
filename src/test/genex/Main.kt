package src.test.genex

import ActivationFunction.Companion.CAPPED_RELU
import ActivationFunction.Companion.RELU
import MultilayerPerceptron.Blueprint
import MultilayerPerceptron.Structure
import src.main.genex.Experiment
import src.main.genex.ExperimentDirectory.Companion.defaultDirectory
import src.main.mnist.MNIST.INPUT_SIZE
import src.main.mnist.MNIST.OUTPUT_SIZE
import src.main.mnist.MNIST.determineLabel
import src.main.mnist.MNIST.training

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val hiddenLayerSizes = listOf(50, 50, 50)
        val experiment = Experiment(
            seed = (Math.random() * Long.MAX_VALUE).toLong(),
            directory = defaultDirectory,
            data = training.slice(0 until 10),
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
            mutationProbability = 0.01f,
            logEvery = 1000,
            saveEvery = 10000L,
        )
//        println(experiment.getConfig())
        println("Press Enter to start...")
        readln()
        experiment.run()
    }
}