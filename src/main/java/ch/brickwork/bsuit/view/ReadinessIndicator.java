package ch.brickwork.bsuit.view;

import ch.brickwork.bsuit.util.FontUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcelcamporelli on 11.9.2015.
 */
public class ReadinessIndicator extends JPanel {

    private static final int H_GAP = 2;

    private IProcessingResultDisplay.RUN_STATUS status;

    private JLabel statusLabel = new JLabel("Ready");

    private class Light extends JPanel {
        private static final int LIGHT_WIDTH = 10;
        private static final int LIGHT_HEIGHT = 10;

        public Light(Color color) {
            setBackground(color);
            setSize(LIGHT_WIDTH, LIGHT_HEIGHT);
            setPreferredSize(new Dimension(LIGHT_WIDTH, LIGHT_HEIGHT));
        }
    }

    public ReadinessIndicator() {
        setStatus(IProcessingResultDisplay.RUN_STATUS.READY);
    }



    public void setStatus(IProcessingResultDisplay.RUN_STATUS status) {
        this.status = status;



        removeAll();
        Box verticalBox = Box.createVerticalBox();

        setLayout(new BorderLayout(H_GAP, 0));//new BoxLayout(this, BoxLayout.X_AXIS));

        setBorder(BorderFactory.createEmptyBorder(0, H_GAP, 0, 0));

        verticalBox.add(Box.createVerticalGlue());
        switch(status) {
            case READY:
                verticalBox.add(new Light(Color.GREEN)); statusLabel.setText("Ready"); break;
            case BUSY:
                verticalBox.add(new Light(Color.RED)); statusLabel.setText("Busy..."); break;
        }
        verticalBox.add(Box.createVerticalGlue());

        add(verticalBox, BorderLayout.WEST);


        add(statusLabel = new JLabel("Ready"), BorderLayout.EAST);
        statusLabel.setFont(FontUtils.getFont(this, "ClearSans-Regular.ttf", 14, 2));


        invalidate();
    }
}
