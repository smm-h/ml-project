object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Genetics(
            data = MNIST.testing.slice(0 until 100),
            labeler = MNIST::determineLabel,
            createEmptyModel = {
                MultilayerPerceptron.create(
                    inputSize = MNIST.INPUT_SIZE,
                    outputSize = MNIST.OUTPUT_SIZE,
                    hiddenLayerSizes = listOf(16, 16),
                    hiddenLayerAF = ActivationFunction.RELU,
                    outputAF = ActivationFunction.CAPPED_RELU,
                )
            }
        ).run()
    }
}