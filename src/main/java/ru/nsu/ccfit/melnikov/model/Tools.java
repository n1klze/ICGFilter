package ru.nsu.ccfit.melnikov.model;

import lombok.Getter;

@Getter
public enum Tools {
    CURSOR("/icon-cursor.png"),
    ERASER("/eraser.png"),
    PEN("/pen.png"),
    LINE("/line.png"),
    FILL("/fill.png"),
    POLYGON("/polygon.png"),
    STAR("/star.png"),
    PALETTE("/palette.png");

    private final String iconPath;

    Tools(String iconPath) {
        this.iconPath = iconPath;
    }
}
