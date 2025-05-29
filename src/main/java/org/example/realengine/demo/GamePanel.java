package org.example.realengine.demo;

import org.example.realengine.control.RControl;
import org.example.realengine.demo.mapmenu.MapMenuPanel;
import org.example.realengine.entity.EntityManager;
import org.example.realengine.entity.Player;
import org.example.realengine.graphics.Camera;
import org.example.realengine.graphics.Render;
import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.example.realengine.object.ObjectManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static org.example.realengine.game.GameConstants.TILE_SIZE;

/**
 * <p>The main game panel responsible for rendering, game logic, and managing the game loop.</p>
 * <p>Extends {@code JPanel} and implements {@code Runnable} to run the game loop in a separate thread.</p>
 * <p>Manages the game map, player, camera, and input processing.</p>
 */
public class GamePanel extends JPanel implements Runnable {
    /**
     * The maximum number of world columns.
     */
    public static final int MAX_WORLD_COL = 180;
    /**
     * The maximum number of world rows.
     */
    public static final int MAX_WORLD_ROW = 14;
    /**
     * Delay for box gravity application.
     */
    private static final int BOX_GRAVITY_DELAY = 6;
    /**
     * Frames per second for the game loop.
     */
    private static final int FPS = 60;
    /**
     * The width of the game world in pixels.
     */
    public static int WORLD_WIDTH = TILE_SIZE * MAX_WORLD_COL;
    /**
     * The height of the game world in pixels.
     */
    public static int WORLD_HEIGHT = TILE_SIZE * MAX_WORLD_ROW;
    /**
     * The renderer responsible for drawing game elements.
     */
    private final Render render;
    /**
     * The camera controlling the view of the game world.
     */
    private final Camera camera;
    /**
     * The player entity.
     */
    private final Player player;
    /**
     * The main JFrame of the application.
     */
    private final JFrame frame;
    /**
     * Manages all entities in the game.
     */
    private final EntityManager entityManager;
    /**
     * Records and manages best times for game maps.
     */
    private final RecordTime recordTime = new RecordTime("resources\\saves\\best_times.csv");
    /**
     * Manages all objects in the game.
     */
    private final ObjectManager objectManager;
    /**
     * The current screen width.
     */
    private int screenWidth;
    /**
     * The current screen height.
     */
    private int screenHeight;
    /**
     * The current game map.
     */
    private RMap map;
    /**
     * The player's spawn point on the map.
     */
    private Point spawnPoint;
    /**
     * The thread running the game loop.
     */
    private Thread gameThread;
    /**
     * Flag indicating if the game is paused.
     */
    private boolean isPaused = false;
    /**
     * Tick counter for box gravity.
     */
    private int boxGravityTick = 0;
    /**
     * The current audio being played.
     */
    private Audio audio;
    /**
     * The current game time in seconds.
     */
    private int time = 0;
    /**
     * Timer for tracking game time.
     */
    private final Timer timer = new Timer(1000, _ -> time++);

    /**
     * <p>Constructs a new {@code GamePanel}.</p>
     * <p>Initializes game components such as the renderer, map, player, camera, and entity/object managers.</p>
     * <p>Attempts to load the default map and find the player spawn point.</p>
     * @param frame The parent {@code JFrame} for this panel.
     * @throws RuntimeException if the default map cannot be loaded.
     */
    public GamePanel(JFrame frame) {
        this.frame = frame;
        this.render = new Render();
        try {
            this.map = RMap.loadFromPng("resources\\maps\\defaultmap.png");
            WORLD_WIDTH = map.getWidth() * TILE_SIZE;
            WORLD_HEIGHT = map.getHeight() * TILE_SIZE;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.spawnPoint = findSpawnPoint(map);
        if (spawnPoint == null) {
            System.out.println("WARN: PLAYER_SPAWN not found, using default spawn position.");
            spawnPoint = new Point(2 * TILE_SIZE, (MAX_WORLD_ROW - 5) * TILE_SIZE);
        }
        this.player = new Player(spawnPoint.x, spawnPoint.y, this);
        this.player.setWidth(TILE_SIZE);
        this.player.setHeight(TILE_SIZE);
        this.map.addEntity(player);
        this.camera = new Camera(1, 1, WORLD_WIDTH, WORLD_HEIGHT);
        this.camera.follow(player);
        this.entityManager = new EntityManager(map, camera, player);
        this.objectManager = new ObjectManager();
        this.init();
    }

    /**
     * Retrieves the {@code RecordTime} instance used for managing best times.
     * @return The {@code RecordTime} instance.
     */
    public RecordTime getRecordTime() {
        return recordTime;
    }

    /**
     * Ends the current level, saves the player's time, and displays the map menu.
     */
    public void endLevel() {
        recordTime.saveTime(map.getPath(), time);
        showMapMenu();
    }

    /**
     * Initializes the game panel's properties, sets up audio, input listeners, and full-screen mode.
     */
    private void init() {
        this.setBackground(new Color(25, 25, 40));
        this.setDoubleBuffered(true);
        this.setAudio();
        this.addKeyListener(new RControl(player));
        this.setFocusable(true);
        this.setFullScreen();
        this.timer.start();
    }

    /**
     * Retrieves the {@code EntityManager} instance responsible for managing game entities.
     * @return The {@code EntityManager} instance.
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Retrieves the {@code ObjectManager} instance responsible for managing game objects.
     * @return The {@code ObjectManager} instance.
     */
    public ObjectManager getObjectManager() {
        return objectManager;
    }

    /**
     * Sets the panel to full-screen mode by adjusting its preferred size to match the frame's dimensions.
     * Also updates the camera's screen dimensions and follow offset.
     */
    public void setFullScreen() {
        this.screenWidth = frame.getWidth();
        this.screenHeight = frame.getHeight();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        camera.updateScreenDimensions(screenWidth, screenHeight);
        camera.setFollowOffsetX(-screenWidth / 4.0f);
    }

    /**+
     * Finds the first occurrence of {@code PLAYER_SPAWN} in the map's collision layer.
     * @param mapToSearch The {@code RMap} to search within.
     * @return A {@code Point} representing the top-left pixel coordinates of the spawn tile, or {@code null} if not found.
     */
    private Point findSpawnPoint(final RMap mapToSearch) {
        final EObject[][] collisionLayer = mapToSearch.getCollisionMap();
        if (collisionLayer == null) return null;
        for (int y = 0; y < mapToSearch.getHeight(); y++) {
            for (int x = 0; x < mapToSearch.getWidth(); x++) {
                if (x < collisionLayer.length && y < collisionLayer[x].length)
                    if (collisionLayer[x][y] == EObject.PLAYER_SPAWN)
                        return new Point(x * TILE_SIZE, y * TILE_SIZE);

            }
        }
        return null;
    }

    /**
     * Starts the main game loop thread.
     * If screen dimensions are not yet set, it attempts to get them from the frame or default to screen size.
     */
    public void startGameThread() {
        if (screenWidth == 0 || screenHeight == 0) {
            if (frame.getWidth() > 0 && frame.getHeight() > 0) {
                this.screenWidth = frame.getWidth();
                this.screenHeight = frame.getHeight();
            } else {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                this.screenWidth = screenSize.width;
                this.screenHeight = screenSize.height;
            }
            this.setPreferredSize(new Dimension(screenWidth, screenHeight));
            camera.updateScreenDimensions(screenWidth, screenHeight);
            camera.setFollowOffsetX(-screenWidth / 4.0f);
        }
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Retrieves the current game map.
     * @return The current {@code RMap} instance.
     */
    public RMap getMap() {
        return map;
    }

    /**
     * Updates the game state for all relevant components.
     * This includes applying box gravity, updating falling platforms, checking player bounds,
     * updating entities, and updating the camera.
     */
    private void update() {
        boxGravityTick++;
        if (boxGravityTick >= BOX_GRAVITY_DELAY) {
            objectManager.applyBoxGravity(this.map);
            boxGravityTick = 0;
        }
        final int playerTileX = (int) ((player.getX() + player.getWidth() / 2.0) / TILE_SIZE);
        final int playerTileY = (int) ((player.getY() + player.getHeight() / 2.0) / TILE_SIZE);
        objectManager.updateFallingPlatforms(this.map, playerTileX, playerTileY);
        if (player.getY() > WORLD_HEIGHT) {
            respawnPlayer();
        }
        entityManager.updateEntities();
        camera.update();
    }

    /**
     * Respawns the player at the designated spawn point.
     */
    private void respawnPlayer() {
        resetPlayer(spawnPoint);
    }

    /**
     * Renders the current game state using the Renderer.
     * Called by repaint().
     * @param g Graphics context (automatically provided by Swing).
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render.renderScene(g, map, camera);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Use arrow or wsad to move and jump", 10, 30);
        g.drawString("Time: " + time + " seconds", 10, 60);
        g.drawString("Best time: " + recordTime.getBestTime(map.getPath()), 10, 90);
    }

    /**
     * The main game loop. Runs at a fixed frame rate (FPS).
     * Updates game state and repaints the panel.
     */
    @Override
    public void run() {
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
        }
    }

    /**
     * Loads a new map, replacing the current one. Updates camera bounds and player position.
     * Resets game time and audio.
     * @param newMap The new {@code RMap} instance to load.
     */
    public void loadMap(final @NotNull RMap newMap) {
        if (this.map != null) {
            this.map.clearEntities();
        }
        this.map = newMap;
        WORLD_WIDTH = map.getWidth() * TILE_SIZE;
        WORLD_HEIGHT = map.getHeight() * TILE_SIZE;
        camera.setWorldDimensions(WORLD_WIDTH, WORLD_HEIGHT);
        spawnPoint = findSpawnPoint(this.map);
        player.setSpawnPoint(spawnPoint);
        resetPlayer(spawnPoint);
        this.map.addEntity(player);
        setAudio();
        this.time = 0;
        this.timer.restart();
    }

    /**
     * Sets the audio for the current map. Stops any currently playing audio and starts the new one.
     * Defaults to a predefined audio if no specific music is found for the map.
     */
    public void setAudio() {
        stopAudio();
        audio = Audio.musicMap.getOrDefault(this.map.getPath(), Audio.DEFAULT_AUDIO);
        audio.startAudio();
    }

    /**
     * Stops the currently playing audio, if any.
     */
    public void stopAudio() {
        if (audio != null) audio.stopMusic();
    }

    /**
     * Resets the player's position, health, and velocity to the given spawn point.
     * If the provided spawn point is null, a default spawn point is used.
     * @param playerSpawn The {@code Point} representing the desired spawn coordinates for the player.
     */
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

    /**
     * Pauses the game by setting the {@code isPaused} flag to true and stopping the audio.
     * This prevents the game loop from updating.
     */
    public void pauseGame() {
        if (gameThread != null) {
            isPaused = true;
            stopAudio();
        }
    }

    /**
     * Resumes the game by setting the {@code isPaused} flag to false and restarting the game timer.
     * Allows the game loop to continue updating.
     */
    public void resumeGame() {
        isPaused = false;
        this.timer.start();
    }

    /**
     * Pauses the game and displays the map selection menu.
     * Removes the current game panel from the frame and adds the {@code MapMenuPanel}.
     */
    public void showMapMenu() {
        pauseGame();
        frame.getContentPane().remove(this);
        final MapMenuPanel mapMenu = new MapMenuPanel(frame, this);
        frame.getContentPane().add(mapMenu);
        mapMenu.requestFocus();
        frame.revalidate();
        frame.repaint();
        this.timer.stop();
    }

    /**
     * Retrieves the current {@code Audio} instance being used by the game panel.
     * @return The current {@code Audio} instance.
     */
    public Audio getAudio() {
        return audio;
    }
}