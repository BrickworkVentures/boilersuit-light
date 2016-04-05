package ch.brickwork.bsuit.control;

import ch.brickwork.bsuit.database.Variable;
import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;
import ch.brickwork.bsuit.interpreter.DefaultCommandInterpreterFactory;
import ch.brickwork.bsuit.interpreter.ScriptProcessor;
import ch.brickwork.bsuit.interpreter.interpreters.ProcessingResult;
import ch.brickwork.bsuit.util.ILog;
import ch.brickwork.bsuit.util.LogMessage;
import ch.brickwork.bsuit.view.IProcessingResultDisplay;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Created by marcel on 21.07.15.
 */
public class Executer {

    /**
     * Utility class used to launch commands in the background, if execution takes longer a indicator is appears.
     * User: marcelcamporelli
     * Date: 19.05.13
     * Time: 18:39
     */

    private static final Logger LOG = Logger.getLogger(Executer.class.getCanonicalName());


    private final IBoilersuitApplicationContext context;
    private final IProcessingResultDisplay processingResultDisplay;
    private ProcessingResult processingResult;


    public Executer(IBoilersuitApplicationContext context, IProcessingResultDisplay processingResultDisplay) {
        this.context = context;
        this.processingResultDisplay = processingResultDisplay;
    }

    /**
     * executes the executionText in the background, sending status updates to jComponent
     *
     * @param executionText   text to be executed
     * @param alternativeText text which will be consider when scriptText won't be understand
     */
    private void executeScriptInBackground(final String executionText, final String alternativeText) {
        final SwingWorker<LogMessage, LogMessage> cpWorker = new SwingWorker<LogMessage, LogMessage>() {

            @Override
            protected LogMessage doInBackground() throws Exception {
                final ILog log = new ILog() {
                    @Override
                    public void err(String message) {
                        publish(new LogMessage(LogMessage.MessageType.ERR, message));
                    }

                    @Override
                    public void info(String message) {
                        context.getLog().info(message);
                    }

                    @Override
                    public void log(String message) {
                        context.getLog().log(message);
                    }

                    @Override
                    public void warn(String message) {
                        context.getLog().warn(message);
                    }
                };

                processingResult = new ScriptProcessor(new DefaultCommandInterpreterFactory(), context).processScript(executionText, null, alternativeText);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        processingResultDisplay.displayProcessingResult(processingResult);
                        processingResultDisplay.setRunStatus(IProcessingResultDisplay.RUN_STATUS.READY);
                    }
                });

                return new LogMessage(LogMessage.MessageType.INFO, "Done");
            }

            protected void done() {
                try {
                    get();
                } catch (ExecutionException e) {
                    LOG.warning(e.getMessage());
                } catch (CancellationException e) {

                    //componentLog.log("Process interrupted");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            protected void process(List<LogMessage> chunks) {
                context.getLog().info(chunks.get(chunks.size() - 1).getText());
            }
        };

        cpWorker.execute();
    }

    /**
     * checks first whether execution text is a table or ch.brickwork.bsuit.view name (only a table or ch.brickwork.bsuit.view name!), if so, simply the
     * content of that table or ch.brickwork.bsuit.view will be displayed in the result table. Otherwise, tries to execute the
     * text as a script
     *
     * @param executionText   text will be executed
     * @param alternativeText text which will be consider when scriptText won't be understand
     */
    public void executeScriptOrExpression(final String executionText, final String alternativeText) {
        if (executionText != null) {
            // if expression, then show in table:
            processingResultDisplay.setRunStatus(IProcessingResultDisplay.RUN_STATUS.BUSY);
            executeScriptInBackground(executionText, alternativeText);

        }
    }
}
