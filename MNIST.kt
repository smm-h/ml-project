import MNIST.testing
import MNIST.training
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.FileInputStream

/**
 * MNIST (Modified NIST) is a dataset of 28x28 grayscale images of handwritten
 * digits, remixed from the original dataset created in 1994. It contains
 * 60,000 [training] images and 10,000 [testing] images.
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/MNIST_database)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object MNIST {

    const val SIZE = 28
    const val INPUT_SIZE = SIZE * SIZE
    const val OUTPUT_SIZE = 10

    private val outputArrays = Array(OUTPUT_SIZE) { label -> FloatArray(OUTPUT_SIZE) { if (it == label) 1f else 0f } }

    /**
     * An [MNIST] [Datapoint] is a 28 by 28 grayscale drawing of a decimal
     * digit, compiled by NIST during the 1970s. It has [data] as a [FloatArray]
     * of size [INPUT_SIZE] (28x28=784), and an integer [label] from 0 to 9,
     * indicating which digit it represents.
     */
    class Datapoint(val label: Int) {
        val data = FloatArray(INPUT_SIZE)

        val asOutputArray: FloatArray get() = outputArrays[label]
    }

    val training
            by lazy { read("data/train-images.idx3-ubyte", "data/train-labels.idx1-ubyte") }
    val testing
            by lazy { read("data/t10k-images.idx3-ubyte", "data/t10k-labels.idx1-ubyte") }

    /**
     * Reads images and their labels from binary dataset files, given a
     * [imagesFilename] and a [labelsFilename].
     *
     * The binary format of the files is the one used by Yann LeCun.
     */
    fun read(imagesFilename: String, labelsFilename: String): Array<Datapoint> {

        // create the streams for images and labels files
        val images = DataInputStream(BufferedInputStream(FileInputStream(imagesFilename)))
        val labels = DataInputStream(BufferedInputStream(FileInputStream(labelsFilename)))

        // verify their magic numbers
        assert(images.readInt() == 2051)
        assert(labels.readInt() == 2049)

        // read and verify counts and dimensions
        val count = images.readInt()
        assert(count == labels.readInt())
        assert(images.readInt() == SIZE)
        assert(images.readInt() == SIZE)

        // read the actual data
        return Array(count) {
            Datapoint(labels.readUnsignedByte()).also {
                for (i in 0 until INPUT_SIZE)
                    it.data[i] = images.readUnsignedByte() / 255f
            }
        }
    }

    /**
     * Matches an output array to a label.
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