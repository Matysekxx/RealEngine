package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
            case KeyEvent.VK_ESCAPE -> System.exit(0);//TODO: nastaveni hry
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
}
