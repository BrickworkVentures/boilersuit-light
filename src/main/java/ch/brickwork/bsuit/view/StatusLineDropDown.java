package ch.brickwork.bsuit.view;

import ch.brickwork.bsuit.util.FontUtils;
import ch.brickwork.bsuit.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by marcel on 21/10/15.
 */
public class StatusLineDropDown extends JPanel {
    private final JLabel textLabel;
    private final String text;
    private final JPopupMenu menu;

    public StatusLineDropDown(String text, JPopupMenu menu) {
        //setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(textLabel = new JLabel(this.text = text));//, BorderLayout.CENTER);
        JLabel dropdownLabel = createDropDownLabel();
        add(dropdownLabel);//, BorderLayout.EAST);
        this.menu = menu;
    }

    public StatusLineDropDown(String iconName, String text, JPopupMenu menu) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        add(new JLabel(new ImageIcon(ImageUtils.loadImage(this, iconName))));
        add(textLabel = new JLabel(this.text = text));
        textLabel.setFont(FontUtils.getFont(this, "ClearSans-Regular.ttf", 14, 2));

        JLabel dropdownLabel = createDropDownLabel();
        add(dropdownLabel);//, BorderLayout.EAST);
        this.menu = menu;
    }

    private JLabel createDropDownLabel() {
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("dropdownarrows.png"));
        final JLabel dropdownLabel = new JLabel(imageIcon);
        dropdownLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               menu.setLocation(e.getX(), e.getY());
                menu.show(dropdownLabel, e.getX(), e.getY());
            }
        });
        return dropdownLabel;
    }
}
