package org.kelemenattila.rectlife.neural;

import java.util.*;
import org.jtrim.utils.ExceptionHelper;

/**
 *
 * @author Kelemen Attila
 */
public final class MLP implements java.io.Serializable {
    private static final long serialVersionUID = -648342052220353643L;

    public static interface NeuronFactory {
        public Neuron createNeuron();
    }

    public static class LayerDescription {
        private final NeuronFactory neuronFactory;
        private final int layerSize;

        public LayerDescription(NeuronFactory neuronFactory, int layerSize) {
            ExceptionHelper.checkNotNullArgument(neuronFactory, "neuronFactory");

            this.neuronFactory = neuronFactory;
            this.layerSize = layerSize;
        }

        public int getLayerSize() {
            return layerSize;
        }

        public NeuronFactory getNeuronFactory() {
            return neuronFactory;
        }
    }

    private final Neuron[][] layers;
    private final Neuron biasNeuron;

    private transient InputNeuron[] inputLayer;
    private transient Neuron[] outputLayer;

    public MLP(List<? extends LayerDescription> layerDescr, int inputCount) {
        this(layerDescr, inputCount, null);
    }

    public MLP(List<? extends LayerDescription> layerDescr, int inputCount, double[] genes) {
        int geneIndex = 0;
        int layerIndex = 0;
        biasNeuron = new InputNeuron(1.0);

        this.inputLayer = new InputNeuron[inputCount];
        Neuron[] lastLayer = this.inputLayer;

        layers = new Neuron[layerDescr.size() + 1][];

        for (int i = 0; i < inputCount; i++) {
            lastLayer[i] = new InputNeuron();
        }

        layers[layerIndex++] = lastLayer;

        for (LayerDescription descr: layerDescr) {
            int thisSize = descr.getLayerSize();

            Neuron[] thisLayer = new Neuron[thisSize];
            for (int i = 0; i < thisSize; i++) {
                thisLayer[i] = descr.getNeuronFactory().createNeuron();

                if (genes == null) thisLayer[i].addChild(biasNeuron, 0.0);
                else thisLayer[i].addChild(biasNeuron, genes[geneIndex++]);

                for (Neuron last: lastLayer) {
                    if (genes == null) thisLayer[i].addChild(last, 0.0);
                    else thisLayer[i].addChild(last, genes[geneIndex++]);
                }
            }

            layers[layerIndex++] = thisLayer;
            lastLayer = thisLayer;
        }

        this.outputLayer = lastLayer;
    }

    private void fireNeurons() {
        biasNeuron.fire();
        for (Neuron neuron: inputLayer) {
            neuron.fire();
        }
    }

    public double[] getOutputs() {
        fireNeurons();

        double[] result = new double[outputLayer.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = outputLayer[i].readValue();
        }

        return result;
    }

    public int getInputCount() {
        return inputLayer.length;
    }

    public double getOutput(int index) {
        fireNeurons();
        return outputLayer[index].readValue();
    }

    public void setInputs(double... values) {
        for (int i = 0; i < values.length; i++) {
            inputLayer[i].setValue(values[i]);
        }
    }

    public void setInput(int index, double value) {
        inputLayer[index].setValue(value);
    }

    public InputNeuron getInputNeuron(int index) {
        return inputLayer[index];
    }

    public InputNeuron[] getAllInputNeurons() {
        InputNeuron[] result = new InputNeuron[inputLayer.length];
        System.arraycopy(inputLayer, 0, result, 0, result.length);
        return result;
    }

    public void randomWeights(double lowerBound, double upperBound) {
        for (int i = 1; i < layers.length; i++) {
            for (Neuron neuron: layers[i]) {
                neuron.randomInputWeights(lowerBound, upperBound);
            }
        }
    }

    public double train(double alpha, double[] inputs, double[] expOutput) {
        for (int i = 0; i < inputs.length; i++) {
            setInput(i, inputs[i]);
        }

        double[] outputs = getOutputs();

        int index = 0;
        double mse = 0.0;
        for (Neuron n: outputLayer) {
            double e = expOutput[index] - outputs[index];
            n.backPropagateError(alpha, e);

            index++;
            mse += e*e;
        }

        return mse / outputLayer.length;
    }

    public double[] getGenes() {
        int size = 0;
        for (int i = 1; i < layers.length; i++) {
            size += layers[i].length * (layers[i-1].length + 1);
        }

        double[] result = new double[size];
        int index = 0;
        for (int i = 1; i < layers.length; i++) {
            for (Neuron neuron: layers[i]) {
                double[] weights = neuron.getAllInputWeights();
                for (double w: weights) {
                    result[index++] = w;
                }
            }
        }

        return result;
    }

    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();

        inputLayer = (InputNeuron[])layers[0];
        outputLayer = layers[layers.length - 1];
    }
}

