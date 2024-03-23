package ru.nsu.ccfit.melnikov.view;

import lombok.Getter;
import ru.nsu.ccfit.melnikov.controller.Controller;
import ru.nsu.ccfit.melnikov.model.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener {
    private final Controller controller;
    @Getter
    private BufferedImage image;
    private Graphics2D g2d;
    private final JScrollPane spIm;
    private static final int INDENT = 4;
    private Point prevPoint = new Point(-1, -1);
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    public Canvas(Controller controller, Dimension dimension, JScrollPane scrollPane) {
        setPreferredSize(dimension);
        this.controller = controller;
        image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
        g2d = image.createGraphics();

        spIm = scrollPane;
        spIm.setWheelScrollingEnabled(false);
        spIm.setDoubleBuffered(true);
        spIm.setViewportView(this);
        scrollPane.setViewportBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(INDENT, INDENT, INDENT, INDENT), // this creates indent between frame and image area
                BorderFactory.createDashedBorder(Color.BLACK, 5, 2)));

        spIm.validate();					// added panel to scrollPane

        addMouseListener(this);
        addMouseMotionListener(this);

        setDefaultBackground();
    }

    public void setDefaultBackground() {
        g2d.setColor(DEFAULT_BACKGROUND_COLOR);
        g2d.setBackground(DEFAULT_BACKGROUND_COLOR);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        repaint();
    }

    public void setImage(BufferedImage newImage) {
        image = newImage;
        g2d = image.createGraphics();
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        revalidate();
        spIm.paintAll(spIm.getGraphics());
    }

    public void resizeCanvas(int newWidth, int newHeight) {
        setPreferredSize(new Dimension(newWidth, newHeight));

        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        g2d = newImage.createGraphics();
        setDefaultBackground();
        newImage.setData(image.getData());
        image = newImage;

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawImage(image, 0, 0, panelSize.width, panelSize.height, this);
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (controller.getCurrentTool()) {
            case PEN -> {
                prevPoint = e.getPoint();
                g2d.setColor(controller.getCurrentColor());
                g2d.fillOval(e.getX() - controller.getThickness() / 2, e.getY() - controller.getThickness() / 2,
                        controller.getThickness(), controller.getThickness());
            }
            case LINE -> prevPoint = e.getPoint();
            case FILL -> controller.fill(image, e.getPoint());
            case POLYGON -> controller.drawPolygon(image, e.getPoint());
            case STAR -> controller.drawStar(image, e.getPoint());
            case CURSOR -> prevPoint = e.getPoint();
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (controller.getCurrentTool() == Tools.LINE)
            controller.drawLine(image, prevPoint, e.getPoint());

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        switch (controller.getCurrentTool()) {
            case PEN -> {
                g2d.setColor(controller.getCurrentColor());
                g2d.fillOval(e.getX() - controller.getThickness() / 2, e.getY() - controller.getThickness() / 2,
                        controller.getThickness(), controller.getThickness());
                g2d.setStroke(new BasicStroke(controller.getThickness(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(prevPoint.x, prevPoint.y, e.getX(), e.getY());
                prevPoint = e.getPoint();
                repaint();
            }
            case CURSOR -> {
                // move picture using scroll
                Point scroll = spIm.getViewport().getViewPosition();
                scroll.x += ( prevPoint.x - e.getX() );
                scroll.y += ( prevPoint.y - e.getY() );

                //spIm.getViewport().setViewPosition(scroll);
                spIm.getHorizontalScrollBar().setValue(scroll.x);
                spIm.getVerticalScrollBar().setValue(scroll.y);
                spIm.repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
