package ru.nsu.ccfit.melnikov.view.components.buttons;

import javax.swing.*;

public class IconButton extends JToggleButton {
    public IconButton(String iconPath) {
        super();
        var iconUrl = getClass().getResource(iconPath);

        if (iconUrl != null)
            setIcon(new ImageIcon(iconUrl));

        setFocusPainted(false);
    }
}
