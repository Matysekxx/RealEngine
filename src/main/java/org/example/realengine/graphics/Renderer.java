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

import static org.example.realengine.game.GameConstants.TILE_SIZE;

/**
 * Zodpovídá za vykreslování herního světa (mapy, entit) na obrazovku.
 * Používá {@link Camera} pro určení viditelné oblasti a {@link TextureManager} pro získání textur.
 */
public class Renderer {

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
    }

    /**
     * Vykreslí jednoduché jednobarevné pozadí.
     * Lze přepsat v podtřídě pro vykreslení obrázku nebo paralaxního pozadí.
     *
     * @param g      Grafický kontext.
     * @param camera Kamera (pro získání rozměrů obrazovky).
     */
    protected void renderBackground(Graphics g, Camera camera) {
        g.setColor(ETile.SKY.getColor());
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

        EObject[][] collisionMap = map.getCollisionMap();
        if (collisionMap == null) {
            System.err.println("WARN: Collision map is null in Renderer.renderMap");
            return;
        }

        for (int y = startTileY; y < endTileY; y++) {
            for (int x = startTileX; x < endTileX; x++) {
                if (x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight()) {
                    EObject object = collisionMap[x][y];
                    if (object != null && object != EObject.EMPTY) {
                        int screenX = (int) (x * TILE_SIZE - camX);
                        int screenY = (int) (y * TILE_SIZE - camY);
                        BufferedImage texture = textureManager.getTextureForObject(object);
                        Texture defaultTexture = textureManager.getTexture("default");
                        if (texture != null && (defaultTexture == null || texture != defaultTexture.getImage())) {
                            g.drawImage(texture, screenX, screenY, TILE_SIZE, TILE_SIZE, null);
                        } else {
                            Color color = object.getColor();
                            if (color != null) {
                                g.setColor(color);
                                g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                            }
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
}
