package ru.nsu.ccfit.melnikov.model;

import lombok.Getter;

public enum Filters {
    ROTATION("Rotation", "/icon-rotate.png"),
    FLOYD_STEINBERG_DITHERING("Floyd-Steinberg dithering", "/icon-floydsteinbergdithering.png"),
    ORDERED_DITHERING("Ordered dithering", "/icon-orderlydithering.png"),
    BLUR("Blur", "/icon-gausssmoothing.png"),
    GRAYSCALE("Grayscale", "/icon-whiteandblack.png"),
    WATERCOLOR("Watercolor effect", "/icon-aquarealization.png"),
    ZOOM("Zoom", "/icon-changeviewmode.png"),
    NORMAL_MAP("Normal mapping", "/icon-normalmap.png"),
    TWIRL("Twirl", "T"),
    EMBOSSING("Embossing", "/icon-embossing.png"),
    SHARPNESS("Sharpness", "/icon-sharpnessincrease.png"),
    SOBEL("Sobel operator", "/icon-sobeloperator.png"),
    ROBERTS("Roberts operator", "/icon-robertsoperator.png"),
    GAMMA("Gamma correction", "/icon-gammacorrection.png"),
    INVERSE("Inverse", "/icon-negative.png");

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
