package src.test.mlp

import src.main.mlp.ActivationFunction.Companion.RELU
import src.main.mlp.MultilayerPerceptron.Blueprint
import src.main.mlp.MultilayerPerceptron.Companion.readModel
import src.main.mlp.MultilayerPerceptron.Structure
import java.io.File
import java.util.*

object TinyModelTest {

    val structure = Structure(1, 1, listOf(1))
    val blueprint = Blueprint(structure, RELU, listOf(RELU))
    val model = blueprint.instantiate()

    @JvmStatic
    fun main(args: Array<String>) {
        val filename = "tiny.mlp"
        val random = Random()
        model.randomize(random)
        model.writeToFile(filename)
        assert(model == readModel(filename))
        File(filename).delete()
    }
}