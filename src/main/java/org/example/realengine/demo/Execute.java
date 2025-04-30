package org.example.realengine.demo;

import javax.swing.*;
import java.io.IOException;

/**
 * Třída zajišťující spuštění hlavního okna aplikace a inicializaci herního panelu.
 * Obsahuje statickou metodu pro spuštění hry v samostatném vlákně Swing.
 */
public final class Execute implements Runnable {

    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        GamePanel gamePanel;
        try {
            gamePanel = new GamePanel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.startGameThread();
    }
}
