import java.util.*

object Base {
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
}