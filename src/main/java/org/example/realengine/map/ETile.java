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
    STONE(new Color(0x000000), "textures/stone.png"),
    HARD_BLOCK(new Color(0x000000), "textures/hardblock.png"),
    LAVA_UP(new Color(0xFF9900), "textures/lava_up.png"),
    WOOD(new Color(0x000000), "textures/wood.png"),
    WATER(new Color(0x000000), "textures/water.png"),
    SNOW(new Color(0x000000), "textures/snow.png"),
    SAND(new Color(0x000000), "textures/sand.png"),
    BRICK(new Color(0x000000), "textures/brick.png"),
    PLAYER_SPAWN(new Color(0xFFFF00), "textures/sky.png"),
    EMPTY(new Color(0xFFFFFF), "textures/sky.png"),
    SKY(new Color(0x87CEEB), "textures/sky.png"),
    TRAP(new Color(0x8B0000), "textures/grass.png"),
    SLIME(new Color(0xB8860B), "textures/slime.png"),
    VINE(new Color(0x00BFFF), "textures/vine.png"),
    PLANT(new Color(0x00FFFF), "textures/plant.png"),
    BOX(new Color(0x8B4513), "textures/box.png"),
    UNKNOWN(new Color(0xFF00FF), "textures/sky.png"),
    SPRING(new Color(0xFF0000), "textures/spring.png"),
    TELEPORT_BLUE(new Color(0x0000FF), "textures/teleport_blue.png"),
    TELEPORT_PURPLE(new Color(0xC800FF), "textures/teleport_purple.png"),
    TELEPORT_RED(new Color(0xFF0033), "textures/teleport_red.png");

    private static final Map<Integer, ETile> rgbToTileMap = new HashMap<>();

    static {
        for (ETile tile : values()) {
            if (rgbToTileMap.containsKey(tile.color.getRGB())) {
                System.err.println("WARN: Duplicate RGB value " + Integer.toHexString(tile.color.getRGB()) +
                        " detected for ETile." + tile.name() +
                        " and ETile." + rgbToTileMap.get(tile.color.getRGB()).name());
            }
            rgbToTileMap.put(tile.color.getRGB(), tile);
        }

    }

    public final Color color;
    public BufferedImage texture;

    ETile(Color color, String path) {
        this.color = color;
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
        return color.getRGB();
    }

    public Color getColor() {
        return color;
    }
}