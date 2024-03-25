package ru.nsu.ccfit.melnikov.view.components.buttons;

import ru.nsu.ccfit.melnikov.controller.Controller;
import ru.nsu.ccfit.melnikov.model.Tools;

import javax.swing.*;
import java.util.Map;

public class ToolButton extends IconToggleButton {
    public ToolButton(Controller controller, Tools tool, Map<Tools, ToolButton> toolBarButtons, Map<Tools, JRadioButtonMenuItem> viewMenuToolButtons) {
        super(tool.getIconPath());
        addActionListener(e -> controller.setCurrentTool(tool, toolBarButtons, viewMenuToolButtons));
    }
}
