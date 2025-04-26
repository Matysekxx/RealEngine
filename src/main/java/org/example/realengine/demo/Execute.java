package org.example.realengine.demo;

import javax.swing.*;
import java.io.IOException;

public class Execute {
    public static void execute() {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            final GamePanel gamePanel;
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
        });
    }
}
