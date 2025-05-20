package org.example.realengine.object;

import org.example.realengine.map.RMap;

/**
 * Enum representing different types of collision objects in the game.
 * Each object defines whether it is walkable and whether it is harmful.
 * Used to build the collision map in {@link RMap}.
 */
public enum EObject {
    /**
     * Impassable wall or general obstacle.
     */
    WALL(false),
    /**
     * Impassable map border (for objects outside the defined area).
     */
    BORDER(false),
    /**
     * Impassable box, potentially pushable (base is impassable).
     */
    BOX(false),
    /**
     * Empty space, completely passable.
     */
    EMPTY(true),
    /**
     * Ladder, allows vertical movement. Passable.
     */
    LADDER(true),

    PLATFORM(false),

    SLIME(true),
    CHECKPOINT(true),
    /**
     * Player starting position. Passable.
     */
    PLAYER_SPAWN(true),
    /**
     * Trap (e.g., pitfall). Passable but harmful.
     */
    BACKGROUND_OBJECT(true),
    /**
     * Lava/acid. Passable but harmful.
     */
    HAZARD_LIQUID(true),
    SPIKE(true),

    TELEPORT_BLUE(true),
    TELEPORT_RED(true),
    TELEPORT_PURPLE(true),
    SPRING(true),
    ENEMY_SPAWN(true),
    END(true);

    private final boolean walkable;


    EObject(boolean walkable) {
        this.walkable = walkable;
    }

    /**
     * Checks if this object type is solid (not walkable).
     * Convenience method, equivalent to !isWalkable().
     *
     * @return true if the object is solid, false otherwise.
     */
    public boolean isSolid() {
        return !walkable;
    }
}
