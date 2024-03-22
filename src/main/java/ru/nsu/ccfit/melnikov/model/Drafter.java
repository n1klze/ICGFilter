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
}
