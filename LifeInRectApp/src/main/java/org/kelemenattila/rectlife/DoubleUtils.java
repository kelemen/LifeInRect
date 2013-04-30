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

    public static double findMinNanSafe(double[] values) {
        double min = Double.NaN;
        for (double value: values) {
            if ((Double.isNaN(min) && !Double.isNaN(value)) || value < min) {
                min = value;
            }
        }
        return min;
    }

    private DoubleUtils() {
        throw new AssertionError();
    }
}
