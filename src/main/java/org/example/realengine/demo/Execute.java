package org.example.realengine.demo;

import javax.swing.*;

/**
 * <p>The {@code Execute} class is responsible for launching the main application window and initializing the game panel.</p>
 * <p>It contains a static {@code Runnable} field to start the game in a dedicated Swing thread.</p>
 */
public final class Execute {

    /**
     * <p>A {@code Runnable} instance that sets up and starts the main game window.</p>
     * <p>This includes creating the {@code JFrame}, setting its properties (e.g., full screen, undecorated),
     * initializing the {@code GamePanel}, adding it to the frame, and starting the game thread.</p>
     */
    public final static Runnable run = () -> {
        final JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setResizable(false);
        GamePanel gamePanel;
        gamePanel = new GamePanel(frame);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.startGameThread();
    };

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private Execute() {
    }
}