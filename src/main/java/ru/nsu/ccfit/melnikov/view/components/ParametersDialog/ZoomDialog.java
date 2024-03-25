package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import javax.swing.*;
import java.awt.*;

public class ZoomDialog extends JPanel {
    private static final int MIN_SIZE = 2;
    private static final int DEFAULT_SIZE = 2;
    private static final int MAX_SIZE = 5;
    private static final int STEP = 1;
    private final Chooser zoomSizeChooser;

    public ZoomDialog() {
        setPreferredSize(new Dimension(200, 40));
        setLayout(new GridLayout(1, 1));

        zoomSizeChooser = new Chooser("Zoom size:", MIN_SIZE, DEFAULT_SIZE, MAX_SIZE, STEP);

        add(zoomSizeChooser);
    }

    public int getZoomSize() {
        return zoomSizeChooser.getSlider().getValue();
    }
}
