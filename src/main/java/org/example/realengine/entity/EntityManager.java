package org.example.realengine.entity;

import org.example.realengine.graphics.Camera;
import org.example.realengine.map.RMap;

import static org.example.realengine.demo.GamePanel.MAX_WORLD_COL;

/**
 * Manages all entities within the game world, including their creation, removal, and updates.
 * It also handles interactions between entities, such as player-enemy collisions.
 */
public class EntityManager {
    /**
     * The camera used for determining which entities are within the viewable area.
     */
    private final Camera camera;
    /**
     * The player entity, which is a special entity that Lakitu targets and interacts with.
     */
    private final Player player;
    /**
     * The current game map, which contains the list of active entities.
     */
    private RMap map;

    /**
     * Constructs a new EntityManager.
     *
     * @param rMap   The initial game map.
     * @param camera The game camera.
     * @param player The player entity.
     */
    public EntityManager(RMap rMap, Camera camera, Player player) {
        this.map = rMap;
        this.camera = camera;
        this.player = player;
    }

    /**
     * Sets the current game map.
     *
     * @param map The new game map to set.
     */
    public void setMap(RMap map) {
        this.map = map;
    }

    /**
     * Removes an entity from the game map.
     *
     * @param entity The entity to remove.
     */
    public void removeEntity(Entity entity) {
        map.getEntities().remove(entity);
    }

    /**
     * Updates the state of all active entities in the game world.
     * This includes updating their positions, handling interactions, and removing dead or out-of-bounds entities.
     */
    public void updateEntities() {
        for (int i = 0; i < map.getEntities().size(); i++) {
            Entity entity = map.getEntities().get(i);
            if (entity instanceof Lakitu l) {
                if (l.getTarget() == null) {
                    l.setTarget(player);
                }
            }
            if (entity.getX() + entity.getWidth() >= camera.getX() &&
                    entity.getX() <= camera.getX() + camera.getScreenWidth() &&
                    entity.getY() + entity.getHeight() >= camera.getY() &&
                    entity.getY() <= camera.getY() + camera.getScreenHeight()) {
                entity.update((float) 0.016666668, map);
            }
            if ((entity.isDead() || entity.getX() == 0 || entity.getX() == MAX_WORLD_COL) && !(entity instanceof Player) && !(entity instanceof Lakitu)) {
                removeEntity(entity);
                i--;
                continue;
            }
            if (entity instanceof Enemy || entity instanceof Lakitu) {
                if (player.getX() < entity.getX() + entity.getWidth() &&
                        player.getX() + player.getWidth() > entity.getX() &&
                        player.getY() < entity.getY() + entity.getHeight() &&
                        player.getY() + player.getHeight() > entity.getY()) {
                    player.onDead();
                }
            }
        }
    }
}
