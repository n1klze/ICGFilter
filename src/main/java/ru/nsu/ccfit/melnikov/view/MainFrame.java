package ru.nsu.ccfit.melnikov.view;

import ru.nsu.ccfit.melnikov.controller.Controller;
import ru.nsu.ccfit.melnikov.model.Filters;
import ru.nsu.ccfit.melnikov.model.Tools;
import ru.nsu.ccfit.melnikov.view.components.ParametersDialog.*;

import ru.nsu.ccfit.melnikov.view.components.buttons.ColoredButton;
import ru.nsu.ccfit.melnikov.view.components.buttons.IconButton;
import ru.nsu.ccfit.melnikov.view.components.buttons.ToolButton;
import ru.nsu.ccfit.melnikov.view.components.menu.AboutMenu;

import javax.swing.*;
import java.awt.*;
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
    private final ParametersDialog parametersDialog = new ParametersDialog(controller);
    private final ResizeDialog resizeDialog = new ResizeDialog();
    private final BlurDialog blurDialog = new BlurDialog();
    private final ZoomDialog zoomDialog = new ZoomDialog();
    private final GammaDialog gammaDialog = new GammaDialog();
    private final BorderDialog borderDialog = new BorderDialog();
    private final DitheringDialog ditheringDialog = new DitheringDialog();
    private final OrderedDitheringDialog orderedDitheringDialog = new OrderedDitheringDialog();
    private final AngleDialog rotationDialog = new AngleDialog();
    private final AngleDialog twirlDialog = new AngleDialog();
    private final Map<Tools, ToolButton> toolBarButtons = new HashMap<>();
    private final Map<Tools, JRadioButtonMenuItem> viewMenuToolButtons = new HashMap<>();

    public MainFrame() {
        setTitle(TITLE);
        setMinimumSize(MINIMUM_SIZE);
        setSize(670, 585);
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
        menuBar.add(createFiltersMenu());
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
                            "Options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (JOptionPane.OK_OPTION == confirm) {
                        controller.setThickness(parametersDialog.getThickness());
                        controller.setNumOfAngles(parametersDialog.getNumOfAngles());
                        controller.setRadius(parametersDialog.getRadius());
                        controller.setRotation(parametersDialog.getRotation());
                        controller.setCurrentInterpolationType(parametersDialog.getInterpolationTypeChooser());
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

        var cursor = new JRadioButtonMenuItem(Tools.CURSOR.toString());
        cursor.addActionListener(e -> controller.setCurrentTool(Tools.CURSOR, toolBarButtons, viewMenuToolButtons));
        cursor.setSelected(true);
        viewMenuToolButtons.put(Tools.CURSOR, cursor);

        var eraser = new JRadioButtonMenuItem(Tools.ERASER.toString());
        eraser.addActionListener(e -> {
            canvas.setDefaultBackground();
            controller.setCurrentTool(Tools.ERASER, toolBarButtons, viewMenuToolButtons);
        });
        viewMenuToolButtons.put(Tools.ERASER, eraser);

        var pen = new JRadioButtonMenuItem(Tools.PEN.toString());
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

        tools.add(cursor);
        tools.add(eraser);
        tools.add(pen);
        tools.add(line);
        tools.add(fill);
        tools.add(polygon);
        tools.add(star);

        view.add(cursor);
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
                    "Resize", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.resizeCanvas(canvas, resizeDialog.getWidth(), resizeDialog.getHeight());
                scrollPane.updateUI();
            }
        });
        view.add(resize);

        return view;
    }

    private JMenu createFiltersMenu() {
        var filters = new JMenu("Filters");

        var zoom = new JMenuItem(Filters.ZOOM.toString());

        zoom.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, zoomDialog,
                    Filters.ZOOM.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeZoom(canvas, zoomDialog.getZoomSize());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });

        filters.add(zoom);

        var rotation = new JMenuItem(Filters.ROTATION.toString());
        rotation.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, rotationDialog,
                    Filters.ROTATION.toString(), JOptionPane.OK_CANCEL_OPTION);
            if (JOptionPane.OK_OPTION == confirm)
                controller.makeRotation(canvas, rotationDialog.getAngle());
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(rotation);

        var floydDithering = new JMenuItem(Filters.FLOYD_STEINBERG_DITHERING.toString());
        floydDithering.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, ditheringDialog,
                    Filters.FLOYD_STEINBERG_DITHERING.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                if(ditheringDialog.getDitheringType() == DitheringDialog.Types.Silitskiy)
                    controller.ditherImageFloydAS(canvas, ditheringDialog.getQuantsCountChooserR(),
                            ditheringDialog.getQuantsCountChooserG(), ditheringDialog.getQuantsCountChooserB());
                else{
                    controller.ditherImageFloydNM(canvas, ditheringDialog.getQuantsCountChooserR(),
                            ditheringDialog.getQuantsCountChooserG(), ditheringDialog.getQuantsCountChooserB());
                }
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(floydDithering);

        var orderedDithering = new JMenuItem(Filters.ORDERED_DITHERING.toString());
        orderedDithering.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, orderedDitheringDialog,
                    Filters.ORDERED_DITHERING.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                if(orderedDitheringDialog.getDitheringType() == DitheringDialog.Types.Silitskiy)
                    controller.ditherImageOrderedAS(canvas, orderedDitheringDialog.getQuantsCountChooserR(),
                            orderedDitheringDialog.getQuantsCountChooserG(), orderedDitheringDialog.getQuantsCountChooserB());
                else{
                    controller.ditherImageOrderedNM(canvas, orderedDitheringDialog.getQuantsCountChooserR(),
                            orderedDitheringDialog.getQuantsCountChooserG(), orderedDitheringDialog.getQuantsCountChooserB());
                }
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(orderedDithering);

        var blur = new JMenuItem(Filters.BLUR.toString());
        blur.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, blurDialog,
                    Filters.BLUR.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeBlur(canvas, blurDialog.getMaskSize());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(blur);

        var grayscale = new JMenuItem(Filters.GRAYSCALE.toString());
        grayscale.addActionListener(e -> controller.makeGrayShaded(canvas));
        filters.add(grayscale);

        var watercolor = new JMenuItem(Filters.WATERCOLOR.toString());
        watercolor.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            controller.makeWaterColored(canvas);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(watercolor);

        var normalMap = new JMenuItem(Filters.NORMAL_MAP.toString());
        normalMap.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            controller.makeNormalMap(canvas);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(normalMap);

        var twirl = new JMenuItem(Filters.TWIRL.toString());
        twirl.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, twirlDialog,
                    Filters.TWIRL.toString(), JOptionPane.OK_CANCEL_OPTION);
            if (JOptionPane.OK_OPTION == confirm)
                controller.makeTwirl(canvas, rotationDialog.getAngle());
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(twirl);

        var embossing = new JMenuItem(Filters.EMBOSSING.toString());
        embossing.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            controller.makeEmbossing(canvas);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(embossing);

        var sharpness = new JMenuItem(Filters.SHARPNESS.toString());
        sharpness.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            controller.makeSharpness(canvas);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(sharpness);

        var sobel = new JMenuItem(Filters.SOBEL.toString());
        sobel.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, borderDialog,
                    Filters.SOBEL.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeSobel(canvas, borderDialog.getThreshold());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(sobel);

        var roberts = new JMenuItem(Filters.ROBERTS.toString());
        roberts.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, borderDialog,
                    Filters.ROBERTS.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeRoberts(canvas, borderDialog.getThreshold());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(roberts);

        var gamma = new JMenuItem(Filters.GAMMA.toString());
        gamma.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, gammaDialog,
                    Filters.GAMMA.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeGamma(canvas, gammaDialog.getGamma());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filters.add(gamma);

        var inverse = new JMenuItem(Filters.INVERSE.toString());
        inverse.addActionListener(e -> controller.makeInverse(canvas));
        filters.add(inverse);

        return filters;
    }

    private JMenu createAboutMenu() {
        return new AboutMenu();
    }

    private JToolBar createToolBar() {
        var toolBar = new JToolBar();
        toolBar.setFloatable(false);

        var toolButtonGroup = new ButtonGroup();

        IconButton openFile = new IconButton("/icon-opefile.png");
        openFile.addActionListener(e -> controller.loadImage(canvas));
        toolBar.add(openFile);

        IconButton undo = new IconButton("/icon-backarrow.png");
        undo.addActionListener(e -> canvas.undo());
        toolBar.add(undo);

        IconButton fit = new IconButton("/icon-fitimage.png");
        fit.addActionListener(e -> canvas.fitToScreen());
        toolBar.add(fit);

        toolBar.addSeparator();

        var cursor = new ToolButton(controller, Tools.CURSOR, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.CURSOR, cursor);
        toolBar.add(cursor);
        cursor.setSelected(true);
        toolButtonGroup.add(cursor);

        toolBar.addSeparator();

        var eraser = new ToolButton(controller, Tools.ERASER, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.ERASER, eraser);
        eraser.addActionListener(e -> canvas.setDefaultBackground());
        toolBar.add(eraser);
        toolButtonGroup.add(eraser);

        toolBar.addSeparator();

        var pen = new ToolButton(controller, Tools.PEN, toolBarButtons, viewMenuToolButtons);
        toolBarButtons.put(Tools.PEN, pen);
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

        IconButton rotateButton = new IconButton(Filters.ROTATION.getPict());
        rotateButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, rotationDialog,
                    Filters.ROTATION.toString(), JOptionPane.OK_CANCEL_OPTION);
            if (JOptionPane.OK_OPTION == confirm)
                controller.makeRotation(canvas, rotationDialog.getAngle());
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(rotateButton);

        IconButton ditherButton = new IconButton(Filters.FLOYD_STEINBERG_DITHERING.getPict());
        ditherButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, ditheringDialog,
                    Filters.FLOYD_STEINBERG_DITHERING.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                if(ditheringDialog.getDitheringType() == DitheringDialog.Types.Silitskiy)
                    controller.ditherImageFloydAS(canvas, ditheringDialog.getQuantsCountChooserR(),
                            ditheringDialog.getQuantsCountChooserG(), ditheringDialog.getQuantsCountChooserB());
                else{
                    controller.ditherImageFloydNM(canvas, ditheringDialog.getQuantsCountChooserR(),
                            ditheringDialog.getQuantsCountChooserG(), ditheringDialog.getQuantsCountChooserB());
                }
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(ditherButton);
        IconButton orderedDitherButton = new IconButton(Filters.ORDERED_DITHERING.getPict());
        orderedDitherButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, orderedDitheringDialog,
                    Filters.ORDERED_DITHERING.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                if(orderedDitheringDialog.getDitheringType() == DitheringDialog.Types.Silitskiy)
                    controller.ditherImageOrderedAS(canvas, orderedDitheringDialog.getQuantsCountChooserR(),
                            orderedDitheringDialog.getQuantsCountChooserG(), orderedDitheringDialog.getQuantsCountChooserB());
                else{
                    controller.ditherImageOrderedNM(canvas, orderedDitheringDialog.getQuantsCountChooserR(),
                            orderedDitheringDialog.getQuantsCountChooserG(), orderedDitheringDialog.getQuantsCountChooserB());
                }
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(orderedDitherButton);

        IconButton blurButton = new IconButton(Filters.BLUR.getPict());
        blurButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, blurDialog,
                    Filters.BLUR.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeBlur(canvas, blurDialog.getMaskSize());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(blurButton);

        IconButton grayShadedButton = new IconButton(Filters.GRAYSCALE.getPict());
        grayShadedButton.addActionListener(e -> controller.makeGrayShaded(canvas));
        toolBar.add(grayShadedButton);

        IconButton waterColoredButton = new IconButton(Filters.WATERCOLOR.getPict());
        waterColoredButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            controller.makeWaterColored(canvas);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(waterColoredButton);

        IconButton zoomButton = new IconButton(Filters.ZOOM.getPict());
        zoomButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, zoomDialog,
                    Filters.ZOOM.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeZoom(canvas, zoomDialog.getZoomSize());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(zoomButton);

        IconButton normalMapButton = new IconButton(Filters.NORMAL_MAP.getPict());
        normalMapButton.addActionListener(e -> controller.makeNormalMap(canvas));
        toolBar.add(normalMapButton);
      
        JButton twirlButton = new IconButton(Filters.TWIRL.getPict());
        twirlButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, twirlDialog,
                    Filters.TWIRL.toString(), JOptionPane.OK_CANCEL_OPTION);
            if (JOptionPane.OK_OPTION == confirm)
                controller.makeTwirl(canvas, twirlDialog.getAngle());
        });
        toolBar.add(twirlButton);
      
        IconButton embossingButton = new IconButton(Filters.EMBOSSING.getPict());
        embossingButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            controller.makeEmbossing(canvas);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(embossingButton);

        IconButton sharpnessButton = new IconButton(Filters.SHARPNESS.getPict());
        sharpnessButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            controller.makeSharpness(canvas);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(sharpnessButton);

        IconButton sobelButton = new IconButton(Filters.SOBEL.getPict());
        sobelButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, borderDialog,
                    Filters.SOBEL.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeSobel(canvas, borderDialog.getThreshold());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(sobelButton);

        IconButton robertsButton = new IconButton(Filters.ROBERTS.getPict());
        robertsButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, borderDialog,
                    Filters.ROBERTS.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeRoberts(canvas, borderDialog.getThreshold());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(robertsButton);

        IconButton gammaButton = new IconButton(Filters.GAMMA.getPict());
        gammaButton.addActionListener(e -> {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int confirm = JOptionPane.showConfirmDialog(this, gammaDialog,
                    Filters.GAMMA.toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (JOptionPane.OK_OPTION == confirm) {
                controller.makeGamma(canvas, gammaDialog.getGamma());
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        toolBar.add(gammaButton);

        IconButton inverseButton = new IconButton(Filters.INVERSE.getPict());
        inverseButton.addActionListener(e -> controller.makeInverse(canvas));
        toolBar.add(inverseButton);

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
