package org.example.realengine.map;

import org.example.realengine.entity.Entity;
import org.example.realengine.graphics.ImageNotFoundException;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reprezentuje herní mapu.
 * Obsahuje jednu nebo více vizuálních vrstev dlaždic {@link ETile}
 * a jednu kolizní mapu {@link EObject}. Může také spravovat entity na mapě.
 * Poskytuje také statické metody pro načítání map z obrázků, včetně vytváření entit ze spawn pointů.
 */
public class RMap {
    /**
     * Správce mapových elementů pro konverzi mezi ETile a EObject.
     */
    private static final MapElementManager elementManager = new MapElementManager();
    /**
     * Seznam vizuálních vrstev mapy. Každá vrstva je 2D pole dlaždic.
     */
    private final List<ETile[][]> layers = new ArrayList<>();
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
     * Kolizní mapa určující pevné a průchozí oblasti.
     */
    private EObject[][] collisionMap;
    /**
     * Název mapy (volitelné).
     */
    private String name = "Unnamed Map";
    private String description;

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

    public static RMap loadFromFiles(@NotNull String... filePaths) throws ImageNotFoundException {
        RMap map = new RMap(0, 0);
        for (String path : filePaths) {
            try {
                BufferedImage image = ImageIO.read(new File(path));
                map.addLayer(processImage(image));
            } catch (IOException e) {
                throw new ImageNotFoundException(path);
            }
        }
        return map;
    }

    public static RMap loadFromResources(@NotNull String... resourcePaths) {
        RMap map = new RMap(0, 0);
        for (String path : resourcePaths) {
            try {
                URL url = RMap.class.getClassLoader().getResource(path);
                if (url != null) map.addLayer(processImage(ImageIO.read(url)));
            } catch (IOException e) {
                System.err.println("Failed to load: " + path);
            }
        }
        return map;
    }

    private static ETile[][] processImage(@NotNull BufferedImage image) {
        return createETiles(image, image.getWidth(), image.getHeight());
    }

    /**
     * Načte mapu z obrázku a vytvoří kolizní mapu.
     *
     * @param imagePath Cesta k obrázku.
     * @return Nově vytvořená mapa.
     * @throws IOException Pokud se nepodaří načíst obrázek.
     */
    public static RMap loadMapFromImage(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        int width = image.getWidth();
        int height = image.getHeight();
        RMap map = new RMap(width, height);
        ETile[][] visualLayer = createETiles(image, width, height);
        map.addLayer(visualLayer);
        map.collisionMap = elementManager.createCollisionMapFromImage(image);
        return map;
    }

    private static ETile[][] createETiles(BufferedImage image, int width, int height) {
        ETile[][] visualLayer = new ETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                visualLayer[x][y] = ETile.fromRGB(image.getRGB(x, y));
            }
        }
        return visualLayer;
    }

    /**
     * Načte mapu z PNG souboru. Určí rozměry mapy z rozměrů obrázku.
     *
     * @param imagePath Cesta k PNG souboru.
     * @return Nová instance RMap.
     * @throws IOException Pokud dojde k chybě při načítání souboru.
     */
    public static RMap loadFromPng(String imagePath) throws IOException {
        BufferedImage image = loadImage(imagePath);
        if (image == null) {
            throw new IOException("Failed to load image: " + imagePath);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        MapElementManager manager = new MapElementManager();
        ETile[][] tileLayer = manager.createTileLayerFromImage(image);
        EObject[][] collisionData = manager.createCollisionMapFromImage(image);
        RMap map = new RMap(width, height);
        map.addLayer(tileLayer);
        map.setCollisionMap(collisionData);
        map.setName(new File(imagePath).getName().replaceFirst("[.][^.]+$", ""));
        return map;
    }

    /**
     * Načte mapu z PNG souboru.
     *
     * @param filePath Cesta k PNG souboru
     * @return Nová instance RMap
     * @throws IOException Pokud dojde k chybě při načítání souboru nebo formát není podporován
     */
    public static RMap loadFromFiles(String filePath) throws IOException {
        if (filePath != null && filePath.toLowerCase().endsWith(".png")) {
            return loadFromPng(filePath);
        } else {
            throw new IOException("Unsupported file format or null path: " + filePath);
        }
    }

    private static BufferedImage loadImage(String path) throws IOException {
        File imgFile = new File(path);
        if (!imgFile.exists()) {
            throw new IOException("Image file not found: " + path);
        }
        return ImageIO.read(imgFile);
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
    public void addLayer(@NotNull ETile[][] layer) {
        if (layer.length != width || layer.length == 0 || layer[0].length != height) {
            throw new IllegalArgumentException("Layer dimensions (" + layer.length + "x" + (layer.length > 0 ? layer[0].length : 0)
                    + ") do not match map dimensions (" + width + "x" + height + ").");
        }
        layers.add(layer);
        System.out.println("Added visual layer to map. Total layers: " + layers.size());
    }

    /**
     * Aktualizuje stav všech aktivních entit na mapě.
     * Tato metoda by měla být volána v hlavní herní smyčce.
     *
     * @param deltaTime Čas uplynulý od posledního snímku.
     */
    public void updateEntities(float deltaTime) {
        for (Entity entity : entities) {
            if (entity != null && entity.isActive()) {
                entity.update(deltaTime, collisionMap);
            }
        }
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
     * Odstraní entitu z mapy.
     *
     * @param entity Entita, která se má odstranit.
     * @return `true`, pokud byla entita úspěšně odstraněna, jinak `false`.
     */
    public boolean removeEntity(Entity entity) {
        return entities.remove(entity);
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
     * Pro přidání nebo odstranění použijte {@link #addEntity(Entity)} a {@link #removeEntity(Entity)}.
     */
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    /**
     * Získá typ kolizního objektu {@link EObject} na daných souřadnicích mapy.
     *
     * @param x Souřadnice X (sloupec).
     * @param y Souřadnice Y (řádek).
     * @return Typ objektu na daných souřadnicích. Vrací {@link EObject#BORDER}, pokud jsou souřadnice mimo mapu.
     */
    public EObject getObjectAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return EObject.BORDER;
        }
        return collisionMap[x][y] != null ? collisionMap[x][y] : EObject.EMPTY;
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
     * Získá dlaždici {@link ETile} na daných souřadnicích ve specifikované vizuální vrstvě.
     *
     * @param layerIndex Index vrstvy (0 pro první přidanou vrstvu atd.).
     * @param x          Souřadnice X (sloupec).
     * @param y          Souřadnice Y (řádek).
     * @return Typ dlaždice na daných souřadnicích nebo `null`, pokud jsou index vrstvy nebo souřadnice neplatné.
     */
    public ETile getTileAt(int layerIndex, int x, int y) {
        if (layerIndex < 0 || layerIndex >= layers.size() ||
                x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return layers.get(layerIndex)[x][y];
    }

    /**
     * Nastaví dlaždici {@link ETile} na daných souřadnicích ve specifikované vizuální vrstvě.
     * Umožňuje dynamickou úpravu vzhledu mapy během hry.
     *
     * @param layerIndex Index vrstvy (0 pro první přidanou vrstvu atd.).
     * @param x          Souřadnice X (sloupec).
     * @param y          Souřadnice Y (řádek).
     * @param tile       Typ dlaždice, která se má nastavit. Může být `null` pro prázdnou dlaždici.
     */
    public void setTileAt(int layerIndex, int x, int y, ETile tile) {
        if (layerIndex >= 0 && layerIndex < layers.size() &&
                x >= 0 && x < width && y >= 0 && y < height) {
            layers.get(layerIndex)[x][y] = tile;
        }
    }

    /**
     * @return Seznam všech vizuálních vrstev mapy.
     */
    public List<ETile[][]> getLayers() {
        return layers;
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
    public void setCollisionMap(@NotNull EObject[][] map) {
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

    /**
     * @return Název mapy.
     */
    public String getName() {
        return name;
    }

    /**
     * Nastaví název mapy.
     *
     * @param name Nový název mapy.
     */
    public void setName(String name) {
        this.name = (name != null && !name.isEmpty()) ? name : "Unnamed Map";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

