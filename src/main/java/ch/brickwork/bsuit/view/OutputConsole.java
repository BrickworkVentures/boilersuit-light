package ch.brickwork.bsuit.view;

import ch.brickwork.bsuit.util.ILog;
import com.eleet.dragonconsole.DragonConsole;
import com.eleet.dragonconsole.util.TextColor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcelcamporelli on 14.9.2015.
 */
public class OutputConsole extends JPanel implements ILog {

    public final static Color OUTPUT_CONSOLE_BACKGROUND = new Color(58, 58 ,58);

    public static final String DEFAULT_BS_COLOR = "&da";

    public static final String DEFAULT_WARNING_COLOR = "&ra";
    public static final String DEFAULT_INFO_COLOR = "&xa";

    private DragonConsole console;

    public OutputConsole(Font consoleFont) {
        setLayout(new BorderLayout());
        add(new JScrollPane(initConsole(consoleFont)), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel initConsole(Font consoleFont) {
        console = new BoilerSuitDragonConsole(1000, 200, OUTPUT_CONSOLE_BACKGROUND);
        console.setConsoleFont(consoleFont.deriveFont(12f));
        console.setBackground(new Color(77, 77, 77));

        // install custom console colors
        try {
            console.addTextColor('a', new Color(57, 57, 57));
        } catch (TextColor.InvalidCharCodeException e) {
            e.printStackTrace();
        }
        console.setVisible(true);
        return console;
    }

    public void err(String s) {
        console.append("\n" + DEFAULT_WARNING_COLOR + s + DEFAULT_BS_COLOR + "\n");
    }

    public void info(final String s) {
        console.append("\n" + DEFAULT_INFO_COLOR + s + DEFAULT_BS_COLOR);
    }

    public void log(String s) {
        info(s);
    }

    public void warn(String s) {
        console.append("\n" + DEFAULT_WARNING_COLOR + s + DEFAULT_BS_COLOR);
    }
}
