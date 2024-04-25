
struct Neuron {
    float bias;
    u32 weightCount;
    float weights[weightCount];
};

struct Layer {
    u32 size;
    Neuron neurons[size];
};

struct ActivationFunction {
    u8 nameSize;
    char name[nameSize];
};

struct MultilayerPerceptron {
    u16 magicNumber;
    u8 versionNumber;
    u32 inputSize;
    u32 outputSize;
    u32 hiddenLayerCount;
    u32 hiddenLayerSizes[hiddenLayerCount];
    u32 activationFunctionsCount;
    ActivationFunction activationFunctions[activationFunctionsCount];
    u32 hiddenLayerActivationFunctionIds[hiddenLayerCount];
    u32 outputLayerActivationFunctionId;
    Layer hiddenLayers[hiddenLayerCount];
    Layer outputLayer;
};

be MultilayerPerceptron multilayerPerceptron @ 0;