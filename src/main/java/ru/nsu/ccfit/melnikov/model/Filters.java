package ru.nsu.ccfit.melnikov.model;

import lombok.Getter;

public enum Filters {
    ROTATION("Rotation", "R"),
    FLOYD_STEINBERG_DITHERING("Floyd-Steinberg dithering", "FSD"),
    ORDERED_DITHERING("Ordered dithering", "OD"),
    BLUR("Blur", "B"),
    GRAYSCALE("Grayscale", "GS"),
    WATERCOLOR("Watercolor effect", "WE"),
    ZOOM("Zoom", "Z"),
    NORMAL_MAP("Normal mapping", "N"),
    TWIRL("Twirl", "T"),
    EMBOSSING("Embossing", "E"),
    SHARPNESS("Sharpness", "S"),
    SOBEL("Sobel operator", "So"),
    ROBERTS("Roberts operator", "Ro"),
    GAMMA("Gamma correction", "G"),
    INVERSE("Inverse", "I");

    private final String name;
    @Getter
    private final String pict;

    Filters(String name, String pict) {
        this.name = name;
        this.pict = pict;
    }

    @Override
    public String toString() {
        return name;
    }
}
