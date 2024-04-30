package src.main.mlp

import src.main.util.Util.sqr
import java.io.*
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.extension

/**
 * [Wikipedia](https://en.wikipedia.org/wiki/Multilayer_perceptron)
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class MultilayerPerceptron private constructor(
    val blueprint: Blueprint,
    private val layers: Array<Layer>,
) {
    val structure: Structure get() = blueprint.structure
    val inputSize: Int get() = blueprint.structure.inputSize
    val outputSize: Int get() = blueprint.structure.outputSize

    fun forwardPropagate(input: FloatArray): FloatArray {
        assert(input.size == inputSize)
        return layers.fold(input) { values, layer -> layer.forwardPropagate(values) }
    }

    fun forwardPropagateAlsoRecord(input: FloatArray): List<FloatArray> {
        assert(input.size == inputSize)
        val r = mutableListOf<FloatArray>()
        layers.fold(input) { values, layer -> layer.forwardPropagate(values).also { r.add(it) } }
        return r
    }

    fun calculateLoss(actualOutput: FloatArray, expectedOutput: FloatArray): Float {
        assert(actualOutput.size == outputSize)
        assert(expectedOutput.size == outputSize)
        return actualOutput.indices.fold(0f) { loss, i -> loss + sqr(actualOutput[i] - expectedOutput[i]) }
    }

    private class Layer(
        val neurons: Array<Neuron>,
        val activationFunction: ActivationFunction
    ) : ReadableLayer {
        val size: Int get() = neurons.size

        fun forwardPropagate(input: FloatArray) =
            FloatArray(neurons.size) { activationFunction(neurons[it].calculate(input)) }

        override fun equals(other: Any?): Boolean =
            other is Layer && activationFunction == other.activationFunction && neurons.contentEquals(other.neurons)

        override fun hashCode(): Int =
            neurons.contentHashCode() xor activationFunction.hashCode()

        override fun getBias(neuronIndex: Int): Float =
            neurons[neuronIndex].bias

        override fun getWeight(neuronIndex: Int, prevNeuronIndex: Int): Float =
            neurons[neuronIndex].weights[prevNeuronIndex]
    }

    private class Neuron(val bias: Float, val weights: FloatArray) {
        fun calculate(input: FloatArray): Float =
            weights.indices.fold(bias) { sum, i -> sum + input[i] * weights[i] }

        override fun equals(other: Any?): Boolean =
            other is Neuron && bias == other.bias && weights.contentEquals(other.weights)

        override fun hashCode(): Int =
            bias.hashCode() xor weights.contentHashCode()
    }

    fun getReadableLayer(index: Int): ReadableLayer =
        layers[index]

    private fun randomNeuron(size: Int, random: Random) =
        Neuron(
            1f, // 1f + random.nextFloat(sqrt(size.toFloat())),
            FloatArray(size) { random.nextFloat(2f) - 1 },
        )

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

    class Structure(
        val inputSize: Int,
        val outputSize: Int,
        val hiddenLayerSizes: List<Int>,
    ) {
        init {
            inputSize.also { assert(it in 1..<MAX_LAYER_SIZE) }
            outputSize.also { assert(it in 1..<MAX_LAYER_SIZE) }
            hiddenLayerSizes.also { assert(it.size in 1..<MAX_HIDDEN_LAYER_COUNT) }
            hiddenLayerSizes.forEach { assert(it in 1..<MAX_LAYER_SIZE) }
        }

        private val string: String by lazy {
            hiddenLayerSizes.joinToString(",", "[$inputSize,", ",$outputSize]")
        }

        override fun hashCode(): Int =
            string.hashCode()

        override fun equals(other: Any?): Boolean =
            other is Structure && string == other.string

        override fun toString(): String =
            string

        /**
         * in bytes
         */
        val fileSizeLowerBound: Long by lazy {
            var size = 27L + hiddenLayerSizes.size * 12
            for (i in hiddenLayerSizes.indices) {
                val prev = if (i == 0) inputSize else hiddenLayerSizes[i - 1]
                size += hiddenLayerSizes[i] * (4 * prev + 8)
            }
            size += outputSize * (4 * hiddenLayerSizes[hiddenLayerSizes.size - 1] + 8)
            size
        }
    }

    data class Blueprint(
        val structure: Structure,
        val outputLayerActivationFunction: ActivationFunction,
        val hiddenLayerActivationFunctions: List<ActivationFunction>,
    ) {
        init {
            assert(hiddenLayerActivationFunctions.size == structure.hiddenLayerSizes.size)
        }

        fun instantiate(): MultilayerPerceptron {
            val n = structure.hiddenLayerSizes.size
            val layers = Array(n + 1) { i ->
                val size =
                    if (i == n) structure.outputSize
                    else structure.hiddenLayerSizes[i]
                val activationFunction =
                    if (i == n) outputLayerActivationFunction
                    else hiddenLayerActivationFunctions[i]
                Layer(Array(size) { PLACEHOLDER_NEURON }, activationFunction)
            }
            return MultilayerPerceptron(this, layers)
        }

        /**
         * in bytes
         */
        val fileSizeExact: Long by lazy {
            structure.fileSizeLowerBound +
                    hiddenLayerActivationFunctions
                        .toMutableSet()
                        .apply { add(outputLayerActivationFunction) }
                        .map(ActivationFunction::name)
                        .sumOf { it.length + 1 }
        }
    }

    override fun hashCode(): Int =
        blueprint.hashCode()

    override fun equals(other: Any?): Boolean =
        other is MultilayerPerceptron && blueprint == other.blueprint && layers.contentEquals(other.layers)

    companion object {
        const val FILE_EXT = "mlp"

        private const val MAGIC_NUMBER = 30489 // 7719 ~= MLP
        private const val VERSION_NUMBER = 1
        private const val MAX_LAYER_SIZE = Int.MAX_VALUE
        private const val MAX_HIDDEN_LAYER_COUNT = Int.MAX_VALUE

        private val PLACEHOLDER_NEURON = Neuron(0f, FloatArray(0))

        private enum class OutputType { STRUCTURE, BLUEPRINT, MODEL }

        fun readStructure(filename: String) = read(filename, OutputType.STRUCTURE) as Structure
        fun readBlueprint(filename: String) = read(filename, OutputType.BLUEPRINT) as Blueprint
        fun readModel(filename: String) = read(filename, OutputType.MODEL) as MultilayerPerceptron

        private fun read(filename: String, outputType: OutputType): Any {
            assert(Path(filename).extension == FILE_EXT)
            DataInputStream(BufferedInputStream(FileInputStream(filename))).use { s ->

                assert(s.readShort().toInt() == MAGIC_NUMBER)
                assert(s.readByte().toInt() == VERSION_NUMBER)

                val inputSize = s.readInt()
                val outputSize = s.readInt()
                val hiddenLayerCount = s.readInt()
                val hiddenLayerSizes = List(hiddenLayerCount) { s.readInt() }

                val structure = Structure(inputSize, outputSize, hiddenLayerSizes)
                if (outputType == OutputType.STRUCTURE)
                    return structure

                val activationFunctionsCount = s.readInt()
                val activationFunctions = List(activationFunctionsCount) {
                    val name = String(s.readNBytes(s.readByte().toInt()))
                    ActivationFunction.findByName(name)
                        ?: throw UnsupportedOperationException("Invalid activation function: '$name'")
                }

                val hiddenLayerActivationFunctions = List(hiddenLayerCount) { activationFunctions[s.readInt()] }
                val outputLayerActivationFunction = activationFunctions[s.readInt()]

                val blueprint = Blueprint(structure, outputLayerActivationFunction, hiddenLayerActivationFunctions)
                if (outputType == OutputType.BLUEPRINT)
                    return blueprint

                val model = blueprint.instantiate()

                model.layers.forEach { layer ->
                    val a = layer.neurons
                    assert(s.readInt() == a.size)
                    for (i in a.indices) {
                        a[i] = Neuron(s.readFloat(), FloatArray(s.readInt()) { s.readFloat() })
                    }
                }

                return model
            }
        }
    }

    fun writeToFile(filename: String) {
        assert(Path(filename).extension == FILE_EXT)
        DataOutputStream(BufferedOutputStream(FileOutputStream(filename))).use { s ->

            s.writeShort(MAGIC_NUMBER)
            s.writeByte(VERSION_NUMBER)
            s.writeInt(inputSize)
            s.writeInt(outputSize)

            val hiddenLayerSizes = blueprint.structure.hiddenLayerSizes
            s.writeInt(hiddenLayerSizes.size)
            hiddenLayerSizes.forEach { s.writeInt(it) }

            val activationFunctions = layers
                .map(Layer::activationFunction)
                .toSet()
                .sortedBy(ActivationFunction::name)

            s.writeInt(activationFunctions.size)
            activationFunctions.forEach {
                s.writeByte(it.name.length)
                s.writeBytes(it.name)
            }

            layers.forEach {
                s.writeInt(activationFunctions.indexOf(it.activationFunction))
            }

            layers.forEach { layer ->
                s.writeInt(layer.size)
                layer.neurons.forEach { neuron ->
                    s.writeFloat(neuron.bias)
                    val weights = neuron.weights
                    s.writeInt(weights.size)
                    weights.forEach { s.writeFloat(it) }
                }
            }
        }
    }
}