package com.company;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args)
    {

        MainFrame mFrame  = new MainFrame();
        mFrame.setVisible(true);
//        AlgorithmHandler aR = new AlgorithmHandler(mFrame);

        Rectangl nR = new Rectangl();

        nR.setPreferredSize(new Dimension(800, 1000));
        nR.setOpaque(false);

        mFrame.visualizationPanel.add(nR);

    }
}

class Rectangl extends JPanel
{
    int x = 150, y = 0;
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
//        g.fillRect(0, 20, 800, 1000);
//        System.out.println("g res x = " + g.getClipBounds());
//        for (int i = 0; i < 100; i++)
//        {
//            g.fillRect(100 + i * 10, i * 10, 10, 1000);
//        }
    }
}