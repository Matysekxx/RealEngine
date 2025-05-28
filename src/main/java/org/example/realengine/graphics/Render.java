package org.example.realengine.graphics;

import org.example.realengine.entity.Entity;
import org.example.realengine.entity.Player;
import org.example.realengine.map.ETile;
import org.example.realengine.map.MapElementManager;
import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.example.realengine.game.GameConstants.TILE_SIZE;

/**
 * Handles the rendering of the game world (maps, entities) to the screen.
 * Utilizes {@link Camera} to determine the visible area and {@link MapElementManager} to obtain textures.
 */
public class Render {
    /**
     * Manages the mapping between game elements, colors, tiles, and objects.
     * Used to retrieve textures for rendering.
     */
    private static final MapElementManager manager = new MapElementManager();
    /**
     * A map associating {@link EObject} types with their corresponding {@link ETile} representations.
     * Used for rendering based on collision map data.
     */
    private static final Map<EObject, ETile> tiles = manager.getObjectToTileMap();

    /**
     * A boolean flag indicating whether textures should be rendered. If false, solid colors are used instead.
     */
    public static boolean texturesOn = true;

    /**
     * Toggles the `texturesOn` flag, switching between texture rendering and solid color rendering.
     */
    public static void reverseTexturesOn() {
        texturesOn = !texturesOn;
    }

    /**
     * The main method for rendering the entire game scene.
     * Renders the background, the map, and all entities visible through the camera.
     * If debug mode is enabled, additional information might be rendered.
     *
     * @param g      The graphics context to draw on.
     * @param map    The game map ({@link RMap}) to be rendered.
     * @param camera The camera ({@link Camera}) defining the view of the scene.
     */
    public void renderScene(final Graphics g, final RMap map, final Camera camera) {
        if (texturesOn) renderBackground(g, camera, map);
        renderMap(g, map, camera);
        renderEntities(g, map.getEntities(), camera);
    }

    /**
     * Renders the background of the scene.
     * This method draws the appropriate background image based on the current map's path.
     *
     * @param g      The graphics context.
     * @param camera The camera (used to get screen dimensions).
     * @param map    The current game map, used to determine the background image.
     */
    public void renderBackground(final Graphics g, final Camera camera, final RMap map) {
        g.drawImage(EBackground.backgrounds.getOrDefault(map.getPath(), EBackground.DEFAULT).getBackground(),
                0, 0, camera.getScreenWidth(), camera.getScreenHeight(), null);
    }

    /**
     * Renders all visual layers of the map visible through the camera.
     * Optimizes rendering by drawing only tiles within the camera's field of view.
     *
     * @param g      The graphics context.
     * @param map    The map to render.
     * @param camera The camera defining the visible area.
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
     * Renders all entities from the list that are visible through the camera.
     *
     * @param g        The graphics context.
     * @param entities The list of entities to render.
     * @param camera   The camera defining the visible area.
     */
    public void renderEntities(final Graphics g, final List<Entity> entities, final Camera camera) {
        if (entities == null) return;

        final float camX = camera.getX();
        final float camY = camera.getY();
        final int camW = camera.getScreenWidth();
        final int camH = camera.getScreenHeight();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity != null) {
                if (entity.getX() + entity.getWidth() >= camX && entity.getX() <= camX + camW &&
                        entity.getY() + entity.getHeight() >= camY && entity.getY() <= camY + camH) {
                    try {
                        renderEntity(g, entity, camera);
                    } catch (IOException _) {
                    }
                }
            }
        }
    }

    /**
     * Renders a single entity if it is visible through the camera.
     * Uses the entity's texture if available, otherwise renders a colored rectangle.
     * Can also render a health indicator.
     *
     * @param g      The graphics context.
     * @param entity The entity to render.
     * @param camera The camera defining the visible area.
     * @throws IOException If an I/O error occurs while retrieving the entity's texture.
     */
    public void renderEntity(final Graphics g, final Entity entity, final Camera camera) throws IOException {
        final var screenX = (int) (entity.getX() - camera.getX());
        final var screenY = (int) (entity.getY() - camera.getY());

        if (screenX + entity.getWidth() >= 0 && screenX <= camera.getScreenWidth() &&
                screenY + entity.getHeight() >= 0 && screenY <= camera.getScreenHeight())
            if (entity instanceof final Player p) {
                renderPlayer(g, p, screenX, screenY);
                return;
            }
        if (!texturesOn) {
            g.setColor(switch (entity.getType()) {
                case "enemy" -> Color.red;
                case "jumping" -> Color.yellow;
                default -> Color.orange;
            });
            g.fillRect(screenX, screenY, entity.getWidth(), entity.getHeight());
        } else {
            BufferedImage texture = entity.getTexture(entity.getCurrentAnimationState());
            if (texture != null) {
                g.drawImage(texture, screenX, screenY, TILE_SIZE, TILE_SIZE, null);
            }
        }

    }

    /**
     * Renders the player character.
     * If textures are off, it renders a blue rectangle. Otherwise, it uses the player's current animation texture.
     *
     * @param g       The graphics context.
     * @param player  The player entity to render.
     * @param screenX The X-coordinate on the screen where the player should be rendered.
     * @param screenY The Y-coordinate on the screen where the player should be rendered.
     */
    public void renderPlayer(final Graphics g, final Player player, int screenX, int screenY) {
        if (!texturesOn) {
            g.setColor(Color.blue);
            if (!player.isOnGround()) g.fillRect(screenX, screenY, player.getWidth() - 5, player.getHeight() + 5);
            else g.fillRect(screenX, screenY, player.getWidth(), player.getHeight());
        } else {
            g.drawImage(getCurrentTexture(player), screenX, screenY, TILE_SIZE, TILE_SIZE, null);
        }
    }

    /**
     * Retrieves the current texture for the player based on their animation state.
     *
     * @param player The player entity.
     * @return The {@link BufferedImage} representing the player's current texture.
     */
    private BufferedImage getCurrentTexture(Player player) {
        return player.getTexture(player.getCurrentAnimationState());
    }
}
