package src.test.mnist

import MultilayerPerceptron.Structure
import util.Util

object MNISTModelFileSizes {
    @JvmStatic
    fun main(args: Array<String>) {
        listOf(
            listOf(16, 16), // 52,23 KB
            listOf(400, 200, 100, 50, 25), // 1,69 MB
            listOf(800), // 2,55 MB
            listOf(2500, 2000, 1500, 1000, 500), // 47,92 MB
            listOf(40, 80, 500, 1000, 2000), // 10,41 MB
            listOf(50, 100, 500, 1000), // 2,43 MB
            listOf(128, 128, 128, 128, 128, 256, 256, 256, 512, 2048, 256, 256), // 8,44 MB
        ).forEach {
            val structure = Structure(784, 10, it)
            println("$structure => ${Util.formatFileSizeBase2(structure.fileSizeLowerBound)}")
        }
    }
}