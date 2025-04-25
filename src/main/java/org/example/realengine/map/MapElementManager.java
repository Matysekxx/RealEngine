package org.example.realengine.map;

import org.example.realengine.object.EObject;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Třída pro jednotnou správu mapových elementů (ETile a EObject).
 * Umožňuje konverzi mezi barvami a elementy a mezi různými typy elementů.
 */
public class MapElementManager {
    private final Map<Integer, ETile> rgbToTileMap = new HashMap<>();
    private final Map<ETile, EObject> tileToObjectMap = new HashMap<>();
    private final Map<EObject, ETile> objectToTileMap = new HashMap<>();

    public Map<EObject, ETile> getObjectToTileMap() {
        return objectToTileMap;
    }

    public Map<ETile, EObject> getTileToObjectMap() {
        return tileToObjectMap;
    }

    /**
     * Vytvoří novou instanci správce mapových elementů s výchozím mapováním.
     */
    public MapElementManager() {
        initializeDefaultMappings();
    }

    /**
     * Inicializuje výchozí mapování mezi barvami a elementy.
     */
    private void initializeDefaultMappings() {
        for (ETile tile : ETile.values()) {
            registerTile(tile);
        }
        mapTileToObject(ETile.STONE, EObject.WALL);
        mapTileToObject(ETile.LAVA_UP, EObject.HAZARD_LIQUID);
        mapTileToObject(ETile.PLAYER_SPAWN, EObject.PLAYER_SPAWN);
        mapTileToObject(ETile.EMPTY, EObject.EMPTY);
        mapTileToObject(ETile.SKY, EObject.EMPTY);
        mapTileToObject(ETile.UNKNOWN, EObject.EMPTY);
        mapTileToObject(ETile.SLIME, EObject.SLIME);
        mapTileToObject(ETile.VINE, EObject.LADDER);
        mapTileToObject(ETile.PLANT, EObject.LADDER);
        mapTileToObject(ETile.TRAP, EObject.TRAP);
        mapTileToObject(ETile.BOX, EObject.BOX);
        mapTileToObject(ETile.SPRING, EObject.SPRING);
        mapTileToObject(ETile.TELEPORT_BLUE, EObject.TELEPORT_BLUE);
        mapTileToObject(ETile.TELEPORT_PURPLE, EObject.TELEPORT_PURPLE);
        mapTileToObject(ETile.TELEPORT_RED, EObject.TELEPORT_RED);
        mapTileToObject(ETile.WOOD, EObject.WALL);
        mapTileToObject(ETile.SNOW, EObject.SLIME);
        mapTileToObject(ETile.SAND, EObject.WALL);
        mapTileToObject(ETile.BRICK, EObject.WALL);

    }

    /**
     * Registruje vizuální dlaždici podle její RGB hodnoty.
     *
     * @param tile Dlaždice k registraci.
     */
    public void registerTile(ETile tile) {
        if (tile != null) {
            rgbToTileMap.put(tile.getRGB(), tile);
        }
    }


    public void mapTileToObject(ETile tile, EObject object) {
        if (tile != null && object != null) {
            tileToObjectMap.put(tile, object);
            if (!objectToTileMap.containsKey(object)) {
                objectToTileMap.put(object, tile);
            }
        }
    }

    /**
     * Získá vizuální dlaždici podle RGB hodnoty.
     *
     * @param rgb RGB hodnota barvy.
     * @return Odpovídající ETile nebo ETile.UNKNOWN.
     */
    public ETile getTileFromRGB(int rgb) {
        return rgbToTileMap.getOrDefault(rgb, ETile.EMPTY);
    }

    /**
     * Získá kolizní objekt odpovídající vizuální dlaždici.
     *
     * @param tile Vizuální dlaždice.
     * @return Odpovídající kolizní objekt nebo EObject.EMPTY.
     */
    public EObject getObjectFromTile(ETile tile) {
        if (tile == null) return EObject.EMPTY;
        return tileToObjectMap.getOrDefault(tile, EObject.EMPTY);
    }

    /**
     * Získá vizuální dlaždici odpovídající koliznímu objektu.
     * (Poznámka: Toto mapování nemusí být jednoznačné, pokud více dlaždic mapuje na stejný objekt)
     *
     * @param object Kolizní objekt.
     * @return Odpovídající vizuální dlaždice nebo ETile.UNKNOWN.
     */
    public ETile getTileFromObject(EObject object) {
        if (object == null) return ETile.UNKNOWN;
        return objectToTileMap.getOrDefault(object, ETile.UNKNOWN);
    }

    /**
     * Zpracuje obrázek a vytvoří 2D pole vizuálních dlaždic (ETile).
     *
     * @param image Obrázek mapy.
     * @return 2D pole ETile.
     */
    public ETile[][] createTileLayerFromImage(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Input image cannot be null.");
        }
        int width = image.getWidth();
        int height = image.getHeight();
        ETile[][] layer = new ETile[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                layer[x][y] = getTileFromRGB(rgb);
            }
        }
        return layer;
    }

    /**
     * Vytvoří kolizní mapu (EObject[][]) z vrstvy vizuálních dlaždic (ETile[][]).
     *
     * @param tileLayer Vrstva vizuálních dlaždic.
     * @return 2D pole EObject.
     */
    public EObject[][] createCollisionMapFromLayer(ETile[][] tileLayer) {
        if (tileLayer == null || tileLayer.length == 0) {
            throw new IllegalArgumentException("Input tile layer cannot be null or empty.");
        }
        int width = tileLayer.length;
        int height = tileLayer[0].length;
        EObject[][] collisionMap = new EObject[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                collisionMap[x][y] = getObjectFromTile(tileLayer[x][y]);
            }
        }
        return collisionMap;
    }

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
                //System.out.printf("Pixel (%d,%d): #%06X -> %s\n", x, y, rgb, tile.name());
                if (tile == ETile.UNKNOWN) {
                    System.out.printf("Unknown color at (%d,%d): #%06X\n", x, y, rgb);
                }
            }
        }
        return collisionMap;
    }
}