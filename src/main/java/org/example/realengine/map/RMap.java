package org.example.realengine.map;

import org.example.realengine.entity.Enemy;
import org.example.realengine.entity.Entity;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.example.realengine.game.GameConstants.TILE_SIZE;


/**
 * Reprezentuje herní mapu.
 * Obsahuje jednu nebo více vizuálních vrstev dlaždic {@link ETile}
 * a jednu kolizní mapu {@link EObject}. Může také spravovat entity na mapě.
 * Poskytuje také statické metody pro načítání map z obrázků, včetně vytváření entit ze spawn pointů.
 */
public class RMap {
    /**
     * Seznam entit aktuálně přítomných na mapě (načtených nebo přidaných později).
     */
    private final List<Entity> entities = new ArrayList<>();
    /**
     * Šířka mapy v dlaždicích.
     */
    private final int width;
    /**
     * Výška mapy v dlaždicích.
     */
    private final int height;
    /**
     * Seznam vizuálních vrstev mapy. Každá vrstva je 2D pole dlaždic.
     */
    private ETile[][] layer;
    /**
     * Kolizní mapa určující pevné a průchozí oblasti.
     */
    private EObject[][] collisionMap;
    private String path = "resources\\maps\\defaultmap.png";

    /**
     * Vytvoří novou prázdnou mapu se zadanými rozměry.
     * Inicializuje prázdnou kolizní mapu (všechny objekty jsou {@link EObject#EMPTY}).
     *
     * @param width  Šířka mapy v počtu dlaždic.
     * @param height Výška mapy v počtu dlaždic.
     * @throws IllegalArgumentException pokud je šířka nebo výška nekladná.
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
     * Načte mapu z PNG souboru. Určí rozměry mapy z rozměrů obrázku.
     *
     * @param imagePath Cesta k PNG souboru.
     * @return Nová instance RMap.
     * @throws IOException Pokud dojde k chybě při načítání souboru.
     */
    public static RMap loadFromPng(final String imagePath) throws IOException {
        BufferedImage image = loadImage(imagePath);
        if (image == null) {
            throw new IOException("Failed to load image: " + imagePath);
        }
        var width = image.getWidth();
        var height = image.getHeight();
        MapElementManager manager = new MapElementManager();
        ETile[][] tileLayer = manager.createTileLayerFromImage(image);
        EObject[][] collisionData = manager.createCollisionMapFromImage(image);
        RMap map = new RMap(width, height);
        map.setPath(imagePath);
        map.setLayer(tileLayer);
        map.setCollisionMap(collisionData);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (collisionData[x][y] == EObject.ENEMY_SPAWN) {
                    Enemy enemy = switch (tileLayer[x][y]) {
                        case ENEMY_SPAWN -> new Enemy(x * TILE_SIZE, y * TILE_SIZE,
                                false, "enemy");
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
        File imgFile = new File(path);
        if (!imgFile.exists()) {
            throw new IOException("Image file not found: " + path);
        }
        return ImageIO.read(imgFile);
    }

    public ETile[][] getLayer() {
        return layer;
    }

    /**
     * Přidá novou vizuální vrstvu dlaždic do mapy.
     * Vrstva musí mít stejné rozměry jako mapa.
     * Vrstvy se vykreslují v pořadí, v jakém byly přidány.
     *
     * @param layer Dvourozměrné pole dlaždic {@link ETile}, které se má přidat. Nesmí být `null`.
     * @throws NullPointerException     pokud je `layer` `null`.
     * @throws IllegalArgumentException pokud rozměry vrstvy nesouhlasí s rozměry mapy.
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

