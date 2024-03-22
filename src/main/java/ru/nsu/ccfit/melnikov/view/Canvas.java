package ru.nsu.ccfit.melnikov.view;

import lombok.Getter;
import ru.nsu.ccfit.melnikov.controller.Controller;
import ru.nsu.ccfit.melnikov.model.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private final Controller controller;
    @Getter
    private BufferedImage image;
    private Graphics2D g2d;
    private Dimension panelSize;		// visible image size
    private final JScrollPane spIm;
    private final double zoomK = 0.05;	// scroll zoom coefficient
    private Dimension imSize = null;	// real image size
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

        panelSize = dimension;	// adjust panel size to maximum visible in scrollPane
        spIm.validate();					// added panel to scrollPane

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        setDefaultBackground();
    }

    private Dimension getVisibleRectSize() {
        // maximum size for panel with or without scrolling (inner border of the ScrollPane)
        Dimension viewportSize = spIm.getViewport().getSize();
        if (viewportSize.height == 0)
            return new Dimension( spIm.getWidth()-3, spIm.getHeight()-3 );
        else
            return viewportSize;
    }

    /**
     * Sets panelSize to the maximum avaible view-size with hidden scroll bars.
     */
    private void setMaxVisibleRectSize()
    {
        // maximum size for panel without scrolling (inner border of the ScrollPane)
        panelSize = getVisibleRectSize();	// max size, but possibly with enabled scroll-bars
        revalidate();
        spIm.validate();
        panelSize = getVisibleRectSize();	// max size, without enabled scroll-bars
        revalidate();
    }

    public void setDefaultBackground() {
        g2d.setColor(DEFAULT_BACKGROUND_COLOR);
        g2d.setBackground(DEFAULT_BACKGROUND_COLOR);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        repaint();
    }

    public void setImage(BufferedImage newImage) {
//        image = newImage;
//        g2d = image.createGraphics();
//        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
//        repaint();
        // defaultView means "fit screen (panel)"

        // Draw black screen for no image
        var defaultView = false;
        image = newImage;
        if (image == null)
        {
            // make full defaultView
            setMaxVisibleRectSize();	// panelSize = getVisibleRectSize();
            repaint();
            revalidate();	// spIm.validate();
            return;
        }

        // Check if it is possible to use defaultView
        Dimension newImSize = new Dimension(image.getWidth(), image.getHeight());
        //if (imSize == null)
        //    defaultView = true;
        //else if ( (newImSize.height != imSize.height) || (newImSize.width != imSize.width) )
        //    defaultView = true;

        imSize = newImSize;

        if (defaultView)
        {
            setMaxVisibleRectSize();	// panelSize = getVisibleRectSize();

            double kh = (double)imSize.height / panelSize.height;
            double kw = (double)imSize.width / panelSize.width;
            double k = (kh > kw) ? kh : kw;

            panelSize.width = (int)(imSize.width / k);
            panelSize.height = (int)(imSize.height / k);
            //this.setSize(panelSize);

            //repaint();
            spIm.getViewport().setViewPosition(new Point(0,0));
            //spIm.getHorizontalScrollBar().setValue(0);
            //spIm.getVerticalScrollBar().setValue(0);
            revalidate();	// spIm.validate();
            //spIm.repaint();	// wipe off the old picture in "spare" space
            spIm.paintAll(spIm.getGraphics());
        }
        else
        {
            // just change image
            //repaint();
            spIm.paintAll(spIm.getGraphics());
        }
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
        g.drawImage(image, 0, 0, panelSize.width, panelSize.height, this);
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
        if (controller.getCurrentTool() == Tools.PEN) {
            g2d.setColor(controller.getCurrentColor());
            g2d.fillOval(e.getX() - controller.getThickness() / 2, e.getY() - controller.getThickness() / 2,
                    controller.getThickness(), controller.getThickness());
            g2d.setStroke(new BasicStroke(controller.getThickness(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(prevPoint.x, prevPoint.y, e.getX(), e.getY());
            prevPoint = e.getPoint();
            repaint();
        } else if (controller.getCurrentTool() == Tools.CURSOR) {
            // Move image with mouse

            if (e.getModifiers() == InputEvent.BUTTON3_MASK)		// ( (e.getModifiers() & MouseEvent.BUTTON3_MASK) == 0)
                return;

            // move picture using scroll
            Point scroll = spIm.getViewport().getViewPosition();
            scroll.x += ( prevPoint.x - e.getX() );
            scroll.y += ( prevPoint.y - e.getY() );

            //spIm.getViewport().setViewPosition(scroll);
            spIm.getHorizontalScrollBar().setValue(scroll.x);
            spIm.getVerticalScrollBar().setValue(scroll.y);
            spIm.repaint();

            // We changed the position of the underlying picture, take it into account
            //lastX = e.getX() + (lastX - e.getX());	// lastX = lastX
            //lastY = e.getY() + (lastY - e.getY());	// lastY = lastY
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Change zoom when scrolling
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (image == null)
            return;

        // Zoom
        double k = 1 - e.getWheelRotation()*zoomK;

        // Check for minimum size where we can still increase size
        int newPW = (int)(panelSize.width*k);
        if (newPW == (int)(newPW * (1+zoomK)) )
            return;
//		if (newW/imSize.width > 50)
//			return;
        if (k > 1)
        {
            int newPH = (int)(panelSize.height*k);
            Dimension viewSize = getVisibleRectSize();
            int pixSizeX = newPW / imSize.width;
            int pixSizeY = newPH / imSize.height;
            if (pixSizeX>0 && pixSizeY>0)
            {
                int pixNumX = viewSize.width / pixSizeX;
                int pixNumY = viewSize.height / pixSizeY;
                if (pixNumX<2 || pixNumY<2)
                    return;
            }
        }

        panelSize.width = newPW;
        // panelSize.height *= k;
        panelSize.height = (int) ((long)panelSize.width * imSize.height / imSize.width);	// not to lose ratio

        // Move so that mouse position doesn't visibly change
        int x = (int) (e.getX() * k);
        int y = (int) (e.getY() * k);
        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x -= e.getX();
        scroll.y -= e.getY();
        scroll.x += x;
        scroll.y += y;

        repaint();	// можно и убрать
        revalidate();
        spIm.validate();
        // сначала нужно, чтобы scroll понял новый размер, потом сдвигать

        //spIm.getViewport().setViewPosition(scroll);	// так верхний левый угол может выйти за рамки изображения
        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        spIm.repaint();
    }

    public JScrollPane getScrollPane ()	{ return spIm; }

    /**
     * Sets "fit-screen" view.
     */
    public void fitScreen()	{ setImage(image); }

    /**
     * Sets "real-size" view.
     */
    public void realSize()
    {
        if (imSize == null)
            return;

        double k = (double)imSize.width / panelSize.width;
        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x *= (int) k;
        scroll.y *= (int) k;

        panelSize.setSize(imSize);

        //repaint();
        revalidate();	// spIm.validate();
        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        //spIm.repaint();
        spIm.paintAll(spIm.getGraphics());
    }

    public boolean setView(Rectangle rect)
    { return setView(rect, 10); }

    private boolean setView(Rectangle rect, int minSize)
    {
        // should also take into account ScrollBars size
        if (image == null)
            return false;
        if (imSize.width<minSize || imSize.height<minSize)
            return false;

        if (minSize <= 0)
            minSize = 10;

        if (rect.width < minSize) 	rect.width=minSize;
        if (rect.height < minSize) 	rect.height=minSize;
        if (rect.x < 0) rect.x=0;
        if (rect.y < 0) rect.y=0;
        if (rect.x > imSize.width-minSize) 		rect.x=imSize.width-minSize;
        if (rect.y > imSize.height-minSize) 	rect.y=imSize.height-minSize;
        if ((rect.x+rect.width) > imSize.width) 	rect.width=imSize.width-rect.x;
        if ((rect.y+rect.height) > imSize.height) 	rect.height=imSize.height-rect.y;

        Dimension viewSize = getVisibleRectSize();
        double kw = (double)rect.width / viewSize.width;
        double kh = (double)rect.height / viewSize.height;
        double k = Math.max(kh, kw);

        int newPW = (int)(imSize.width / k);
        int newPH = (int)(imSize.height / k);
        // Check for size whether we can still zoom out
        if (newPW == (int)(newPW * (1-2*zoomK)) )
            return setView(rect, minSize*2);
        panelSize.width = newPW;
        panelSize.height = newPH;

        revalidate();
        spIm.validate();
        // сначала нужно, чтобы scroll понял новый размер, потом сдвигать

        int xc = rect.x+rect.width/2, yc = rect.y+rect.height/2;
        xc = (int)(xc/k); yc = (int)(yc/k);	// we need to center new view
        //int x0 = (int)(rect.x/k), y0 = (int)(rect.y/k);
        spIm.getViewport().setViewPosition(new Point(xc-viewSize.width/2, yc-viewSize.height/2));
        revalidate();	// spIm.validate();
        spIm.paintAll(spIm.getGraphics());

        return true;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return panelSize;
    }

}
