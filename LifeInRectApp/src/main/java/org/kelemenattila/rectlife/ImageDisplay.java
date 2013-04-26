package org.kelemenattila.rectlife;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.*;

/**
 *
 * @author Kelemen Attila
 */
@SuppressWarnings("serial")
public class ImageDisplay extends JPanel {
    private Image image;

    public ImageDisplay() {
        this(null);
    }

    public ImageDisplay(Image image) {
        this.image = image;
        setOpaque(false);
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        if (isOpaque()) {
            g.clearRect(0, 0, panelWidth, panelHeight);
        }

        if (image != null) {
            int imgWidth = image.getWidth(this);
            int imgHeight = image.getHeight(this);
            if (imgWidth > 0 && imgHeight > 0) {
                double zoomX = (double)panelWidth / (double)imgWidth;
                double zoomY = (double)panelHeight / (double)imgHeight;
                double zoom = Math.min(zoomX, zoomY);

                int newWidth = (int)Math.round(imgWidth * zoom);
                int newHeight = (int)Math.round(imgHeight * zoom);

                int offsetX = (panelWidth - newWidth) / 2;
                int offsetY = (panelHeight - newHeight) / 2;
                g.drawImage(image, offsetX, offsetY, newWidth, newHeight, this);
            }
        }
    }
}
