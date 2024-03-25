package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import javax.swing.*;
import java.awt.*;

public class AngleDialog extends JPanel {
    private static final int MIN_ANGLE = -360;
    private static final int MAX_ANGLE = 360;
    private static final int STEP =  1;
    private final Chooser angleChooser;

    public AngleDialog() {
        setPreferredSize(new Dimension(350, 40));
        setLayout(new GridLayout(1, 1));
        angleChooser = new Chooser("Angle:", MIN_ANGLE, 0, MAX_ANGLE, STEP);

        add(angleChooser, BorderLayout.CENTER);
    }

    public int getAngle() {
        return angleChooser.getSlider().getValue();
    }
}
