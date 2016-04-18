package ch.brickwork.bsuit.control;

import ch.brickwork.bsuit.Console;
import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;
import ch.brickwork.bsuit.util.ConfigFile;
import com.eleet.dragonconsole.CommandProcessor;

import java.io.File;

/**
 * Created by marcel on 2/19/16.
 */
public class BoilersuitCommandController extends CommandProcessor {
    public static final String RECONC_COMMAND = "rv";
    private static final String EXIT_COMMAND = "exit";
    private final Console processingResultDisplay;
    private final IBoilersuitApplicationContext context;

    public static Executer executer;

    public BoilersuitCommandController(Console processingResultDisplay, IBoilersuitApplicationContext context) {
        this.processingResultDisplay = processingResultDisplay;
        this.context = context;


        // init command processor
        executer = new Executer(context, processingResultDisplay);
    }

    /**
     * @todo exit part necessary?
     */
    public void processCommand(final String input) {
        if (input.trim().equalsIgnoreCase(EXIT_COMMAND) || input.trim().equalsIgnoreCase(EXIT_COMMAND + ";")) {
            context.getLog().log("Deleting temporary tables...");
            context.getDatabase().deleteTemporaryTables();
            context.getLog().log("Done deleting temporary tables");
            System.exit(0);
        } else if (input.trim().equals("")) {
            if (processingResultDisplay.getLastTableOrViewProcessingResult() != null) {
                processingResultDisplay.displayTableOrViewInTable(processingResultDisplay.getLastTableOrViewProcessingResult().getResultSummary(), processingResultDisplay.getLastTableOrViewProcessingResult().getResultSummary(), null, null);
            }
            processingResultDisplay.setDefaultColor();
        }
        else {
            if (isComplex(input))
                processingResultDisplay.setProgressMessage("Executing - please stand by and pray...");
            executer.executeScriptOrExpression(input, "");

            if(input.trim().toLowerCase().startsWith("cd ")) {
                String path = input.trim().substring(3).replaceAll(";", "");
                if(new File(path).isDirectory())
                    ConfigFile.getInstance(context).refreshConfigFile(path);
            }
        }
    }

    private boolean isComplex(String executionText) {
        return executionText.contains("./.") ||
                executionText.contains("reconcile (");
    }
}
