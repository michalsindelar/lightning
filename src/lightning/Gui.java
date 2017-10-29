package lightning;

import com.sun.deploy.util.SystemUtils;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

@SuppressWarnings("serial")
public class Gui extends JFrame {
    private Graphics2D g2d;
    public JPanel container;
    private JPanel buttonPanel;

    private static String curveType = "sin";

    private JButton renderButton;
    private JButton exportSettingsButton;
    private JButton importSettingsButton;


    private JButton curveColorPicker;
    private JButton backgroundColorPicker;

    private JSlider speedSlider;
    private JSlider jumpSlider;
    private JSlider moveSlider;
    private JSlider iterationArgSlider;
    private JSlider opacityArgSlider;
    private JSlider offsetXArgSlider;

    private JCheckBox gradient;
    private JButton gradientColor1Button;
    private JButton gradientColor2Button;
    private JLabel titleGradientTypes;

    private JComboBox<String> gradientBox;
    private JComboBox<String> curveTypeBox;

    private BufferedGraphics renderImage;

    private JPanel aboutPanel;
    private JPanel curvePanel;
    private JPanel colorPanel;
    private JPanel curveTypePanel;
    private JPanel outputPanel;


    private JTextField renderWidthList;
    private JTextField renderHeightList;

    private static int speedArg = 50;
    private static int singleCurveHeight = 250;
    private static int iteration = 1;
    private static float opacity = 0.25f;
    private static int offsetX = -100;

    private int renderWidth = 2400;
    private int renderHeight = 1800;

    private int previewWidth = 800;
    private int previewHeight = 600;

    private static int move = 100;

    private static Color backgroundColor = new Color(0, 0, 0);
    private static Color curveColor = new Color(106, 142, 200);

    private static Color gradientColor1 = new Color(255,255,255);
    private static Color gradientColor2 = new Color(0,0,0);

    private static boolean drawGradient = false;

    private static String gradientType = "radial";


    private JLabel previewWindow = new JLabel();

    public Gui() {
        try {
            // Set System L&F
            if ((System.getProperty("os.name").startsWith("Linux"))) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            }
            else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }


        setContainer();
        setButtonPanel();
        setPreviewWindow();


        getContentPane().setLayout(null);

        setSpeedSlider();
        setJumpSlider();
        setIterationArgSlider();
        setOpacityArgSlider();
        setOffsetXArgSlider();

        setGradientCheckBox();
        setGradientColor1Button();
        setGradientColor2Button();
        setBoxGradient();

        setMoveArgSlider();
        setCurveColorButton();
        setBackgroundColorButton();
        setExportButton();
        setImportButton();

        setRenderButton();

        setTabs();

    }
    private void setContainer () {
        container =  new JPanel();
        container.setBounds(0, 0, previewWidth, previewHeight);
        container.setLayout(null);
        container.setBackground(backgroundColor);
        container.setOpaque(true);
    }
    private void setButtonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setBounds(800, 0, 200, 800);
        buttonPanel.setLayout(null);
        getContentPane().add(buttonPanel);

    }
    public void setPreviewWindow () {
        previewWindow = new JLabel() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics og) {
                super.paintComponent(og);
                g2d = (Graphics2D)og;


                Sin sinPaint = new Sin(g2d);
                ThreadWorker threadWorker = new ThreadWorker(iteration, sinPaint);
                try {
                    lookBusy();
                    threadWorker.doInBackground();
                    lookReady();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        previewWindow.setBounds(0, 0, 800, 600);
        container.add(previewWindow);
        getContentPane().add(container);
    }

    private void setCurveColorButton() {
        curveColorPicker = new JButton("Choose curve color");

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Color initial = curveColor;
                Color newCurveColor = JColorChooser.showDialog(null,"Choose curve curve color", initial);
                if (newCurveColor != null) {
                    curveColor = newCurveColor;
                }
                container.repaint();
            }
        };
        curveColorPicker.addActionListener(actionListener);
    }

    private void setBackgroundColorButton() {
        backgroundColorPicker = new JButton("Choose background color");

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Color initial = curveColor;
                Color newCurveColor = JColorChooser.showDialog(null,"Choose background color", initial);
                if (newCurveColor != null) {
                    backgroundColor = newCurveColor;
                }
                container.setBackground(backgroundColor);
                container.repaint();
            }
        };
        backgroundColorPicker.addActionListener(actionListener);
    }

    private void setRenderButton() {
        renderButton = new JButton("Render image");
        renderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renderWidth = Integer.parseInt(renderWidthList.getText());
                renderHeight = Integer.parseInt(renderHeightList.getText());

                renderImage = new BufferedGraphics(renderWidth, renderHeight);
                lookBusy();
                renderImage.updatePaint(iteration, backgroundColor);
                lookReady();

                JFileChooser saveFile = new JFileChooser();
                saveFile.setDialogTitle("Export image");
                saveFile.showSaveDialog(null);

                try {
                    File path = saveFile.getSelectedFile();
                    renderImage.save(path);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        buttonPanel.add(renderButton);
    }

    private void setExportButton() {
        exportSettingsButton = new JButton("Save file");
        exportSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser saveFile = new JFileChooser();
                saveFile.setDialogTitle("Export settings");
                saveFile.showSaveDialog(null);

                OutputSettings exportSettings = new OutputSettings();
                try {
                    File path = saveFile.getSelectedFile();
                    exportSettings.export(path.getPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    private void setImportButton() {
        importSettingsButton = new JButton("Load file");
        importSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser loadFile = new JFileChooser();
                loadFile.setDialogTitle("Load settings");
                loadFile.showSaveDialog(null);

                try {
                    File path = loadFile.getSelectedFile();
                    importSettings(path.getPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    public void importSettings(String path) {
        String line;
        String lineSplit[];

        try (
                InputStream fis = new FileInputStream(path);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr)
        ) {

            /* Curve type */
            line = br.readLine();
            lineSplit = line.split(":");
            String curveType = lineSplit[1].replace(" ", "");
            String[] curveTypeArray = {"sin", "tan", "upDown"};
            int index = Arrays.asList(curveTypeArray).indexOf(curveType);
            curveTypeBox.setSelectedIndex(index);
            setCurveType(curveType);

            /* Iteration */
            line = br.readLine();
            lineSplit = line.split(":");
            int iteration = Integer.parseInt(lineSplit[1].replace(" ", ""));
            iterationArgSlider.setValue(iteration);
            setIteration(iteration);

            /* Speed */
            line = br.readLine();
            lineSplit = line.split(":");
            int speed = Integer.parseInt(lineSplit[1].replace(" ", ""));
            speedSlider.setValue(speed);
            setSpeedArg(speed);

            /* Height */
            line = br.readLine();
            lineSplit = line.split(":");
            int height = Integer.parseInt(lineSplit[1].replace(" ", ""));
            jumpSlider.setValue(height);
            setSingleCurveHeight(height);

            /* X-offset */
            line = br.readLine();
            lineSplit = line.split(":");
            int xOffset = Integer.parseInt(lineSplit[1].replace(" ", ""));
            offsetXArgSlider.setValue(xOffset);
            setOffsetX(xOffset);

            /* Horizontal step move */
            line = br.readLine();
            lineSplit = line.split(":");
            int move = Integer.parseInt(lineSplit[1].replace(" ", ""));
            moveSlider.setValue(move);
            setMove(move);

            /* Opacity */
            line = br.readLine();
            lineSplit = line.split(":");
            float opacity = Float.parseFloat(lineSplit[1].replace(" ", ""));
            opacityArgSlider.setValue((int) (opacity * 100));
            setCurveOpacity(opacity);

            /* Curve color */
            line = br.readLine();
            lineSplit = line.split(":");
            Color curveColor  = Color.decode(lineSplit[1].replace(" ", ""));
            setCurveColor(curveColor);

            /* Background color */
            line = br.readLine();
            lineSplit = line.split(":");
            Color backgroundColor  = Color.decode(lineSplit[1].replace(" ", ""));
            setBackgroundColor(backgroundColor);
            container.setBackground(backgroundColor);

            /* Gradient loading */
            line = br.readLine();
            lineSplit = line.split(" ");
            String gradientActive = lineSplit[1];

            if (gradientActive.equals("enabled")) {
                line = br.readLine();
                lineSplit = line.split(":");
                String gradientType = lineSplit[1].replace(" ", "");

                setGradientType(gradientType);

                drawGradient = true;

                gradientColor1Button.setVisible(true);
                gradientColor2Button.setVisible(true);
                gradientBox.setVisible(true);
                titleGradientTypes.setVisible(true);


                /* Gradient color 1 */
                line = br.readLine();
                lineSplit = line.split(":");
                Gui.gradientColor1 = Color.decode(lineSplit[1].replace(" ", ""));

                /* Gradient color 1 */
                line = br.readLine();
                lineSplit = line.split(":");
                Gui.gradientColor2 = Color.decode(lineSplit[1].replace(" ", ""));

            }

            container.repaint();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSpeedSlider () {
        speedSlider = new JSlider(30, 400);
        speedSlider.setBounds(5, 20, 190, 30);
        speedSlider.setValue(50);
        speedSlider.setPaintLabels(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintTrack(true);
        //buttonPanel.add(speedSlider);

        speedSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                speedArg = speedSlider.getValue();
                container.repaint();
            }
        });
    }

    private void setJumpSlider () {
        jumpSlider = new JSlider(30, 600);
        jumpSlider.setBounds(5, 70, 190, 20);
        jumpSlider.setValue(singleCurveHeight);
        //buttonPanel.add(jumpSlider);

        jumpSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                singleCurveHeight = jumpSlider.getValue();
                container.repaint();
            }
        });
    }
    private void setIterationArgSlider () {
        iterationArgSlider = new JSlider(0, 20);
        iterationArgSlider.setBounds(5, 110, 190, 20);
        iterationArgSlider.setValue(iteration);

        iterationArgSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                iteration = (iterationArgSlider.getValue());
                container.repaint();
            }
        });
    }
    private void setOpacityArgSlider () {
        opacityArgSlider = new JSlider(0, 50);
        opacityArgSlider.setBounds(5, 150, 190, 20);
        opacityArgSlider.setValue((int) (opacity * 100));
        //buttonPanel.add(opacityArgSlider);

        opacityArgSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                opacity = (opacityArgSlider.getValue());
                opacity /= 100;
                container.repaint();
            }
        });
    }

    private void setMoveArgSlider () {
        moveSlider = new JSlider(0, 800);
        //moveSlider.setBounds(5, 150, 190, 20);
        moveSlider.setValue(move);
        //buttonPanel.add(opacityArgSlider);

        moveSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                move = moveSlider.getValue();
                container.repaint();
            }
        });
    }


    private void setOffsetXArgSlider () {
        offsetXArgSlider = new JSlider(-200, 0);
        offsetXArgSlider.setBounds(5, 190, 190, 20);
        offsetXArgSlider.setValue(-100);
        //buttonPanel.add(offsetXArgSlider);

        offsetXArgSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me) {
                offsetX = (offsetXArgSlider.getValue());
                container.repaint();
            }
        });
    }
    private void setGradientCheckBox () {
        gradient = new JCheckBox("Enable gradient options");
        buttonPanel.add(gradient);
        gradient.setBounds(135, 210, 20, 20);
        gradient.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                drawGradient = gradient.isSelected();

                gradientColor1Button.setVisible(gradient.isSelected());
                gradientColor2Button.setVisible(gradient.isSelected());
                gradientBox.setVisible(gradient.isSelected());
                titleGradientTypes.setVisible(gradient.isSelected());

                //colors by default like curve and bg
                gradientColor1 = getCurveColor();
                gradientColor2 = getBgColor();

                container.repaint();
            }
        });
    }
    private void setGradientColor1Button() {
        gradientColor1Button = new JButton("Color 1");
        buttonPanel.add(gradientColor1Button);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Color newColor = JColorChooser.showDialog(null,"Choose color", gradientColor1);
                if (newColor != null) {
                    gradientColor1 = newColor;
                }
                container.repaint();
            }
        };
        gradientColor1Button.addActionListener(actionListener);
        gradientColor1Button.setVisible(false);
    }
    private void setGradientColor2Button() {
        gradientColor2Button = new JButton("Color 2");
        buttonPanel.add(gradientColor2Button);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Color newColor = JColorChooser.showDialog(null,"Choose color", gradientColor2);
                if (newColor != null) {
                    gradientColor2 = newColor;
                }
                container.repaint();
            }
        };
        gradientColor2Button.addActionListener(actionListener);
        gradientColor2Button.setVisible(false);
    }
    private void setBoxGradient () {
        String[] gradientTypeArray = {"radial", "radial-repeat", "linear"};
        gradientBox = new JComboBox<>(gradientTypeArray);
        gradientBox.setSelectedIndex(0);
        buttonPanel.add(gradientBox);

        gradientBox.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        gradientType = (String) gradientBox.getSelectedItem();
                        container.repaint();
                    }
                }
        );
        gradientBox.setVisible(false);
    }

    private void setTabs () {
        JTabbedPane tabDialog = new JTabbedPane();
        tabDialog.setBounds(0, 0, 200, 600);

        setShapePanel();
        setColorPanel();
        setCurveTypePanel();
        setOuputPanel();
        setAboutPanel();

        tabDialog.add(this.curvePanel);
        tabDialog.add(this.colorPanel);
        tabDialog.add(this.curveTypePanel);
        tabDialog.add(this.outputPanel);
        tabDialog.add(this.aboutPanel);

        buttonPanel.add(tabDialog);
    }

    private void setShapePanel() {
        JPanel curvePanel = new JPanel();
        curvePanel.setName("Shape");


        curvePanel.add(title("Iteration"));
        curvePanel.add(iterationArgSlider);

        curvePanel.add(title("Speed"));
        curvePanel.add(speedSlider);

        curvePanel.add(title("Height"));
        curvePanel.add(jumpSlider);

        curvePanel.add(title("X-offset"));
        curvePanel.add(offsetXArgSlider);

        curvePanel.add(title("Horizontal step move"));
        curvePanel.add(moveSlider);

        this.curvePanel = curvePanel;
    }

    private void setColorPanel() {
        colorPanel = new JPanel();
        colorPanel.setName("Color");

        colorPanel.add(title("Opacity"));
        colorPanel.add(opacityArgSlider);

        colorPanel.add(title("Curve color"));
        colorPanel.add(curveColorPicker);
        colorPanel.add(title("Background color"));
        colorPanel.add(backgroundColorPicker);

        colorPanel.add(title("Gradient managment"));
        colorPanel.add(gradient);
        colorPanel.add(gradientColor1Button);
        colorPanel.add(gradientColor2Button);


        titleGradientTypes = title("Type of gradient");
        titleGradientTypes.setVisible(false);
        colorPanel.add(titleGradientTypes);

        colorPanel.add(gradientBox);

    }

    private void setCurveTypePanel() {
        setCurveType();
        curveTypePanel = new JPanel();
        curveTypePanel.setName("Curve type");
        curveTypePanel.add(title("Select type of curve"));
        curveTypePanel.add(curveTypeBox);
    }

    private void setOuputPanel() {
        outputPanel = new JPanel();
        outputPanel.setName("Output");
        setRenderWidth();
        setRenderHeight();

        outputPanel.add(title("Render width"));
        outputPanel.add(renderWidthList);

        outputPanel.add(title("Render height"));
        outputPanel.add(renderHeightList);
        outputPanel.add(renderButton);

        outputPanel.add(title("Export settings"));
        outputPanel.add(exportSettingsButton);

        outputPanel.add(title("Import settings"));
        outputPanel.add(importSettingsButton);
    }

    private void setAboutPanel() {
        JPanel aboutPanel = new JPanel();
        aboutPanel.setName("About");

        String style= "<style> body {width: 135px;} h1 {font-size: 15pt;} h2 {font-size: 14pt;} h3 {font-size: 13pt; margin-bottom: 0px; padding-bottom: 0px;} ul {padding-left: 0; margin-left: 0px;}</style>";
        String headlineLightning = "<h1>Lightning</h1>";
        String headlineAbout = "<h2>About project Lightning</h2>";
        String howToUse = "<h3>How to use</h3>";
        String howToUseDescription = "<ul><li>At first you can choose from 3 types of curves in panel <em>Curve type</em>.</li>";
        howToUseDescription += "<li>To adjust shape please use sliders in <em>Shape</em> panel.</li>";
        howToUseDescription += "<li>For color and gradient adjustment open <em>Color</em> panel.";
        howToUseDescription += "<li>Finally, after all customization render and export image with custom resolution using <em>Output</em> panel.";
        howToUseDescription += "</ul>";

        String aboutDescription = "<p>Project <strong>Lightning</strong> was created as a final project for course <strong>PV097</strong> at <strong>Faculty Informatics</strong>, <strong>Masaryk university</strong> in autumn semester 2014 by <strong>Michal Šindelář</strong> (422355).</p>";
        String headlineUse = "<h2>Terms of use</h2>";
        String useDescription = "For any commercial use please contact author on michalsindelar@protonmail.ch.";
        JLabel description = new JLabel("<html>"+style+"<div>" + headlineLightning + headlineAbout + aboutDescription + howToUse + howToUseDescription + headlineUse + useDescription + "</div></html>");


        aboutPanel.add(description);

        this.aboutPanel = aboutPanel;
    }

    private void setRenderWidth () {
        renderWidthList = new JTextField();
        renderWidthList.setText("1920");
    }
    private void setRenderHeight () {
        renderHeightList = new JTextField();
        renderHeightList.setText("1050");
    }


    private JLabel title(String title) {
        JLabel titleLabel = new JLabel();
        titleLabel.setText(title);
        return titleLabel;
    }

    private void setCurveType () {
        String[] curveTypeArray = {"sin", "tan", "upDown"};
        curveTypeBox = new JComboBox<>(curveTypeArray);
        curveTypeBox.setSelectedIndex(0);
        curveTypeBox.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        gradientType = (String) gradientBox.getSelectedItem();

                        curveType = (String) curveTypeBox.getSelectedItem();
                        container.repaint();
                    }
                }
        );
        curveTypeBox.setVisible(true);
    }


    private void lookBusy(){
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        buttonPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }

    private void lookReady(){
        container.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        buttonPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public String toString() {
        String settingsString = "";

        settingsString += "Curve type: ";
        settingsString += curveType;
        settingsString += System.lineSeparator();

        settingsString += "Iteration: ";
        settingsString += getIteration();
        settingsString += System.lineSeparator();

        settingsString += "Speed: ";
        settingsString += getSpeedArg();
        settingsString += System.lineSeparator();

        settingsString += "Height: ";
        settingsString += getJumpArg();
        settingsString += System.lineSeparator();

        settingsString += "X-offset: ";
        settingsString += getOffsetX();
        settingsString += System.lineSeparator();

        settingsString += "Horizontal step move: ";
        settingsString += getMove();
        settingsString += System.lineSeparator();

        settingsString += "Opacity: ";
        settingsString += getCurveOpacity();
        settingsString += System.lineSeparator();

        settingsString += "Curve color: ";
        settingsString += getCurveColor().getRGB();
        settingsString += System.lineSeparator();

        settingsString += "Background color: ";
        settingsString += getBgColor().getRGB();
        settingsString += System.lineSeparator();

        if (isDrawGradient()) {
            settingsString += "Gradient enabled ";
            settingsString += System.lineSeparator();

            settingsString += "Gradient type: ";
            settingsString += getGradientType();
            settingsString += System.lineSeparator();

            settingsString += "Color 1: ";
            settingsString += getGradientColor1().getRGB();
            settingsString += System.lineSeparator();

            settingsString += "Color 2: ";
            settingsString += getGradientColor2().getRGB();
            settingsString += System.lineSeparator();
        }
        else {
            settingsString += "Gradient disabled ";
        }

        return settingsString;
    }

    public int getIteration() {
        return iterationArgSlider.getValue();
    }

    public int getSpeedArg() {
        return speedArg;
    }

    public int getJumpArg() {
        return singleCurveHeight;
    }

    public float getCurveOpacity() {
        return opacity;
    }



    public Color getBgColor() {
        return backgroundColor;
    }

    public Color getCurveColor() {
        return curveColor;
    }

    public static int getOffsetX() {
        return offsetX;
    }

    public boolean isDrawGradient() {
        return drawGradient;
    }

    public static Color getGradientColor1() {
        return gradientColor1;
    }

    public static Color getGradientColor2() {
        return gradientColor2;
    }

    public static String getGradientType() {
        return gradientType;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public static String getCurveType() {
        return curveType;
    }

    public int getMove() {
        return move;
    }


    public static void setCurveType(String curveType) {
        Gui.curveType = curveType;
    }

    public void setIteration(int iteration) {
        Gui.iteration = iteration;
    }

    public static void setSpeedArg(int speedArg) {
        Gui.speedArg = speedArg;
    }

    public static void setSingleCurveHeight(int singleCurveHeight) {
        Gui.singleCurveHeight = singleCurveHeight;
    }

    public static void setOffsetX(int offsetX) {
        Gui.offsetX = offsetX;
    }

    public static void setCurveOpacity(float opacity) {
        Gui.opacity = opacity;
    }

    public static void setBackgroundColor(Color backgroundColor) {
        Gui.backgroundColor = backgroundColor;
    }

    public static void setCurveColor(Color curveColor) {
        Gui.curveColor = curveColor;
    }

    public static void setMove(int move) {
        Gui.move = move;
    }

    public static void setGradientType(String gradientType) {
        Gui.gradientType = gradientType;
    }
}