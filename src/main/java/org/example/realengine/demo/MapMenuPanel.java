package org.example.realengine.demo;

import org.example.realengine.map.RMap;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel menu pro výběr a načítání map ve hře.
 * Umožňuje uživateli procházet dostupné mapy, vybírat je a načítat do herního panelu.
 * Dědí z JPanel a obsahuje vlastní logiku pro zobrazení seznamu map a ovládání pomocí klávesnice.
 */
public final class MapMenuPanel extends JPanel {
    private final JFrame parentFrame;
    private final GamePanel gamePanel;
    private JList<String> mapList;
    private DefaultListModel<String> listModel;
    private final List<String> mapPaths = new ArrayList<>();

    public MapMenuPanel(JFrame parentFrame, GamePanel gamePanel) {
        this.parentFrame = parentFrame;
        this.gamePanel = gamePanel;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 50));
        setPreferredSize(parentFrame.getSize());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel titlePanel = createTitlePanel();
        JScrollPane scrollPane = createMapListScrollPane();
        JPanel instructionsPanel = createInstructionsPanel();
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(instructionsPanel, BorderLayout.SOUTH);
        setFocusable(true);
        addKeyListener(new MapMenuControl());
        loadMapList();
    }

    private JPanel createTitlePanel() {
        TitlePanel titlePanel = new TitlePanel();
        titlePanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("MAPS");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 32));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.YELLOW),
                BorderFactory.createEmptyBorder(20, 0, 20, 0)));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        return titlePanel;
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

    private JPanel createInstructionsPanel() {
        JPanel instructionsPanel = new InstructionsPanel();
        instructionsPanel.setLayout(new BorderLayout());
        JLabel instructionsLabel = new JLabel("↑↓ - Select | ENTER - Load | ESC - Back");
        instructionsLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        instructionsLabel.setForeground(Color.YELLOW);
        instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        instructionsPanel.add(instructionsLabel, BorderLayout.CENTER);
        return instructionsPanel;
    }

    //TODO: presunout vnorenou tridu mimo tridu MapMenuPanel
    private class MapMenuControl extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE, KeyEvent.VK_L -> returnToGame();
                case KeyEvent.VK_ENTER -> {
                    int selectedIndex = mapList.getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < mapPaths.size()) {
                        loadSelectedMap(mapPaths.get(selectedIndex));
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

    private void loadSelectedMap(String mapPath) {
        try {
            RMap newMap = RMap.loadFromPng(mapPath);
            gamePanel.loadMap(newMap);
            returnToGame();
        } catch (IOException e) {
            System.err.println("Failed to load map: " + mapPath);
        }
    }

    private void returnToGame() {
        parentFrame.getContentPane().remove(this);
        parentFrame.getContentPane().add(gamePanel);
        gamePanel.requestFocus();
        gamePanel.resumeGame();
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}