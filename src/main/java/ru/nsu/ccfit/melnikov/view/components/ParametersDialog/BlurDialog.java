package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import javax.swing.*;
import java.awt.*;

public class BlurDialog extends JPanel{
    private static final int MIN_SIZE = 3;
    private static final int DEFAULT_SIZE = 5;

    private static final int MAX_SIZE = 11;
    private static final int STEP = 2;
    private final Chooser maskSizeChooser;

    public BlurDialog() {
        setPreferredSize(new Dimension(200, 40));
        setLayout(new GridLayout(1, 1));

        maskSizeChooser = new Chooser("Mask size:", MIN_SIZE, DEFAULT_SIZE, MAX_SIZE, STEP);

        add(maskSizeChooser);
    }

    public int getMaskSize() {
        return maskSizeChooser.getSlider().getValue();
    }

}
