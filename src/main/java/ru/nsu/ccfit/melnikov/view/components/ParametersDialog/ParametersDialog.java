package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import ru.nsu.ccfit.melnikov.controller.Controller;

import javax.swing.*;
import java.awt.*;

public class ParametersDialog extends JPanel {
    private static final int MIN_THICKNESS = 1;
    private static final int MAX_THICKNESS = 20;
    private static final int MIN_ANGLES = 3;
    private static final int MAX_ANGLES = 16;
    private static final int MIN_RADIUS = 50;
    private static final int MAX_RADIUS = 100;
    private static final int MIN_ROTATION = 0;
    private static final int MAX_ROTATION = 360;
    private static final int STEP_SIZE = 1;
    private final Chooser thicknessChooser;
    private final Chooser anglesChooser;
    private final Chooser radiusChooser;
    private final Chooser rotationChooser;
    private final InterpolationTypeChooser interpolationTypeChooser;

    public ParametersDialog(Controller controller) {
        setPreferredSize(new Dimension(360, 150));
        setLayout(new GridLayout(5, 1));

        thicknessChooser = new Chooser("Line thickness:", MIN_THICKNESS, controller.getThickness(), MAX_THICKNESS, STEP_SIZE);
        anglesChooser = new Chooser("Number of angles:", MIN_ANGLES, controller.getNumOfAngles(), MAX_ANGLES, STEP_SIZE);
        radiusChooser = new Chooser("Radius:", MIN_RADIUS, controller.getRadius(), MAX_RADIUS, STEP_SIZE);
        rotationChooser = new Chooser("Rotation angle:", MIN_ROTATION, controller.getRotation(), MAX_ROTATION, STEP_SIZE);

        interpolationTypeChooser = new InterpolationTypeChooser();

        add(thicknessChooser);
        add(anglesChooser);
        add(radiusChooser);
        add(rotationChooser);
        add(interpolationTypeChooser);
    }

    public int getThickness() {
        return thicknessChooser.getSlider().getValue();
    }

    public int getNumOfAngles() {
        return anglesChooser.getSlider().getValue();
    }

    public int getRadius() {
        return radiusChooser.getSlider().getValue();
    }

    public int getRotation() {
        return rotationChooser.getSlider().getValue();
    }

    public int getInterpolationTypeChooser() {
        return interpolationTypeChooser.getCurrentType();
    }
}
