package org.kelemenattila.rectlife.neural;

/**
 *
 * @author Kelemen Attila
 */
public final class LinearNeuron extends Neuron {
    private static final long serialVersionUID = -6304331340247991859L;

    private final double c;

    public LinearNeuron(double c) {
        this.c = c;
    }

    @Override
    protected double activationFunction(double x) {
        return c * x;
    }

    @Override
    protected double dActivationFunction(double x) {
        return c;
    }

    public static final class Factory implements MLP.NeuronFactory {
        private final double c;

        public Factory(double c) {
            this.c = c;
        }

        @Override
        public LinearNeuron createNeuron() {
            return new LinearNeuron(c);
        }
    }
}
