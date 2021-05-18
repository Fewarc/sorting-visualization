package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class MainFrame extends JFrame
{
    /* Font initialization */
    static Font componentLabelFont = new Font("Arial", Font.BOLD, 15);

    /* Colors */
    static Color backgroundColor = Color.decode("#2c2e2e") ;
    static Color borderColor = Color.decode("#ff9100") ;
    static Color algorithmColor = Color.decode("#4d4f4f") ;

    private AlgorithmHandler algorithmHandler;
    private static Thread algorithmThread;
    private MainFrame thisFrame = this;

    private static JPanel menuPanel = new JPanel();
    private static JPanel algorithmPanel = new JPanel();
    public static JPanel visualizationPanel = new JPanel();
    private static JLabel visualizationLabel;
    private static JLabel statusLabel;
    public static JLabel algorithmStatus;
    private static JLabel timeLabel;
    private static JLabel algorithmTime;
    private static JComboBox menuCB;
    private static JSlider speedSlider;
    private static JCheckBox sliderLockBox;
    private static JCheckBox fullSpeedLockBox;
    private static JSpinner itemsSpinner;
    private static JButton runButton = new BuildButton("RUN", componentLabelFont, borderColor);
    private static JButton pauseButton = new BuildButton("PAUSE", componentLabelFont, borderColor);
    private static JButton resumeButton = new BuildButton("RESUME", componentLabelFont, borderColor);
    private static JButton stopButton = new BuildButton("STOP", componentLabelFont, borderColor);
    private static JButton randomizeButton = new BuildButton("RANDOMIZE ITEMS", componentLabelFont, borderColor);
    private static String algorithms[] = { "Quick sort", "Bubble sort", "Selection sort" };
    private static String algorithmToRun = algorithms[0];
    private static boolean measureTime;

    private String runningAlgorithm;
    private static boolean algorithmRunning = false;

    /* Needed getters */
    public String getAlgorithmToRun(){ return algorithmToRun; }
    public int getSpeedSliderValue(){ return speedSlider.getValue(); }
    public boolean getSliderLockBoxStatement(){ return measureTime; }
    public int getSpinnerValue(){ return (int) itemsSpinner.getValue(); }
    public boolean getNoDelay(){ return this.fullSpeedLockBox.isSelected() ? true : false; }
    /* Needed setters */
    public void setIsRunning(boolean in){ this.algorithmRunning = in; }

    /* method to update visualization label text*/
    private void updateVisLabelText()
    {
        if(this.algorithmRunning)
            if (!getNoDelay()) {
                visualizationLabel.setText(runningAlgorithm + " with " + getSpeedSliderValue() + "ms delay");
            } else {
                visualizationLabel.setText(runningAlgorithm + " with no delay");
            }
        else
            if (!getNoDelay()) {
             visualizationLabel.setText(getAlgorithmToRun() + " with " + getSpeedSliderValue() + "ms delay");
            } else {
              visualizationLabel.setText(getAlgorithmToRun() + " with no delay");
            }
    }

    /**
     *  Constructor
     */
    public MainFrame()
    {
        initProperties();
        initFrameComponents();
        algorithmHandler = new AlgorithmHandler(this);
    }

    /**
     *  A method to initialize components inside of frame
     */
    private void initFrameComponents()
    {
        /* Panels initialization*/
        JPanel menuPlusButton = new JPanel();

        /* Labels */
        // combo box label
        BuildLabel comboBoxLabel = new BuildLabel("Chosen algorithm ", componentLabelFont);
        // slider label
        BuildLabel sliderLabel = new BuildLabel("Sorting delay ", componentLabelFont);
        // slider hash table label
        Hashtable sliderTable = new Hashtable();
        sliderTable.put(1, new BuildLabel("1ms", componentLabelFont));
        sliderTable.put(50, new BuildLabel("50ms", componentLabelFont));
        sliderTable.put(100, new BuildLabel("100ms", componentLabelFont));
        // spinner label
        BuildLabel spinnerLabel = new BuildLabel("Items to sort", componentLabelFont);
        // spinner info label
        BuildLabel spinnerInfoLabel = new BuildLabel("Choose value between 10 and 580 !", new Font("Arial", Font.ITALIC, 12));
        spinnerInfoLabel.setForeground(backgroundColor);
        // status and time labels
        statusLabel = new BuildLabel("Algorithm status : ", componentLabelFont);
        algorithmStatus = new BuildLabel("STOPPED", componentLabelFont);
        timeLabel = new BuildLabel("Time : ", componentLabelFont);
        algorithmTime = new BuildLabel("00:00.000", componentLabelFont);

        /* Menu components */
        // run button
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if((int)itemsSpinner.getValue() >= 10 && (int)itemsSpinner.getValue() <= 580)
                {
                    runningAlgorithm = algorithmToRun;
                    Runnable algorithmHandler = new AlgorithmHandler(thisFrame);
                    thisFrame.algorithmThread = new Thread(algorithmHandler);
                    algorithmThread.start();
                    algorithmStatus.setText("RUNNING");
                }
            }
        });
        // pause button
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(thisFrame.algorithmRunning)
                {
                    try
                    {
                        algorithmThread.interrupt();
                        algorithmHandler.isPaused = true;
                    }
                    catch(Exception e)
                    {
                        System.out.println("At pause button -> thread.interrupt() : " + e.getMessage());
                    }
                    algorithmStatus.setText("PAUSED");
                }
                else
                    System.out.println("At pause button -> thread.interrupt() : isInterrupted() = true");

            }
        });
        // resume button
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(algorithmHandler.isPaused)
                {
                    thisFrame.algorithmThread = new Thread(algorithmHandler);
                    algorithmThread.start();
                    algorithmStatus.setText("RUNNING");
                }
            }
        });
        // stop button
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(thisFrame.algorithmRunning)
                {
                    algorithmThread.interrupt();
                    algorithmHandler.isPaused = false;
                    thisFrame.algorithmHandler = new AlgorithmHandler(thisFrame); // TODO : prevent stop from randomizing items
                    algorithmStatus.setText("STOPPED");
                }
            }
        });
        // randomize button // TODO : randomize button - possibly starting and immediately pausing a new thread
        randomizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                thisFrame.algorithmHandler.itemsToSort.randomizeItems();
                thisFrame.getContentPane().repaint();
            }
        });
        // combo box
        String cbItems[] = { "Quick sort", "Bubble sort", "Selection sort" };
        menuCB = new JComboBox(cbItems);
        menuCB.setEditable(false);
        menuCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox cb = (JComboBox)actionEvent.getSource();
                algorithmToRun = algorithms[cb.getSelectedIndex()];
                updateVisLabelText();
            }
        });

        // slider
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
        speedSlider.setLabelTable(sliderTable);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                updateVisLabelText();
            }
        });
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setMajorTickSpacing(25);
        speedSlider.setPaintTicks(true);
        speedSlider.setOpaque(false);
//        speedSlider.setInverted(true);
        // full speed lock box
        fullSpeedLockBox = new JCheckBox("Set no delay");
        fullSpeedLockBox.setFont(componentLabelFont);
        fullSpeedLockBox.setForeground(Color.WHITE);
        fullSpeedLockBox.setFocusPainted(false);
        fullSpeedLockBox.setBorderPainted(false);
        fullSpeedLockBox.setOpaque(false);
        fullSpeedLockBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateVisLabelText();
                if(fullSpeedLockBox.isSelected())
                {
                    speedSlider.setEnabled(false);
                }
                else
                    speedSlider.setEnabled(true);
            }
        });
        // check box
        sliderLockBox = new JCheckBox("Lock speed to measure time");
        sliderLockBox.setFont(componentLabelFont);
        sliderLockBox.setForeground(Color.WHITE);
        sliderLockBox.setFocusPainted(false);
        sliderLockBox.setBorderPainted(false);
        sliderLockBox.setOpaque(false);
        sliderLockBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(sliderLockBox.isSelected())
                {
                    speedSlider.setEnabled(false);
                    fullSpeedLockBox.setEnabled(false);
                    measureTime = true;
                }
                else {
                    speedSlider.setEnabled(true);
                    fullSpeedLockBox.setEnabled(true);
                    measureTime = false;
                }
            }
        });
        // spinner
        itemsSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 1000, 1));
        itemsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                System.out.println("Spinner : " + itemsSpinner.getValue());

                if((int)itemsSpinner.getValue() < 10 || (int)itemsSpinner.getValue() > 580)
                    spinnerInfoLabel.setForeground(Color.RED);
                else
                    spinnerInfoLabel.setForeground(backgroundColor);
            }
        });

        /* Border initialization */
        final int borderGap = 15;
        Border mainBorder = new LineBorder(borderColor);

        /* Layouts - group layout manager */
        // entire frame layout
        GroupLayout mainFrameLayout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(mainFrameLayout);
        // menu + button panel layout
        GroupLayout menuButtonLayout = new GroupLayout(menuPlusButton);
        menuPlusButton.setLayout(menuButtonLayout);
        // algorithm panel layout
        GroupLayout algorithmPanelLayout = new GroupLayout(algorithmPanel);
        algorithmPanel.setLayout(algorithmPanelLayout);
        // menu panel layout
        GroupLayout menuLayout = new GroupLayout(menuPanel);
        menuPanel.setLayout(menuLayout);

        this.getContentPane().setBackground(backgroundColor);

        // Menu panel layout init
        menuLayout.setHorizontalGroup(menuLayout.createSequentialGroup()
                .addGap(borderGap)
                .addGroup(menuLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(comboBoxLabel, GroupLayout.Alignment.LEADING)
                        .addComponent(menuCB)
                        .addComponent(sliderLabel, GroupLayout.Alignment.LEADING)
                        .addComponent(speedSlider)
                        .addComponent(fullSpeedLockBox)
                        .addComponent(sliderLockBox)
                        .addComponent(spinnerLabel, GroupLayout.Alignment.LEADING)
                        .addComponent(itemsSpinner)
                        .addComponent(spinnerInfoLabel, GroupLayout.Alignment.CENTER)
                        .addComponent(statusLabel, GroupLayout.Alignment.CENTER)
                        .addComponent(algorithmStatus, GroupLayout.Alignment.CENTER)
                        .addComponent(timeLabel, GroupLayout.Alignment.CENTER)
                        .addComponent(algorithmTime, GroupLayout.Alignment.CENTER))
            .addGap(borderGap));

        menuLayout.setVerticalGroup(menuLayout.createSequentialGroup()
        .addGap(25)
        .addComponent(true, comboBoxLabel)
                .addGap(10)
        .addComponent(menuCB, 25, 25, 25)
        .addGap(borderGap)
        .addComponent(sliderLabel)
                .addGap(10)
        .addComponent(speedSlider)
        .addGap(borderGap)
        .addComponent(fullSpeedLockBox)
        .addGap(borderGap)
        .addComponent(sliderLockBox)
        .addGap(borderGap)
        .addComponent(spinnerLabel)
        .addGap(10)
        .addComponent(itemsSpinner, 25, 25, 25)
        .addGap(10)
        .addComponent(spinnerInfoLabel)
        .addGap(5)
        .addComponent(statusLabel)
        .addGap(5)
        .addComponent(algorithmStatus)
        .addGap(10)
        .addComponent(timeLabel)
        .addGap(5)
        .addComponent(algorithmTime)
        .addGap(10));
        // ----------------------

        // Menu + buttons panel layout init
        menuButtonLayout.setHorizontalGroup(menuButtonLayout.createSequentialGroup()
        .addGroup(menuButtonLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(menuPanel)
                .addComponent(runButton, 300, 300, 300)
                .addGroup(menuButtonLayout.createSequentialGroup()
                        .addComponent(pauseButton, 148, 148, 148)
                        .addGap(4)
                        .addComponent(resumeButton, 148, 148, 148))
                .addComponent(stopButton, 300, 300, 300)
                .addComponent(randomizeButton, 300, 300, 300))

        );

        menuButtonLayout.setVerticalGroup(menuButtonLayout.createSequentialGroup()
        .addComponent(menuPanel)
        .addGap(borderGap)
        .addComponent(runButton, 50, 50, 50)
                .addGap(4)
        .addGroup(menuButtonLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(pauseButton, 50, 50, 50)
                .addComponent(resumeButton, 50, 50, 50))
                .addGap(4)
        .addComponent(stopButton, 50, 50, 50)
        .addGap(4)
        .addComponent(randomizeButton, 50, 50, 50));
        // ------------------------------

        // Algorithm panel layout
        visualizationLabel = new JLabel( getAlgorithmToRun() + " with " + getSpeedSliderValue() + "ms delay", JLabel.CENTER);
        visualizationLabel.setForeground(Color.WHITE);
        visualizationLabel.setFont(new Font("Arial", Font.BOLD, 40));
        visualizationPanel.setPreferredSize(new Dimension(50, 60));
//        visualizationPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
//        visualizationPanel.setBorder(BorderFactory.createRaisedSoftBevelBorder());

        algorithmPanelLayout.setHorizontalGroup( algorithmPanelLayout.createSequentialGroup()
                .addGap(25)
                .addGroup( algorithmPanelLayout.createParallelGroup( GroupLayout.Alignment.CENTER)
                        .addComponent(visualizationLabel, 800, 800, 800)
                        .addComponent(visualizationPanel))
                .addGap(25)
        );

        algorithmPanelLayout.setVerticalGroup( algorithmPanelLayout.createSequentialGroup()
                .addGap(25)
        .addComponent(visualizationLabel)
                .addGap(25)
        .addComponent(visualizationPanel)
        .addGap(25));

        // ----------------------

        // Main frame layout initialization
        mainFrameLayout.setHorizontalGroup( mainFrameLayout.createSequentialGroup()
                .addGap(borderGap)
                .addComponent(menuPlusButton, 300, 300, 300)
                .addGap(borderGap)
                .addComponent(algorithmPanel)
                .addGap(borderGap));

        mainFrameLayout.setVerticalGroup( mainFrameLayout.createSequentialGroup()
                .addGap(borderGap)
                .addGroup(mainFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(borderGap)
                        .addComponent(menuPlusButton, 700, 700,700)
                        .addGap(borderGap)
                        .addComponent(algorithmPanel, GroupLayout.Alignment.CENTER))
                        .addGap(borderGap));
        // --------------------------------

        /* Border customization */
        TitledBorder mainTitledBorder = new TitledBorder(mainBorder, "Algorithm parameters", TitledBorder.LEFT,
                TitledBorder.CENTER, componentLabelFont, Color.WHITE);
        mainTitledBorder.setTitleJustification(TitledBorder.CENTER);
        menuPanel.setBackground(backgroundColor);

        menuPlusButton.setBackground(backgroundColor);
        visualizationPanel.setBackground(backgroundColor);

        TitledBorder mainAlgorithmTitledBorder = new TitledBorder(mainBorder, "Algorithm visualisation", TitledBorder.LEFT,
                TitledBorder.CENTER, componentLabelFont, Color.WHITE);
        mainAlgorithmTitledBorder.setTitleJustification(TitledBorder.CENTER);
        algorithmPanel.setBackground(backgroundColor);

        /* Border setting */
        menuPanel.setBorder(mainTitledBorder);
        algorithmPanel.setBorder(mainAlgorithmTitledBorder);



        pack();
    }

    /**
     *  A method to initialize frame properties
     */
    private void initProperties()
    {
        this.setTitle("Sorting algorithms visualisation");
        final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setSize(1200, 800);

        final int frameWidth = getSize().width;
        final int frameHeight = getSize().height;
        this.setLocation((screenWidth - frameWidth) / 2, (screenHeight - frameHeight) / 2);

        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}

/**
 *  Class to build labels
 */
class BuildLabel extends JLabel
{
    BuildLabel(String name, Font font)
    {
        super(name, JLabel.LEFT);
        this.setFont(font);
        this.setForeground(Color.WHITE);
    }
}

/**
 *  Class to build buttons
 */
class BuildButton extends JButton
{
    BuildButton(String name, Font font, Color color)
    {
        super(name);
        this.setFont(font);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setBackground(color);
    }
}