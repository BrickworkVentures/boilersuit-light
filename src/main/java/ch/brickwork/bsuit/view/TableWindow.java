package ch.brickwork.bsuit.view;


import ch.brickwork.bsuit.control.IZoomable;
import ch.brickwork.bsuit.control.ZoomController;
import ch.brickwork.bsuit.util.FontUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
  * table shown under the table result tab
  */
public class TableWindow extends JPanel implements IZoomable {

    public static final Color TABLE_BACKGROUND_COLOR = BoilerSuitDragonConsole.DEFAULT_CONSOLE_BACKGROUND;

    public static final Color TABLE_FOREGROUND_COLOR = new Color(0, 150, 0);

    private static final String CELL_BREAK = "\t";

    private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

    private static final String COPY_ACTION_KEY = "COPY";

    private static final String LINE_BREAK = System.lineSeparator();

    private static final int ROW_HEIGHT = 25;

    private static final float ROW_HEIGHT_EXTRA_FACTOR = 1.2f;

    private static final int INITIAL_FONT_SIZE = 14;

    private static final String TABLE_FONT = "AnonymousProRegular.ttf";

    private JTable table;

    private JScrollPane scrollPane;

    private float rowHeightPerScaleUnit;

    private int previousScale;

    public TableWindow(TableModel tableModel)
    {
        super();
        setLayout(new BorderLayout());
        initTable(tableModel);

        add(scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);

        initZoomer();
        initCopyAction();

        setVisible(true);
    }

    public JTable getTable() {
        return table;
    }

    @Override
    public void setScale(int scale) {
        setFontSize(scale);
        previousScale = scale;
    }

    @Override
    public Component getTargetComponent() {
        return scrollPane;
    }

    /**
     * @TODO: No idea why we implemented this. Check whether this was for compatibility reasons. On Linux, it works without
     */
    private void initCopyAction() {
        // initCopyRowAction();
        //initCopyCellAction();
    }

    private void initCopyCellAction() {
        final JTextField textField = new JTextField();

        textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK), COPY_ACTION_KEY);
        final Action copyCellAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e)
            {
                final StringSelection data = new StringSelection(textField.getSelectedText());
                CLIPBOARD.setContents(data, data);
            }
        };

        textField.getActionMap().put(COPY_ACTION_KEY, copyCellAction);
        table.setDefaultEditor(Object.class, new DefaultCellEditor(textField) {
            @Override
            public Object getCellEditorValue()
            {
                return super.getCellEditorValue();
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
            {
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        });
    }

    private void initCopyRowAction() {
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK), COPY_ACTION_KEY);
        final Action copyRowAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e)
            {
                copyToClipboard();
            }
        };
        table.getActionMap().put(COPY_ACTION_KEY, copyRowAction);
    }

    private void initZoomer() {
        rowHeightPerScaleUnit = table.getRowHeight() / INITIAL_FONT_SIZE;
        previousScale = INITIAL_FONT_SIZE;
        new ZoomController(this, INITIAL_FONT_SIZE, 40);
    }

    private void initTable(TableModel tableModel) {
        table = new JTable(tableModel);
        table.setBackground(TABLE_BACKGROUND_COLOR);
        table.setForeground(TABLE_FOREGROUND_COLOR);
        table.setFont(FontUtils.getFont(this, TABLE_FONT, INITIAL_FONT_SIZE));
        table.setRowHeight(ROW_HEIGHT);
        table.setVisible(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    private void copyToClipboard()
    {
        int numCols = table.getColumnCount();
        int[] selectedRows = table.getSelectedRows();
        StringBuilder excelStr = new StringBuilder();
        for (int i : selectedRows) {
            for (int j = 0; j < numCols; j++) {
                excelStr.append(escape(table.getValueAt(i, j)));
                if (j < numCols - 1) {
                    excelStr.append(CELL_BREAK);
                }
            }
            excelStr.append(LINE_BREAK);
        }

        StringSelection sel = new StringSelection(excelStr.toString());
        CLIPBOARD.setContents(sel, sel);
    }

    private String escape(Object cell) {
        return (cell == null ? "" : cell.toString().replace(LINE_BREAK, " ").replace(CELL_BREAK, " "));
    }

    private void setFontSize(int scale) {
        table.setFont(FontUtils.getFont(this, "AnonymousProRegular.ttf", scale));
        table.setRowHeight((int) (scale * rowHeightPerScaleUnit * ROW_HEIGHT_EXTRA_FACTOR));
        for(int col = 0; col < table.getColumnCount(); col ++) {
            int currentWidth = table.getColumnModel().getColumn(col).getWidth();
            table.getColumnModel().getColumn(col).setWidth((int)((float) currentWidth * ((float) scale / (float) previousScale)));
        }
        table.invalidate();
        table.repaint();
    }
}
