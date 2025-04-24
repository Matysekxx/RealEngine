package org.example.realengine;

import org.example.realengine.control.RControl;
import org.example.realengine.entity.Player;
import org.example.realengine.game.GameConstants;
import org.example.realengine.graphics.Camera;
import org.example.realengine.graphics.Render;
import org.example.realengine.graphics.TextureManager;
import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {
    public static final int MAX_SCREEN_COL = 26;
    public static final int MAX_SCREEN_ROW = 16;
    public static final int MAX_WORLD_COL = 80;
    public static final int MAX_WORLD_ROW = 20;
    private static final int TILE_SIZE = GameConstants.TILE_SIZE;
    public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;
    public static final int WORLD_WIDTH = TILE_SIZE * MAX_WORLD_COL;
    public static final int WORLD_HEIGHT = TILE_SIZE * MAX_WORLD_ROW;
    private static final int BOX_GRAVITY_DELAY = 6;
    private final RControl rControl;
    private final TextureManager textureManager;
    private final Render render;
    private final Camera camera;
    private final Player player;
    private final int FPS = 60;
    private RMap map;
    private Point spawnPoint;
    private Thread gameThread;
    private boolean isPaused = false;
    private int boxGravityTick = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(25, 25, 40));
        this.setDoubleBuffered(true);
        textureManager = new TextureManager("textures/");
        render = new Render(textureManager);
        try {
            map = RMap.loadFromPng("maps/test.png");
        }catch (IOException _){}
        map = new RMap(MAX_WORLD_COL, MAX_WORLD_ROW);
        createChallengeMap(map);
        Point playerSpawn = findSpawnPoint(map);
        if (playerSpawn == null) {
            System.out.println("WARN: PLAYER_SPAWN not found, using default spawn position.");
            playerSpawn = new Point(2 * TILE_SIZE, (MAX_WORLD_ROW - 5) * TILE_SIZE);
        }
        player = new Player(playerSpawn.x, playerSpawn.y);
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
        int tileSize = TILE_SIZE;
        for (int y = 0; y < mapToSearch.getHeight(); y++) {
            for (int x = 0; x < mapToSearch.getWidth(); x++) {
                if (x < collisionLayer.length && y < collisionLayer[x].length) {
                    if (collisionLayer[x][y] == EObject.PLAYER_SPAWN) {
                        return new Point(x * tileSize, y * tileSize);
                    }
                }
            }
        }
        throw new IllegalArgumentException("Spawn point not found");
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        render.renderScene(g2, map, camera);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Use arrow keys to move and jump", 10, 30);
        g2.dispose();
    }

    /**
     * Helper method to clear the entire map
     */
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
    private void fillArea(RMap targetMap, int startX, int startY, int width, int height, EObject object) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                targetMap.setObjectAt(startX + x, startY + y, object);
            }
        }
    }

    private void createChallengeMap(RMap targetMap) {
        clearMap(targetMap);
        int groundY = MAX_WORLD_ROW - 3;
        int mapBottomHeight = MAX_WORLD_ROW - groundY;

        fillArea(targetMap, 0, groundY, 15, mapBottomHeight, EObject.WALL);
        fillArea(targetMap, 15, groundY, 3, mapBottomHeight, EObject.HAZARD_LIQUID);
        fillArea(targetMap, 18, groundY, 12, mapBottomHeight, EObject.WALL);
        fillArea(targetMap, 30, groundY, 4, mapBottomHeight, EObject.HAZARD_LIQUID);
        fillArea(targetMap, 34, groundY, 10, mapBottomHeight, EObject.WALL);
        fillArea(targetMap, 44, groundY, 3, mapBottomHeight, EObject.HAZARD_LIQUID);
        fillArea(targetMap, 47, groundY, 10, mapBottomHeight, EObject.WALL);
        fillArea(targetMap, 57, groundY, 3, mapBottomHeight, EObject.HAZARD_LIQUID);
        fillArea(targetMap, 60, groundY, MAX_WORLD_COL - 60, mapBottomHeight, EObject.WALL);
        targetMap.setObjectAt(2, groundY - 1, EObject.PLAYER_SPAWN);
        targetMap.setObjectAt(8, groundY - 4, EObject.BOX);
        targetMap.setObjectAt(22, groundY - 4, EObject.BOX);
        targetMap.setObjectAt(24, groundY - 4, EObject.BOX);
        fillArea(targetMap, 26, groundY - 8, 1, 8, EObject.EMPTY);
        fillArea(targetMap, 26, groundY - 8, 1, 8, EObject.LADDER);
        fillArea(targetMap, 27, groundY - 8, 15, 1, EObject.WALL);
        targetMap.setObjectAt(28, groundY - 9, EObject.BOX);
        fillArea(targetMap, 35, groundY - 9, 4, 1, EObject.HONEY);
        targetMap.setObjectAt(43, groundY - 1, EObject.SPRING);
        fillArea(targetMap, 47, groundY - 7, 6, 1, EObject.WALL);
        targetMap.setObjectAt(60, groundY - 1, EObject.WALL);
        targetMap.setObjectAt(60, groundY - 2, EObject.WALL);
        targetMap.setObjectAt(60, groundY - 3, EObject.WALL);
        fillArea(targetMap, 62, groundY - 4, 3, 1, EObject.WALL);
        fillArea(targetMap, 68, groundY - 6, 4, 1, EObject.WALL);
        targetMap.setObjectAt(70, groundY - 7, EObject.BOX);
        fillArea(targetMap, 74, groundY - 7, 1, 7, EObject.EMPTY);
        fillArea(targetMap, 74, groundY - 7, 1, 7, EObject.LADDER);
        fillArea(targetMap, 75, groundY - 7, 3, 1, EObject.WALL);
        fillArea(targetMap, 65, groundY - 5, 2, 1, EObject.WALL);
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
        camera.setWorldDimensions(newWorldWidth, newWorldHeight);
        spawnPoint = findSpawnPoint(this.map);
        if (spawnPoint == null) {
            System.err.println("CRITICAL: PLAYER_SPAWN not found in map: " + newMap.getName() + ". Using default spawn.");
            spawnPoint = new Point(2 * TILE_SIZE, (newMap.getHeight() - 5) * TILE_SIZE);
            spawnPoint.x = Math.max(0, Math.min(spawnPoint.x, newWorldWidth - TILE_SIZE));
            spawnPoint.y = Math.max(0, Math.min(spawnPoint.y, newWorldHeight - TILE_SIZE));
        }
        player.setSpawnPoint(spawnPoint);
        resetPlayer(spawnPoint);
        this.map.addEntity(player);
        System.out.println("Loaded map: " + this.map.getName() + " (" + this.map.getWidth() + "x" + this.map.getHeight() + ")");
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


