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
                    canvas.setImage(newImage);
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
  
    public void ditherImageFloydAS(Canvas canvas){
        BufferedImage newImage = Drafter.ditherImageFloydAS(canvas.getImage());
        canvas.setImage(newImage);
    }
    public void makeBlur(Canvas canvas){
        double[][] mask = {{0.0947416, 0.118318, 0.0947416},
                           {0.118318, 0.147761, 0.118318},
                           {0.0947416, 0.118318 , 0.0947416}};
        double[][] maskRezko = {{0, -1, 0},
                                {-1, 5, -1},
                                {0, -1 , 0}};
        double[][] maskBorder = {{0, 1, 0},
                                 {-1, 0, 1},
                                 {0, -1, 0}};
        double[][] maskShnobelH = {{1, 0, -1},
                                  {2, 0, -2},
                                  {1, 0, -1}};
        double[][] maskShnobelV = {{1, 2, 1},
                                   {0, 0, -0},
                                   {-1, -2, -1}};
        double[][] maskRoberts = {{1, 0},
                                  {0, -1}};
        double[][] mask2 = {{1/256.0, 4/256.0, 6/256.0, 4/256.0, 1/256.0},
                            {4/256.0, 16/256.0, 24/256.0, 16/256.0, 4/256.0},
                            {6/256.0, 24/256.0, 36/256.0, 24/256.0, 6/256.0},
                            {4/256.0, 16/256.0, 24/256.0, 16/256.0, 4/256.0},
                            {1/256.0, 4/256.0, 6/256.0, 4/256.0, 1/256.0}};
        BufferedImage newImage = Drafter.maskPixels(canvas.getImage(), maskShnobelV);
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
    public void makeZoom(Canvas canvas){
        BufferedImage newImage = Drafter.makeZoom(canvas.getImage(), 2);
        canvas.setImage(newImage);
    }
    public void makeRotation(Canvas canvas){
        int angle = 45;
        canvas.setImage(Drafter.getRotated(canvas.getImage(), angle));
    }
}
