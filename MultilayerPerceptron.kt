import java.io.*
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.math.pow

/**
 * [Wikipedia](https://en.wikipedia.org/wiki/Multilayer_perceptron)
 */
@Suppress("unused")
class MultilayerPerceptron private constructor(
    private val inputSize: Int,
    private val layers: Array<Layer>,
) {
    @Suppress("MemberVisibilityCanBePrivate")
    val outputSize: Int get() = layers.last().size

    fun forwardPropagate(input: FloatArray): FloatArray {
        assert(input.size == inputSize)
        return layers.fold(input) { values, layer -> layer.forwardPropagate(values) }
    }

    fun calculateLoss(actualOutput: FloatArray, expectedOutput: FloatArray): Float {
        assert(actualOutput.size == outputSize)
        assert(expectedOutput.size == outputSize)
        return actualOutput.indices.fold(0f) { loss, i -> loss + (actualOutput[i] - expectedOutput[i]).pow(2) }
    }

    private class Layer(
        val neurons: Array<Neuron>,
        val activationFunction: ActivationFunction
    ) {
        val size: Int get() = neurons.size

        fun forwardPropagate(input: FloatArray) =
            FloatArray(neurons.size) { activationFunction(neurons[it].calculate(input)) }
    }

    class Neuron(val weights: FloatArray, val bias: Float) {
        fun calculate(input: FloatArray): Float = weights.indices.fold(-bias) { sum, i -> sum + input[i] * weights[i] }
    }

    private fun randomNeuron(size: Int, random: Random) =
        Neuron(FloatArray(size) { random.nextFloat(2f) - 1 }, 0f)

    fun randomize(random: Random) {
        for (l in layers.indices) {
            val s = if (l == 0) inputSize else layers[l - 1].size
            val n = layers[l].neurons
            for (i in n.indices)
                n[i] = randomNeuron(s, random)
        }
    }

    /**
     * Calling this method on an empty model will populate its [layers] with
     * neurons that are either completely random (i.e. *mutation*) (the chances
     * of which happening is given by [mutationRate] which is a `Float` between
     * 0 and 1) or a randomly chosen neuron *at the same position* (meaning in
     * the same layer and in the same position in that layer) from one of the
     * models in the [sources] list, each of which have an equal chance of being
     * picked. Both the caller and the sources must have equivalent structures.
     */
    fun populate(sources: List<MultilayerPerceptron>, random: Random, mutationRate: Float) {
        val k = sources.size
        for (l in layers.indices) {
            val m = sources.map { it.layers[l].neurons }
            val s = if (l == 0) inputSize else layers[l - 1].size
            val n = layers[l].neurons
            for (i in n.indices)
                n[i] =
                    if (random.nextFloat() < mutationRate)
                        randomNeuron(s, random)
                    else
                        m[random.nextInt(k)][i]
        }
    }

    companion object {

        const val FILE_EXT = "mlp"

        private val PLACEHOLDER_NEURON = Neuron(FloatArray(0), 0f)

        fun create(
            inputSize: Int,
            hiddenLayerSizes: List<Int>,
            outputSize: Int,
            hiddenLayerAF: ActivationFunction,
            outputAF: ActivationFunction,
        ): MultilayerPerceptron {
            val n = hiddenLayerSizes.size
            val layerSizes = IntArray(n + 1) { i ->
                if (i == n) outputSize else hiddenLayerSizes[i]
            }
            return MultilayerPerceptron(inputSize, Array(n + 1) { i ->
                Layer(Array(layerSizes[i]) { PLACEHOLDER_NEURON }, if (i == n) outputAF else hiddenLayerAF)
            })
        }

        fun readFromFile(filename: String): MultilayerPerceptron {
            assert(Path(filename).extension == FILE_EXT)

            val s = DataInputStream(BufferedInputStream(FileInputStream(filename)))

            // afCount
            val numberOfActivationFunctions = s.readByte()

            // afs
            val afs: Map<Int, ActivationFunction> = (0 until numberOfActivationFunctions).associateWith {
                val nameSize = s.readByte().toInt()
                val name = String(s.readNBytes(nameSize))
                ActivationFunction.findByName(name)
                    ?: throw UnsupportedOperationException("Invalid activation function name: '$name'")
            }

            // inputSize
            val inputSize = s.readShort().toInt()

            // layerCount
            val numberOfLayers = s.readByte().toInt()
                .also { if (it < 2) throw IllegalArgumentException("Wrong number of layers: $it, must at least be: 2") }

            // layers
            val layers = Array(numberOfLayers) {
                val afId = s.readByte().toInt()
                val size = s.readShort().toInt()
                val af = afs[afId]
                    ?: throw NoSuchElementException("Invalid activation function index: '$afId'")
                Layer(Array(size) { PLACEHOLDER_NEURON }, af)
            }

            // neuronData
            layers.forEach { layer ->
                val neuronsArray = layer.neurons
                for (i in neuronsArray.indices) {
                    val bias = s.readFloat()
                    val weightsSize = s.readShort()
                    val weights = FloatArray(weightsSize.toInt()) {
                        s.readFloat()
                    }
                    neuronsArray[i] = Neuron(weights, bias)
                }
            }

            return MultilayerPerceptron(inputSize, layers)
        }
    }

    fun writeToFile(filename: String) {

        assert(Path(filename).extension == FILE_EXT)

        val s = DataOutputStream(BufferedOutputStream(FileOutputStream(filename)))

        val afs = layers.map(Layer::activationFunction).toSet().sortedBy { it.name }

        // afCount
        s.writeByte(afs.size)

        // afs
        afs.forEach { af ->
            s.writeByte(af.name.length)
            s.writeBytes(af.name)
        }

        // inputSize
        s.writeShort(inputSize)

        // layerCount
        s.writeByte(layers.size)

        // layers
        layers.forEach { layer ->
            s.writeByte(afs.indexOf(layer.activationFunction))
            s.writeShort(layer.size)
        }

        // neuronData
        layers.forEach { layer ->
            layer.neurons.forEach { neuron ->
                s.writeFloat(neuron.bias)
                val weights = neuron.weights
                s.writeShort(weights.size)
                weights.forEach { weight ->
                    s.writeFloat(weight)
                }
            }
        }

        s.close()
    }
}