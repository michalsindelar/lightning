package lightning;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.MultipleGradientPaint.CycleMethod;

/**
 * Class for drawing
 */

public class Sin extends Gui{


    private Graphics2D g2d;
    private int curveHeightPosition;

    private int width;
    private int height;

    private String function;


    /**
     * Sin
     *
     * @param g2d      graphics
     */
    public Sin (Graphics2D g2d) {

        this.g2d = g2d;

        this.width = super.getPreviewWidth();
        this.height = super.getPreviewHeight();

        this.curveHeightPosition = this.height / 2;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // enables opacity
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, super.getCurveOpacity()));
    }


    /**
     * draw once
     */
    public void draw() {
        //set curve type
        function = getCurveType();

        // functional Values -- sin calculating
        double functionalValue = 0;
        double inc = Math.PI / super.getSpeedArg();

        int move = getMove();
        double step = 1;

        // int optimization = 0;

        for (double index = this.width * (-1.25); index < this.width * 1.25; index+=step) {
            int i = (int) index;
            Point2D startOfLine = new Point((i + getOffsetX()), curveHeightPosition);
            Point2D endOfLine = new Point(((i + move + getOffsetX())), computeFunctionValue(functionalValue));

            Line2D functionLine = new Line2D.Double(startOfLine, endOfLine);
            Stroke lineStroke = new BasicStroke(1f);

            g2d.setStroke(lineStroke);
            g2d.draw(functionLine);

            /*
            if (optimization % 9 == 1) {

            }
            optimization++;
            */

            functionalValue += inc;
        }
    }

    /**
     * draw multiple times
     * @param iteration how many curves to draw
     */
    public void draw(int iteration) {

        // check if gradient is enabled
        setGradient();

        int spaceBetween = this.height / (iteration + 1);
        int posY1 = spaceBetween;
        for (int i = 0; i < iteration; i++){

            this.curveHeightPosition = posY1;
            draw();
            posY1 += spaceBetween;

        }
        //frame.setVisible(false);
    }

    /**
     * Full render
     * @param iteration count of iteration
     */
    public void render(int renderWidth, int renderHeight, int iteration) {



        this.width = renderWidth;
        this.height = renderHeight;

        this.draw(iteration);
    }

    /**
     * setGradient
     * check for gradient option
     * set radial/radial-repeat/liner/normal
     */
    public void setGradient () {
        if (super.isDrawGradient()) {

            RadialGradientPaint paintRadial;
            Point2D center;
            float radius;
            float[] dist;
            Color[] colors;

            center = new Point2D.Float(this.width / 2, this.height / 2);
            radius = this.height / 2;
            switch (getGradientType()) {
                case ("radial-repeat"):
                    radius = 90;
                    dist = new float[]{0.05f, .95f};
                    colors = new Color[]{getGradientColor1(), getGradientColor2()};
                    paintRadial = new RadialGradientPaint(center, radius, dist, colors, CycleMethod.REPEAT);
                    // CycleMethod.REFLECT
                    g2d.setPaint(paintRadial);
                    break;
                case ("radial"):
                    dist = new float[]{0.05f, .95f};
                    colors = new Color[]{getGradientColor1(), getGradientColor2()};
                    paintRadial = new RadialGradientPaint(center, radius, dist, colors, CycleMethod.NO_CYCLE);
                    g2d.setPaint(paintRadial);
                    break;
                case ("linear"):
                    GradientPaint paintLinear = new GradientPaint(0, 0, getGradientColor1(), this.width, 0, getGradientColor2());
                    g2d.setPaint(paintLinear);
                    break;

                default:
                    dist = new float[]{0.05f, .95f};
                    colors = new Color[]{super.getCurveColor(), super.getBgColor()};
                    paintRadial = new RadialGradientPaint(center, radius, dist, colors, CycleMethod.NO_CYCLE);
                    g2d.setPaint(paintRadial);
                    break;
            }
        }
        else {
            // set curve color via getCurveColor from slider
            g2d.setColor(getCurveColor());
        }

    }

    private int computeFunctionValue (double functionalValue) {
        int value;
        switch (function) {
            case "upDown" :
                value = (int) ( ((Math.tan(functionalValue) - Math.sin(functionalValue)) / Math.sin(functionalValue) ) * super.getJumpArg() + curveHeightPosition);
                break;
            case "tan" :
                value = (int) ( (Math.tan(functionalValue)) * super.getJumpArg() + curveHeightPosition);
                break;
            case "sin" :
                value = (int) ( (Math.sin(functionalValue)) * super.getJumpArg() + curveHeightPosition);
                break;

            default:
                value = (int) ( ((Math.tan(functionalValue) - Math.sin(functionalValue)) / Math.sin(functionalValue) ) * super.getJumpArg() + curveHeightPosition);

        }

        return value;
    }
}
