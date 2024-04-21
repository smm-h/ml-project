
struct ActivationFunction {
    u8 nameSize;
    char name[nameSize];
};

struct Neuron {
    float bias;
    u16 weightCount;
};

struct NeuronData {
    u16 size;
    Neuron neurons[size];
};

struct Layer {
    u8 afId;
    u16 size;
};

struct Perceptron {
    u8 afCount;
    ActivationFunction afs[afCount];
    u16 inputSize;
    u8 layerCount;
    Layer layers[layerCount];
    NeuronData neuronData[layerCount];
};

be Perceptron perceptron @ 0;