object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val structure = MultilayerPerceptron.Structure(
            inputSize = MNIST.INPUT_SIZE,
            outputSize = MNIST.OUTPUT_SIZE,
            hiddenLayerSizes = listOf(50, 100, 500, 1000), // listOf(16, 16),
        )
        val blueprint = MultilayerPerceptron.Blueprint(
            structure = structure,
            hiddenLayerActivationFunctions = List(4) { ActivationFunction.RELU },
            outputLayerActivationFunction = ActivationFunction.CAPPED_RELU,
        )
        val directory = ExperimentDirectory("experiments", true)
        Experiment(
            directory = directory,
            data = MNIST.testing.slice(0 until 100),
            labeler = MNIST::determineLabel,
            blueprint = blueprint,
        ).run()
    }
}