package ru.nsu.ccfit.melnikov;

import ru.nsu.ccfit.melnikov.view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        /*for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }*/
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        var mainFrame = new MainFrame();
        mainFrame.display();
    }
}