package org.kelemenattila.rectlife;

/**
 *
 * @author Kelemen Attila
 */
public final class DoubleUtils {
    public static double findMaxNanSafe(double[] values) {
        double max = Double.NaN;
        for (double value: values) {
            if ((Double.isNaN(max) && !Double.isNaN(value)) || value > max) {
                max = value;
            }
        }
        return max;
    }

    private DoubleUtils() {
        throw new AssertionError();
    }
}
