package ru.nsu.ccfit.melnikov.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
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
    public static BufferedImage ditherImageFloydAS(BufferedImage image){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int quantCountR = 2, quantCountG = 2, quantCountB = 2;
        int quantIntervalR = 255 / (quantCountR - 1);
        int quantIntervalG = 255 / (quantCountG - 1);
        int quantIntervalB = 255 / (quantCountB - 1);
        byte[] curRowMistakesR = new byte[image.getWidth()];
        byte[] curRowMistakesG = new byte[image.getWidth()];
        byte[] curRowMistakesB = new byte[image.getWidth()];
        byte[] nextRowMistakesR = new byte[image.getWidth()];
        byte[] nextRowMistakesG = new byte[image.getWidth()];
        byte[] nextRowMistakesB = new byte[image.getWidth()];
        /*for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getHeight(); x++){
                image.setRGB(x, y, 0xff000000);
            }
        }
        return image;*/
        for(int y = 0; y < image.getHeight(); y++){
            byte mistakeR = 0;
            byte mistakeG = 0;
            byte mistakeB = 0;
            for(int x = 0; x < image.getWidth(); x++){
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                int oldR = (oldPixel >> 16) & 0xff;
                int oldG = (oldPixel >> 8) & 0xff;
                int oldB = (oldPixel) & 0xff;
                int newR = Math.round(oldR / (float)quantIntervalR) * quantIntervalR;
                int newG = Math.round(oldG / (float)quantIntervalG) * quantIntervalG;
                int newB = Math.round(oldB / (float)quantIntervalB) * quantIntervalB;
                /*if (oldR / quantIntervalR == quantCountR)
                    newR = newR - 1;]
                if (oldG / quantIntervalG == quantCountG)
                    newG = newG - 1;
                if (oldB / quantIntervalB == quantCountB)
                    newB = newB - 1;*/
                int newPixel = (oldA << 24) & 0xFF000000 | (oldR << 16) & 0x00FF0000
                             | (oldG << 8) & 0x0000FF00 | oldB & 0x000000FF;
                newImage.setRGB(x, y, newPixel);
                mistakeR = (byte)(oldR - newR);
                mistakeG = (byte)(oldG - newG);
                mistakeB = (byte)(oldB - newB);
                if(x != 0 && x != image.getWidth() - 1){
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

            for(int i = 0; i < image.getWidth(); i ++){
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
    public static BufferedImage maskPixels(BufferedImage image, double[][] mask){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int maskRadius = mask.length / 2;
        for(int y = maskRadius; y < image.getHeight() - maskRadius; y++){
            for(int x = maskRadius; x < image.getWidth() - maskRadius; x++){
                int oldPixel = image.getRGB(x, y);
                int oldA = (oldPixel >> 24) & 0xff;
                double newR = 0, newG = 0, newB = 0;
                for(int vertical = -maskRadius; vertical <= maskRadius; vertical++){
                    for(int horizontal = -maskRadius; horizontal <= maskRadius; horizontal++){
                        int currentPixel = image.getRGB(x + horizontal, y + vertical);
                        int currentR = (currentPixel >> 16) & 0xff;
                        int currentG = (currentPixel >> 8) & 0xff;
                        int currentB = (currentPixel) & 0xff;
                        newR += (double)currentR * mask[horizontal + maskRadius][vertical + maskRadius];
                        newG += (double)currentG * mask[horizontal + maskRadius][vertical + maskRadius];
                        newB += (double)currentB * mask[horizontal + maskRadius][vertical + maskRadius];
                    }
                }
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
        for(int y = 0; y < image.getHeight() - 1; y++){
            for(int x = 0; x < image.getWidth() - 1; x++){
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
}
