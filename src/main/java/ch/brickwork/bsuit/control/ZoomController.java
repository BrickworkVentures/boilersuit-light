package ch.brickwork.bsuit.control;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created by marcel on 09.10.15.
 */
public class ZoomController {

    private final IZoomable zoomable;
    private int scale;
    private int maxScale;

    public ZoomController(IZoomable zoomable, int initialScale, final int maxScale) {
        this.zoomable = zoomable;
        scale = initialScale;
        this.maxScale = maxScale;

        initMouseWheelListener();
    }

    private void initMouseWheelListener() {
        final MouseWheelListener[] defaultMouseWheelListeners = zoomable.getTargetComponent().getMouseWheelListeners();
        for(MouseWheelListener mwl : defaultMouseWheelListeners)
            zoomable.getTargetComponent().removeMouseWheelListener(mwl);

        zoomable.getTargetComponent().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    if (scale - e.getWheelRotation() <= maxScale && scale - e.getWheelRotation() > 0) {
                        scale -= e.getWheelRotation();
                        zoomable.setScale(scale);
                        zoomable.getTargetComponent().invalidate();
                        zoomable.getTargetComponent().repaint();
                    }
                } else {
                    for (MouseWheelListener mwl : defaultMouseWheelListeners) {
                            mwl.mouseWheelMoved(e);
                    }
                }
            }
        });
    }
}
