package ru.nsu.ccfit.melnikov.view.components.buttons;

import ru.nsu.ccfit.melnikov.controller.Controller;

import javax.swing.*;
import java.awt.*;

public class ColoredButton extends JButton {
    public ColoredButton(Controller controller, Color color) {
        super("     ");

        setBackground(color);
        setFocusPainted(false);
        addActionListener(e -> controller.setCurrentColor(color));
    }
}
