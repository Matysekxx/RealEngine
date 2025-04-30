package org.example.realengine.control;

import org.example.realengine.demo.GamePanel;
import org.example.realengine.entity.Entity;
import org.example.realengine.entity.Player;
import org.example.realengine.map.RMap;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class RControl implements MouseListener, KeyListener {

    private Entity controlledEntity;

    public RControl(@NotNull Entity entity) {
        this.controlledEntity = entity;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            controlledEntity.setMovingLeft(true);
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            controlledEntity.setMovingRight(true);
        }
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W || code == KeyEvent.VK_SPACE) {
            controlledEntity.jump();
            if (controlledEntity instanceof Player p) {
                p.setClimbingUp(true);
            }
        }
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            controlledEntity.setMovingDown(true);
            if (controlledEntity instanceof Player p) {
                p.setClimbingDown(true);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_L) {
            Component c = e.getComponent();
            while (c != null && !(c instanceof GamePanel)) {
                c = c.getParent();
            }
            if (c != null) {
                ((GamePanel) c).showMapMenu();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (controlledEntity == null) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> controlledEntity.setMovingLeft(false);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> controlledEntity.setMovingRight(false);
            case KeyEvent.VK_SPACE, KeyEvent.VK_W, KeyEvent.VK_UP -> {
                controlledEntity.setMovingUp(false);
                if (controlledEntity instanceof Player p) {
                    p.setClimbingUp(false);
                }
            }
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                controlledEntity.setMovingDown(false);
                if (controlledEntity instanceof Player p) {
                    p.setClimbingDown(false);
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public Entity getControlledEntity() {
        return controlledEntity;
    }

    public void setControlledEntity(Entity entity) {
        this.controlledEntity = entity;
    }
}
