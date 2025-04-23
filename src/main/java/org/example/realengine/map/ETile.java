package org.example.realengine.map;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the visual tiles based on their RGB color values.
 * Used for rendering the map and as the source for determining collision objects
 * via {@link MapElementManager}.
 */
public enum ETile {
    WALL(0x000000),
    LAVA(0xFF9900),
    PLAYER_SPAWN(0xFFFF00),
    EMPTY(0xFFFFFF),
    SKY(0x87CEEB),
    SPIKES(0xAAAAAA),
    TRAP(0x8B0000),
    HONEY(0xB8860B),
    LADDER(0x00BFFF),
    BOX(0x8B4513),
    UNKNOWN(0xFF00FF),
    SPRING(0xFF0000),
    TELEPORT_BLUE(0x0000FF),
    TELEPORT_GREEN(0x00FF00),
    TELEPORT_RED(0xFF0033);

    private static final Map<Integer, ETile> rgbToTileMap = new HashMap<>();

    static {
        for (ETile tile : values()) {
            if (rgbToTileMap.containsKey(tile.rgb)) {
                System.err.println("WARN: Duplicate RGB value " + Integer.toHexString(tile.rgb) +
                        " detected for ETile." + tile.name() +
                        " and ETile." + rgbToTileMap.get(tile.rgb).name());
            }
            rgbToTileMap.put(tile.rgb, tile);
        }
    }

    public final int rgb;

    ETile(int rgb) {
        this.rgb = rgb;
    }

    public static ETile fromRGB(int rgb) {
        ETile tile = rgbToTileMap.get(rgb);
        return tile != null ? tile : UNKNOWN;
    }

    public int getRGB() {
        return rgb;
    }

    public Color getColor() {
        return new Color(rgb);
    }
}
