package ch.brickwork.bsuit.view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * Created by marcelcamporelli on 11.9.2015.
 */
public class BSTitledBorder extends AbstractBorder {
    private static final int DEFAULT_TOP_INSET = 25;
    private String title;


    public BSTitledBorder(String title) {
        this.title = title;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
                            int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);


        g.setColor(new Color(66, 66, 66));
        g.fillRect(0, 0, width, DEFAULT_TOP_INSET);

        g.setColor(Color.GRAY);
        g.drawLine(0, 0, width, 0);

        g.setColor(Color.BLACK);
        g.drawLine(0, DEFAULT_TOP_INSET - 2, width, DEFAULT_TOP_INSET - 2);
        g.setColor(new Color(66, 66, 66));
        g.drawLine(0, DEFAULT_TOP_INSET - 1, width, DEFAULT_TOP_INSET - 1);

        int h = g.getFontMetrics().getHeight();
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(title, 10, DEFAULT_TOP_INSET / 2 + h / 3);
    }


    public Insets getBorderInsets(Component c)  {
        return new Insets(20, 0, 0, 0);
    }

    public Insets getBorderInsets(Component c,
                                  Insets insets) {
        return new Insets(20, 0, 0, 0);
    }
}
