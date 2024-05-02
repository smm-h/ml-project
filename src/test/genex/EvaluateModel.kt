package src.test.genex

import src.main.mlp.MultilayerPerceptron
import src.main.mnist.MNIST
import src.main.util.Util.formatPercentage

object EvaluateModel {
    @JvmStatic
    fun main(args: Array<String>) {
        val filename = "D:/CE/ML/project/experiments/best.mlp"
        val model = MultilayerPerceptron.readModel(filename)
        val data = MNIST.training
        val count = IntArray(MNIST.OUTPUT_SIZE)
        val successful = IntArray(MNIST.OUTPUT_SIZE)
        for (datapoint in data) {
            val label = datapoint.label
            val output = MNIST.determineLabel(model.forwardPropagate(datapoint.data))
            count[label]++
            if (output == label) successful[label]++
        }
        for (i in 0 until MNIST.OUTPUT_SIZE) {
            val percentage = successful[i].toFloat() / count[i]
            println("LABEL: $i \t RATIO: ${successful[i]}/${count[i]} ~= ${formatPercentage(percentage)}")
        }
    }
}