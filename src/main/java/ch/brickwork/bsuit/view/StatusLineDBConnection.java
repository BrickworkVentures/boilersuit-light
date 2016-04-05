package ch.brickwork.bsuit.view;

import ch.brickwork.bsuit.globals.BoilerSuitGlobals;
import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcel on 22/10/15.
 */
public class StatusLineDBConnection extends JPanel {

    private final IBoilersuitApplicationContext context;
    private StatusLineDropDown connectionLabel;

    public StatusLineDBConnection(String dbName, String iconName, IBoilersuitApplicationContext context) {
        this.context = context;
        connectionLabel = createConnectionDropDown(dbName, iconName);
        setLayout(new BorderLayout(0, 0));
        add(connectionLabel, BorderLayout.CENTER);
        setVisible(true);
    }

    private StatusLineDropDown createConnectionDropDown(String dbName, String iconName) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem("Change..."));
        StatusLineDropDown dd = new StatusLineDropDown(iconName, dbName + context.getWorkingDirectory(), menu);
        return dd;
    }
}
