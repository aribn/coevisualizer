package coeviz.visualization.views.elements;


import java.awt.*;
import javax.swing.*;


public class Rule extends JComponent {
    public static final int MARKER_INCREMENT = 50;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int SIZE = 35;

    public int orientation;
    private int increment;
    private int units;

    public Rule(int o, boolean m) {
        orientation = o;
        increment = MARKER_INCREMENT;
        units = increment;
    }

    public int getIncrement() {
        return increment;
    }

    public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(SIZE, ph));
    }

    public void setPreferredWidth(int pw) {
        setPreferredSize(new Dimension(pw, SIZE));
    }

    protected void paintComponent(Graphics g) {
        Rectangle drawHere = g.getClipBounds();

        // Do the ruler labels in a small font that's black.
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(Color.black);

        // Some vars we need.
        int end = 0;
        int start = 0;
        int tickLength = 0;
        String text = null;

        // Use clipping bounds to calculate first and last tick locations.
        if (orientation == HORIZONTAL) {
            start = (drawHere.x / increment) * increment;
            end = (((drawHere.x + drawHere.width) / increment) + 1)
                  * increment;
        } else {
            start = (drawHere.y / increment) * increment;
            end = (((drawHere.y + drawHere.height) / increment) + 1)
                  * increment;
        }

        
        // Make a special case of 0 to display the number
        // within the rule and draw a units label.
        if (start == 0) {
            tickLength = 10;
            if (orientation == HORIZONTAL) {
                g.setFont(new Font("SansSerif", Font.BOLD, 10));
                g.drawString("Generation of Test/Teacher population", 2, 11);
                g.drawLine(0, SIZE-1, 0, SIZE-tickLength-1);
            } else {
                g.drawString("", 9, 10);
                g.drawLine(SIZE-1, 0, SIZE-tickLength-1, 0);
    }
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        }

        
        // ticks and labels
        for (int i = increment; i < end; i += increment) {
            if (i % units == 0)  {
                tickLength = 4;
                text = Integer.toString(i);
            } else {
                tickLength = 2;
                text = null;
            }

            if (tickLength != 0) {
                if (orientation == HORIZONTAL) {
                    g.drawLine(i, SIZE-1, i, SIZE-tickLength-1);
                    if (text != null)
                        g.drawString(text, i-3, 25);
                } else {
                    g.drawLine(SIZE-1, i, SIZE-tickLength-1, i);
                    if (text != null)
                        g.drawString(text, 9, i+3);
                }
            }
        }
    }
}
