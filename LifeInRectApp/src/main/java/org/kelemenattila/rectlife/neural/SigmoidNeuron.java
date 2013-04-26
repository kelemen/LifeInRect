package org.kelemenattila.rectlife.neural;

/**
 *
 * @author Kelemen Attila
 */
public final class SigmoidNeuron extends Neuron {
    private static final long serialVersionUID = -7868860979610274646L;

    private final double lambda;

    public SigmoidNeuron(double lambda) {
        this.lambda = lambda;
    }

    @Override
    protected double activationFunction(double x) {
        return 1 / (1 + Math.exp(-lambda * x));
    }

    @Override
    protected double dActivationFunction(double x) {
        double ax = activationFunction(x);
        return lambda * ax * (1 - ax);
    }

    public static final class Factory implements MLP.NeuronFactory {
        private final double lambda;

        public Factory(double lambda) {
            this.lambda = lambda;
        }

        @Override
        public SigmoidNeuron createNeuron() {
            return new SigmoidNeuron(lambda);
        }
    }
}
