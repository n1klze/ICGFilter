package ru.nsu.ccfit.melnikov.view.components.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ImageSaver extends JFileChooser {
    public ImageSaver() {
        removeChoosableFileFilter(getFileFilter());
        addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setSelectedFile(new File("anonymous.png"));
    }
}
