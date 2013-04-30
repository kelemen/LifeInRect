package org.kelemenattila.rectlife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jtrim.utils.ExceptionHelper;
import org.kelemenattila.rectlife.neural.LinearNeuron;
import org.kelemenattila.rectlife.neural.MLP;
import org.kelemenattila.rectlife.neural.SigmoidNeuron;

/**
 *
 * @author Kelemen Attila
 */
public final class Entity<EntityAction> {
    private static final double LAMBDA = 1.0;
    private static final double INITIAL_MIND_STATE = 1.0;

    private static final int OUTPUT_OFFSET_APPEARANCE = 0;
    private static final int OUTPUT_OFFSET_NEW_MIND_STATE = 1;
    private static final int OUTPUT_OFFSET_ACTIONS = 2;

    private final int inputCount;
    private final int neuronCount;
    private final MLP mlp;
    private final double appearance;
    private final EntityAction[] actions;
    private double mindState;
    private long age;

    public Entity(int inputCount, int neuronCount, EntityAction[] actions) {
        this(inputCount, neuronCount, null, INITIAL_MIND_STATE, actions);
    }

    private Entity(
            int inputCount,
            int neuronCount,
            double[] genes,
            double mindState,
            EntityAction[] actions) {

        ExceptionHelper.checkArgumentInRange(inputCount, 0, Integer.MAX_VALUE, "inputCount");
        ExceptionHelper.checkArgumentInRange(actions.length, 1, Integer.MAX_VALUE, "actions.length");

        List<MLP.LayerDescription> layers = new ArrayList<>(2);
        layers.add(new MLP.LayerDescription(new SigmoidNeuron.Factory(LAMBDA), neuronCount));

        // The input layer:
        // 0: mind state (1.0 initially).
        // 1..(1 + inputCount): Provided by the caller (e.g., appearance of neighbours).

        // The output layer:
        // 0: appearance (calculated with every input being 1.0)
        // 1: new mind state (passed as an input in the next "generation")
        // 2..(2 + ACTIONS.length): attack neighbour or self
        layers.add(new MLP.LayerDescription(new LinearNeuron.Factory(1.0), 13));

        this.inputCount = inputCount;
        this.neuronCount = neuronCount;
        this.actions = actions.clone();
        this.age = 0;
        this.mindState = mindState;
        this.mlp = new MLP(layers, inputCount + 1, genes);
        if (genes == null) {
            this.mlp.randomWeights(-1.0, 1.0);
        }

        this.mlp.setInputs(arrayOfValue(1.0, inputCount));
        this.appearance = normalizeAppearance(this.mlp.getOutput(OUTPUT_OFFSET_APPEARANCE));
    }

    private static double normalizeAppearance(double value) {
        return value - Math.floor(value);
    }

    private static double[] arrayOfValue(double value, int count) {
        double[] result = new double[count];
        for (int i = 0; i < result.length; i++) {
            result[i] = value;
        }
        return result;
    }

    public double getAppearance() {
        return appearance;
    }

    public long getAge() {
        return age;
    }

    private double[] getOutputs(double[] neighbours) {
        ExceptionHelper.checkArgumentInRange(neighbours.length, inputCount, inputCount, "neighbours.length");

        double[] inputs = new double[neighbours.length + 1];
        System.arraycopy(neighbours, 0, inputs, 1, neighbours.length);
        inputs[0] = mindState;

        mlp.setInputs(inputs);
        double[] outputs = mlp.getOutputs();

        return outputs;
    }

    private EntityAction chooseActionBasedOnOutputs(double[] outputs) {
        int chosenIndex = 0;
        for (int i = 1; i < actions.length; i++) {
            double actionWeight = outputs[OUTPUT_OFFSET_ACTIONS + i];
            if (actionWeight > outputs[chosenIndex]) {
                chosenIndex = i;
            }
        }

        return actions[chosenIndex];
    }

    public EntityAction think(double[] neighbours) {
        double[] outputs = getOutputs(neighbours);

        mindState = outputs[OUTPUT_OFFSET_NEW_MIND_STATE];
        age++;

        return chooseActionBasedOnOutputs(outputs);
    }

    public EntityAction thinkWithoutAging(double[] neighbours) {
        return chooseActionBasedOnOutputs(getOutputs(neighbours));
    }

    public Entity<EntityAction> breed(Entity<EntityAction> other, DnsCombiner combiner) {
        double[] myGenes = this.mlp.getGenes();
        double[] otherGenes = other.mlp.getGenes();
        double[] combinedGenes = combiner.combineDns(myGenes, otherGenes);
        if (combinedGenes.length != myGenes.length) {
            throw new IllegalArgumentException(
                    "Combing genes resulted in different species: "
                    + Arrays.toString(combinedGenes));
        }

        return new Entity<>(inputCount, neuronCount, combinedGenes, INITIAL_MIND_STATE, actions);
    }
}
