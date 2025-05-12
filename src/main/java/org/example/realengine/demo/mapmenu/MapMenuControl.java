package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.util.List;

public final class MapMenuControl extends KeyAdapter {
    private final JList<String> mapList;
    private final List<String> mapPaths;
    private final MapMenuPanel mapMenuPanel;

    public MapMenuControl(JList<String> mapList, List<String> mapPaths, MapMenuPanel mapMenuPanel) {
        this.mapList = mapList;
        this.mapPaths = mapPaths;
        this.mapMenuPanel = mapMenuPanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_L -> mapMenuPanel.returnToGame();
            case KeyEvent.VK_ESCAPE -> WSL();
            case KeyEvent.VK_ENTER -> {
                int selectedIndex = mapList.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < mapPaths.size()) {
                    mapMenuPanel.loadSelectedMap(mapPaths.get(selectedIndex));
                }
            }
            case KeyEvent.VK_UP -> {
                int selectedIndex = mapList.getSelectedIndex();
                if (selectedIndex > 0) {
                    mapList.setSelectedIndex(selectedIndex - 1);
                    mapList.ensureIndexIsVisible(selectedIndex - 1);
                }
            }
            case KeyEvent.VK_DOWN -> {
                int selectedIndex = mapList.getSelectedIndex();
                if (selectedIndex < mapList.getModel().getSize() - 1) {
                    mapList.setSelectedIndex(selectedIndex + 1);
                    mapList.ensureIndexIsVisible(selectedIndex + 1);
                }
            }
        }
    }

    private static void WSL() {
        try {
            final Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_WINDOWS);
            robot.keyRelease(KeyEvent.VK_WINDOWS);
            robot.delay(100);
            robot.keyPress(KeyEvent.VK_W);
            robot.keyRelease(KeyEvent.VK_W);
            robot.keyPress(KeyEvent.VK_S);
            robot.keyRelease(KeyEvent.VK_S);
            robot.keyPress(KeyEvent.VK_L);
            robot.keyRelease(KeyEvent.VK_L);
            robot.delay(100);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException _) {}
    }
}
