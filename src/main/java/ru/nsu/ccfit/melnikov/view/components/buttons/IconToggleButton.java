package ru.nsu.ccfit.melnikov.view.components.buttons;

import javax.swing.*;
import java.awt.*;

public class IconToggleButton extends JToggleButton {
    public IconToggleButton(String iconPath) {
        super();
        var iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            Image image = icon.getImage().getScaledInstance(19, 19, Image.SCALE_SMOOTH);
            icon = new ImageIcon(image, icon.getDescription());
            setIcon(icon);
        }

        setFocusPainted(false);
    }

}
