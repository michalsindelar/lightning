package lightning;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Buffered graphics used for full render
 */
public class BufferedGraphics extends JPanel{
    private java.awt.image.BufferedImage fullResolutionImage;

    private int renderWidth;
    private int renderHeight;

    public BufferedGraphics (int renderWidth, int renderHeight) {
        this.fullResolutionImage = new java.awt.image.BufferedImage(renderWidth, renderHeight, java.awt.image.BufferedImage.TYPE_3BYTE_BGR);

        this.renderWidth = renderWidth;
        this.renderHeight = renderHeight;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(fullResolutionImage, 0, 0, renderWidth, renderHeight, null);
        g.setColor(Color.red);

    }

    /**
     * draw painting
     * @param iteration         curve iterations
     * @param backgroundColor   background color
     */
    public void updatePaint(int iteration, Color backgroundColor){
        // draw on paintImage using Graphics
        Graphics2D g2d = fullResolutionImage.createGraphics();
        setBackgroundColor(g2d, backgroundColor);

        Sin sinPaint = new Sin(g2d);
        //g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        sinPaint.render(renderWidth, renderHeight, iteration);
        g2d.dispose();
        repaint();

        //renderProgressWindow.closeProgressRenderWindow();
    }

    private void setBackgroundColor(Graphics2D g2d, Color color) {
        g2d.setColor(color);
        g2d.setBackground(color);
        g2d.setPaint(color);
        g2d.fillRect(0,0,renderWidth, renderHeight);
    }

    public void save(File path) throws IOException, IllegalArgumentException {
        if (path == null){
            throw new IllegalArgumentException("File cannot be null!");
        }
        //Date date = new Date();
        ImageIO.write(fullResolutionImage, "PNG", path);
    }

}
