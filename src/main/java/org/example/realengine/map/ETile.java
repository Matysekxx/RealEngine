package org.example.realengine.map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the visual tiles based on their RGB color values.
 * Used for rendering the map and as the source for determining collision objects
 * via {@link MapElementManager}.
 */
public enum ETile {
    STONE(0x000000, "textures/stone.png"),
    LAVA_UP(0xFF9900, "textures/lava_up.png"),
    /*
    HARD_BLOCK(0, "textures/hardblock.png"),

    WOOD(0, "textures/wood.png"),
    WATER(0, "textures/water.png"),
    SNOW(0, "textures/snow.png"),
    SAND(0, "textures/sand.png"),
    BRICK(0, "textures/brick.png"),

     */
    PLAYER_SPAWN(0xFFFF00, "textures/sky.png"),
    EMPTY(0xFFFFFF, "textures/sky.png"),
    SKY(0x87CEEB, "textures/sky.png"),
    TRAP(0x8B0000, "textures/grass.png"),
    SLIME(0xB8860B, "textures/slime.png"),
    VINE(0x00BFFF, "textures/vine.png"),
    PLANT(0x00FFFF, "textures/plant.png"),
    BOX(0x8B4513, "textures/box.png"),
    UNKNOWN(0xFF00FF, "textures/sky.png"),
    SPRING(0xFF0000, "textures/spring.png"),
    TELEPORT_BLUE(0x0000FF, "textures/teleport_blue.png"),
    TELEPORT_PURPLE(0x00FF00, "textures/teleport_purple.png"),
    TELEPORT_RED(0xFF0033, "textures/teleport_red.png");

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
    public BufferedImage texture;

    ETile(int rgb, String path) {
        this.rgb = rgb;
        if (path != null) try {
            this.texture = ImageIO.read(new File(path));
        } catch (IOException _) {}
        else this.texture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

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
