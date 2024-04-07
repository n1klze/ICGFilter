package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import javax.swing.*;
import java.awt.*;

public class BorderDialog extends JPanel {
    private static final int MIN_SIZE = 0;
    private static final int DEFAULT_SIZE = 50;
    private static final int MAX_SIZE = 255;
    private static final int STEP = 1;
    private final Chooser thresholdChooser;

    public BorderDialog() {
        setPreferredSize(new Dimension(200, 40));
        setLayout(new GridLayout(1, 1));

        thresholdChooser = new Chooser("Threshold:", MIN_SIZE, DEFAULT_SIZE, MAX_SIZE, STEP);

        add(thresholdChooser);
    }

    public int getThreshold() {
        return thresholdChooser.getSlider().getValue();
    }
}
