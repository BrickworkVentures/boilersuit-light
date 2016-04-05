package ch.brickwork.bsuit.control;

import java.awt.*;

/**
 * Created by marcel on 09.10.15.
 */
public interface IZoomable {
    void setScale(int scale);
    Component getTargetComponent();
}
