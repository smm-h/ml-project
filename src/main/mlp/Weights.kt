package src.main.mlp

fun interface Weights {
    fun getWeight(neuronIndex: Int, prevNeuronIndex: Int): Float
}