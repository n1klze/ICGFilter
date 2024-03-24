package ru.nsu.ccfit.melnikov.view;

import ru.nsu.ccfit.melnikov.controller.Controller;
import ru.nsu.ccfit.melnikov.model.Tools;
import ru.nsu.ccfit.melnikov.view.components.ParametersDialog.ResizeDialog;
import ru.nsu.ccfit.melnikov.view.components.buttons.ColoredButton;
import ru.nsu.ccfit.melnikov.view.components.buttons.IconButton;
import ru.nsu.ccfit.melnikov.view.components.ParametersDialog.FigureParametersDialog;
import ru.nsu.ccfit.melnikov.view.components.buttons.ToolButton;
import ru.nsu.ccfit.melnikov.view.components.menu.AboutMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private static final String TITLE = "ICG Paint";
    private static final Dimension MINIMUM_SIZE = new Dimension(640, 480);
    private static final Color[] MAIN_PALETTE_COLORS =
            {Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.WHITE};
    private final Controller controller = new Controller();
    private final JScrollPane scrollPane = new JScrollPane();
    private final Canvas canvas = new Canvas(controller, MINIMUM_SIZE, scrollPane);
    private final FigureParametersDialog parametersDialog = new FigureParametersDialog(controller);
    private final ResizeDialog resizeDialog = new ResizeDialog();
    private final Map<Tools, ToolButton> toolBarButtons = new HashMap<>();
    private final Map<Tools, JRadioButtonMenuItem> viewMenuToolButtons = new HashMap<>();

    public MainFrame() {
        setTitle(TITLE);
        setMinimumSize(MINIMUM_SIZE);
        setSize(660, 580);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setJMenuBar(createMenuBar());
        getContentPane().add(createToolBar(), BorderLayout.NORTH);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createAboutMenu());

        return menuBar;
    }

    private JMenu createFileMenu() {
        var file = new JMenu("File");
        var open = new JMenuItem("Open");
        open.addActionListener(e -> {
            controller.loadImage(canvas);
            scrollPane.revalidate();
        });
        var save = new JMenuItem("Save");
        save.addActionListener(e -> controller.saveImage(canvas));
        var settings = new JMenuItem("Settings") {
            {
                addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, parametersDialog,
                            "Options", JOptionPane.OK_CANCEL_OPTION);
                    if (JOptionPane.OK_OPTION == confirm) {
                        controller.setThickness(parametersDialog.getThickness());
                        controller.setNumOfAngles(parametersDialog.getNumOfAngles());
                        controller.setRadius(parametersDialog.getRadius());
                        controller.setRotation(parametersDialog.getRotation());
                    }
                });
            }
        };

        file.add(open);
        file.add(save);

        file.addSeparator();

        file.add(settings);

        file.addSeparator();

        file.add(new JMenuItem("Exit") {
            {
                addActionListener(e -> System.exit(0));
            }
        });

        return file;
    }

    private JMenu createViewMenu() {
        var view = new JMenu("View");
        var tools = new ButtonGroup();

        var eraser = new JRadioButtonMenuItem(Tools.ERASER.toString());
        eraser.addActionListener(e -> {
            canvas.setDefaultBackground();
            controller.setCurrentTool(Tools.ERASER, toolBarButtons, viewMenuToolButtons);
        });
        viewMenuToolButtons.put(Tools.ERASER, eraser);
        var pen = new JRadioButtonMenuItem(Tools.PEN.toString());
        pen.setSelected(true);
        pen.addActionListener(e -> controller.setCurrentTool(Tools.PEN, toolBarButtons, viewMenuToolButtons));
        viewMenuToolButtons.put(Tools.PEN, pen);
        var line = new JRadioButtonMenuItem(Tools.LINE.toString());
        line.addActionListener(e -> controller.setCurrentTool(Tools.LINE, toolBarButtons, viewMenuToolButtons));
        viewMenuToolButtons.put(Tools.LINE, line);
        var fill = new JRadioButtonMenuItem(Tools.FILL.toString());
        fill.addActionListener(e -> controller.setCurrentTool(Tools.FILL, toolBarButtons, viewMenuToolButtons));
        viewMenuToolButtons.put(Tools.FILL, fill);
        var polygon = new JRadioButtonMenuItem(Tools.POLYGON.toString());
        polygon.addActionListener(e -> controller.setCurrentTool(Tools.POLYGON, toolBarButtons, viewMenuToolButtons));
        viewMenuToolButtons.put(Tools.POLYGON, polygon);
        var star = new JRadioButtonMenuItem(Tools.STAR.toString());
        star.addActionListener(e -> controller.setCurrentTool(Tools.STAR, toolBarButtons, viewMenuToolButtons));
        viewMenuToolButtons.put(Tools.STAR, star);

        tools.add(eraser);
        tools.add(pen);
        tools.add(line);
        tools.add(fill);
        tools.add(polygon);
        tools.add(star);

        view.add(eraser);
        view.add(pen);
        view.add(line);
        view.add(fill);
        view.add(polygon);
        view.add(star);

        view.addSeparator();
        var resize = new JMenuItem("Resize");
        resize.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, resizeDialog,
                    "Resize", JOptionPane.OK_CANCEL_OPTION);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.resizeCanvas(canvas, resizeDialog.getWidth(), resizeDialog.getHeight());
                scrollPane.updateUI();
            }
        });
        view.add(resize);

        return view;
    }

    private JMenu createAboutMenu() {
        return new AboutMenu();
    }

    private JToolBar createToolBar() {
        var toolBar = new JToolBar();
        toolBar.setFloatable(false);

        var toolButtonGroup = new ButtonGroup();

        var eraser = new ToolButton(controller, Tools.ERASER, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.ERASER, eraser);
        eraser.addActionListener(e -> canvas.setDefaultBackground());
        toolBar.add(eraser);
        toolButtonGroup.add(eraser);

        toolBar.addSeparator();

        var pen = new ToolButton(controller, Tools.PEN, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.PEN, pen);
        pen.setSelected(true);
        toolBar.add(pen);
        toolButtonGroup.add(pen);
        var line = new ToolButton(controller, Tools.LINE, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.LINE, line);
        toolBar.add(line);
        toolButtonGroup.add(line);
        var fill = new ToolButton(controller, Tools.FILL, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.FILL, fill);
        toolBar.add(fill);
        toolButtonGroup.add(fill);

        toolBar.addSeparator();

        var polygon = new ToolButton(controller, Tools.POLYGON, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.POLYGON, polygon);
        toolBar.add(polygon);
        toolButtonGroup.add(polygon);
        var star = new ToolButton(controller, Tools.STAR, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.STAR, star);
        toolBar.add(star);
        toolButtonGroup.add(star);

        toolBar.addSeparator();

        JButton rotateButton = new JButton("R");
        rotateButton.addActionListener(e -> {
            controller.makeRotation(canvas);
        });
        toolBar.add(rotateButton);
        JButton ditherButtonAS = new JButton("D");
        ditherButtonAS.addActionListener(e -> {
            controller.ditherImageFloydAS(canvas);
        });
        toolBar.add(ditherButtonAS);
        JButton blurButton = new JButton("B");
        blurButton.addActionListener(e -> {
            controller.makeBlur(canvas);
        });
        toolBar.add(blurButton);
        JButton grayShadedButton = new JButton("GS");
        grayShadedButton.addActionListener(e -> {
            controller.makeGrayShaded(canvas);
        });
        toolBar.add(grayShadedButton);
        JButton waterColoredButton = new JButton("AQ");
        waterColoredButton.addActionListener(e -> {
            controller.makeWaterColored(canvas);
        });
        toolBar.add(waterColoredButton);
        JButton zoomButton = new JButton("Z");
        zoomButton.addActionListener(e -> {
            controller.makeZoom(canvas);
        });
        toolBar.add(zoomButton);

        toolBar.addSeparator();

        var colorButtonGroup = new ButtonGroup();
        for (var color : MAIN_PALETTE_COLORS) {
            var coloredButton = new ColoredButton(controller, color);
            toolBar.add(coloredButton);
            colorButtonGroup.add(coloredButton);
        }

        toolBar.addSeparator();

        var palette = new IconButton(Tools.PALETTE.getIconPath());
        palette.addActionListener(e -> {
            controller.setCurrentColor(
                    JColorChooser.showDialog(null, "Changing the palette", controller.getCurrentColor()));
            palette.setSelected(false);
        });
        toolBar.add(palette);

        return toolBar;
    }

    public void display() {
        setVisible(true);
    }
}
