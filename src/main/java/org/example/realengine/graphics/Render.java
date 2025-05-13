package org.example.realengine.graphics;

import org.example.realengine.entity.Enemy;
import org.example.realengine.entity.Entity;
import org.example.realengine.entity.Player;
import org.example.realengine.map.ETile;
import org.example.realengine.map.MapElementManager;
import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.example.realengine.game.GameConstants.TILE_SIZE;

/**
 * Zodpovídá za vykreslování herního světa (mapy, entit) na obrazovku.
 * Používá {@link Camera} pro určení viditelné oblasti a {@link MapElementManager} pro získání textur.
 */
public class Render {
    private static final MapElementManager manager = new MapElementManager();
    private static final Map<EObject, ETile> tiles = manager.getObjectToTileMap();

    public static boolean texturesOn = true;

    /**
     * Hlavní metoda pro vykreslení celé herní scény.
     * Vykreslí pozadí, mapu a všechny entity viditelné kamerou.
     * Pokud je zapnutý debug režim, vykreslí i další informace.
     *
     * @param g      Grafický kontext, na který se má kreslit.
     * @param map    Herní mapa {@link RMap}, která se má vykreslit.
     * @param camera Kamera {@link Camera} určující pohled na scénu.
     */
    public void renderScene(final Graphics g, final RMap map, final Camera camera) {
        if (g == null || map == null || camera == null) {
            System.err.println("WARN: Attempting to render scene with null Graphics, RMap, or Camera.");
            return;
        }
        renderBackground(g, camera, map);
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
    public void renderBackground(final Graphics g, final Camera camera, final RMap map) {
        if (texturesOn)
            g.drawImage(EBackground.backgrounds.getOrDefault(map.getPath(), EBackground.DEFAULT).getBackground(),
                    0, 0, camera.getScreenWidth(), camera.getScreenHeight(), null);
    }

    /**
     * Vykreslí všechny vizuální vrstvy mapy viditelné kamerou.
     * Optimalizuje vykreslování tím, že kreslí pouze dlaždice v zorném poli kamery.
     *
     * @param g      Grafický kontext.
     * @param map    Mapa k vykreslení.
     * @param camera Kamera určující viditelnou oblast.
     */
    public void renderMap(final Graphics g, final RMap map, final Camera camera) {
        final float camX = camera.getX();
        final float camY = camera.getY();

        final var startTileX = Math.max(0, (int) (camX / TILE_SIZE));
        final var startTileY = Math.max(0, (int) (camY / TILE_SIZE));
        final var endTileX = Math.min(map.getWidth(), (int) ((camX + camera.getScreenWidth()) / TILE_SIZE) + 1);
        final var endTileY = Math.min(map.getHeight(), (int) ((camY + camera.getScreenHeight()) / TILE_SIZE) + 1);

        EObject[][] collisionMap = map.getCollisionMap();
        if (collisionMap == null) {
            System.err.println("WARN: Collision map is null in Renderer.renderMap");
            return;
        }

        for (int y = startTileY; y < endTileY; y++) {
            for (int x = startTileX; x < endTileX; x++) {
                if (x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight()) {
                    EObject object = collisionMap[x][y];
                    if (object != null && object != EObject.EMPTY && object != EObject.PLAYER_SPAWN && object != EObject.ENEMY_SPAWN) {
                        int screenX = (int) (x * TILE_SIZE - camX);
                        int screenY = (int) (y * TILE_SIZE - camY);
                        if (texturesOn) {
                            BufferedImage texture = map.getLayer()[x][y].getTexture();
                            g.drawImage(texture, screenX, screenY, TILE_SIZE, TILE_SIZE, null);
                        } else {
                            Color color = tiles.get(object).getColor();
                            g.setColor(color);
                            g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
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
    public void renderEntities(final Graphics g, final List<Entity> entities, final Camera camera) {
        if (entities == null) return;
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i) != null) {
                try {
                    renderEntity(g, entities.get(i), camera);
                } catch (IOException _) {
                }
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
    public void renderEntity(final Graphics g, final Entity entity, final Camera camera) throws IOException {
        var screenX = (int) (entity.getX() - camera.getX());
        var screenY = (int) (entity.getY() - camera.getY());

        if (screenX + entity.getWidth() >= 0 && screenX <= camera.getScreenWidth() &&
                screenY + entity.getHeight() >= 0 && screenY <= camera.getScreenHeight())
            if (entity instanceof Player p) {
                renderPlayer(g, p, screenX, screenY);
                return;
            } else if (entity instanceof Enemy enemy){
                g.drawImage(enemy.getTexturesFromDirection().get(enemy.getDirection())[enemy.wasWalking() ? 1 : 0], screenX, screenY, TILE_SIZE, TILE_SIZE, null);
            }
        if (!texturesOn) {
            g.setColor(switch (entity.getType()) {
                case "enemy" -> Color.red;
                case "jumping_enemy" -> Color.yellow;
                default -> throw new IllegalStateException("Unexpected value: " + entity);
            });
            g.fillRect(screenX, screenY, entity.getWidth(), entity.getHeight());
        }

    }


    public void renderPlayer(final Graphics g, final Player player, int screenX, int screenY) {
        g.setColor(Color.blue);
        if (!player.isOnGround()) g.fillRect(screenX, screenY, player.getWidth() - 5, player.getHeight() + 5);
        else g.fillRect(screenX, screenY, player.getWidth(), player.getHeight());
    }
}
