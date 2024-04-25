import Util.formatPercentage
import kotlin.math.abs

object DiversityMeasurer {
    @JvmStatic
    fun main(args: Array<String>) {
        val data = MNIST.testing.slice(0 until 25)
        val n = data.size
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
                offsets += abs(offset) * label.size
                println("${formatPercentage(ratio)} \t ${formatPercentage(offset)}: \t $label")
            }

    }
}
