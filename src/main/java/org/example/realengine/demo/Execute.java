package org.example.realengine.demo;

import javax.swing.*;
import java.io.IOException;

/**
 * Třída zajišťující spuštění hlavního okna aplikace a inicializaci herního panelu.
 * Obsahuje statickou metodu pro spuštění hry v samostatném vlákně Swing.
 */
public final class Execute {

    public final static Runnable run = () -> {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setResizable(true);
        GamePanel gamePanel;
        try {
            gamePanel = new GamePanel(frame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.startGameThread();
    };

    private Execute() {
    }
}