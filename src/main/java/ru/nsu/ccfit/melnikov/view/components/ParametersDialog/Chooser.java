package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class Chooser extends JPanel {
    private final JSlider slider;

    public Chooser(String message, int minimum, int current, int maximum, int step) {
        setLayout(new GridLayout(1, 3));
        slider = new JSlider(minimum, maximum);

        var label = new JLabel(message);
        label.setIcon(null);
        add(label);

        slider.setMajorTickSpacing(step);
        slider.setPaintTicks(true);
        slider.setValue(current);
        add(slider);

        var spinnerModel = new SpinnerNumberModel(current, minimum, maximum, step);
        var spinner = new JSpinner(spinnerModel);
        add(spinner);

        slider.addChangeListener(e -> spinner.setValue(slider.getValue()));

        spinner.addChangeListener(e -> {
            if ((Integer) spinner.getValue() > maximum)
                spinner.setValue(maximum);
            slider.setValue((Integer) spinner.getValue());
        });
    }
}
