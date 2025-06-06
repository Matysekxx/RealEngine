package org.example.realengine.control;

import org.example.realengine.demo.GamePanel;
import org.example.realengine.entity.Player;
import org.example.realengine.graphics.Render;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * RControl is responsible for handling keyboard input and controlling the player's movement and actions.
 * Implements the {@link KeyListener} interface to process key events for movement, jumping, climbing,
 * and triggering game-specific actions such as opening the map menu or reversing textures.
 */
public final class RControl implements KeyListener {

    /**
     * The player instance controlled by this input handler.
     */
    private final Player controlledPlayer;

    /**
     * Constructs a new RControl for the specified player.
     *
     * @param player the player to be controlled by keyboard input
     */
    public RControl(@NotNull Player player) {
        this.controlledPlayer = player;
    }

    /**
     * Invoked when a key has been pressed. Handles movement, jumping, climbing,
     * bunny jumping, opening the map menu, and reversing textures.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            controlledPlayer.setMovingLeft(true);
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            controlledPlayer.setMovingRight(true);
        }
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W || code == KeyEvent.VK_SPACE) {
            controlledPlayer.jump();
            controlledPlayer.setClimbingUp(true);
        }
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            controlledPlayer.setMovingDown(true);
            controlledPlayer.setClimbingDown(true);
        }
        if (code == KeyEvent.VK_SHIFT) {
            controlledPlayer.bunnyJump();
        }
        if (e.getKeyCode() == KeyEvent.VK_L) {
            GamePanel gamePanel = (GamePanel) e.getComponent();
            gamePanel.getAudio().stopMusic();
            gamePanel.showMapMenu();
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            Render.reverseTexturesOn();
        }
    }

    /**
     * Invoked when a key has been released. Stops movement or climbing actions as appropriate.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> controlledPlayer.setMovingLeft(false);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> controlledPlayer.setMovingRight(false);
            case KeyEvent.VK_SPACE, KeyEvent.VK_W, KeyEvent.VK_UP -> {
                controlledPlayer.setMovingUp(false);
                controlledPlayer.setClimbingUp(false);
            }
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                controlledPlayer.setMovingDown(false);
                controlledPlayer.setClimbingDown(false);
            }
        }
    }

    /**
     * Invoked when a key has been typed. Not used in this implementation.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
}
