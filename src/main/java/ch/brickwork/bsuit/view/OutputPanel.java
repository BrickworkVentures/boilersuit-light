package ch.brickwork.bsuit.view;

import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;
import ch.brickwork.bsuit.interpreter.interpreters.ProcessingResult;
import ch.brickwork.bsuit.util.FontUtils;
import ch.brickwork.bsuit.util.ILog;
import ch.brickwork.bsuit.util.ImageUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcelcamporelli on 14.9.2015.
 */
public class OutputPanel extends JTabbedPane implements IProcessingResultDisplay, ILog {

    private final IBoilersuitApplicationContext context;

    private OutputConsole outputConsole;

    private TablePanel tablePanel;


    public OutputPanel(Font consoleFont, IBoilersuitApplicationContext context) {
        this.context = context;
        init(consoleFont);
    }

    private void init(Font consoleFont) {
        setFont(FontUtils.getFont(this, "ClearSans-Regular.ttf", 12f));
        addTab("Console", new ImageIcon(ImageUtils.loadImage(this, "tab.png")), outputConsole = new OutputConsole(consoleFont));
        addTab("Output", new ImageIcon(ImageUtils.loadImage(this, "tab.png")), tablePanel = new TablePanel(context));
        setVisible(true);
    }



    @Override
    public void err(String s) {
        outputConsole.err(s);
    }

    @Override
    public void info(String s) {
        outputConsole.info(s);
    }

    @Override
    public void log(String s) {
        outputConsole.log(s);
    }

    @Override
    public void warn(String s) {
        outputConsole.warn(s);
    }

    @Override
    public void displayProcessingResult(ProcessingResult processingResult) {
        tablePanel.displayProcessingResult(processingResult);
    }

    @Override
    public void displayTableOrView(String tableOrViewName, String variableName, String sortField, Boolean sortAsc) {
        tablePanel.displayTableOrView(tableOrViewName, variableName, sortField, sortAsc);
    }

    @Override
    public void setRunStatus(RUN_STATUS runStatus) {
        // do nothing
    }
}
