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

    /**
     * A platform object. Impassable from below, passable from sides and above.
     */
    PLATFORM(false),

    /**
     * Slime object. Passable and potentially affects movement.
     */
    SLIME(true),
    /**
     * Checkpoint object. Passable and saves player progress.
     */
    CHECKPOINT(true),
    /**
     * Player starting position. Passable.
     */
    PLAYER_SPAWN(true),
    /**
     * Background object. Passable and not interactive.
     */
    BACKGROUND_OBJECT(true),
    /**
     * Lava/acid. Passable but harmful.
     */
    HAZARD_LIQUID(true),
    /**
     * Spike hazard. Passable but harmful.
     */
    SPIKE(true),

    /**
     * Blue teleport object. Passable and teleports the player.
     */
    TELEPORT_BLUE(true),
    /**
     * Red teleport object. Passable and teleports the player.
     */
    TELEPORT_RED(true),
    /**
     * Purple teleport object. Passable and teleports the player.
     */
    TELEPORT_PURPLE(true),
    /**
     * Spring object. Passable and provides a jump boost.
     */
    SPRING(true),
    /**
     * Enemy starting position. Passable.
     */
    ENEMY_SPAWN(true),
    /**
     * Falling platform object. Impassable initially, becomes passable and falls when stepped on.
     */
    FALLING_PLATFORM(false),
    /**
     * End of level object. Passable and signifies level completion.
     */
    END(true);

    private final boolean walkable;

    /**
     * Constructs an EObject enum constant.
     *
     * @param walkable True if the object is walkable (passable), false otherwise.
     */
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
