package src.test.genex

import src.main.genex.Experiment
import src.main.genex.ExperimentDirectory.Companion.defaultDirectory
import src.main.mlp.ActivationFunction.Companion.CAPPED_RELU
import src.main.mlp.ActivationFunction.Companion.RELU
import src.main.mlp.MultilayerPerceptron.Blueprint
import src.main.mlp.MultilayerPerceptron.Structure
import src.main.mnist.MNIST.INPUT_SIZE
import src.main.mnist.MNIST.OUTPUT_SIZE
import src.main.mnist.MNIST.determineLabel
import src.main.mnist.MNIST.training

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val hiddenLayerSizes = listOf(270, 90, 30)
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
            populationSize = 10,
            parentsCount = 2,
            mutationProbability = 0.01f,
            logEvery = 10,
            saveEvery = 30000L,
        )
//        println(experiment.getConfig())
        println("Press Enter to start...")
        readln()
        experiment.run()
    }
}