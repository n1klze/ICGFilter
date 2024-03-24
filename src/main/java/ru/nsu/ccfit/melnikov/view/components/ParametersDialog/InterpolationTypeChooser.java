package ru.nsu.ccfit.melnikov.view.components.ParametersDialog;

import lombok.Getter;

import javax.swing.*;
import java.awt.image.AffineTransformOp;

public class InterpolationTypeChooser extends JPanel {
    private final JComboBox<Types> cb;

    private enum Types {
        TYPE_BILINEAR("Bilinear", AffineTransformOp.TYPE_BILINEAR),
        TYPE_BICUBIC("Bicubic", AffineTransformOp.TYPE_BICUBIC),
        TYPE_NEAREST_NEIGHBOR("Nearest neighbour", AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        private final String methodName;
        @Getter
        private final int type;

        Types(String methodName, int type) {
            this.methodName = methodName;
            this.type = type;
        }

        @Override
        public String toString() {
            return methodName;
        }
    }

    public InterpolationTypeChooser() {
        var label = new JLabel("Interpolation method:");
        label.setIcon(null);
        add(label);

        var cbModel = new DefaultComboBoxModel<Types>();
        for (Types type : Types.values())
            cbModel.addElement(type);

        cb = new JComboBox<>(cbModel);
        cbModel.setSelectedItem(Types.TYPE_BILINEAR);

        add(cb);
    }

    public int getCurrentType() {
        return Types.values()[cb.getSelectedIndex()].type;
    }
}
