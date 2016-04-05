package ch.brickwork.bsuit.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by marcel on 25.09.15.
 */
public class IconActionButton extends JButton {
    private Color myColor;
    public IconActionButton(String path, String toolTip) {
        setIcon(new ImageIcon(getClass().getClassLoader().getResource(path)));
        setPreferredSize(new Dimension(28, 28));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        myColor = getBackground();
        setOpaque(true);
        setToolTipText(toolTip);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(isEnabled()) {
                    super.mouseEntered(e);
                    setBackground(Color.LIGHT_GRAY);
                    invalidate();
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(isEnabled()) {
                    super.mouseExited(e);
                    setBackground(myColor);
                }
            }
        });
    }
}
