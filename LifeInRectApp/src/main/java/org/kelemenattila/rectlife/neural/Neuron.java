package org.kelemenattila.rectlife.neural;

import java.util.*;

/**
 *
 * @author Kelemen Attila
 */
public abstract class Neuron implements java.io.Serializable {
    private static final long serialVersionUID = 5473866466023691362L;

    private static class Connection implements java.io.Serializable {
        private static final long serialVersionUID = 9126996632653921397L;

        private Neuron parent;
        private Neuron child;
        private double weight;

        private boolean fired;
        private double value;

        private boolean errorFired;
        private double error;

        public Connection(Neuron parent, Neuron child, double weight) {
            this.parent = parent;
            this.child = child;
            this.weight = weight;

            this.fired = false;
            this.errorFired = false;
        }

        public void fireValue(double value) {
            boolean old_fired = fired;

            fired = true;
            this.value = value;

            if (old_fired != true) parent.newChildReady();
        }

        public void resetFire() {
            fired = false;
        }

        public boolean isFired() {
            return fired;
        }

        public void fireError(double alpha, double error) {
            boolean old_fired = errorFired;

            errorFired = true;
            this.error = error;

            if (old_fired != true) {
                child.newParentReady(alpha);
            }
        }

        public void resetErrorFire() {
            errorFired = false;
        }

        public boolean isErrorFired() {
            return errorFired;
        }

        public Neuron getParent() {
            return parent;
        }

        public Neuron getChild() {
            return child;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }

        public double readError() {
            return weight * error;
        }

        public double readValue() {
            return weight * value;
        }

        public double requestValue() {
            return weight * child.requestValue();
        }
    }

    private List<Connection> outputConnections;
    private List<Connection> inputConnections;
    private double lastOutput;
    private double lastSum;

    private int childrenReady;
    private int parentReady;

    public Neuron() {
        outputConnections = new LinkedList<>();
        inputConnections = new LinkedList<>();
        childrenReady = 0;
        parentReady = 0;
    }

    public double fire() {
        double output = lastOutput;

        for (Connection outputC: outputConnections) {
            outputC.fireValue(output);
        }

        return output;
    }

    public double readValue() {
        return lastOutput;
    }

    public double requestValue() {
        double sum = 0.0;
        for (Connection input: inputConnections) {
            sum += input.requestValue();
        }

        return activationFunction(sum);
    }

    public final void addParent(Neuron parent, double weight) {
        if (parent != null) {
            Connection connection = new Connection(parent, this, weight);

            this.outputConnections.add(connection);
            parent.inputConnections.add(connection);
        }
    }

    public final void addChild(Neuron child, double weight) {
        if (child != null) {
            child.addParent(this, weight);
        }
    }

    public final double getInputWeight(int input_index) {
        return inputConnections.get(input_index).getWeight();
    }

    public final void setInputWeight(int input_index, double weight) {
        inputConnections.get(input_index).setWeight(weight);
    }

    private static double random(double lowerBound, double upperBound) {
        return Math.random() * (upperBound - lowerBound) + lowerBound;
    }

    public void randomInputWeights(double lowerBound, double upperBound) {
        for (Connection c: inputConnections) {
            c.setWeight(random(lowerBound, upperBound));
        }
    }

    public final double[] getAllInputWeights() {
        double[] result = new double[inputConnections.size()];
        int index = 0;
        for (Connection c: inputConnections) {
            result[index++] = c.getWeight();
        }

        return result;
    }

    public void backPropagateError(double alpha, double error) {
        double mul = alpha * error;

        for (Connection c: inputConnections) {
            c.fireError(alpha, error);

            double w = c.getWeight();
            c.setWeight(w + mul * c.getChild().lastOutput);
        }
    }

    protected abstract double activationFunction(double x);
    protected abstract double dActivationFunction(double x);

    protected void setLastOutput(double value) {
        lastOutput = value;
    }

    private void newChildReady() {
        childrenReady++;

        if (childrenReady >= inputConnections.size()) {
            childrenReady = 0;

            double sum = 0.0;
            for (Connection child: inputConnections) {
                sum += child.readValue();
                child.resetFire();
            }

            lastSum = sum;
            lastOutput = activationFunction(sum);
            fire();
        }
    }

    private void newParentReady(double alpha) {
        parentReady++;

        if (parentReady >= outputConnections.size()) {
            parentReady = 0;

            double sum = 0.0;
            for (Connection parent: outputConnections) {
                sum += parent.readError();
                parent.resetErrorFire();
            }

            backPropagateError(alpha, dActivationFunction(lastSum) * sum);
        }
    }
}
