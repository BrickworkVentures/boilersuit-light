package ch.brickwork.bsuit.view;

import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcelcamporelli on 11.9.2015.
 */
public class BSStatusLine extends JPanel {
    private final IBoilersuitApplicationContext context;
    private ReadinessIndicator readinessIndicator;
    private StatusLineDBConnection localConnection;
    private StatusLineDBConnection extConnection;


    public BSStatusLine(IBoilersuitApplicationContext context) {
        this.context = context;
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 0));
        JPanel leftArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(leftArea, BorderLayout.WEST);
        JPanel rightArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rightArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(rightArea, BorderLayout.EAST);

        rightArea.add(localConnection = new StatusLineDBConnection("SQLite@localhost", "dbhome.png", context));
        rightArea.add(extConnection = new StatusLineDBConnection("No external DB selected", "dbext.png", context));
        leftArea.add(readinessIndicator = new ReadinessIndicator());
    }

    public void setReadinessStatus(IProcessingResultDisplay.RUN_STATUS runStatus) {
        readinessIndicator.setStatus(runStatus);
    }
}
