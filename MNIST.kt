import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.FileInputStream


object MNIST {

    const val SIZE = 28
    const val INPUT_SIZE = 28 * 28
    const val OUTPUT_SIZE = 10

    /**
     * An MNIST datapoint is basically a fixed-size two-dimensional integer array with a label
     */
    class Datapoint(val label: Int) {
        private val data = Array(SIZE) { FloatArray(SIZE) }

        operator fun get(x: Int, y: Int): Float = data[x][y]

        operator fun set(x: Int, y: Int, value: Float) {
            data[x][y] = value
        }

        val asInput: FloatArray by lazy {
            FloatArray(INPUT_SIZE) { data[it.rem(SIZE)][it.div(SIZE)] }
        }

        val asExpectedOutput: FloatArray by lazy {
            FloatArray(OUTPUT_SIZE) { if (it == label) 1f else 0f }
        }

        override fun toString(): String = StringBuilder().also {
            it.append("LABEL: $label\n")
            for (x in 0 until SIZE) {
                for (y in 0 until SIZE)
                    it.append(if (this[x, y] > 0) "##" else "  ")
                it.append("\n")
            }
        }.toString()
    }

    val TRAINING_DATASET by lazy { load("data/train-images.idx3-ubyte", "data/train-labels.idx1-ubyte") }
    val TESTING_DATASET by lazy { load("data/t10k-images.idx3-ubyte", "data/t10k-labels.idx1-ubyte") }

    private fun load(images: String, labels: String): Array<Datapoint> {

        // create the streams for images and labels files
        val imagesStream = DataInputStream(BufferedInputStream(FileInputStream(images)))
        val labelsStream = DataInputStream(BufferedInputStream(FileInputStream(labels)))

        // verify their magic numbers
        assert(imagesStream.readInt() == 2051)
        assert(labelsStream.readInt() == 2049)

        // read and verify counts and dimensions
        val count = imagesStream.readInt()
        assert(count == labelsStream.readInt())
        assert(imagesStream.readInt() == SIZE)
        assert(imagesStream.readInt() == SIZE)

        // read the actual data
        return Array(count) {
            Datapoint(labelsStream.readUnsignedByte()).also {
                for (x in 0 until SIZE)
                    for (y in 0 until SIZE)
                        it[x, y] = imagesStream.readUnsignedByte() / 255f
            }
        }
    }

    /**
     * Matches the output vector from a model to a label.
     *
     * ## Arguments
     *
     * - [output]: a [FloatArray] of size of `10` with likelihood values between
     * `0.0` and `1.0`
     * - [atLeast]: the minimum threshold for a likelihood to count as certainly
     * yes (inclusive); default value is `0.8`
     * - [atMost]: the maximum threshold for a likelihood to count as certainly
     * no (exclusive); default value is `0.6`
     *
     * ## Returns
     *
     * - `0-9`: if exactly one likelihood is certainly yes, and all others are
     * certainly no
     * - `-1`: otherwise
     */
    fun determineLabel(
        output: FloatArray,
        atLeast: Float = 0.8f,
        atMost: Float = 0.6f,
    ): Int {
        var label = -1
        for (i in output.indices) {
            if (output[i] >= atLeast) {
                if (label == -1) label = i else return -1
            } else {
                if (output[i] >= atMost) return -1
            }
        }
        return label
    }
}