package src.main.mlp

interface ReadableLayer {
    fun getBias(neuronIndex: Int): Float
    fun getWeight(neuronIndex: Int, prevNeuronIndex: Int): Float
}