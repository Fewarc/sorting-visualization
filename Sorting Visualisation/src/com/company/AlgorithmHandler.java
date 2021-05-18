package com.company;

import javax.swing.*;
import java.awt.*;

public class AlgorithmHandler implements Runnable
{
    /* MainFrame class object to provide parameters data */
    MainFrame mainDisplayFrame;
    ItemFactory itemsToSort;

    public static boolean isPaused;

    /* Initializing constructor */
    AlgorithmHandler(MainFrame MF)
    {
        this.mainDisplayFrame = MF;
        this.initAlgorithmPanel();
    }

    public void initAlgorithmPanel()
    {
        itemsToSort = new ItemFactory(mainDisplayFrame, mainDisplayFrame.getSpinnerValue());
        itemsToSort.randomizeItems();
    }

    @Override
    public void run() {
        runAlgorithm();
        if (this.isPaused) {
            mainDisplayFrame.algorithmStatus.setText("PAUSED");
        } else {
            mainDisplayFrame.algorithmStatus.setText("FINISHED!"); // TODO : fix label
        }
    }

    public void runAlgorithm()
    {
        this.isPaused = false;
        mainDisplayFrame.setIsRunning(true);

        switch(mainDisplayFrame.getAlgorithmToRun()) {
            case "Quick sort" :
                runQuickSort();
            break;

            case "Bubble sort" :
                runBubbleSort();
            break;

            case "Selection sort" :
                runSelectionSort();
            break;
        }
    }

    private void runBubbleSort()
    {
        int n = itemsToSort.itemOrder.length;

        for(int i = 0; i < n - 1; i++)
        {
            for (int j = 0; j < n - i - 1; j++)
            {
                try
                {
                    Thread.sleep((mainDisplayFrame.getNoDelay()) ? 0 : mainDisplayFrame.getSpeedSliderValue());

                    if( itemsToSort.itemOrder[j] > itemsToSort.itemOrder[j + 1] )
                    {
                        itemsToSort.swapItems(itemsToSort.itemOrder, j, j + 1);
                    }
                    mainDisplayFrame.getContentPane().repaint();
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
        mainDisplayFrame.setIsRunning(false);
    }

    private void runQuickSort()
    {
        // TODO
    }

    private void runSelectionSort()
    {
        // TODO
    }


}

class ItemFactory
{
    MainFrame mainDisplayFrame;
    static int[] itemOrder;
    static ItemPanel itemPanel;
    static float itemWidth;
    static float itemYGap;

    ItemFactory(MainFrame MF, int itemsToCreate)
    {
        initProperties(MF, itemsToCreate);
        initItems();
    }

    ItemFactory(MainFrame MF, int itemsToCreate, int[] tableInProgress) // TODO : wywalić
    {
        initProperties(MF, itemsToCreate);
        itemOrder = tableInProgress;
    }

    void initProperties(MainFrame MF, int itemsToCreate)
    {
        this.mainDisplayFrame = MF;
        itemOrder = new int[itemsToCreate];
        itemWidth = 800 / itemsToCreate /*Math.round(800. / itemsToCreate)*/;
        itemYGap = 570 / itemsToCreate /*Math.round(540. / itemsToCreate)*/;
        itemPanel = new ItemPanel(this);
        itemPanel.setPreferredSize(new Dimension(800, 1000));
        itemPanel.setOpaque(false);
    }

    void initItems()
    {
        for (int i = 0; i < itemOrder.length; i++)
        {
            itemOrder[i] = i;
        }
//        randomizeItems();
        mainDisplayFrame.visualizationPanel.add(itemPanel);
    }

    void randomizeItems()
    {
        for (int i = 0; i < (itemOrder.length)*(itemOrder.length); i++) {
            swapItems(itemOrder, (int) (Math.random() * (itemOrder.length)), (int) (Math.random() * (itemOrder.length)));
        }
    }

    void swapItems(int[] itemOrder, int i, int j)
    {
        int temp;
        temp = itemOrder[i];
        itemOrder[i] = itemOrder[j];
        itemOrder[j] = temp;
    }
}

class ItemPanel extends JPanel
{
    ItemFactory dataItemFactory;

    ItemPanel(ItemFactory IF)
    {
        this.dataItemFactory = IF;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        for (int i = 0; i < dataItemFactory.itemOrder.length; i++)
        {
            g.fillRect(
                     /*dataItemFactory.itemOrder[i]*/((800 % dataItemFactory.itemOrder.length) / 2) + (int)(i * dataItemFactory.itemWidth),
                    ((580 % dataItemFactory.itemOrder.length) / 2) + (int)(dataItemFactory.itemOrder[dataItemFactory.itemOrder.length - i - 1] * dataItemFactory.itemYGap),
                    (int)(dataItemFactory.itemWidth),
                    2000);
        }
    }
}