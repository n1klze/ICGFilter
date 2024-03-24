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

    public static int maskPixel(BufferedImage image, int x, int y, double[][] mask){
        int maskRadius = mask.length / 2;
        int oldPixel = image.getRGB(x, y);
        int oldA = (oldPixel >> 24) & 0xff;
        double newR = 0, newG = 0, newB = 0;
        for(int vertical = -maskRadius; vertical <= maskRadius; vertical++){
            for(int horizontal = -maskRadius; horizontal <= maskRadius; horizontal++){
                int currentX = x + horizontal;
                int currentY = y + vertical;
                if (currentX < 0 || currentY < 0 || currentX >= image.getWidth() || currentY >= image.getHeight())
                    continue;
                int currentPixel = image.getRGB(currentX, currentY);
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
        return newPixel;
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
        return newImage;
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
        System.out.println(degree);
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
