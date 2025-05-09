package org.example.realengine.demo.mapmenu;

import org.example.realengine.demo.GamePanel;
import org.example.realengine.map.RMap;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel menu pro výběr a načítání map ve hře.
 * Umožňuje uživateli procházet dostupné mapy, vybírat je a načítat do herního panelu.
 * Dědí z JPanel a obsahuje vlastní logiku pro zobrazení seznamu map a ovládání pomocí klávesnice.
 */
public class MapMenuPanel extends JPanel {
    private final JFrame parentFrame;
    private final GamePanel gamePanel;
    private final List<String> mapPaths = new ArrayList<>();
    private JList<String> mapList;
    private DefaultListModel<String> listModel;

    public MapMenuPanel(JFrame parentFrame, GamePanel gamePanel) {
        this.parentFrame = parentFrame;
        this.gamePanel = gamePanel;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 50));
        setPreferredSize(parentFrame.getSize());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel titlePanel = new TitlePanel();
        JScrollPane scrollPane = createMapListScrollPane();
        JPanel instructionsPanel = new InstructionsPanel();
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(instructionsPanel, BorderLayout.SOUTH);
        setFocusable(true);
        addKeyListener(new MapMenuControl(mapList, mapPaths, this));
        loadMapList();
    }

    private JScrollPane createMapListScrollPane() {
        listModel = new DefaultListModel<>();
        mapList = new JList<>(listModel);
        mapList.setFont(new Font("Verdana", Font.PLAIN, 22));
        mapList.setBackground(new Color(30, 30, 60));
        mapList.setForeground(Color.YELLOW);
        mapList.setSelectionBackground(new Color(60, 60, 120));
        mapList.setSelectionForeground(Color.YELLOW);
        mapList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(mapList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        scrollPane.setBackground(new Color(20, 20, 50));
        scrollPane.getViewport().setBackground(new Color(30, 30, 60));
        return scrollPane;
    }

    public void loadMapList() {
        listModel.clear();
        mapPaths.clear();
        File customMapsDir = new File("maps");
        if (!customMapsDir.exists()) throw new RuntimeException("maps directory does not exist");
        loadMapsFromDirectory(customMapsDir);
        if (listModel.isEmpty()) {
            listModel.addElement("No maps found");
        }
    }

    /**
     * Načte mapy z daného adresáře do seznamu.
     *
     * @param directory Adresář, ze kterého se mají načíst mapy
     */
    private void loadMapsFromDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] mapFiles = directory.listFiles((_, name) -> name.toLowerCase().endsWith(".png"));
            if (mapFiles != null) {
                for (File mapFile : mapFiles) {
                    String mapName = mapFile.getName();
                    listModel.addElement(mapName);
                    mapPaths.add(mapFile.getAbsolutePath());
                }
            }
        }
    }

    void loadSelectedMap(String mapPath) {
        try {
            RMap newMap = RMap.loadFromPng(mapPath);
            gamePanel.loadMap(newMap);
            returnToGame();
        } catch (IOException e) {
            System.err.println("Failed to load map: " + mapPath);
        }
    }

    void returnToGame() {
        parentFrame.getContentPane().remove(this);
        parentFrame.getContentPane().add(gamePanel);
        gamePanel.requestFocus();
        gamePanel.resumeGame();
        gamePanel.setAudio();
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}