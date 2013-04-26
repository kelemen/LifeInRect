
package org.kelemenattila.rectlife;

/**
 *
 * @author Kelemen Attila
 */
public final class StandardGeneticCombiner implements DnsCombiner {
    private static final double MIN_MUTATE_SIZE = 1.0;
    private static final double MAX_GENE_SIZE_FOR_MUTATE = 1.0E10;
    private static final double MUTATE_MULTIPLIER = 1.5;

    private final double mutateRate;

    public StandardGeneticCombiner(double mutateRate) {
        this.mutateRate = mutateRate;
    }

    private static double random(double lower, double upper) {
        return (upper - lower) * Math.random() + lower;
    }

    @Override
    public double[] combineDns(double[] dns1, double[] dns2) {
        if (dns1.length != dns2.length) {
            throw new IllegalArgumentException("Cannot combine dns because they are from different species.");
        }

        double[] genes = new double[dns1.length];

        int index = (int)(genes.length * Math.random()) + 1;

        System.arraycopy(dns1, 0, genes, 0, index);
        System.arraycopy(dns2, index, genes, index, genes.length - index);

        for (int i = 0; i < genes.length; i++) {
            if (Math.random() < mutateRate) {
                double maxMut = MUTATE_MULTIPLIER * Math.max(MIN_MUTATE_SIZE, Math.abs(genes[i]));
                maxMut = Math.min(MAX_GENE_SIZE_FOR_MUTATE, maxMut);
                genes[i] = random(-maxMut, maxMut);
            }
        }

        return genes;
    }
}
