import ActivationFunction.Companion.RELU
import MultilayerPerceptron.Blueprint
import MultilayerPerceptron.Companion.readModel
import MultilayerPerceptron.Structure
import java.util.*

object TinyModelTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val filename = "tiny.mlp"
        val random = Random()

        Blueprint(
            Structure(1, 1, listOf(1)),
            RELU,
            listOf(RELU),
        ).instantiate().also {
            it.randomize(random)
            it.writeToFile(filename)
            assert(it == readModel(filename))
        }

//        File(filename).delete()
    }
}