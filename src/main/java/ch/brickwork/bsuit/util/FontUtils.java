package ch.brickwork.bsuit.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by marcel on 09.10.15.
 */
public class FontUtils {
    public static Font getFont(Object whoAreYou, String fontName, float size) {
        // font
        InputStream is = whoAreYou.getClass().getResourceAsStream("/" + fontName);
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return font.deriveFont(size);
    }

    public static Font getFont(Object whoAreYou, String fontName, float size, int moveUp) {
        return getFont(whoAreYou, fontName, size).deriveFont(new AffineTransform(1, 0, 0, 1, 0, -moveUp));
    }
}
