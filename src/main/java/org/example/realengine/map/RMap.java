package org.example.realengine.map;

import org.example.realengine.entity.Enemy;
import org.example.realengine.entity.Entity;
import org.example.realengine.entity.Lakitu;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.realengine.game.GameConstants.TILE_SIZE;


/**
 * Represents the game map.
 * Contains one or more visual tile layers ({@link ETile})
 * and one collision map ({@link EObject}). It can also manage entities on the map.
 * Provides static methods for loading maps from images, including creating entities from spawn points.
 */
public class RMap {
    private final static Random random = new Random();
    /**
     * List of entities currently present on the map (loaded or added later).
     */
    private final List<Entity> entities = new ArrayList<>();
    /**
     * Map width in tiles.
     */
    private final int width;
    /**
     * Map height in tiles.
     */
    private final int height;
    /**
     * A 2D array of {@link ETile} objects representing the visual layer of the map.
     */
    private ETile[][] layer;
    /**
     * Collision map determining solid and passable areas.
     */
    private EObject[][] collisionMap;
    private String path = "resources\\maps\\defaultmap.png";

    /**
     * Creates a new empty map with the specified dimensions.
     * Initializes an empty collision map (all objects are {@link EObject#EMPTY}).
     *
     * @param width  The width of the map in tiles.
     * @param height The height of the map in tiles.
     * @throws IllegalArgumentException if the width or height is non-positive.
     */
    public RMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive. Got: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
        this.collisionMap = new EObject[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                collisionMap[x][y] = EObject.EMPTY;
            }
        }
        System.out.println("Created new RMap (" + width + "x" + height + ")");
    }

    /**
     * Loads a map from a PNG file. Determines map dimensions from the image dimensions.
     * This method reads the image pixel by pixel and uses the color information
     * to determine the type of tile and collision object at each position.
     * It also identifies and creates entities based on specific spawn point colors.
     *
     * <blockquote><pre>
     * Example usage:
     * RMap gameMap;
     * try {
     *      gameMap = RMap.loadFromPng("resources/maps/my_level.png");
     * } catch(IOException e) {
     *     throw new RunTimeException();
     * }
     * </pre></blockquote>
     *
     * @param imagePath The path to the PNG file.
     * @return A new RMap instance.
     * @throws IOException If an error occurs while loading the file.
     */
    public static RMap loadFromPng(final String imagePath) throws IOException {
        final BufferedImage image = loadImage(imagePath);
        if (image == null) {
            throw new IOException("Failed to load image: " + imagePath);
        }
        var width = image.getWidth();
        var height = image.getHeight();
        final MapElementManager manager = new MapElementManager();
        final ETile[][] tileLayer = manager.createTileLayerFromImage(image);
        final EObject[][] collisionData = manager.createCollisionMapFromImage(image);
        final RMap map = new RMap(width, height);
        map.setPath(imagePath);
        map.setLayer(tileLayer);
        map.setCollisionMap(collisionData);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (collisionData[x][y] == EObject.ENEMY_SPAWN) {
                    Entity enemy = switch (tileLayer[x][y]) {
                        case ENEMY_SPAWN -> new Enemy(x * TILE_SIZE, y * TILE_SIZE,
                                false, "enemy");
                        case JUMPING_ENEMY_SPAWN -> new Enemy(x * TILE_SIZE, y * TILE_SIZE,
                                true, "jumping");
                        case LAKITU_ENEMY_SPAWN -> new Lakitu(x * TILE_SIZE, y * TILE_SIZE, false);
                        case ANGRY_LAKITU_ENEMY -> new Lakitu(x * TILE_SIZE, y * TILE_SIZE, true);
                        default -> null;
                    };
                    map.addEntity(enemy);
                    collisionData[x][y] = EObject.EMPTY;
                }
            }
        }

        return map;
    }


    private static BufferedImage loadImage(final String path) throws IOException {
        final File imgFile = new File(path);
        if (!imgFile.exists()) {
            throw new IOException("Image file not found: " + path);
        }
        return ImageIO.read(imgFile);
    }

    /**
     * Returns the visual tile layer of the map.
     *
     * @return The 2D array of ETile representing the visual layer.
     */
    public ETile[][] getLayer() {
        return layer;
    }

    /**
     * Sets the visual tile layer for the map.
     * The layer must have the same dimensions as the map.
     * Layers are rendered in the order they are added.
     *
     * @param layer The 2D array of {@link ETile} to add. Must not be {@code null}.
     * @throws NullPointerException     if {@code layer} is {@code null}.
     * @throws IllegalArgumentException if the layer dimensions do not match the map dimensions.
     */
    public void setLayer(@NotNull final ETile[][] layer) {
        if (layer.length != width || layer.length == 0 || layer[0].length != height) {
            throw new IllegalArgumentException("Layer dimensions (" + layer.length + "x" + (layer.length > 0 ? layer[0].length : 0)
                    + ") do not match map dimensions (" + width + "x" + height + ").");
        }
        this.layer = layer;
    }


    /**
     * Přidá entitu na mapu.
     *
     * @param entity Entita, která se má přidat. Pokud je `null`, nic se nestane.
     */
    public void addEntity(Entity entity) {
        if (entity != null && !entities.contains(entity)) {
            entity.setDirection((random.nextBoolean() ? 1 : -1));
            entities.add(entity);
        }
    }

    /**
     * Odstraní všechny entity z mapy.
     */
    public void clearEntities() {
        entities.clear();
        System.out.println("All entities cleared from map.");
    }

    /**
     * @return Nemodifikovatelný seznam všech entit na mapě.
     * Pro přidání použijte {@link #addEntity(Entity)}.
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * Nastaví typ kolizního objektu {@link EObject} na daných souřadnicích mapy.
     * Umožňuje dynamickou úpravu kolizní mapy během hry.
     *
     * @param x      Souřadnice X (sloupec).
     * @param y      Souřadnice Y (řádek).
     * @param object Typ objektu, který se má nastavit. Pokud je `null`, je nastaveno {@link EObject#EMPTY}.
     */
    public void setObjectAt(int x, int y, EObject object) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            collisionMap[x][y] = (object != null) ? object : EObject.EMPTY;
        }
    }

    /**
     * @return Kolizní mapa (2D pole {@link EObject}).
     */
    public EObject[][] getCollisionMap() {
        return collisionMap;
    }

    /**
     * Nastaví kolizní mapu.
     * Nahradí stávající kolizní mapu novou. Mapa musí mít stejné rozměry jako RMap.
     *
     * @param map Nová kolizní mapa (pole {@link EObject}). Nesmí být `null`.
     * @throws NullPointerException     pokud je `map` `null`.
     * @throws IllegalArgumentException pokud rozměry mapy nesouhlasí s rozměry RMap.
     */
    public void setCollisionMap(@NotNull final EObject[][] map) {
        if (map.length != width || map.length == 0 || map[0].length != height) {
            throw new IllegalArgumentException("Collision map dimensions (" + map.length + "x" + (map.length > 0 ? map[0].length : 0)
                    + ") do not match RMap dimensions (" + width + "x" + height + ").");
        }
        this.collisionMap = map;
        System.out.println("Collision map set for RMap.");
    }

    /**
     * @return Šířka mapy v počtu dlaždic.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Výška mapy v počtu dlaždic.
     */
    public int getHeight() {
        return height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

