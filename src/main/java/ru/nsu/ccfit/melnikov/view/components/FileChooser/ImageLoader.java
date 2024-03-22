package ru.nsu.ccfit.melnikov.view.components.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageLoader extends JFileChooser {
    public ImageLoader() {
        removeChoosableFileFilter(getFileFilter());
        addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
        addChoosableFileFilter(new FileNameExtensionFilter("JPEG (*.jpg;*.jpeg;*.jpe;*.jfif)", "jpg","jpeg","jpe","jfif"));
        addChoosableFileFilter(new FileNameExtensionFilter("Bitmap pictures (*.bmp)", "bmp"));
        addChoosableFileFilter(new FileNameExtensionFilter("GIF (*.gif)", "gif"));
    }
}
