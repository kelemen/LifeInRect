package org.kelemenattila.rectlife;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.jtrim.utils.ExceptionHelper;

/**
 *
 * @author Kelemen Attila
 */
public final class GraphicUtils {
    private static int getCoordY(int height, double value) {
        return height - (int)Math.round((double)height * value);
    }

    private static int getCoordX(int width, double value) {
        return (int)Math.round(width * value);
    }

    public static void drawGraph(BufferedImage output, double minValue, double maxValue, double[] values) {
        ExceptionHelper.checkArgumentInRange(values.length, 1, Integer.MAX_VALUE, "values.length");

        Graphics2D g2d = output.createGraphics();
        try {
            int width = output.getWidth();
            int height = output.getHeight();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);

            g2d.setColor(Color.GREEN.darker());
            g2d.setStroke(new BasicStroke(3.0f));

            double dValue = maxValue - minValue;

            double prevValue = Double.NaN;
            for (int i = 0; i < values.length; i++) {
                double currentValue = values[i];
                if (Double.isNaN(prevValue) || Double.isNaN(currentValue)) {
                    prevValue = currentValue;
                    continue;
                }

                int prevY = getCoordY(height, (prevValue - minValue) / dValue);
                int currentY = getCoordY(height, (currentValue - minValue) / dValue);
                prevValue = currentValue;

                int x0 = getCoordX(width, (double)(i - 1) / (double)(values.length - 1));
                int x1 = getCoordX(width, (double)i / (double)(values.length - 1));

                g2d.drawLine(x0, prevY, x1, currentY);
            }

            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawLine(width / 2, 0, width / 2, height);
        } finally {
            g2d.dispose();
        }
    }

    private GraphicUtils() {
        throw new AssertionError();
    }
}
