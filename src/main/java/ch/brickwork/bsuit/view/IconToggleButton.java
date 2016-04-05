package ch.brickwork.bsuit.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcel on 14.10.15.
 */
public class IconToggleButton extends JToggleButton {
    public IconToggleButton(String path, String toolTip) {
        setIcon(new ImageIcon(getClass().getClassLoader().getResource(path)));
        setPreferredSize(new Dimension(28, 28));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setOpaque(true);
        setToolTipText(toolTip);
    }
}
