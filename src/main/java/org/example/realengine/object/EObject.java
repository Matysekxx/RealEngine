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
    WALL(false, false),
    /**
     * Impassable map border (for objects outside the defined area).
     */
    BORDER(false, false),
   /**
     * Impassable box, potentially pushable (base is impassable).
     */
    BOX(false, false),
    /**
     * Empty space, completely passable.
     */
    EMPTY(true, false),
    /**
     * Ladder, allows vertical movement. Passable.
     */
    LADDER(true, false),

    SLIME(true, false),
    CHECKPOINT(true, false),
    /**
     * Player starting position. Passable.
     */
    PLAYER_SPAWN(true, false),
    /**
     * Trap (e.g., pitfall). Passable but harmful.
     */
    TRAP(true, true),
    /**
     * Lava/acid. Passable but harmful.
     */
    HAZARD_LIQUID(true, true),

    TELEPORT_BLUE(true, false),
    TELEPORT_RED(true, false),
    TELEPORT_PURPLE(true, false ),
    SPRING(false, false),;

    private final boolean walkable;
    private final boolean harmful;

    EObject(boolean walkable, boolean harmful) {
        this.walkable = walkable;
        this.harmful = harmful;
    }

    /**
     * Checks if this object type is generally walkable.
     * Specific entities might have different interactions (e.g., falling through platforms).
     *
     * @return true if the object is generally passable, false otherwise.
     */
    public boolean isWalkable() {
        return walkable;
    }

    /**
     * Checks if this object type causes harm on contact.
     *
     * @return true if the object is harmful, false otherwise.
     */
    public boolean isHarmful() {
        return harmful;
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
