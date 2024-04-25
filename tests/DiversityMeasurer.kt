object DiversityMeasurer {
    @JvmStatic
    fun main(args: Array<String>) {
        val data = MNIST.testing.slice(0 until 25)
        val expectedLabels = (0 until MNIST.OUTPUT_SIZE).toSet()
        val labels = mutableMapOf<Int, Int>()
        expectedLabels.forEach { k ->
            labels[k] = 0
        }
        data.forEach {
            val k = it.label
            labels[k] = 1 + (labels[k] ?: 0)
        }
        labels
            .toList()
            .groupBy({ it.second }, { it.first })
            //.mapValues { it.value.sorted() }
            .toList()
            .sortedBy { it.first }
    }
}
