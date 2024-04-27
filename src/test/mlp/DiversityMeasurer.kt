package src.test.mlp

import src.main.mnist.MNIST
import util.Util.formatPercentage
import kotlin.math.abs

object DiversityMeasurer {
    @JvmStatic
    fun main(args: Array<String>) {
        val n = 1000
//        for (n in listOf(1, 5, 10, 25, 50, 100, 200, 500, 1000, 2000, 5000, 10000)) {
        val data = MNIST.training.slice(0 until n)
        val expectedRatio = 1f / MNIST.OUTPUT_SIZE
        val expectedLabels = (0 until MNIST.OUTPUT_SIZE)
        val labels = mutableMapOf<Int, Int>()
        expectedLabels.forEach { labels[it] = 0 }
        data.forEach {
            val k = it.label
            labels[k] = 1 + (labels[k] ?: 0)
        }
        var offsets = 0f
        labels
            .toList()
            .groupBy({ it.second }, { it.first })
            //.mapValues { it.value.sorted() }
            .toList()
            .sortedBy { it.first }
            .forEach {
                val count = it.first
                val label = it.second
                val ratio = count.toFloat() / n
                val offset = ratio - expectedRatio
                offsets += abs(offset) * label.size * count
                println("${formatPercentage(ratio)} \t ${formatPercentage(offset)}: \t $label")
            }

//            val y = offsets / n

//            println("$n: \t $y")
//        }
    }
}
