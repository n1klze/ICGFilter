package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;


import javax.swing.*;
import java.awt.*;

public class ResizeDialog extends JPanel {
    private static final int MIN_SIZE = 12;
    private static final int MAX_SIZE = 10000;
    private static final int STEP = 1;
    private final JSpinner widthField;
    private final JSpinner heightField;

    public ResizeDialog() {
        setPreferredSize(new Dimension(200, 80));
        setLayout(new GridLayout(3, 1));

        SpinnerNumberModel widthModel = new SpinnerNumberModel(640, MIN_SIZE, MAX_SIZE, STEP);
        widthField = new JSpinner(widthModel);

        SpinnerNumberModel heightModel = new SpinnerNumberModel(480, MIN_SIZE, MAX_SIZE, STEP);
        heightField = new JSpinner(heightModel);

        add(new JLabel("Width: "));
        add(widthField);
        add(new JLabel("Height:"));
        add(heightField);
    }

    public int getWidth() {
        return (int) widthField.getValue();
    }

    public int getHeight() {
        return (int) heightField.getValue();
    }
}
