package ch.brickwork.bsuit.control;


import ch.brickwork.bsuit.database.Value;
import ch.brickwork.bsuit.database.Record;
import ch.brickwork.bsuit.globals.BoilerSuitGlobals;
import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;
import ch.brickwork.bsuit.interpreter.interpreters.ProcessingResult;
import ch.brickwork.bsuit.view.IProcessingResultDisplay;
import ch.brickwork.bsuit.view.TableWindow;



import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Display the processing results in {@link ch.brickwork.bsuit.view.TableWindow} component.
 */
public class TableWindowController implements IProcessingResultDisplay {

    private static final Logger LOG = Logger.getLogger(TableWindowController.class.getCanonicalName());

    /**
     * maximal count of records that will be shown in the display table. if more than this amount, message is shown
     * within the display table
     */
    private static final int MAX_COUNT_TO_DISPLAY = 50000;

    private static final int SAMPLE_SIZE_IF_TOO_BIG_TO_DISPLAY = 1000;

    private final DefaultTableModel model;

    private final TableWindow tableWindow;

    private final IBoilersuitApplicationContext context;

    private boolean sortAsc = true;

    private String sortField;

    private String tableOrViewName;

    private String variableName;

    public TableWindowController(final TableWindow tableWindow, final DefaultTableModel model, final IBoilersuitApplicationContext context)
    {
        this.tableWindow = tableWindow;
        this.model = model;
        this.context = context;

        final JTable table = tableWindow.getTable();

        final JTableHeader header = table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new ColumnListener(table));
        header.setReorderingAllowed(true);

        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
    }

    private static List<Record> createDummyTable(final String resultText)
    {
        final List<Record> result = new ArrayList<Record>();
        final Record dummyRecord = new Record();
        dummyRecord.put("Result", resultText);
        result.add(dummyRecord);
        return result;
    }

    private static List<Record> createDummyTable(final List<ProcessingResult> processingResults)
    {
        final List<Record> result = new ArrayList<Record>();
        LOG.info("processingResults has " + processingResults.size() + " elements.");
        for (final ProcessingResult pr : processingResults) {
            LOG.info("Element: " + pr + ((pr == null) ? "null!" : pr.getResultSummary()));
            final Record dummyRecord = new Record();
            if (pr != null) {
                dummyRecord.put("Result", pr.getResultSummary());
            }
            result.add(dummyRecord);
        }
        return result;
    }



    @Override
    public void displayProcessingResult(ProcessingResult processingResult)
    {
        // if only one result, use single-command-display:
        if (!processingResult.getType().equals(ProcessingResult.ResultType.COMPOSITE)) {
            displaySingleProcessingResult(processingResult);
        } else {
            final List<ProcessingResult> subResults = processingResult.getSubResults();
            if (subResults != null) {
                displayResults(createDummyTable(subResults), "Result");
            }
        }
    }


    @Override
    public void displayTableOrView(final String tableOrViewName, final String variableName, final String sortField,
                                   final Boolean sortAsc)
    {
        this.sortField = sortField;

        List<Record> result;

        if (context.getDatabase().existsTableOrView(tableOrViewName)) {

            boolean onlySample = false;
            final long count = context.getDatabase().count(tableOrViewName);
            if (count > MAX_COUNT_TO_DISPLAY) {
                result = context.getDatabase().getAllRecordsFromTableOrView(tableOrViewName, 0, SAMPLE_SIZE_IF_TOO_BIG_TO_DISPLAY, sortField, sortAsc);
                onlySample = true;
            } else {
                result = context.getDatabase().getAllRecordsFromTableOrView(tableOrViewName, sortField, sortAsc);
            }

            if (null != result && result.size() == 0) {
                context.getLog().info("No results");
            } else {
                context.getLog().info("Output Table updated" + (onlySample ? (" - show only " + SAMPLE_SIZE_IF_TOO_BIG_TO_DISPLAY + " of " + count) : ""));
            }
        } else {
            result = createDummyTable("Unknown table " + tableOrViewName);
        }
        if (result != null) {
            if (null == variableName) {
                displayResults(result, "");
            } else {
                displayResults(result, variableName + " (in context.getDatabase(): " + tableOrViewName + ")");
            }
        }

        this.tableOrViewName = tableOrViewName;
        this.variableName = variableName;
    }

    @Override
    public void setRunStatus(RUN_STATUS runStatus) {
        // do nothing
    }

    private void displayMessage(final String message)
    {
        displayResults(createDummyTable(message), "Result");
    }

    private void displayResults(final List<Record> results, final String tableOrViewName)
    {
        model.setRowCount(0);
        model.setColumnCount(0);

        if (results.size() > 0) {
            // init column names
            final Record firstLine = results.get(0);
            for (final Value v : firstLine) {
                model.addColumn(v.getAttributeName());
            }

            // rows
            for (final Record rowRecord : results) {
                final Object[] row = new Object[model.getColumnCount()];
                for (int col = 0; col < model.getColumnCount(); col++) {
                    row[col] = rowRecord.getValue(model.getColumnName(col)).getValue();
                }
                model.addRow(row);
            }
        }

        tableWindow.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnAdjuster tca = new TableColumnAdjuster(tableWindow.getTable());
        tca.adjustColumns();

        this.tableOrViewName = tableOrViewName;
    }

    private void displaySingleProcessingResult(final ProcessingResult processingResult)
    {
        switch (processingResult.getType()) {
            case MESSAGE:
            case SYNTAX_ERROR:
            case FATAL_ERROR:
                displayMessage(processingResult.getResultSummary());
                break;

            case TABLE:
            case VIEW:
                displayTableOrView(processingResult.getResultSummary(), processingResult.getResultSummary(), null, null);
        }
    }

    /**
     * Sort columns by click on them
     */
    private class ColumnListener extends MouseAdapter {

        private final JTable table;

        public ColumnListener(final JTable table)
        {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            final TableColumnModel colModel = table.getColumnModel();
            final int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
            final int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();

            if (modelIndex < 0) {
                return;
            }

            sortField = colModel.getColumn(modelIndex).getHeaderValue().toString();
            sortAsc = !sortAsc;
            displayTableOrView(tableOrViewName, variableName, context.getDatabase().sanitizeName(sortField), sortAsc);
        }
    }

    /**
     * Set column sort icons
     */
    private class HeaderRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
        {

            String title = (String) value;
            setText(title);
            setBorder(BorderFactory.createRaisedSoftBevelBorder());
            if (null != title && null != sortField && title.equals(sortField.replaceAll("\"", ""))) {
                if (sortAsc) {
                    setIcon(UIManager.getIcon("Table.ascendingSortIcon"));
                } else {
                    setIcon(UIManager.getIcon("Table.descendingSortIcon"));
                }
            } else {
                setIcon(null);
            }

            return this;

        }
    }
}
