package ru.nsu.ccfit.melnikov.view.components.menu;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AboutMenu extends JMenu implements MouseListener {
    private static final String MESSAGE = "This application is developed for filtering images.\n" +
            "Available filters: \n" +
            "Rotation, \n" +
            "Dithering(Floyd), \n" +
            "Dithering(Ordered), \n" +
            "Blur(Gaussian), \n" +
            "Grayshaded, \n" +
            "Watercolor effect, \n" +
            "Zoom, \n" +
            "Normal map construction, \n" +
            "Twirl effect, \n" +
            "Embossing, \n" +
            "Sharpening, \n" +
            "Sobel operator, \n" +
            "Roberts operator, \n" +
            "Gamma correction, \n" +
            "Inverse. \n ";

    public AboutMenu() {
        super("About");
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        JOptionPane.showMessageDialog(this, new JLabel(MESSAGE));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
