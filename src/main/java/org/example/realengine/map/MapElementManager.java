package org.example.realengine.map;

import org.example.realengine.object.EObject;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for managing map elements, specifically {@link ETile} and {@link EObject}.
 * It facilitates conversions between RGB colors, visual tiles, and collision objects.
 * This class is crucial for parsing map images into game-understandable data structures.
 */
public final class MapElementManager {
    /**
     * Maps RGB integer values to their corresponding {@link ETile} enum constants.
     * This is used when parsing map images to identify visual tiles by their color.
     */
    private final Map<Integer, ETile> rgbToTileMap = new HashMap<>();
    /**
     * Maps {@link ETile} enum constants to their corresponding {@link EObject} enum constants.
     * This defines the collision properties associated with each visual tile.
     */
    private final Map<ETile, EObject> tileToObjectMap = new HashMap<>();
    /**
     * Maps {@link EObject} enum constants back to their primary {@link ETile} representation.
     * Useful for rendering or debugging purposes where a visual representation of an object is needed.
     */
    private final Map<EObject, ETile> objectToTileMap = new HashMap<>();

    /**
     * Constructs a new instance of the MapElementManager and initializes default mappings
     * between colors, tiles, and objects.
     */
    public MapElementManager() {
        initializeDefaultMappings();
    }

    public Map<EObject, ETile> getObjectToTileMap() {
        return objectToTileMap;
    }

    /**
     * Initializes the default mappings between RGB colors, {@link ETile}s, and {@link EObject}s.
     * This method registers all known {@link ETile}s and then defines their corresponding
     * collision {@link EObject}s.
     */
    private void initializeDefaultMappings() {
        for (ETile tile : ETile.values()) {
            registerTile(tile);
        }
        mapTileToObject(ETile.STONE, EObject.WALL);
        mapTileToObject(ETile.LAVA, EObject.HAZARD_LIQUID);
        mapTileToObject(ETile.PLAYER_SPAWN, EObject.PLAYER_SPAWN);
        mapTileToObject(ETile.EMPTY, EObject.EMPTY);
        mapTileToObject(ETile.UNKNOWN, EObject.EMPTY);
        mapTileToObject(ETile.SLIME, EObject.SLIME);
        mapTileToObject(ETile.VINE, EObject.LADDER);
        mapTileToObject(ETile.PLANT, EObject.LADDER);
        mapTileToObject(ETile.BACKGROUND_GRASS, EObject.BACKGROUND_OBJECT);
        mapTileToObject(ETile.BACKGROUND_DIRT, EObject.BACKGROUND_OBJECT);
        mapTileToObject(ETile.BACKGROUND_STONE, EObject.BACKGROUND_OBJECT);
        mapTileToObject(ETile.SPIKE, EObject.SPIKE);
        mapTileToObject(ETile.BOX, EObject.BOX);
        mapTileToObject(ETile.GRASS, EObject.WALL);
        mapTileToObject(ETile.DIRT, EObject.WALL);
        mapTileToObject(ETile.SPRING, EObject.SPRING);
        mapTileToObject(ETile.TELEPORT_BLUE, EObject.TELEPORT_BLUE);
        mapTileToObject(ETile.TELEPORT_PURPLE, EObject.TELEPORT_PURPLE);
        mapTileToObject(ETile.TELEPORT_RED, EObject.TELEPORT_RED);
        mapTileToObject(ETile.HARD_BLOCK, EObject.WALL);
        mapTileToObject(ETile.WOOD, EObject.WALL);
        mapTileToObject(ETile.BRICK, EObject.WALL);
        mapTileToObject(ETile.CLOUD, EObject.WALL);
        mapTileToObject(ETile.END1, EObject.END);
        mapTileToObject(ETile.END2, EObject.END);
        mapTileToObject(ETile.ENEMY_SPAWN, EObject.ENEMY_SPAWN);
        mapTileToObject(ETile.JUMPING_ENEMY_SPAWN, EObject.ENEMY_SPAWN);
        mapTileToObject(ETile.CHECKPOINT, EObject.CHECKPOINT);
        mapTileToObject(ETile.FALLING_PLATFORM, EObject.FALLING_PLATFORM);
        mapTileToObject(ETile.LAKITU_ENEMY_SPAWN, EObject.ENEMY_SPAWN);
        mapTileToObject(ETile.ANGRY_LAKITU_ENEMY, EObject.ENEMY_SPAWN);
    }

    /**
     * Registers a visual tile ({@link ETile}) based on its RGB color value.
     * This mapping allows the system to identify tiles from image data.
     *
     * @param tile The {@link ETile} to register. Its RGB value will be used as the key.
     */
    public void registerTile(final ETile tile) {
        if (tile != null) {
            rgbToTileMap.put(tile.getRGB(), tile);
        }
    }


    /**
     * Maps a visual tile ({@link ETile}) to a collision object ({@link EObject}).
     * This establishes the physical properties of a tile within the game world.
     * If an {@link EObject} does not yet have a primary {@link ETile} mapping, this method also sets it.
     *
     * @param tile   The {@link ETile} to map.
     * @param object The {@link EObject} it should map to.
     */
    public void mapTileToObject(final ETile tile, final EObject object) {
        if (tile != null && object != null) {
            tileToObjectMap.put(tile, object);
            if (!objectToTileMap.containsKey(object)) {
                objectToTileMap.put(object, tile);
            }
        }
    }

    /**
     * Retrieves a visual tile ({@link ETile}) based on its RGB color value.
     * If no specific tile is mapped to the given RGB, {@link ETile#EMPTY} is returned as a default.
     *
     * @param rgb The integer RGB value representing the color of the tile.
     * @return The corresponding {@link ETile} or {@link ETile#EMPTY} if not found.
     */
    public ETile getTileFromRGB(int rgb) {
        return rgbToTileMap.getOrDefault(rgb, ETile.EMPTY);
    }

    /**
     * Retrieves the collision object ({@link EObject}) corresponding to a given visual tile ({@link ETile}).
     * If the tile is null or no specific object is mapped to it, {@link EObject#EMPTY} is returned.
     *
     * @param tile The {@link ETile} for which to retrieve the collision object.
     * @return The corresponding {@link EObject} or {@link EObject#EMPTY} if no mapping exists or tile is null.
     */
    public EObject getObjectFromTile(final ETile tile) {
        if (tile == null) return EObject.EMPTY;
        return tileToObjectMap.getOrDefault(tile, EObject.EMPTY);
    }

    /**
     * Processes an image and creates a 2D array of visual tiles ({@link ETile}).
     * Each pixel's RGB value in the input image is converted into an {@link ETile} based on registered mappings.
     *
     * @param image The {@link BufferedImage} representing the map layer.
     * @return A 2D array of {@link ETile} objects, representing the visual layout of the map.
     * @throws IllegalArgumentException If the input image is null.
     */
    public ETile[][] createTileLayerFromImage(final BufferedImage image) {
        if (image == null) throw new IllegalArgumentException("Input image cannot be null.");

        int width = image.getWidth();
        int height = image.getHeight();
        final var layer = new ETile[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                layer[x][y] = getTileFromRGB(rgb);
            }
        }
        return layer;
    }

    /**
     * Creates a 2D array of collision objects ({@link EObject}) from a given image.
     * This method reads the RGB values from the image, converts them to {@link ETile}s,
     * and then maps those tiles to their corresponding {@link EObject}s to form the collision map.
     *
     * @param image The {@link BufferedImage} representing the map's collision layer.
     * @return A 2D array of {@link EObject}s, representing the collision properties of the map.
     * @throws IllegalArgumentException If the input image is null.
     */
    public EObject[][] createCollisionMapFromImage(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Input image cannot be null.");
        }
        int width = image.getWidth();
        int height = image.getHeight();
        EObject[][] collisionMap = new EObject[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                ETile tile = getTileFromRGB(rgb);
                collisionMap[x][y] = getObjectFromTile(tile);
            }
        }
        return collisionMap;
    }
}