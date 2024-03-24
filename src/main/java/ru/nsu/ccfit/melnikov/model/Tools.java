package ru.nsu.ccfit.melnikov.model;

import lombok.Getter;

@Getter
public enum Tools {
    CURSOR(""),
    ERASER("/eraser.png"),
    PEN("/pen.png"),
    LINE("/line.png"),
    FILL("/fill.png"),
    POLYGON("/polygon.png"),
    STAR("/star.png"),
    PALETTE("/palette.png"),
    DITHER_AS("");

    private final String iconPath;

    Tools(String iconPath) {
        this.iconPath = iconPath;
    }
}
