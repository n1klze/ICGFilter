package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import javax.swing.*;
import java.awt.*;

public class GammaDialog extends JPanel {
    private static final int MIN_SIZE = 10;
    private static final int DEFAULT_SIZE = 50;
    private static final int MAX_SIZE = 1000;
    private static final int STEP = 10;
    private final Chooser gammaSizeChooser;

    public GammaDialog() {
        setPreferredSize(new Dimension(200, 40));
        setLayout(new GridLayout(1, 1));

        gammaSizeChooser = new Chooser("Gamma:", MIN_SIZE, DEFAULT_SIZE, MAX_SIZE, STEP);

        add(gammaSizeChooser);
    }

    public float getGamma() {
        return gammaSizeChooser.getSlider().getValue() / 100.0f;
    }
}
