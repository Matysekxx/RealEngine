package org.example.realengine.graphics;

import org.example.realengine.entity.Entity;
import org.example.realengine.game.GameConstants;
import org.example.realengine.map.ETile;
import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Zodpovídá za vykreslování herního světa (mapy, entit) na obrazovku.
 * Používá {@link Camera} pro určení viditelné oblasti a {@link TextureManager} pro získání textur.
 */
public class Renderer {

    /**
     * Výchozí velikost dlaždice v pixelech, používá se pro vykreslování mapy.
     */
    protected static final int TILE_SIZE = GameConstants.TILE_SIZE;
    /**
     * Správce textur pro získávání obrázků dlaždic a entit.
     */
    private final TextureManager textureManager;
    /**
     * Příznak pro zapnutí/vypnutí vykreslování debug informací.
     */
    private boolean debugMode = false;

    /**
     * Vytvoří nový Renderer s daným správcem textur.
     *
     * @param textureManager Správce textur, který bude renderer používat. Nesmí být `null`.
     * @throws NullPointerException pokud je `textureManager` `null`.
     */
    public Renderer(@NotNull TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    /**
     * @return Výchozí velikost dlaždice používaná rendererem.
     */
    public static int getTileSize() {
        return TILE_SIZE;
    }

    /**
     * Hlavní metoda pro vykreslení celé herní scény.
     * Vykreslí pozadí, mapu a všechny entity viditelné kamerou.
     * Pokud je zapnutý debug režim, vykreslí i další informace.
     *
     * @param g      Grafický kontext (`Graphics` nebo `Graphics2D`), na který se má kreslit.
     * @param map    Herní mapa {@link RMap}, která se má vykreslit.
     * @param camera Kamera {@link Camera} určující pohled na scénu.
     */
    public void renderScene(Graphics2D g, RMap map, Camera camera) {
        if (g == null || map == null || camera == null) {
            System.err.println("WARN: Attempting to render scene with null Graphics, RMap, or Camera.");
            return;
        }

        renderBackground(g, camera);
        renderMap(g, map, camera);
        renderEntities(g, map.getEntities(), camera);

        if (debugMode) {
            renderDebugInfo(g, map, camera);
        }
    }

    /**
     * Vykreslí jednoduché jednobarevné pozadí.
     * Lze přepsat v podtřídě pro vykreslení obrázku nebo paralaxního pozadí.
     *
     * @param g      Grafický kontext.
     * @param camera Kamera (pro získání rozměrů obrazovky).
     */
    protected void renderBackground(Graphics g, Camera camera) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, camera.getScreenWidth(), camera.getScreenHeight());
    }

    /**
     * Vykreslí všechny vizuální vrstvy mapy viditelné kamerou.
     * Optimalizuje vykreslování tím, že kreslí pouze dlaždice v zorném poli kamery.
     *
     * @param g      Grafický kontext.
     * @param map    Mapa k vykreslení.
     * @param camera Kamera určující viditelnou oblast.
     */
    public void renderMap(Graphics g, RMap map, Camera camera) {
        float camX = camera.getX();
        float camY = camera.getY();

        int startTileX = Math.max(0, (int) (camX / TILE_SIZE));
        int startTileY = Math.max(0, (int) (camY / TILE_SIZE));
        int endTileX = Math.min(map.getWidth(), (int) ((camX + camera.getScreenWidth()) / TILE_SIZE) + 1);
        int endTileY = Math.min(map.getHeight(), (int) ((camY + camera.getScreenHeight()) / TILE_SIZE) + 1);

        int layerIndex = 0;
        for (ETile[][] layer : map.getLayers()) {
            if (layer != null) {
                renderMapLayer(g, layer, startTileX, startTileY, endTileX, endTileY, camX, camY, layerIndex);
                layerIndex++;
            }
        }
    }

    /**
     * Vykreslí jednu specifickou vrstvu mapy.
     *
     * @param g          Grafický kontext.
     * @param layer      Pole dlaždic reprezentující vrstvu.
     * @param startX     Počáteční X index dlaždice k vykreslení.
     * @param startY     Počáteční Y index dlaždice k vykreslení.
     * @param endX       Koncový (exclusive) X index dlaždice k vykreslení.
     * @param endY       Koncový (exclusive) Y index dlaždice k vykreslení.
     * @param camX       X pozice kamery pro výpočet pozice na obrazovce.
     * @param camY       Y pozice kamery pro výpočet pozice na obrazovce.
     * @param layerIndex Index vykreslované vrstvy (pro informaci).
     */
    protected void renderMapLayer(Graphics g, ETile[][] layer, int startX, int startY, int endX, int endY, float camX, float camY, int layerIndex) {
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if (x < layer.length && y < layer[0].length) {
                    ETile tile = layer[x][y];
                    if (tile != null) {
                        Texture textureObj = textureManager.getTexture(tile.name());

                        if (textureObj != null) {
                            BufferedImage texture = textureObj.getImage();
                            int screenX = (int) (x * TILE_SIZE - camX);
                            int screenY = (int) (y * TILE_SIZE - camY);
                            g.drawImage(texture, screenX, screenY, TILE_SIZE, TILE_SIZE, null);
                        }
                    }
                }
            }
        }
    }

    /**
     * Vykreslí všechny entity ze seznamu, které jsou viditelné kamerou.
     *
     * @param g        Grafický kontext.
     * @param entities Seznam entit k vykreslení.
     * @param camera   Kamera určující viditelnou oblast.
     */
    public void renderEntities(Graphics g, List<Entity> entities, Camera camera) {
        if (entities == null) return;

        for (Entity entity : entities) {
            if (entity != null && entity.isActive()) {
                renderEntity(g, entity, camera);
            }
        }
    }

    /**
     * Vykreslí jednu entitu, pokud je viditelná kamerou.
     * Použije texturu entity, pokud je dostupná, jinak vykreslí barevný obdélník.
     * Může také vykreslit ukazatel zdraví.
     *
     * @param g      Grafický kontext.
     * @param entity Entita k vykreslení.
     * @param camera Kamera určující viditelnou oblast.
     */
    public void renderEntity(Graphics g, Entity entity, Camera camera) {
        float camX = camera.getX();
        float camY = camera.getY();

        int screenX = (int) (entity.getX() - camX);
        int screenY = (int) (entity.getY() - camY);

        if (screenX + entity.getWidth() >= 0 && screenX <= camera.getScreenWidth() &&
                screenY + entity.getHeight() >= 0 && screenY <= camera.getScreenHeight()) {

            Texture texture = entity.getTexture();
            if (texture != null) {
                g.drawImage(texture.getImage(), screenX, screenY,
                        entity.getWidth(), entity.getHeight(), null);
            } else {
                g.setColor(getColorForEntityType(entity.getType()));
                g.fillRect(screenX, screenY, entity.getWidth(), entity.getHeight());
                g.setColor(Color.BLACK);
                g.drawRect(screenX, screenY, entity.getWidth() - 1, entity.getHeight() - 1);
            }

            if (debugMode && entity.getHealth() < entity.getMaxHealth()) {
                renderHealthBar(g, entity, screenX, screenY);
            }
            if (debugMode) {
                g.setColor(Color.YELLOW);
                g.drawRect(screenX, screenY, entity.getWidth(), entity.getHeight());
            }
        }
    }

    /**
     * Returns a default color based on the entity type string.
     * Used as a fallback when textures are unavailable.
     *
     * @param type The entity type string (e.g., "player", "enemy").
     * @return A Color for the entity type.
     */
    protected Color getColorForEntityType(String type) {
        if (type == null) {
            return Color.GRAY;
        }
        return switch (type.toLowerCase()) {
            case "player" -> Color.BLUE;
            default -> Color.GRAY;
        };
    }


    /**
     * Pomocná metoda pro vykreslení ukazatele zdraví nad entitou.
     *
     * @param g       Grafický kontext.
     * @param entity  Entita, jejíž zdraví se má zobrazit.
     * @param screenX X souřadnice entity na obrazovce.
     * @param screenY Y souřadnice entity na obrazovce.
     */
    protected void renderHealthBar(Graphics g, Entity entity, int screenX, int screenY) {
        int barWidth = Math.max(10, entity.getWidth());
        int barHeight = 4;
        int yOffset = -8;

        int barX = screenX + (entity.getWidth() - barWidth) / 2;
        int barY = screenY + yOffset;

        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        float healthPercentage = entity.getHealth() / entity.getMaxHealth();
        int greenWidth = (int) (barWidth * healthPercentage);

        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, greenWidth, barHeight);

        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    /**
     * Vykreslí debug informace přes herní scénu.
     * Může zahrnovat mřížku, kolizní mapu, pozici kamery, FPS atd.
     *
     * @param g      Grafický kontext.
     * @param map    Aktuální mapa.
     * @param camera Aktuální kamera.
     */
    protected void renderDebugInfo(Graphics g, RMap map, Camera camera) {
        float camX = camera.getX();
        float camY = camera.getY();

        g.setColor(new Color(255, 255, 255, 50));
        int startGridX = (int) (camX / TILE_SIZE) * TILE_SIZE;
        int startGridY = (int) (camY / TILE_SIZE) * TILE_SIZE;
        for (int x = startGridX; x < camX + camera.getScreenWidth(); x += TILE_SIZE) {
            int screenX = (int) (x - camX);
            g.drawLine(screenX, 0, screenX, camera.getScreenHeight());
        }
        for (int y = startGridY; y < camY + camera.getScreenHeight(); y += TILE_SIZE) {
            int screenY = (int) (y - camY);
            g.drawLine(0, screenY, camera.getScreenWidth(), screenY);
        }

        g.setColor(new Color(255, 0, 0, 100));
        EObject[][] collisionMap = map.getCollisionMap();
        if (collisionMap != null) {
            int startTileX = Math.max(0, (int) (camX / TILE_SIZE));
            int startTileY = Math.max(0, (int) (camY / TILE_SIZE));
            int endTileX = Math.min(map.getWidth(), (int) ((camX + camera.getScreenWidth()) / TILE_SIZE) + 1);
            int endTileY = Math.min(map.getHeight(), (int) ((camY + camera.getScreenHeight()) / TILE_SIZE) + 1);

            for (int y = startTileY; y < endTileY; y++) {
                for (int x = startTileX; x < endTileX; x++) {
                    if (!collisionMap[x][y].isWalkable()) {
                        int screenX = (int) (x * TILE_SIZE - camX);
                        int screenY = (int) (y * TILE_SIZE - camY);
                        g.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString(String.format("Cam: %.1f, %.1f", camX, camY), 10, 20);
        g.drawString(String.format("Tiles: %d, %d", (int) (camX / TILE_SIZE), (int) (camY / TILE_SIZE)), 10, 35);
    }

    /**
     * @return `true`, pokud je zapnutý režim vykreslování debug informací.
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Zapne nebo vypne režim vykreslování debug informací.
     *
     * @param debugMode `true` pro zapnutí, `false` pro vypnutí.
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

}