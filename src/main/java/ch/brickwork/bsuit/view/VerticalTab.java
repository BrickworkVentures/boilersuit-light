package ch.brickwork.bsuit.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by marcel on 22.09.15.
 */
public class VerticalTab extends JPanel {
    public VerticalTab(String iconPath, String text) {
        setLayout(new BorderLayout());
        add(new VerticalText(text), BorderLayout.CENTER);
        add(new IconActionButton(iconPath, text), BorderLayout.SOUTH);
    }

    private class VerticalText extends JComponent {
        private final String text;

        public VerticalText(String text) {
            this.text = text;
            //setSize(20, 100);
            setPreferredSize(new Dimension(10, 100));
        }


        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int h = ((Graphics2D) g).getFontMetrics().getHeight();
            int w = ((Graphics2D) g).getFontMetrics().stringWidth(text);

            AffineTransform at = new AffineTransform();
            at.setToRotation(Math.toRadians(90), 10, 10);
            g2.setTransform(at);
            g2.drawString(text, getWidth() / 2, 10);

        }


    }
}
