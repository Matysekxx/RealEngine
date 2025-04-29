package org.example.realengine.demo;

import org.example.realengine.control.RControl;
import org.example.realengine.entity.Player;
import org.example.realengine.game.GameConstants;
import org.example.realengine.graphics.Camera;
import org.example.realengine.graphics.Render;
import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Hlavní herní panel, který zajišťuje vykreslování, logiku hry a správu herního cyklu.
 * Dědí z JPanel a implementuje Runnable pro běh herní smyčky v samostatném vlákně.
 * Obsahuje správu mapy, hráče, kamery a zpracování vstupů.
 */
public class GamePanel extends JPanel implements Runnable {
    public static final int MAX_SCREEN_COL = 26;
    public static final int MAX_SCREEN_ROW = 16;
    public static final int MAX_WORLD_COL = 180;
    public static final int MAX_WORLD_ROW = 14;
    private static final int TILE_SIZE = GameConstants.TILE_SIZE;
    public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;
    public static int WORLD_WIDTH = TILE_SIZE * MAX_WORLD_COL;
    public static int WORLD_HEIGHT = TILE_SIZE * MAX_WORLD_ROW;
    private static final int BOX_GRAVITY_DELAY = 6;
    private final RControl rControl;
    private final Render render;
    private final Camera camera;
    private final Player player;
    private static final int FPS = 60;
    private RMap map;
    private Point spawnPoint;
    private Thread gameThread;
    private boolean isPaused = false;
    private int boxGravityTick = 0;

    public GamePanel() throws IOException {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(25, 25, 40));
        this.setDoubleBuffered(true);
        render = new Render();
        this.map = RMap.loadFromPng("resources/defaultmap.png");
        spawnPoint = findSpawnPoint(map);
        if (spawnPoint == null) {
            System.out.println("WARN: PLAYER_SPAWN not found, using default spawn position.");
            spawnPoint = new Point(2 * TILE_SIZE, (MAX_WORLD_ROW - 5) * TILE_SIZE);
        }
        player = new Player(spawnPoint.x, spawnPoint.y);
        player.setWidth(TILE_SIZE);
        player.setHeight(TILE_SIZE);
        map.addEntity(player);
        rControl = new RControl(player);
        this.addKeyListener(rControl);
        this.setFocusable(true);
        camera = new Camera(SCREEN_WIDTH, SCREEN_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
        camera.follow(player);
        camera.setFollowOffsetX(-SCREEN_WIDTH / 4.0f);
    }

    /**
     * Finds the first occurrence of PLAYER_SPAWN in the map's collision layer.
     *
     * @param mapToSearch The RMap to search within.
     * @return A Point representing the top-left pixel coordinates of the spawn tile, or null if not found.
     */
    private Point findSpawnPoint(RMap mapToSearch) {
        EObject[][] collisionLayer = mapToSearch.getCollisionMap();
        if (collisionLayer == null) return null;
        for (int y = 0; y < mapToSearch.getHeight(); y++) {
            for (int x = 0; x < mapToSearch.getWidth(); x++) {
                if (x < collisionLayer.length && y < collisionLayer[x].length)
                    if (collisionLayer[x][y] == EObject.PLAYER_SPAWN) {
                        return new Point(x*TILE_SIZE, y*TILE_SIZE);
                    }
            }
        }
        return null;
    }

    /**
     * Starts the main game loop thread.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Updates the game state for all relevant components.
     */
    private void update() {
        boxGravityTick++;
        if (boxGravityTick >= BOX_GRAVITY_DELAY) {
            applyBoxGravity();
            boxGravityTick = 0;
        }
        if (player.getY() > WORLD_HEIGHT) {
            respawnPlayer();
        }
        player.update((float) 0.016666668, map.getCollisionMap());
        camera.update();
    }

    /**
     * Respawns the player at the spawn point
     */
    private void respawnPlayer() {
        resetPlayer(spawnPoint);
    }

    /**
     * Renders the current game state using the Renderer.
     * Called by repaint().
     *
     * @param g Graphics context (automatically provided by Swing).
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        render.renderScene(g2, map, camera);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Use arrow or wsad to move and jump", 10, 30);
        g2.dispose();
    }

    /**
     * Helper method to clear the entire map
     */
    @Deprecated
    private void clearMap(RMap targetMap) {
        for (int x = 0; x < GamePanel.MAX_WORLD_COL; x++) {
            for (int y = 0; y < GamePanel.MAX_WORLD_ROW; y++) {
                targetMap.setObjectAt(x, y, EObject.EMPTY);
            }
        }
    }

    /**
     * Helper method to fill an area with a specific object
     */
    @Deprecated
    private void fillArea(RMap targetMap, int startX, int startY, int width, int height, EObject object) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                targetMap.setObjectAt(startX + x, startY + y, object);
            }
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double deltaAccumulator = 0;
        long lastTime = System.nanoTime();
        final float fixedDeltaTime = 1.0f / FPS;

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            deltaAccumulator += (currentTime - lastTime) / 1000000000.0;
            lastTime = currentTime;
            deltaAccumulator = Math.min(deltaAccumulator, fixedDeltaTime * 5);

            if (!isPaused) {
                while (deltaAccumulator >= fixedDeltaTime) {
                    update();
                    deltaAccumulator -= fixedDeltaTime;
                }
                repaint();
            }

            try {
                long timeNow = System.nanoTime();
                long sleepTime = (lastTime + (long) drawInterval - timeNow) / 1000000;

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Game loop interrupted!");
                break;
            }
        }
    }

    /**
     * Loads a new map, replacing the current one. Updates camera bounds and player position.
     *
     * @param newMap The new RMap instance to load.
     */
    public void loadMap(@NotNull RMap newMap) {
        if (this.map != null) {
            this.map.clearEntities();
        }
        this.map = newMap;
        int newWorldWidth = newMap.getWidth() * TILE_SIZE;
        int newWorldHeight = newMap.getHeight() * TILE_SIZE;
        WORLD_HEIGHT = newWorldHeight;
        WORLD_WIDTH = newWorldWidth;
        camera.setWorldDimensions(newWorldWidth, newWorldHeight);
        spawnPoint = findSpawnPoint(this.map);
        player.setSpawnPoint(spawnPoint);
        resetPlayer(spawnPoint);
        this.map.addEntity(player);
    }

    private void resetPlayer(Point playerSpawn) {
        if (playerSpawn == null) {
            playerSpawn = new Point(2 * TILE_SIZE, (MAX_WORLD_ROW - 5) * TILE_SIZE);
        }
        player.setX(playerSpawn.x);
        player.setY(playerSpawn.y);
        player.setHealth(player.getMaxHealth());
        player.setVelocityX(0);
        player.setVelocityY(0);
    }

    public void pauseGame() {
        if (gameThread != null) {
            isPaused = true;
        }
    }

    public void resumeGame() {
        isPaused = false;
    }

    public void showMapMenu() {
        pauseGame();
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.getContentPane().remove(this);
            MapMenuPanel mapMenu = new MapMenuPanel(frame, this);
            frame.getContentPane().add(mapMenu);
            mapMenu.requestFocus();
            frame.revalidate();
            frame.repaint();
        }
    }

    private void applyBoxGravity() {
        EObject[][] collisionMap = map.getCollisionMap();
        int width = map.getWidth();
        int height = map.getHeight();
        for (int y = height - 2; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (collisionMap[x][y] == EObject.BOX) {
                    if (y + 1 < height && collisionMap[x][y + 1] == EObject.EMPTY) {
                        collisionMap[x][y + 1] = EObject.BOX;
                        collisionMap[x][y] = EObject.EMPTY;
                    }
                }
            }
        }
    }
}