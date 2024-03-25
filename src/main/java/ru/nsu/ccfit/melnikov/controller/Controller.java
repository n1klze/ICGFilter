package ru.nsu.ccfit.melnikov.controller;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.melnikov.model.Drafter;
import ru.nsu.ccfit.melnikov.model.Tools;
import ru.nsu.ccfit.melnikov.view.Canvas;
import ru.nsu.ccfit.melnikov.view.components.FileChooser.ImageLoader;
import ru.nsu.ccfit.melnikov.view.components.FileChooser.ImageSaver;
import ru.nsu.ccfit.melnikov.view.components.buttons.ToolButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;


@Getter
@Setter
public class Controller {
    private Color currentColor = Color.BLACK;
    private Tools currentTool = Tools.PEN;
    private int currentInterpolationType = AffineTransformOp.TYPE_BILINEAR;
    private int thickness = 1;
    private int numOfAngles = 5;
    private int radius = 70;
    private int rotation = 0;

    public void resizeCanvas(Canvas canvas, int width, int height) {
        canvas.resizeCanvas(width, height);
    }

    public void setCurrentTool(Tools tool, Map<Tools, ToolButton> toolBarButtons, Map<Tools, JRadioButtonMenuItem> viewMenuToolButtons) {
        currentTool = tool;
        toolBarButtons.get(tool).setSelected(true);
        viewMenuToolButtons.get(tool).setSelected(true);
    }

    public void loadImage(Canvas canvas) {
        var loader = new ImageLoader();
        int returnVal = loader.showOpenDialog(loader);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                var newImage = ImageIO.read(loader.getSelectedFile());
                if (newImage != null)
                    canvas.loadImage(newImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveImage(Canvas canvas) {
        var saver = new ImageSaver();
        var returnVal = saver.showSaveDialog(saver);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                var filePath = saver.getSelectedFile().getAbsolutePath();
                var file = filePath.endsWith(".png") ? saver.getSelectedFile() : new File(filePath + ".png");
                ImageIO.write(canvas.getImage(), "png", file);
                JOptionPane.showMessageDialog(saver,"File " + file.getPath() + " saved");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void fill(BufferedImage image, Point seedPoint) {
        Drafter.fill(image, getCurrentColor(), seedPoint.x, seedPoint.y);
    }

    public void drawLine(BufferedImage image, Point from, Point to) {
        if (thickness == 1) {
            Drafter.drawLine(image,
                    currentColor,
                    from.x, from.y,
                    to.x, to.y);
        } else {
            var g2d = (Graphics2D) image.getGraphics();
            g2d.setColor(currentColor);
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(from.x, from.y, to.x, to.y);
        }
    }

    public void drawPolygon(BufferedImage image, Point centerPoint) {
        Point[] vertex = new Point[numOfAngles];
        double k = 2 * Math.PI / numOfAngles;

        for (int i = 0; i < numOfAngles; ++i) {
            var vertexAngle = Math.toRadians(rotation) + i * k;
            var x = (int) (centerPoint.x + radius * Math.cos(vertexAngle));
            var y = (int) (centerPoint.y + radius * Math.sin(vertexAngle));
            vertex[i] = new Point(x, y);
        }

        for (int i = 0; i < numOfAngles; ++i)
            drawLine(image, vertex[i], vertex[(i + 1) % numOfAngles]);
    }

    public void drawStar(BufferedImage image, Point centerPoint) {
        Point[] vertex = new Point[2 * numOfAngles];
        double k = Math.PI / numOfAngles;

        for (int i = 0; i < 2 * numOfAngles; ++i) {
            int x, y;
            var vertexAngle = Math.toRadians(rotation) + i * k;
            if (i % 2 == 0) {
                x = (int) (centerPoint.x + radius * Math.cos(vertexAngle));
                y = (int) (centerPoint.y + radius * Math.sin(vertexAngle));
            } else {
                x = (int) (centerPoint.x + radius / 2.5 * Math.cos(vertexAngle));
                y = (int) (centerPoint.y + radius / 2.5 * Math.sin(vertexAngle));
            }
            vertex[i] = new Point(x, y);
        }

        for (int i = 0; i < 2 * numOfAngles; ++i)
            drawLine(image, vertex[i], vertex[(i + 1) % (2 * numOfAngles)]);
    }
  
    public void ditherImageFloydAS(Canvas canvas, int quantsR, int quantsG, int quantsB){
        BufferedImage newImage = Drafter.ditherImageFloydAS(canvas.getImage(), quantsR, quantsG, quantsB);
        canvas.setImage(newImage);
    }
    public void ditherImageOrderedAS(Canvas canvas, int quantsR, int quantsG, int quantsB){
        int n = Math.max(Math.max(quantsR, quantsG), quantsB) * 2;
        BufferedImage newImage = Drafter.ditherImageOrderedAS(canvas.getImage(), quantsR, quantsG, quantsB, n);
        canvas.setImage(newImage);
    }
    public void makeBlur(Canvas canvas, int maskSize){
        double[][] mask3 = {{0.0947416, 0.118318, 0.0947416},
                            {0.118318, 0.147761, 0.118318},
                            {0.0947416, 0.118318 , 0.0947416}};

        double[][] mask5 = {{1/256.0, 4/256.0, 6/256.0, 4/256.0, 1/256.0},
                            {4/256.0, 16/256.0, 24/256.0, 16/256.0, 4/256.0},
                            {6/256.0, 24/256.0, 36/256.0, 24/256.0, 6/256.0},
                            {4/256.0, 16/256.0, 24/256.0, 16/256.0, 4/256.0},
                            {1/256.0, 4/256.0, 6/256.0, 4/256.0, 1/256.0}};
        double[][] mask = new double[maskSize][maskSize];
        if(maskSize == 3)
            mask = mask3;
        else if (maskSize == 5)
            mask = mask5;
        else
            for(int y = 0; y < maskSize; y++)
                for(int x = 0; x < maskSize; x++)
                    mask[x][y] = 1.0 / (double)(maskSize * maskSize);
        BufferedImage newImage = Drafter.maskPixels(canvas.getImage(), mask);
        canvas.setImage(newImage);
    }
    public void makeGrayShaded(Canvas canvas){
        BufferedImage newImage = Drafter.makeGrayShaded(canvas.getImage());
        canvas.setImage(newImage);
    }
    public void makeWaterColored(Canvas canvas){
        BufferedImage newImage = Drafter.makeWaterColored(canvas.getImage(), 2);
        canvas.setImage(newImage);
    }
    public void makeZoom(Canvas canvas, int times){
        BufferedImage newImage = Drafter.makeZoom(canvas.getImage(), times);
        canvas.setImage(newImage);
    }
    public void makeNormalMap(Canvas canvas){
        BufferedImage newImage = Drafter.makeNormalMap(canvas.getImage());
        canvas.setImage(newImage);
    }
    public void makeTwirl(Canvas canvas, int angle) {
        var newImage = Drafter.makeTwirl(canvas.getImage(), Math.toRadians(angle));
        canvas.setImage(newImage);
    }
    public void makeRotation(Canvas canvas, int angle){
        canvas.setImage(Drafter.getRotated(canvas.getImage(), angle));
    }
    public void makeEmbossing(Canvas canvas){
        BufferedImage newImage = (Drafter.makeEmbossing(canvas.getImage(), 128));
        canvas.setImage(newImage);
    }
    public void makeSharpness(Canvas canvas){
        BufferedImage newImage = (Drafter.makeSharpness(canvas.getImage()));
        canvas.setImage(newImage);
    }
    public void makeGamma(Canvas canvas){
        BufferedImage newImage = (Drafter.makeGamma(canvas.getImage(),  0.5));
        canvas.setImage(newImage);
    }
    public void makeInverse(Canvas canvas){
        BufferedImage newImage = (Drafter.makeInverse(canvas.getImage()));
        canvas.setImage(newImage);
    }
    public void makeSobel(Canvas canvas){
        BufferedImage newImage = (Drafter.makeSobel(canvas.getImage(), 70));
        canvas.setImage(newImage);
    }
    public void makeRoberts(Canvas canvas){
        BufferedImage newImage = (Drafter.makeRoberts(canvas.getImage(), 50));
        canvas.setImage(newImage);
    }
}
