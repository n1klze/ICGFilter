package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import javax.swing.*;
import java.awt.*;

public class OrderedDitheringDialog extends JPanel {
    private static final int MIN_SIZE = 2;
    private static final int DEFAULT_SIZE = 2;
    private static final int MAX_SIZE = 128;
    private static final int STEP = 1;
    private final Chooser quantsCountChooserR;
    private final Chooser quantsCountChooserG;
    private final Chooser quantsCountChooserB;

    private final JComboBox<DitheringDialog.Types> ditheringTypes;

    private enum Types {
        Silitskiy,
        Melnikov;
    }

    public OrderedDitheringDialog() {
        setPreferredSize(new Dimension(300, 120));
        setLayout(new GridLayout(4, 1));

        quantsCountChooserR = new Chooser("Red quants count:", MIN_SIZE, DEFAULT_SIZE, MAX_SIZE, STEP);
        quantsCountChooserG = new Chooser("Green quants count:", MIN_SIZE, DEFAULT_SIZE, MAX_SIZE, STEP);
        quantsCountChooserB = new Chooser("Blue quants count:", MIN_SIZE, DEFAULT_SIZE, MAX_SIZE, STEP);

        add(quantsCountChooserR);
        add(quantsCountChooserG);
        add(quantsCountChooserB);
        JPanel mode = new JPanel();
        mode.setLayout(new GridLayout(1, 2));
        mode.add(new JLabel("Dithering mode:"));
        DefaultComboBoxModel<DitheringDialog.Types> ditheringModel = new DefaultComboBoxModel<DitheringDialog.Types>();
        for (DitheringDialog.Types type : DitheringDialog.Types.values())
            ditheringModel.addElement(type);

        ditheringTypes = new JComboBox<>(ditheringModel);
        mode.add(ditheringTypes);
        add(mode);
    }

    public int getQuantsCountChooserR() {
        return quantsCountChooserR.getSlider().getValue();
    }
    public int getQuantsCountChooserG() {
        return quantsCountChooserG.getSlider().getValue();
    }
    public int getQuantsCountChooserB() {
        return quantsCountChooserB.getSlider().getValue();
    }
    public DitheringDialog.Types getDitheringType() {
        return DitheringDialog.Types.values()[ditheringTypes.getSelectedIndex()];
    }
}
