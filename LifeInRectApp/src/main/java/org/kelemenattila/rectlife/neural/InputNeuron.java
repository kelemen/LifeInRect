package org.kelemenattila.rectlife.neural;

/**
 *
 * @author Kelemen Attila
 */
public final class InputNeuron extends Neuron {
    private static final long serialVersionUID = 584496075737081617L;

    public InputNeuron() {
        this(0.0);
    }

    public InputNeuron(double value) {
        super();

        setValue(value);
    }

    @Override
    public double requestValue() {
        return readValue();
    }

    public void setValue(double value) {
        setLastOutput(value);
    }

    @Override
    protected double activationFunction(double x) {
        return x;
    }

    @Override
    protected double dActivationFunction(double x) {
        return 1;
    }

    public static final class Factory implements MLP.NeuronFactory {
        private final double value;

        public Factory(double value) {
            this.value = value;
        }

        @Override
        public InputNeuron createNeuron() {
            return new InputNeuron(value);
        }
    }
}
