package org.example.realengine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Zpracovává události klávesnice a poskytuje snímek aktuálního stavu stisknutých kláves.
 */
public class InputManager implements KeyListener {

    private final Set<Integer> pressedKeys = new HashSet<>();

    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    private boolean jump = false;

    /**
     * Vytvoří snímek aktuálního stavu vstupů.
     *
     * @return {@link InputSnapshot} s aktuálním stavem.
     */
    public synchronized InputSnapshot getSnapshot() {
        return new InputSnapshot(left, right, up, down, jump);
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        updateActionState(e.getKeyCode(), true);
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
        updateActionState(e.getKeyCode(), false);
    }

    /**
     * Aktualizuje stav konkrétních akcí (pohyb, skok) na základě stisknuté/puštěné klávesy.
     *
     * @param keyCode   Kód klávesy.
     * @param isPressed Zda byla klávesa stisknuta (`true`) nebo puštěna (`false`).
     */
    private void updateActionState(int keyCode, boolean isPressed) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> left = isPressed;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> right = isPressed;
            case KeyEvent.VK_UP, KeyEvent.VK_W -> up = isPressed;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> down = isPressed;
            case KeyEvent.VK_SPACE -> jump = isPressed;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Zkontroluje, zda je specifická klávesa aktuálně stisknutá.
     * Může být užitečné pro jednorázové akce (např. pauza, menu).
     *
     * @param keyCode Kód klávesy (např. {@code KeyEvent.VK_ESCAPE}).
     * @return {@code true}, pokud je klávesa stisknutá, jinak {@code false}.
     */
    public synchronized boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }
} 