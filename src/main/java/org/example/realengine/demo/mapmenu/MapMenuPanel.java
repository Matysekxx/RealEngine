package org.example.realengine.demo.mapmenu;

import org.example.realengine.demo.GamePanel;
import org.example.realengine.map.RMap;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * The {@code MapMenuPanel} class represents the menu panel for selecting and loading maps in the game.
 * It allows the user to browse available maps, select one, and load it into the game panel.
 * This class extends {@link JPanel} and includes custom logic for displaying the map list
 * and handling keyboard input for navigation and selection.
 */
public class MapMenuPanel extends JPanel {
    /**
     * The parent {@link JFrame} of the application, used for managing content pane transitions.
     */
    private final JFrame parentFrame;
    /**
     * The main {@link GamePanel} instance, to which maps are loaded and control is returned.
     */
    private final GamePanel gamePanel;
    /**
     * A list of file paths for all available maps.
     */
    private final List<String> mapPaths = new ArrayList<>();
    /**
     * The {@link JList} component that displays the names of the available maps.
     */
    private JList<String> mapList;
    /**
     * A {@link Vector} containing the names of the maps to be displayed in the {@code mapList}.
     */
    private final Vector<String> mapNames = new Vector<>();

    /**
     * Constructs a new {@code MapMenuPanel}.
     *
     * @param parentFrame The main application frame.
     * @param gamePanel The game panel instance to interact with.
     */
    public MapMenuPanel(JFrame parentFrame, GamePanel gamePanel) {
        this.parentFrame = parentFrame;
        this.gamePanel = gamePanel;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 50));
        setPreferredSize(parentFrame.getSize());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        final JPanel titlePanel = new TitlePanel();
        final JScrollPane scrollPane = createMapListScrollPane();
        final JPanel instructionsPanel = new InstructionsPanel();
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(instructionsPanel, BorderLayout.SOUTH);
        setFocusable(true);
        addKeyListener(new MapMenuControl(mapList, mapPaths, this, gamePanel));
        loadMapList();
    }

    /**
     * Creates and configures the {@link JScrollPane} containing the {@link JList} of map names.
     * Sets up the font, colors, and borders for the list and its scroll pane.
     *
     * @return A configured {@link JScrollPane} for the map list.
     */
    private JScrollPane createMapListScrollPane() {
        mapList = new JList<>(mapNames);
        mapList.setFont(new Font("Verdana", Font.PLAIN, 22));
        mapList.setBackground(new Color(30, 30, 60));
        mapList.setForeground(Color.YELLOW);
        mapList.setSelectionBackground(new Color(60, 60, 120));
        mapList.setSelectionForeground(Color.YELLOW);
        mapList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        final JScrollPane scrollPane = new JScrollPane(mapList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        scrollPane.setBackground(new Color(20, 20, 50));
        scrollPane.getViewport().setBackground(new Color(30, 30, 60));
        return scrollPane;
    }

    /**
     * Clears the current map list and loads all available map files from the "resources/maps" directory.
     * If no maps are found, a "No maps found" message is displayed.
     * Throws a {@link RuntimeException} if the maps directory does not exist.
     */
    public void loadMapList() {
        mapNames.clear();
        mapPaths.clear();
        final File customMapsDir = new File("resources\\maps");
        if (!customMapsDir.exists()) throw new RuntimeException("maps directory does not exist");
        loadMapsFromDirectory(customMapsDir);
        if (mapNames.isEmpty()) {
            mapNames.add("No maps found");
        }
        mapList.setListData(mapNames);
    }

    /**
     * Loads map files from the specified directory into the map list.
     * It filters for files ending with ".png" and adds their names and paths to the respective lists.
     *
     * @param directory The directory from which to load map files.
     */
    private void loadMapsFromDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            final File[] mapFiles = directory.listFiles((_, name) -> name.endsWith(".png"));
            if (mapFiles != null) {
                for (File mapFile : mapFiles) {
                    final String mapName = mapFile.getName();
                    mapNames.add(mapName);
                    mapPaths.add(mapFile.getPath());
                    System.err.println(mapFile.getPath());
                }
            }
        }
    }

    /**
     * Loads the map specified by the given path into the game panel and returns to the game.
     * Updates the entity manager and game panel with the new map.
     *
     * @param mapPath The file path of the map to be loaded.
     */
    void loadSelectedMap(final String mapPath) {
        try {
            final RMap newMap = RMap.loadFromPng(mapPath);
            gamePanel.getEntityManager().setMap(newMap);
            gamePanel.loadMap(newMap);
            returnToGame();
        } catch (IOException e) {
            System.err.println("Failed to load map: " + mapPath);
        }
    }

    /**
     * Transitions back to the main game panel from the map menu.
     * Removes this menu panel, adds the game panel to the parent frame's content pane,
     * requests focus for the game panel, resumes the game loop, sets audio, and revalidates/repaints the frame.
     */
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