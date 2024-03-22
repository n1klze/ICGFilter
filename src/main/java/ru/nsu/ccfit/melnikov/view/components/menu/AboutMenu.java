package ru.nsu.ccfit.melnikov.view.components.menu;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AboutMenu extends JMenu implements MouseListener {
    private static final String MESSAGE = """
            <html>
                <h1>ICG Paint<h1>
                <div>
                    Paint utility with 5 image editing tools:
                    <ul>
                        <li>Pen - draws a line following mouse movement.</li>
                        <li>Fill - fills an area of pixels of one color surrounded by pixels of another color</li>
                        <li>Line - draws a line with specified thickness. To draw a line, select instrument, click and drag your mouse.</li>
                        <li>Polygon - draws a regular polygon with specified thickness, rotation angle, radius and number of angles.</li>
                        <li>Star - draws a regular star with specified thickness, rotation angle, radius and number of angles.</li>
                    </ul>
                    There are also some addition options:
                    <ul>
                        <li>Clear - clears the canvas.</li>
                        <li>Fast colors - select one of the colors located on the toolbar.</li>
                        <li>Palette - color chooser.</li>
                    </ul>
                </div>
                <div>
                    Developed by Nikita Melnikov, 2024
                </div>
            </html>
            """;

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
