package ch.brickwork.bsuit.view;

import ch.brickwork.bsuit.control.TableWindowController;
import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;
import ch.brickwork.bsuit.interpreter.interpreters.ProcessingResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by marcel on 09.10.15.
 */
public class TablePanel extends JPanel implements IProcessingResultDisplay {

    private final IBoilersuitApplicationContext context;
    private TableWindowController tableController;

    private TableWindow tableWindow;

    private JComponent currentComponent;
    private BoilerSuitDragonConsole keyValueConsole;
    private BoilerSuitDragonConsole paragraphConsole;

    public TablePanel(IBoilersuitApplicationContext context) {
        this.context = context;
        DefaultTableModel model = new DefaultTableModel();
        tableWindow = new TableWindow(model);
        tableController = new TableWindowController(tableWindow, model, context);

        setLayout(new BorderLayout());
        add(initIconPanel(), BorderLayout.WEST);
        add(currentComponent = tableWindow, BorderLayout.CENTER);
    }

    private JPanel initIconPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        final IconToggleButton tabularButton, paragraphButton, keyvalueButton;
        panel.add(tabularButton = new IconToggleButton("tabular.png", "Tabular View"));
        panel.add(paragraphButton = new IconToggleButton("paragraph.png", "Tabular View"));
        panel.add(keyvalueButton = new IconToggleButton("keyvalue.png", "Tabular View"));
        tabularButton.setEnabled(true);
        tabularButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(currentComponent);
                add(currentComponent = tableWindow, BorderLayout.CENTER);
                tabularButton.setSelected(true);
                paragraphButton.setSelected(false);
                keyvalueButton.setSelected(false);
                revalidate();
                repaint();
            }
        });
        paragraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(currentComponent);
                if (paragraphConsole != null)
                    add(currentComponent = paragraphConsole, BorderLayout.CENTER);
                tabularButton.setSelected(false);
                paragraphButton.setSelected(true);
                keyvalueButton.setSelected(false);
                revalidate();
                repaint();
            }
        });
        keyvalueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(currentComponent);
                if (keyValueConsole != null)
                    add(currentComponent = keyValueConsole, BorderLayout.CENTER);
                currentComponent.setVisible(true);
                tabularButton.setSelected(false);
                paragraphButton.setSelected(false);
                keyvalueButton.setSelected(true);
                revalidate();
                repaint();
            }
        });
        return panel;
    }

    private void initKeyValueConsole(String tableName, String variableName, String sortField, Boolean sortAsc) {
        if(keyValueConsole == null)
            keyValueConsole = new BoilerSuitDragonConsole(context);
        else
            keyValueConsole.clearConsole();

        keyValueConsole.displayTableOrView(tableName, variableName, sortField, sortAsc, null, BoilerSuitDragonConsole.VIEW_MODE.KEYVALUE);
    }

    private void initParagraphConsole(String tableName, String variableName, String sortField, Boolean sortAsc) {
        if(paragraphConsole == null)
            paragraphConsole = new BoilerSuitDragonConsole(context);
        else
            paragraphConsole.clearConsole();

        paragraphConsole.displayTableOrView(tableName, variableName, sortField, sortAsc, null, BoilerSuitDragonConsole.VIEW_MODE.PARAGRAPH);
    }

    @Override
    public void displayProcessingResult(ProcessingResult processingResult) {
        tableController.displayProcessingResult(processingResult);
        keyValueConsole.clearConsole();
        keyValueConsole.displayProcessingResult(processingResult);
        paragraphConsole.clearConsole();
        paragraphConsole.displayProcessingResult(processingResult);
    }

    @Override
    public void displayTableOrView(final String tableOrViewName, final String variableName, final String sortField, final Boolean sortAsc) {
       initKeyValueConsole(tableOrViewName, variableName, sortField, sortAsc);
       initParagraphConsole(tableOrViewName, variableName, sortField, sortAsc);
       tableController.displayTableOrView(tableOrViewName, variableName, sortField, sortAsc);
    }

    @Override
    public void setRunStatus(RUN_STATUS runStatus) {
        // do nothing
    }
}
