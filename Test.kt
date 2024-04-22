object Test {
    @JvmStatic
    fun main(args: Array<String>) {

        val data = MNIST.TESTING_DATASET

        for ((experiment, model) in Genetics.topModels()) {

            println("Experiment: $experiment")

            var total = 0
            var successful = 0
            var loss = 0f

            for (datapoint in data) {
                val output = model.forwardPropagate(datapoint.asInput)
                val expectedLabel = datapoint.label
                val determinedLabel = MNIST.determineLabel(output)
                if (expectedLabel == determinedLabel) {
                    loss += model.calculateLoss(output, datapoint.asExpectedOutput)
                    successful++
                }
                total++
            }

            println("$successful/$total = ${successful * 100f / total}%")
            println("Average successful loss: ${loss / successful}")
        }
    }
}