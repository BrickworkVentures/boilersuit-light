package ch.brickwork.bsuit.control;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage the widths of colunmns in a table.
 * <p/>
 * Various properties control how the width of the column is calculated.
 * Another property controls whether column width calculation should be dynamic.
 * Finally, various Actions will be added to the table to allow the user
 * to customize the functionality.
 * <p/>
 * This class was designed to be used with tables that use an auto resize mode
 * of AUTO_RESIZE_OFF. With all other modes you are constrained as the width
 * of the columns must fit inside the table. So if you increase one column, one
 * or more of the other columns must decrease. Because of this the resize mode
 * of RESIZE_ALL_COLUMNS will work the best.
 * <p/>
 * Probably author Rob Camick. Taken from http://tips4java.wordpress.com/2008/11/10/table-column-adjuster/
 */
public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {

    private final Map<TableColumn, Integer> columnSizes = new HashMap<TableColumn, Integer>();

    private final int spacing;

    private final JTable table;

    private boolean isColumnDataIncluded;

    private boolean isColumnHeaderIncluded;

    private boolean isDynamicAdjustment;

    private boolean isOnlyAdjustLarger;

    /**
     * Specify the table and use default spacing
     */
    public TableColumnAdjuster(final JTable table)
    {
        this(table, 6);
    }

    /**
     * Specify the table and spacing
     */
    private TableColumnAdjuster(final JTable table, final int spacing)
    {
        this.table = table;
        this.spacing = spacing;
        setColumnHeaderIncluded(true);
        setColumnDataIncluded(true);
        setOnlyAdjustLarger(true);
        setDynamicAdjustment(false);
        installActions();
    }

    /**
     * Adjust the widths of all the columns in the table
     */
    public void adjustColumns()
    {
        final TableColumnModel tcm = table.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            adjustColumn(i);
        }
    }

    public void propertyChange(final PropertyChangeEvent e)
    {
        //  When the TableModel changes we need to update the listeners
        //  and column widths

        if ("model".equals(e.getPropertyName())) {
            TableModel model = (TableModel) e.getOldValue();
            model.removeTableModelListener(this);

            model = (TableModel) e.getNewValue();
            model.addTableModelListener(this);
            adjustColumns();
        }
    }

    public void tableChanged(TableModelEvent e)
    {
        if (!isColumnDataIncluded) {
            return;
        }

        //  A cell has been updated

        if (e.getType() == TableModelEvent.UPDATE) {
            int column = table.convertColumnIndexToView(e.getColumn());

            //  Only need to worry about an increase in width for this cell

            if (isOnlyAdjustLarger) {
                int row = e.getFirstRow();
                final TableColumn tableColumn = table.getColumnModel().getColumn(column);

                if (tableColumn.getResizable()) {
                    int width = getCellDataWidth(row, column);
                    updateTableColumn(column, width);
                }
            }

            //	Could be an increase of decrease so check all rows
            else {
                adjustColumn(column);
            }
        }

        //  The update affected more than one column so adjust all columns
        else {
            adjustColumns();
        }
    }

    /**
     * Adjust the width of the specified column in the table
     */
    private void adjustColumn(final int column)
    {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if (!tableColumn.getResizable()) {
            return;
        }

        int columnHeaderWidth = getColumnHeaderWidth(column);
        int columnDataWidth = getColumnDataWidth(column);
        int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

        updateTableColumn(column, preferredWidth);
    }

    /**
     * Get the preferred width for the specified cell
     */
    private int getCellDataWidth(int row, int column)
    {
        //  Inovke the renderer for the cell to calculate the preferred width

        final TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
        final Component c = table.prepareRenderer(cellRenderer, row, column);

        return c.getPreferredSize().width + table.getIntercellSpacing().width;
    }

    /**
     * Calculate the width based on the widest cell renderer for the
     * given column.
     */
    private int getColumnDataWidth(int column)
    {
        if (!isColumnDataIncluded) {
            return 0;
        }

        int preferredWidth = 0;
        int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();

        for (int row = 0; row < table.getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));

            //  We've exceeded the maximum width, no need to check other rows

            if (preferredWidth >= maxWidth) {
                break;
            }
        }

        return preferredWidth;
    }

    /**
     * Calculated the width based on the column name
     */
    private int getColumnHeaderWidth(int column)
    {
        if (!isColumnHeaderIncluded) {
            return 0;
        }

        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        final Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();

        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        final Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1, column);
        return c.getPreferredSize().width;
    }

    /**
     * Install Actions to give user control of certain functionality.
     */
    private void installActions()
    {
        installColumnAction(true, true, "adjustColumn", "control ADD");
        installColumnAction(false, true, "adjustColumns", "control shift ADD");
        installColumnAction(true, false, "restoreColumn", "control SUBTRACT");
        installColumnAction(false, false, "restoreColumns", "control shift SUBTRACT");
        installToggleAction(true, false, "toggleDynamic", "control MULTIPLY");
        installToggleAction(false, true, "toggleLarger", "control DIVIDE");
    }

    /**
     * Update the input and action maps with a new ColumnAction
     */
    private void installColumnAction(boolean isSelectedColumn, boolean isAdjust, String key, String keyStroke)
    {
        final Action action = new ColumnAction(isSelectedColumn, isAdjust);
        final KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        table.getInputMap().put(ks, key);
        table.getActionMap().put(key, action);
    }

    /**
     * Update the input and action maps with new ToggleAction
     */
    private void installToggleAction(boolean isToggleDynamic, boolean isToggleLarger, String key, String keyStroke)
    {
        final Action action = new ToggleAction(isToggleDynamic, isToggleLarger);
        final KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        table.getInputMap().put(ks, key);
        table.getActionMap().put(key, action);
    }

    /**
     * Restore the width of the specified column to its previous width
     */
    private void restoreColumn(int column)
    {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        final Integer width = columnSizes.get(tableColumn);
        if (width != null) {
            table.getTableHeader().setResizingColumn(tableColumn);
            tableColumn.setWidth(width);
        }
    }

    /**
     * Restore the widths of the columns in the table to its previous width
     */
    private void restoreColumns()
    {
        final TableColumnModel tcm = table.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            restoreColumn(i);
        }
    }

    /**
     * Indicates whether to include the model data in the width calculation
     */
    private void setColumnDataIncluded(boolean isColumnDataIncluded)
    {
        this.isColumnDataIncluded = isColumnDataIncluded;
    }

    /**
     * Indicates whether to include the header in the width calculation
     */
    private void setColumnHeaderIncluded(boolean isColumnHeaderIncluded)
    {
        this.isColumnHeaderIncluded = isColumnHeaderIncluded;
    }

    /**
     * Indicate whether changes to the model should cause the width to be
     * dynamically recalculated.
     */
    private void setDynamicAdjustment(boolean isDynamicAdjustment)
    {
        //  May need to add or remove the TableModelListener when changed

        if (this.isDynamicAdjustment != isDynamicAdjustment) {
            if (isDynamicAdjustment) {
                table.addPropertyChangeListener(this);
                table.getModel().addTableModelListener(this);
            } else {
                table.removePropertyChangeListener(this);
                table.getModel().removeTableModelListener(this);
            }
        }

        this.isDynamicAdjustment = isDynamicAdjustment;
    }

    /**
     * Indicates whether columns can only be increased in size
     */
    private void setOnlyAdjustLarger(boolean isOnlyAdjustLarger)
    {
        this.isOnlyAdjustLarger = isOnlyAdjustLarger;
    }

    /**
     * Update the TableColumn with the newly calculated width
     */
    private void updateTableColumn(int column, int width)
    {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if (!tableColumn.getResizable()) {
            return;
        }

        width += spacing;

        //  Don't shrink the column width

        if (isOnlyAdjustLarger) {
            width = Math.max(width, tableColumn.getPreferredWidth());
        }

        columnSizes.put(tableColumn, tableColumn.getWidth());
        table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(width);
    }

    /**
     * Action to adjust or restore the width of a single column or all columns
     */
    private class ColumnAction extends AbstractAction {

        private final boolean isAdjust;

        private final boolean isSelectedColumn;

        public ColumnAction(boolean isSelectedColumn, boolean isAdjust)
        {
            this.isSelectedColumn = isSelectedColumn;
            this.isAdjust = isAdjust;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            //  Handle selected column(s) width change actions

            if (isSelectedColumn) {
                final int[] columns = table.getSelectedColumns();

                for (int column : columns) {
                    if (isAdjust) {
                        adjustColumn(column);
                    } else {
                        restoreColumn(column);
                    }
                }
            } else {
                if (isAdjust) {
                    adjustColumns();
                } else {
                    restoreColumns();
                }
            }
        }
    }

    /**
     * Toggle properties of the TableColumnAdjuster so the user can
     * customize the functionality to their preferences
     */
    private class ToggleAction extends AbstractAction {

        private final boolean isToggleDynamic;

        private final boolean isToggleLarger;

        public ToggleAction(boolean isToggleDynamic, boolean isToggleLarger)
        {
            this.isToggleDynamic = isToggleDynamic;
            this.isToggleLarger = isToggleLarger;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (isToggleDynamic) {
                setDynamicAdjustment(!isDynamicAdjustment);
                return;
            }

            if (isToggleLarger) {
                setOnlyAdjustLarger(!isOnlyAdjustLarger);
            }
        }
    }
}