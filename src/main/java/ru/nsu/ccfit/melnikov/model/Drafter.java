package ru.nsu.ccfit.melnikov.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Performs actual component drawing
 *
 * @author Nikita Melnikov
 */
public final class Drafter {
    /**
     * Draws a line of the specified color
     *
     * @param image drawing area
     * @param color specified color
     * @param x0    the x coordinate of the starting point
     * @param y0    the y coordinate of the starting point
     * @param x1    the x coordinate of the end point
     * @param y1    the y coordinate of the end point
     */
    public static void drawLine(BufferedImage image, Color color, int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int error = dx + dy;

        while (true) {
            if (x0 >= 0 && x0 < image.getWidth() && y0 >= 0 && y0 < image.getHeight())
                image.setRGB(x0, y0, color.getRGB());
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * error;
            if (e2 >= dy) {
                if (x0 == x1) break;
                error = error + dy;
                x0 = x0 + sx;
            }
            if (e2 <= dx) {
                if (y0 == y1) break;
                error = error + dx;
                y0 = y0 + sy;
            }
        }
    }

    /**
     * Fills an area with the specified color
     *
     * @param image drawing area
     * @param color specified color
     * @param x     the x coordinate of the seed point
     * @param y     the y coordinate of the seed point
     */
    public static void fill(BufferedImage image, Color color, int x, int y) {
        var seedColor = new Color(image.getRGB(x, y));
        if (seedColor.equals(color)) return;
        if (!inside(image, x, y, seedColor)) return;

        Deque<Point> stack = new ArrayDeque<>();

        stack.push(new Point(x, y));
        while (!stack.isEmpty()) {
            var p = stack.pop();
            var lx = p.x;
            while (inside(image, lx - 1, p.y, seedColor)) {
                image.setRGB(lx - 1, p.y, color.getRGB());
                --lx;
            }
            var rx = p.x;
            while (inside(image, rx, p.y, seedColor)) {
                image.setRGB(rx, p.y, color.getRGB());
                ++rx;
            }
            scan(image, lx, rx - 1, p.y + 1, stack, seedColor);
            scan(image, lx, rx - 1, p.y - 1, stack, seedColor);
        }
    }

    private static boolean inside(BufferedImage image, int x, int y, Color color) {
        return 0 <= x && x < image.getWidth() &&
                0 <= y && y < image.getHeight() &&
                image.getRGB(x, y) == color.getRGB();
    }

    private static void scan(BufferedImage image, int lx, int rx, int y, Deque<Point> stack, Color color) {
        var spanAdded = false;

        for (int x = lx; x <= rx; ++x) {
            if (!inside(image, x, y, color)) {
                spanAdded = false;
            } else if (!spanAdded) {
                stack.push(new Point(x, y));
                spanAdded = true;
            }
        }
    }
    public static BufferedImage ditherImageFloydAS1(BufferedImage image, int quantCountR, int quantCountG, int quantCountB){
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int quantIntervalR = 255 / (quantCountR - 1);
        int quantIntervalG = 255 / (quantCountG - 1);
        int quantIntervalB = 255 / (quantCountB - 1);
        byte[] curRowMistakesR = new byte[width];
        byte[] curRowMistakesG = new byte[width];
        byte[] curRowMistakesB = new byte[width];
        byte[] nextRowMistakesR = new byte[width];
        byte[] nextRowMistakesG = new byte[width];
        byte[] nextRowMistakesB = new byte[width];
        for(int y = 0; y < height; y++){
            byte mistakeR = 0;
            byte mistakeG = 0;
            byte mistakeB = 0;
            for(int x = 0; x < width; x++){
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                int oldR = (oldPixel >> 16) & 0xff;
                int oldG = (oldPixel >> 8) & 0xff;
                int oldB = (oldPixel) & 0xff;
                int newR = Math.round(oldR / (float)quantIntervalR + mistakeR) * quantIntervalR;
                int newG = Math.round(oldG / (float)quantIntervalG + mistakeG) * quantIntervalG;
                int newB = Math.round(oldB / (float)quantIntervalB + mistakeB) * quantIntervalB;

                /*if (oldR / quantIntervalR == quantCountR)
                    newR = newR - 1;]
                if (oldG / quantIntervalG == quantCountG)
                    newG = newG - 1;
                if (oldB / quantIntervalB == quantCountB)
                    newB = newB - 1;*/
                int newPixel = (oldA << 24) & 0xFF000000 | (newR << 16) & 0x00FF0000
                             | (newG << 8) & 0x0000FF00 | newB & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
                mistakeR = (byte)(oldR - newR);
                mistakeG = (byte)(oldG - newG);
                mistakeB = (byte)(oldB - newB);
                if(x != 0 && x != width - 1){
                    nextRowMistakesR[x - 1] = (byte)(mistakeR * 3 / 16);
                    nextRowMistakesG[x - 1] = (byte)(mistakeG * 3 / 16);
                    nextRowMistakesB[x - 1] = (byte)(mistakeB * 3 / 16);
                    nextRowMistakesR[x] = (byte)(mistakeR * 5 / 16);
                    nextRowMistakesG[x] = (byte)(mistakeG * 5 / 16);
                    nextRowMistakesB[x] = (byte)(mistakeB * 5 / 16);
                    nextRowMistakesR[x + 1] = (byte)(mistakeR / 16);
                    nextRowMistakesG[x + 1] = (byte)(mistakeG / 16);
                    nextRowMistakesB[x + 1] = (byte)(mistakeB / 16);
                }
                mistakeR = (byte)(mistakeR * 7 / 16);
                mistakeG = (byte)(mistakeG * 7 / 16);
                mistakeB = (byte)(mistakeB * 7 / 16);

            }

            for(int i = 0; i < width; i ++){
                curRowMistakesR[i] = nextRowMistakesR[i];
                curRowMistakesG[i] = nextRowMistakesG[i];
                curRowMistakesB[i] = nextRowMistakesB[i];
                nextRowMistakesR[i] = 0;
                nextRowMistakesG[i] = 0;
                nextRowMistakesB[i] = 0;
            }
        }
        return newImage;
    }

    private static Color getNearestPaletteColor(Color color, int redValue, int greenValue, int blueValue) {

        int red = 0, green = 0, blue = 0;

        if (redValue > 1) {
            red = ((int) Math.round(color.getRed() * (redValue - 1) / 255d)) * 255 / (redValue - 1);
        }
        if (greenValue > 1) {
            green = ((int) Math.round(color.getGreen() * (greenValue - 1) / 255d)) * 255 / (greenValue - 1);
        }
        if (blueValue > 1) {
            blue = ((int) Math.round(color.getBlue() * (blueValue - 1) / 255d)) * 255 / (blueValue - 1);
        }

        return new Color(red, green, blue);
    }
    public static BufferedImage ditherImageFloydAS(BufferedImage image, int quantCountR, int quantCountG, int quantCountB){
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage floyd = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        int[] red = new int[width * height];
        int[] green = new int[width * height];
        int[] blue = new int[width * height];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(pixels[y * width + x]);
                red[y * width + x] = color.getRed();
                green[y * width + x] = color.getGreen();
                blue[y * width + x] = color.getBlue();
            }
        }

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(red[y * width + x], green[y * width + x], blue[y * width + x]);
                Color nearest = getNearestPaletteColor(color, quantCountR, quantCountG, quantCountB);
                floyd.setRGB(x, y, nearest.getRGB());

                int redDiff = color.getRed() - nearest.getRed();
                int greenDiff = color.getGreen() - nearest.getGreen();
                int blueDiff = color.getBlue() - nearest.getBlue();

                if (y + 1 < height) {
                    red[(y + 1) * width + x] = Math.max(0, Math.min(255, red[(y + 1) * width + x] + (int) ((double) redDiff * (5f / 16f))));
                    green[(y + 1) * width + x] = Math.max(0, Math.min(255, green[(y + 1) * width + x] + (int) ((double) greenDiff * (5f / 16f))));
                    blue[(y + 1) * width + x] = Math.max(0, Math.min(255, blue[(y + 1) * width + x] + (int) ((double) blueDiff * (5f / 16f))));

                    if (x + 1 < width) {
                        red[(y + 1) * width + (x + 1)] = Math.max(0, Math.min(255, red[(y + 1) * width + (x + 1)] + (int) ((double) redDiff * (1f / 16f))));
                        green[(y + 1) * width + (x + 1)] = Math.max(0, Math.min(255, green[(y + 1) * width + (x + 1)] + (int) ((double) greenDiff * (1f / 16f))));
                        blue[(y + 1) * width + (x + 1)] = Math.max(0, Math.min(255, blue[(y + 1) * width + (x + 1)] + (int) ((double) blueDiff * (1f / 16f))));
                    }

                    if (x - 1 >= 0) {
                        red[(y + 1) * width + (x - 1)] = Math.max(0, Math.min(255, red[(y + 1) * width + (x - 1)] + (int) ((double) redDiff * (3f / 16f))));
                        green[(y + 1) * width + (x - 1)] = Math.max(0, Math.min(255, green[(y + 1) * width + (x - 1)] + (int) ((double) greenDiff * (3f / 16f))));
                        blue[(y + 1) * width + (x - 1)] = Math.max(0, Math.min(255, blue[(y + 1) * width + (x - 1)] + (int) ((double) blueDiff * (3f / 16f))));
                    }
                }

                if (x + 1 < width) {
                    red[y * width + (x + 1)] = Math.max(0, Math.min(255, red[y * width + (x + 1)] + (int) ((double) redDiff * (7f / 16f))));
                    green[y * width + (x + 1)] = Math.max(0, Math.min(255, green[y * width + (x + 1)] + (int) ((double) greenDiff * (7f / 16f))));
                    blue[y * width + (x + 1)] = Math.max(0, Math.min(255, blue[y * width + (x + 1)] + (int) ((double) blueDiff * (7f / 16f))));
                }
            }
        }
        return floyd;
    }
    public static BufferedImage ditherImageOrderedAS(BufferedImage image, int redValue, int greenValue, int blueValue, int n) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage ordered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        int[] errorMatrix = getErrors(n);
        double div = 1.0 / Math.pow(n, 2);
        double half = 1.0 / 2.0;
        double dr = 255.0 / (redValue - 1);
        double dg = 255.0 / (greenValue - 1);
        double db = 255.0 / (blueValue - 1);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(pixels[y * width + x]);

                double err = (errorMatrix[(x % n) * n + y % n] * div - half);

                int red = Math.max(0, Math.min(255, (int) Math.round(color.getRed() + err * dr)));
                int green = Math.max(0, Math.min(255, (int) Math.round(color.getGreen() + err * dg)));
                int blue = Math.max(0, Math.min(255, (int) Math.round(color.getBlue() + err * db)));

                Color newColor = getNearestPaletteColor(new Color(red, green, blue), redValue, greenValue, blueValue);
                ordered.setRGB(x, y, newColor.getRGB());
            }
        }
        return ordered;
    }

    private static int[] getErrors(int n) {

        int[] matrix = new int[n * n];

        if (n == 1) {
            matrix[0] = 0;
            return matrix;
        }

        int len = n / 2;
        int[] smaller = getErrors(len);

        for (int y = 0; y < 2; ++y) {
            for (int k = 0; k < len; ++k) {
                for (int l = 0; l < len; ++l) {
                    matrix[k * len * 2 + (len * y + l)] = 4 * smaller[k * len + l] + 2 * y;
                    matrix[((len + k) * len) * 2 + (len * y + l)] = 4 * smaller[k * len + l] + 3 - 2 * y;
                }
            }
        }
        return matrix;
    }

    public static BufferedImage ditherImageOrderedNM(BufferedImage image, int redValue, int greenValue, int blueValue, int n) {
        int width = image.getWidth();
        int height = image.getHeight();
        var newImage = new BufferedImage(width, height, image.getType());
        newImage.createGraphics().drawImage(image, null, 0, 0);
        var dr = 255.0 / (redValue - 1);
        var dg = 255.0 / (greenValue - 1);
        var db = 255.0 / (blueValue - 1);
        var factor = 1.0 / Math.pow(n, 2);
        int[] threshold = getErrors(n); //TODO: переделать

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int oldPixel = newImage.getRGB(x, y);

                double err = (threshold[(x % n) * n + y % n] * factor - 0.5);

                int oldR = (oldPixel >> 16) & 0xFF;
                int oldG = (oldPixel >> 8) & 0xFF;
                int oldB = oldPixel & 0xFF;

                int red = Math.max(0, Math.min(255, (int) Math.round(oldR + err * dr)));
                int green = Math.max(0, Math.min(255, (int) Math.round(oldG + err * dg)));
                int blue = Math.max(0, Math.min(255, (int) Math.round(oldB + err * db)));

                int newR = Math.round(red * (redValue - 1) / 255f) * 255 / (redValue - 1);
                int newG = Math.round(green * (greenValue - 1) / 255f) * 255 / (greenValue - 1);
                int newB = Math.round(blue * (blueValue - 1) / 255f) * 255 / (blueValue - 1);

                int newPixel = 255 << 24 | newR << 16 | newG << 8 | newB;
                newImage.setRGB(x, y, newPixel);
            }
        }

        return newImage;
    }

    private static int[] generateThresholdMatrix(int n) {
        int[] matrix = new int[n * n];

        return matrix;
    }

    public static BufferedImage ditherImageFloydNM(BufferedImage image, int quantCountR, int quantCountG, int quantCountB) {
        int width = image.getWidth();
        int height = image.getHeight();
        var newImage = new BufferedImage(width, height, image.getType());
        newImage.createGraphics().drawImage(image, null, 0, 0);
        int oldPixel, newPixel, neighbourPixel;
        int oldR, oldG, oldB;
        int newR, newG, newB;
        int errR, errG, errB;
        int neighbourR, neighbourG, neighbourB;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                oldPixel = newImage.getRGB(x, y);

                oldR = (oldPixel >> 16) & 0xFF;
                oldG = (oldPixel >> 8) & 0xFF;
                oldB = oldPixel & 0xFF;

                newR = Math.round(oldR * (quantCountR - 1) / 255f) * 255 / (quantCountR - 1);
                newG = Math.round(oldG * (quantCountG - 1) / 255f) * 255 / (quantCountG - 1);
                newB = Math.round(oldB * (quantCountB - 1) / 255f) * 255 / (quantCountB - 1);

                newPixel = 255 << 24 | newR << 16 | newG << 8 | newB;

                newImage.setRGB(x, y, newPixel);

                errR = oldR - newR;
                errG = oldG - newG;
                errB = oldB - newB;

                if (x < newImage.getWidth() - 1) {
                    var factor = 7.0 / 16;
                    neighbourPixel = newImage.getRGB(x + 1, y);
                    neighbourR = Math.max(0, Math.min(((neighbourPixel >> 16) & 0xFF) + (int) (errR * factor), 255));
                    neighbourG = Math.max(0, Math.min(((neighbourPixel >> 8) & 0xFF) + (int) (errG * factor), 255));
                    neighbourB = Math.max(0, Math.min((neighbourPixel & 0xFF) + (int) (errB * factor), 255));

                    neighbourPixel = 255 << 24 | neighbourR << 16 | neighbourG << 8 | neighbourB;
                    newImage.setRGB(x + 1, y, neighbourPixel);
                }
                if (x < newImage.getWidth() - 1 && y < newImage.getHeight() - 1) {
                    var factor = 1.0 / 16;
                    neighbourPixel = newImage.getRGB(x + 1, y + 1);
                    neighbourR = Math.max(0, Math.min(((neighbourPixel >> 16) & 0xFF) + (int) (errR * factor), 255));
                    neighbourG = Math.max(0, Math.min(((neighbourPixel >> 8) & 0xFF) + (int) (errG * factor), 255));
                    neighbourB = Math.max(0, Math.min((neighbourPixel & 0xFF) + (int) (errB * factor), 255));

                    neighbourPixel = 255 << 24 | neighbourR << 16 | neighbourG << 8 | neighbourB;
                    newImage.setRGB(x + 1, y + 1, neighbourPixel);
                }
                if (y < newImage.getHeight() - 1) {
                    var factor = 5.0 / 16;
                    neighbourPixel = newImage.getRGB(x, y + 1);
                    neighbourR = Math.max(0, Math.min(((neighbourPixel >> 16) & 0xFF) + (int) (errR * factor), 255));
                    neighbourG = Math.max(0, Math.min(((neighbourPixel >> 8) & 0xFF) + (int) (errG * factor), 255));
                    neighbourB = Math.max(0, Math.min((neighbourPixel & 0xFF) + (int) (errB * factor), 255));

                    neighbourPixel = 255 << 24 | neighbourR << 16 | neighbourG << 8 | neighbourB;
                    newImage.setRGB(x, y + 1, neighbourPixel);
                }
                if (x > 0 && y < newImage.getHeight() - 1) {
                    var factor = 3.0 / 16;
                    neighbourPixel = newImage.getRGB(x - 1, y + 1);
                    neighbourR = Math.max(0, Math.min(((neighbourPixel >> 16) & 0xFF) + (int) (errR * factor), 255));
                    neighbourG = Math.max(0, Math.min(((neighbourPixel >> 8) & 0xFF) + (int) (errG * factor), 255));
                    neighbourB = Math.max(0, Math.min((neighbourPixel & 0xFF) + (int) (errB * factor), 255));

                    neighbourPixel = 255 << 24 | neighbourR << 16 | neighbourG << 8 | neighbourB;
                    newImage.setRGB(x - 1, y + 1, neighbourPixel);
                }
            }
        }

        return newImage;
    }
    public static BufferedImage maskPixels(BufferedImage image, double[][] mask){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int maskRadius = mask.length / 2;
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                double newR = 0, newG = 0, newB = 0;
                boolean border = false;
                for(int vertical = -maskRadius; vertical <= maskRadius; vertical++){
                    for(int horizontal = -maskRadius; horizontal <= maskRadius; horizontal++){
                        int currentX = x + horizontal;
                        int currentY = y + vertical;
                        if (currentX < 0 || currentY < 0 || currentX >= image.getWidth() || currentY >= image.getHeight()) {
                            newImage.setRGB(x, y, oldPixel);
                            horizontal = maskRadius + 1;
                            vertical = maskRadius + 1;
                            border = true;
                            continue;
                        }
                        int currentPixel = image.getRGB(currentX, currentY);
                        int currentR = (currentPixel >> 16) & 0xff;
                        int currentG = (currentPixel >> 8) & 0xff;
                        int currentB = (currentPixel) & 0xff;
                        newR += (double)currentR * mask[horizontal + maskRadius][vertical + maskRadius];
                        newG += (double)currentG * mask[horizontal + maskRadius][vertical + maskRadius];
                        newB += (double)currentB * mask[horizontal + maskRadius][vertical + maskRadius];
                    }
                }
                if(border)
                    continue;
                newR = newR < 0 ? 0 : newR;
                newG = newG < 0 ? 0 : newG;
                newB = newB < 0 ? 0 : newB;
                newR = newR > 255 ? 255 : newR;
                newG = newG > 255 ? 255 : newG;
                newB = newB > 255 ? 255 : newB;
                int newPixel = (oldA << 24) & 0xFF000000 | ((char)newR << 16) & 0x00FF0000
                             | ((char)newG << 8) & 0x0000FF00 | ((char)newB) & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
            }
        }
        return newImage;
    }

    public static BufferedImage makeGrayShaded(BufferedImage image){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                int oldR = (oldPixel >> 16) & 0xff;
                int oldG = (oldPixel >> 8) & 0xff;
                int oldB = oldPixel & 0xff;
                int newGrayShade = (int)(oldR * 0.30 + oldG * 0.59 + oldB * 0.11);

                int newPixel = (oldA << 24) & 0xFF000000 | ((char)newGrayShade << 16) & 0x00FF0000
                        | ((char)newGrayShade << 8) & 0x0000FF00 | ((char)newGrayShade) & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
            }
        }
        return newImage;
    }

    public static BufferedImage makeWaterColored(BufferedImage image, int maskRadius){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int numOfNeighbours = (maskRadius * 2 + 1) * (maskRadius * 2 + 1);
        int[] neighboursR = new int[numOfNeighbours];
        int[] neighboursG = new int[numOfNeighbours];
        int[] neighboursB = new int[numOfNeighbours];
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                double newR = 0, newG = 0, newB = 0;
                boolean border = false;
                for(int vertical = -maskRadius; vertical <= maskRadius; vertical++){
                    for(int horizontal = -maskRadius; horizontal <= maskRadius; horizontal++){
                        int currentX = x + horizontal;
                        int currentY = y + vertical;
                        if (currentX < 0 || currentY < 0 || currentX >= image.getWidth() || currentY >= image.getHeight()) {
                            newImage.setRGB(x, y, oldPixel);
                            horizontal = maskRadius + 1;
                            vertical = maskRadius + 1;
                            border = true;
                            continue;
                        }
                        int currentPixel = image.getRGB(x + horizontal, y + vertical);
                        int currentR = (currentPixel >> 16) & 0xff;
                        int currentG = (currentPixel >> 8) & 0xff;
                        int currentB = (currentPixel) & 0xff;
                        int neighbourIndex = (vertical + maskRadius) * (maskRadius * 2 + 1) + horizontal + maskRadius;
                        neighboursR[neighbourIndex] = currentR;
                        neighboursG[neighbourIndex] = currentG;
                        neighboursB[neighbourIndex] = currentB;
                    }
                }
                if(border)
                    continue;
                Arrays.sort(neighboursR);
                Arrays.sort(neighboursG);
                Arrays.sort(neighboursB);
                newR = neighboursR[numOfNeighbours / 2];
                newG = neighboursG[numOfNeighbours / 2];
                newB = neighboursB[numOfNeighbours / 2];
                int newPixel = (oldA << 24) & 0xFF000000 | ((char)newR << 16) & 0x00FF0000
                        | ((char)newG << 8) & 0x0000FF00 | ((char)newB) & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
            }
        }
        return Drafter.makeSharpness(newImage);
    }
    public static BufferedImage makeZoom(BufferedImage image, int times){
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage zoomed = new BufferedImage(width * times, height * times, BufferedImage.TYPE_INT_RGB);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        int maxZoomSize = 40000;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color first = new Color(pixels[y * width + x]);
                Color second;
                if (x < width - 1) {
                    second = new Color(pixels[y * width + x + 1]);
                } else {
                    second = first;
                }

                int red = first.getRed();
                int green = first.getGreen();
                int blue = first.getBlue();

                int dR = (second.getRed() - first.getRed()) / times;
                int dG = (second.getGreen() - first.getGreen()) / times;
                int dB = (second.getBlue() - first.getBlue()) / times;

                for (int k = 0; k < times; k++) {
                    zoomed.setRGB(x * times + k, y * times, new Color(red, green, blue).getRGB());

                    red += dR;
                    green += dG;
                    blue += dB;
                }
            }
        }

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width * times; ++x) {
                Color first = new Color(zoomed.getRGB(x, y * times));
                Color second;
                if (y < height - 1) {
                    second = new Color(zoomed.getRGB(x, y * times + times));
                } else {
                    second = first;
                }
                int red = first.getRed();
                int green = first.getGreen();
                int blue = first.getBlue();

                int dR = (second.getRed() - first.getRed()) / times;
                int dG = (second.getGreen() - first.getGreen()) / times;
                int dB = (second.getBlue() - first.getBlue()) / times;

                for (int k = 0; k < times; k++) {
                    zoomed.setRGB(x, y * times + k, new Color(red, green, blue).getRGB());

                    red += dR;
                    green += dG;
                    blue += dB;
                }
            }
        }

        int startX = width * times / 2 - maxZoomSize / 2;
        int startY = height * times / 2 - maxZoomSize / 2;

        if (maxZoomSize > width * times && maxZoomSize > height * times) {
            return zoomed;
        } else if (maxZoomSize > width * times) {
            return zoomed.getSubimage(0, startY, width * times, maxZoomSize);
        } else if (maxZoomSize > height * times) {
            return zoomed.getSubimage(startX, 0, maxZoomSize, height * times);
        } else {
            return zoomed.getSubimage(startX, startY, maxZoomSize, maxZoomSize);
        }
    }
    public static BufferedImage getRotated(BufferedImage image, int degree){
        double angle = Math.toRadians(degree);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        int newH = (int) (image.getWidth()*Math.abs(sin) + image.getHeight()*Math.abs(cos));
        int newW = (int) (image.getWidth()*Math.abs(cos) + image.getHeight()*Math.abs(sin));

        BufferedImage newImage = new BufferedImage(newW, newH, image.getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newW, newH);

        for(int x = 0; x < newW; x++) {
            for(int y = 0; y < newH; y++) {
                int newX = (int) ((x - newW/2)*cos - (y - newH/2)*sin) + image.getWidth() / 2;
                int newY = (int) ((x - newW/2)*sin + (y - newH/2)*cos) + image.getHeight() / 2;

                int color = 0;
                if(newX > 0 && newY > 0 && newX < image.getWidth() && newY < image.getHeight())
                    color = image.getRGB(newX, newY);
                else
                    color = -1;

                newImage.setRGB(x, y, color);
            }
        }

        return newImage;
    }

    private static float height(BufferedImage image, int width, int height, int x, int y) {
        if (x >= width)  x %= width;
        while (x < 0)    x += width;
        if (y >= height) y %= height;
        while (y < 0)    y += height;

        return (image.getRGB(x, y)) / 255.0f;
    }

    private static int textureCoordinateToRgb(float value)
    {
        return (int)((value + 1.0) * (255.0 / 2.0));
    }

    private static int calculateNormal(BufferedImage image, int width, int height, int x, int z) {
        float strength = 8.0f;

        float tl = height(image, width, height, x-1, z-1);
        float  l = height(image, width, height, x-1, z);
        float bl = height(image, width, height, x-1, z+1);
        float  t = height(image, width, height, x, z-1);
        float  b = height(image, width, height, x, z+1);
        float tr = height(image, width, height, x+1, z-1);
        float  r = height(image, width, height, x+1, z);
        float br = height(image, width, height, x+1, z+1);

        float dX = (tr + 2.0f * r + br) - (tl + 2.0f * l + bl);
        float dY = (bl + 2.0f * b + br) - (tl + 2.0f * t + tr);
        float dZ = 1.0f / strength;
        float norm = (float)Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        dX /= norm;
        dY /= norm;
        dZ /= norm;
        int normal = 0xFF000000 | ((char)textureCoordinateToRgb(dX) << 16) & 0x00FF0000
                | ((char)textureCoordinateToRgb(dY) << 8) & 0x0000FF00 | ((char)textureCoordinateToRgb(dZ)) & 0x000000FF;
        //n = glm::normalize(n);

        return normal;
    }

    public static BufferedImage makeNormalMap(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        //BufferedImage heightMap = Drafter.makeEmbossing(image, 0);
        BufferedImage heightMap = Drafter.makeGrayShaded(image);
        BufferedImage normalMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        double[][] maskShnobelH = {{1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}};
        double[][] maskShnobelV = {{1, 2, 1},
                {0, 0, -0},
                {-1, -2, -1}};
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int normal = calculateNormal(image, width, height, x, y);
                int newPixel = normal;
                normalMap.setRGB(x, y, newPixel);
            }
        }

        return normalMap;
    }
    public static BufferedImage makeTwirl(BufferedImage image, double angle) {
        int centerX = image.getWidth() / 2;
        int centerY = image.getHeight() / 2;
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int newX, newY;
                double dx = x - centerX;
                double dy = y - centerY;
                double radius = Math.min(centerX, centerY);
                double radius2 = radius * radius;
                double distance = dx * dx + dy * dy;

                if (distance > radius2) {
                    newX = x;
                    newY = y;
                } else {
                    distance = Math.sqrt(distance);
                    var a = Math.atan2(dy, dx) + angle * (radius - distance) / radius;
                    newX = centerX + (int) (distance * Math.cos(a));
                    newY = centerY + (int) (distance * Math.sin(a));
                }
                result.setRGB(x, y, image.getRGB(newX, newY));
            }
        }

        return result;
    }
    public static BufferedImage makeEmbossing(BufferedImage image, int offset){
        double[][] maskBorder = {{0, 1, 0},
                {-1, 0, 1},
                {0, -1, 0}};
        return offsetImage(maskPixels(Drafter.makeGrayShaded(image), maskBorder), offset);
    }
    public static BufferedImage makeSharpness(BufferedImage image){
        double[][] maskRezko = {{0, -1, 0},
                {-1, 5, -1},
                {0, -1 , 0}};
        return maskPixels(image, maskRezko);
    }
    public static BufferedImage makeSobel(BufferedImage image, int threshold){
        double[][] maskShnobelH = {{1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}};
        return binarizePixels(maskPixels(Drafter.makeGrayShaded(image), maskShnobelH), threshold);
    }
    public static BufferedImage makeRoberts(BufferedImage image, int threshold){
        double[][] maskRoberts = {{1, 0, 0},
                {0, 0, 0},
                {0, 0, -1}};
        return binarizePixels(maskPixels(Drafter.makeGrayShaded(image), maskRoberts), threshold);
    }
    public static BufferedImage makeInverse(BufferedImage image){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                int oldR = (oldPixel >> 16) & 0xff;
                int oldG = (oldPixel >> 8) & 0xff;
                int oldB = oldPixel & 0xff;
                int newR = 255 - oldR;
                int newG = 255 - oldG;
                int newB = 255 - oldB;
                int newPixel = (oldA << 24) & 0xFF000000 | ((char)newR << 16) & 0x00FF0000
                        | ((char)newG << 8) & 0x0000FF00 | ((char)newB) & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
            }
        }
        return newImage;
    }
    public static BufferedImage makeGamma(BufferedImage image, double gamma){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                int oldR = (oldPixel >> 16) & 0xff;
                int oldG = (oldPixel >> 8) & 0xff;
                int oldB = oldPixel & 0xff;
                int newR = (int)((Math.pow(oldR / 255.0, gamma)) * 255);
                int newG = (int)((Math.pow(oldG / 255.0, gamma)) * 255);
                int newB = (int)((Math.pow(oldB / 255.0, gamma)) * 255);

                int newPixel = (oldA << 24) & 0xFF000000 | ((char)newR << 16) & 0x00FF0000
                        | ((char)newG << 8) & 0x0000FF00 | ((char)newB) & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
            }
        }
        return newImage;
    }
    private static BufferedImage binarizePixels(BufferedImage image, int threshold){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                int oldR = (oldPixel >> 16) & 0xff;
                int oldG = (oldPixel >> 8) & 0xff;
                int oldB = oldPixel & 0xff;
                int newR = oldR > threshold ? 255 : 0;
                int newG = oldG > threshold ? 255 : 0;
                int newB = oldB > threshold ? 255 : 0;
                int newPixel = (oldA << 24) & 0xFF000000 | ((char)newR << 16) & 0x00FF0000
                        | ((char)newG << 8) & 0x0000FF00 | ((char)newB) & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
            }
        }
        return newImage;
    }
    private static BufferedImage offsetImage(BufferedImage image, int offset){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                int oldR = (oldPixel >> 16) & 0xff;
                int oldG = (oldPixel >> 8) & 0xff;
                int oldB = oldPixel & 0xff;
                int newR = oldR + offset;
                int newG = oldG + offset;
                int newB = oldB + offset;
                newR = Math.max(newR, 0);
                newG = Math.max(newG, 0);
                newB = Math.max(newB, 0);
                newR = Math.min(newR, 255);
                newG = Math.min(newG, 255);
                newB = Math.min(newB, 255);
                int newPixel = (oldA << 24) & 0xFF000000 | ((char)newR << 16) & 0x00FF0000
                        | ((char)newG << 8) & 0x0000FF00 | ((char)newB) & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
            }
        }
        return newImage;
    }
}
