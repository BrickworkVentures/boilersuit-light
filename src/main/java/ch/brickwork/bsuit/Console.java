package ch.brickwork.bsuit;

import ch.brickwork.bsuit.control.BoilersuitCommandController;
import ch.brickwork.bsuit.database.Record;
import ch.brickwork.bsuit.globals.DefaultBoilersuitApplicationContext;
import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;
import ch.brickwork.bsuit.interpreter.DefaultCommandInterpreterFactory;
import ch.brickwork.bsuit.interpreter.interpreters.ProcessingResult;

import ch.brickwork.bsuit.util.ConfigFile;
import ch.brickwork.bsuit.util.FontUtils;
import ch.brickwork.bsuit.util.ILog;
import ch.brickwork.bsuit.util.ImageUtils;
import ch.brickwork.bsuit.view.BSStatusLine;
import ch.brickwork.bsuit.view.BoilerSuitDragonConsole;
import ch.brickwork.bsuit.view.IProcessingResultDisplay;
import ch.brickwork.bsuit.view.OutputPanel;
import com.bulenkov.darcula.DarculaLaf;

import javax.swing.*;
import java.awt.*;


/**
 * Main application window, holding "terminal" and "output panel"
 * Created by marcel on 17.07.15.
 */
public class Console implements ILog, IProcessingResultDisplay {
    private static final String WELCOME = "\n" +
            "\n&da" +
            "\n" +
            "\n" +
            "                                                                  \n" +
            "               ,-----.         ,--.,--.                             ,--.  ,--.   \n" +
            "               |  |) /_  ,---. `--'|  | ,---. ,--.--. ,---. ,--.,--.`--',-'  '-. \n" +
            "               |  .-.  \\| .-. |,--.|  || .-. :|  .--'(  .-' |  ||  |,--.'-.  .-' \n" +
            "               |  '--' /' '-' '|  ||  |\\   --.|  |   .-'  `)'  ''  '|  |  |  |   \n" +
            "               `------'  `---' `--'`--' `----'`--'   `----'  `----' `--'  `--'   \n" +
            "                                                                  \n" +
            "\n" +
            "\n" +
            "\n" +
            "                          Welcome to BoilerSuit Command Line Version\n" +
            "                      (C) Brickwork Ventures, LLC, Winterthur, 2013-2016\n\n" +
            "";

    private static final String HIT_ENTER_TO_DISPLAY_IN_TABLE = "[HIT ENTER TO DISPLAY IN TABLE]";

    private static BoilerSuitDragonConsole inputConsole;

    private OutputPanel outputPanel;

    private BSStatusLine statusLine;

    private IBoilersuitApplicationContext context;

    private ProcessingResult lastTableOrViewProcessingResult;

    private ConfigFile config;

    public Console() {
        init();
    }

    private void init() {
        context = new DefaultBoilersuitApplicationContext();
        context.getDatabase().setLog(this);
        context.setLog(this);

        try {
            SwingUtilities.invokeAndWait(
                    new Runnable() {
                        @Override
                        public void run() {
                            initLookAndFeel();
                            initConsoleAndOutputConsole();
                            initFrame();
                            welcome();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputConsole.setCommandProcessor(new BoilersuitCommandController(this, context));
    }

    public ProcessingResult getLastTableOrViewProcessingResult() {
        return lastTableOrViewProcessingResult;
    }

    /**
     * there was a bug in BoilerSuitCommandController that when this was not called, the console
     * would freeze. I don't know why is it, but it turned out to be a stable workaround, therefore...:
     */
    public void setDefaultColor() {
        inputConsole.append(BoilerSuitDragonConsole.DEFAULT_BS_COLOR);
    }

    public void prompt() {
        inputConsole.append(BoilerSuitDragonConsole.DEFAULT_BS_COLOR);
        inputConsole.append("\n:-> ");
    }

    @Override
    public void displayProcessingResult(ProcessingResult pr) {
        inputConsole.append("\n");
        displayProcessingResultWithoutPrompt(pr);
        inputConsole.append("\n");
    }

    public void displayRecords(java.util.List<Record> records, String tableName) {
        inputConsole.displayRecords(records, tableName, HIT_ENTER_TO_DISPLAY_IN_TABLE, BoilerSuitDragonConsole.VIEW_MODE.PARAGRAPH);
    }

    @Override
    public void displayTableOrView(final String tableName, final String variableName, final String sortField, final Boolean sortAsc) {
        inputConsole.displayTableOrView(tableName, variableName, sortField, sortAsc, HIT_ENTER_TO_DISPLAY_IN_TABLE, BoilerSuitDragonConsole.VIEW_MODE.PARAGRAPH);
    }

    @Override
    public void setRunStatus(RUN_STATUS runStatus) {
        statusLine.setReadinessStatus(runStatus);
        if(runStatus.equals(RUN_STATUS.READY))
            prompt();
    }

    public void setProgressMessage(String text) {
        inputConsole.append("\n" + text + "\n");
    }

    public void displayTableOrViewInTable(final String tableName, final String variableName, final String sortField, final Boolean sortAsc) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                outputPanel.displayTableOrView(tableName, variableName, sortField, sortAsc);
            }
        });
    }

//
// DELEGATES FOR LOGGING
//

    @Override
    public void err(String s) {
        inputConsole.err(s);
    }

    @Override
    public void info(String s) {
        outputPanel.info(s);
    }

    @Override
    public void log(String s) {
        System.out.println(s);
    }

    @Override
    public void warn(String s) {
        outputPanel.warn(s);
    }


    private void displayProcessingResultWithoutPrompt(ProcessingResult pr) {
        if (pr.getType().equals(ProcessingResult.ResultType.COMPOSITE) && pr.getSubResults() != null) {
            inputConsole.append(BoilerSuitDragonConsole.DEFAULT_SUMMARY_COLOR + "\n-- " + pr.getResultSummary() + BoilerSuitDragonConsole.DEFAULT_RESULT_COLOR + "\n");
            for (ProcessingResult prElement : pr.getSubResults()) {
                displayProcessingResultWithoutPrompt(prElement);
            }
            inputConsole.append(BoilerSuitDragonConsole.DEFAULT_SUMMARY_COLOR + "-- " + pr.getResultSummary() + " (end)" + BoilerSuitDragonConsole.DEFAULT_BS_COLOR);
            lastTableOrViewProcessingResult = null;
        } else if (pr.getType().equals(ProcessingResult.ResultType.TABLE)) {
            displayTableOrView(pr.getResultSummary(), pr.getResultSummary(), null, null);
            lastTableOrViewProcessingResult = pr;
        } else if (pr.getType().equals(ProcessingResult.ResultType.VIEW)) {
            if (pr.getResultSummary().toLowerCase().startsWith("select "))
                displayRecords(context.getDatabase().prepare(pr.getResultSummary()), pr.getResultSummary());
            else
                displayTableOrView(pr.getResultSummary(), pr.getResultSummary(), null, null);
            lastTableOrViewProcessingResult = pr;
        } else if (pr.getType().equals(ProcessingResult.ResultType.MESSAGE)) {
            inputConsole.append("" + BoilerSuitDragonConsole.DEFAULT_RESULT_COLOR + pr.getResultSummary() + "\n" + BoilerSuitDragonConsole.DEFAULT_BS_COLOR);
            lastTableOrViewProcessingResult = null;
        } else if (pr.getType().equals(ProcessingResult.ResultType.FATAL_ASSERT)) {
            //err("FATAL: Assertion Stop");
            lastTableOrViewProcessingResult = null;
        } else if (pr.getType().equals(ProcessingResult.ResultType.SYNTAX_ERROR) || pr.getType().equals(ProcessingResult.ResultType.FATAL_ERROR)) {
            err(pr.getResultSummary());
            lastTableOrViewProcessingResult = null;
        }
    }

    private void welcome() {
        inputConsole.append(BoilerSuitDragonConsole.DEFAULT_BS_COLOR);
        inputConsole.append(WELCOME);
        prompt();
    }

    private void initConsoleAndOutputConsole() {
        Font uniFont = FontUtils.getFont(this, "AnonymousProRegular.ttf", 16f);

        // console
        inputConsole = new BoilerSuitDragonConsole(context);
        inputConsole.setInputColor(BoilerSuitDragonConsole.DEFAULT_BS_COLOR);


        // output panel
        outputPanel = new OutputPanel(uniFont, context);
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        UIDefaults def = UIManager.getLookAndFeelDefaults();
        def.put("TabbedPane.tabInsets", new Insets(1, 5, 1, 5));
        def.put("TabbedPane.selectedTabPadInsets", new Insets(1, 5, 1, 5));
        def.put("TabbedPane.tabAreaInsets", new Insets(1, 5, 1, 5));
        def.put("TabbedPane.tabsOverlapBorder", false);
    }

    private JFrame initFrame() {
        JFrame f = new JFrame();
        f.setIconImage(ImageUtils.loadImage(this, "bsuit.gif"));
        f.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputConsole, outputPanel));
        f.add(statusLine = new BSStatusLine(context), BorderLayout.SOUTH);
        String versionString = new DefaultCommandInterpreterFactory().createCommandInterpreter(null, "", context, "").getCoreVersion();
        f.setTitle("BOILERSUIT CONSOLE " + versionString);
        f.pack();
        f.setVisible(true);
        return f;
    }
}
