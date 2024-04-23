import MultilayerPerceptron.Structure

object MNISTModelFileSizes {
    @JvmStatic
    fun main(args: Array<String>) {
        listOf(
            listOf<Int>(), // 31,41 KB
            listOf(16, 16), // 51,93 KB
            listOf(800), // 2,54 MB
            listOf(2500, 2000, 1500, 1000, 500), // 47,87 MB
            listOf(40, 80, 500, 1000, 2000), // 10,38 MB
            listOf(50, 100, 500, 1000), // 2,42 MB
            listOf(128, 128, 128, 128, 128, 256, 256, 256, 512, 2048, 256, 256), // 8,41 MB
        ).forEach {
            val structure = Structure(784, 10, it)
            println(Util.formatFileSizeBase2(structure.estimateFileSize()))
        }
    }
}