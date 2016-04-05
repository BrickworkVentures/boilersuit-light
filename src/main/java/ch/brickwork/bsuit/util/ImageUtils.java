package ch.brickwork.bsuit.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by marcel on 15.10.15.
 */
public class ImageUtils {
    public static BufferedImage loadImage(Object whoOrders, String name) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(
                    whoOrders.getClass().getResource("/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
