package ch.brickwork.bsuit.view;

import ch.brickwork.bsuit.control.IZoomable;
import ch.brickwork.bsuit.control.ZoomController;
import ch.brickwork.bsuit.database.Record;
import ch.brickwork.bsuit.globals.BoilerSuitGlobals;
import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;
import ch.brickwork.bsuit.interpreter.interpreters.ProcessingResult;
import ch.brickwork.bsuit.util.ConfigFile;
import ch.brickwork.bsuit.util.FontUtils;
import com.eleet.dragonconsole.DragonConsole;
import com.eleet.dragonconsole.util.TextColor;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by marcel on 31/07/15.
 */
public class BoilerSuitDragonConsole extends DragonConsole implements IZoomable, IProcessingResultDisplay {

    public final static Color DEFAULT_CONSOLE_BACKGROUND = new Color(68, 68 ,68);
    public static final String DEFAULT_BS_COLOR = "&da";
    public static final String DEFAULT_SUMMARY_COLOR = "&xa";
    public static final String DEFAULT_RESULT_COLOR = "&ga";
    public static final String DEFAULT_ERROR_COLOR = "&ra";
    public static final String DEFAULT_ATTRIBUTE_COLOR = "&xa";
    public static final String DEFAULT_VALUE_COLOR = DEFAULT_RESULT_COLOR;

    private final static String HUGE_SPACER = "                                                                                                                                                                                                                                                                                                                                             ";
    private final static String HUGE_LINE_SPACER = "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";


    private static final int MAX_FONT_SIZE = 40;
    private static final int ATTRIBUTE_NAME_PLUS_SPACER_WIDTH = 32;
    private IBoilersuitApplicationContext context;

    public void err(String errorMessage) {
        append(DEFAULT_ERROR_COLOR);
        append("\n");
        append(errorMessage);
        append("\n");
    }

    public enum VIEW_MODE { PARAGRAPH, KEYVALUE };

    private int fontSize = 16;

    private Font[] fontBySize;

    public BoilerSuitDragonConsole(IBoilersuitApplicationContext context) {
        super(true, false);

        this.context = context;

        consolePane.setBackground(DEFAULT_CONSOLE_BACKGROUND);

        // install custom console colors
        try {
            addTextColor('a', new Color(68, 68, 68));
        } catch (TextColor.InvalidCharCodeException e) {
            e.printStackTrace();
        }

        initFont();

        new ZoomController(this, 16, 40);
    }

    public BoilerSuitDragonConsole(int width, int height, Color bgColor) {
        super(width, height, true, false);
        consolePane.setBackground(bgColor);
        initFont();
        new ZoomController(this, 16, 40);
    }

    private void initFont() {
        fontBySize = new Font[MAX_FONT_SIZE];
        for(int size = 1; size <= MAX_FONT_SIZE; size++)
            fontBySize[size - 1] = FontUtils.getFont(this, "AnonymousProRegular.ttf", size);
        setFontSize(16);
    }

    private void setFontSize(int size) {
        fontSize = size;
        setConsoleFont(fontBySize[size - 1]);
    }

    /**
     * strangely, dragon console uses Shift-Right to show previous entry (upwards) and Shift-Left for the
     * opposite. tricking as below, we can do without changing DC code:
     *
     * @param e The KeyEvent that has occurred and should be processed.
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            e.consume();
            KeyEvent manipulatedKeyEvent = new KeyEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers() | KeyEvent.SHIFT_DOWN_MASK, KeyEvent.VK_RIGHT, e.getKeyChar(), e.getKeyLocation());
            super.keyPressed(manipulatedKeyEvent);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            e.consume();
            KeyEvent manipulatedKeyEvent = new KeyEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers() | KeyEvent.SHIFT_DOWN_MASK, KeyEvent.VK_LEFT, e.getKeyChar());
            super.keyPressed(manipulatedKeyEvent);
        } else {
            super.keyPressed(e);
        }
    }

    @Override
    public void setScale(int scale) {
        setFontSize(scale);
    }

    @Override
    public Component getTargetComponent() {
        return consoleScrollPane;
    }


    @Override
    public void displayProcessingResult(ProcessingResult processingResult) {
        append(processingResult.getResultSummary());
    }

    @Override
    public void displayTableOrView(String tableOrViewName, String variableName, String sortField, Boolean sortAsc) {
        displayTableOrView(tableOrViewName, variableName, sortField, sortAsc, null, VIEW_MODE.PARAGRAPH);
    }

    @Override
    public void setRunStatus(RUN_STATUS runStatus) {
        // do nothing
    }


    public void displayRecords(java.util.List<Record> records, String tableName, String additionalComment, VIEW_MODE viewMode) {
        if(additionalComment == null) additionalComment = "";

        append(DEFAULT_SUMMARY_COLOR + "\n-- ");
        append(tableName);
        append(DEFAULT_RESULT_COLOR + "\n");

        int count = 0;
        for (Record r : records) {
            printRecord(r, viewMode);
            append("\n");
            append(HUGE_LINE_SPACER.substring(0, getApproxNumCharOnLine('-')));
            append("\n");
            count++;
        }
        append(DEFAULT_SUMMARY_COLOR + "-- ");
        append(tableName + " ");
        append(count + " shown (total: " + context.getDatabase().count(tableName) + ") (end) " + additionalComment + DEFAULT_BS_COLOR);
    }

    public void displayTableOrView(final String tableName, final String variableName, final String sortField, final Boolean sortAsc, String additionalComment, VIEW_MODE viewMode) {
        displayRecords(context.getDatabase().getAllRecordsFromTableOrView(tableName, 0, ConfigFile.getInstance(context).getInteger(ConfigFile.KEYS_NUMBER_OF_RESULT_ROWS), null, null), tableName, additionalComment, viewMode);
    }


    public void printRecord(Record r, VIEW_MODE viewMode) {
        boolean first = true;
        for (String attributeName : r.getColumnNames()) {
            if (first)
                first = false;
            else {
                if(viewMode == VIEW_MODE.PARAGRAPH)
                    append(" ");
                else
                    append("\n");
            }

            append(DEFAULT_ATTRIBUTE_COLOR + attributeName);
            if(viewMode == VIEW_MODE.PARAGRAPH)
                append(": ");
            else
                append(spacer(attributeName));

            if(viewMode == VIEW_MODE.PARAGRAPH)
                append(DEFAULT_VALUE_COLOR + r.getValue(attributeName).getValue().toString().replaceAll("&", "&&"));
            else
                append(DEFAULT_VALUE_COLOR + wordWrap(r.getValue(attributeName).getValue().toString().replaceAll("&", "&&")));
        }
    }

    private int getApproxNumCharOnLine(char forChar) {
        return (int)(consolePane.getSize().getWidth() / consolePane.getFontMetrics(consolePane.getFont()).charWidth(forChar));
    }

    private String wordWrap(String s) {
        /*
        int approxNumCharOnLine = getApproxNumCharOnLine('M');
        int approxNumCharBehindSpacer = approxNumCharOnLine - ATTRIBUTE_NAME_PLUS_SPACER_WIDTH;

        if(s.length() < approxNumCharBehindSpacer)
            return s;

        int beginChunk = 0;
        int endChunk = beginChunk + approxNumCharBehindSpacer;
        String result = "";
        while(endChunk <= s.length()) {
            String chunk = s.substring(beginChunk, endChunk);
            result += spacer(chunk) + "\n";
            endChunk += approxNumCharBehindSpacer;
            beginChunk += approxNumCharBehindSpacer;
        }

        // add remainder
        return (result + s.substring(beginChunk));
        */

        return s;
     }

    private String spacer(String attributeName) {
        return HUGE_SPACER.substring(0, Math.max(0, ATTRIBUTE_NAME_PLUS_SPACER_WIDTH - attributeName.length()));
    }

}
