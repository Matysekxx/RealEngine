package org.example.realengine.object;

import org.example.realengine.map.RMap;

import java.awt.*;

/**
 * Enum representing different types of collision objects in the game.
 * Each object defines whether it is walkable and whether it is harmful.
 * Used to build the collision map in {@link RMap}.
 */
public enum EObject {
    /**
     * Impassable wall or general obstacle.
     */
    WALL(false, false, new Color(0x000000)),
    /**
     * Impassable map border (for objects outside the defined area).
     */
    BORDER(false, false, new Color(0x333333)),
    /**
     * Impassable box, potentially pushable (base is impassable).
     */
    BOX(false, false, new Color(0x8B4513)),
    /**
     * Empty space, completely passable.
     */
    EMPTY(true, false, new Color(0xFFFFFF)),
    /**
     * Ladder, allows vertical movement. Passable.
     */
    LADDER(true, false, new Color(0x00BFFF)),

    HONEY(true, false, new Color(0xB8860B)),
    CHECKPOINT(true, false, new Color(255, 215, 0)),
    /**
     * Player starting position. Passable.
     */
    PLAYER_SPAWN(true, false, new Color(0xFFFF00)),
    /**
     * Trap (e.g., pitfall). Passable but harmful.
     */
    TRAP(true, true, new Color(0x8B0000)),
    /**
     * Spikes. Impassable and harmful.
     */
    SPIKES(false, true, new Color(0xAAAAAA)),
    /**
     * Lava/acid. Passable but harmful.
     */
    HAZARD_LIQUID(true, true, new Color(0xFF9900)),

    TELEPORT_BLUE(true, false, new Color(0x0000FF)),
    TELEPORT_GREEN(true, false, new Color(0x00FF00)),
    TELEPORT_RED(true, false, new Color(0xFF0000)),
    SPRING(false, false, new Color(0xFF0000));

    private final boolean walkable;
    private final boolean harmful;
    private final Color color;

    EObject(boolean walkable, boolean harmful, Color color) {
        this.walkable = walkable;
        this.harmful = harmful;
        this.color = color;
    }

    /**
     * Returns the color associated with this object type.
     *
     * @return The Color instance.
     */
    public Color getColor() {
        return color;
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
