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
    STONE(new Color(0x808080), "textures/stone.png"), // #808080 - šedá
    HARD_BLOCK(new Color(0x808000), "textures/hard_block.png"), // #808000 - olivově zelená
    LAVA_UP(new Color(0xFF9900), "textures/lava_up.png"), // #FF9900 - oranžová
    WOOD(new Color(0x963204), "textures/wood.png"), // #963204 - tmavě hnědá
    WATER(new Color(0x2980b9), "textures/water.png"), // #2980B9 - modrá (oceánová)
    SNOW(new Color(0xd7dbdd), "textures/snow.png"), // #D7DBDD - světle šedobílá
    SAND(new Color(0xf4d03f), "textures/sand.png"), // #F4D03F - pískově žlutá
    BRICK(new Color(0xCD5C5C), "textures/brick.png"), // #CD5C5C - cihlově červená
    PLAYER_SPAWN(new Color(0xFFFF00), "textures/sky.png"), // #FFFF00 - jasně žlutá
    EMPTY(new Color(0xFFFFFF), "textures/sky.png"), // #FFFFFF - bílá
    SKY(new Color(0x87CEEB), "textures/sky.png"), // #87CEEB - nebeská modř
    TRAP(new Color(0x8B0000), "textures/grass.png"), // #8B0000 - temně červená
    DIRT(new Color(0x67201a), "textures/dirt.png"), // #67201A - tmavě hnědá (barva hlíny)
    GRASS(new Color(0x049625), "textures/grass.png"), // #049625 - sytě zelená (barva trávy)
    SLIME(new Color(0xB8860B), "textures/slime.png"), // #B8860B - bronzově hnědá
    VINE(new Color(0x00BFFF), "textures/vine.png"), // #00BFFF - jasně modrá (deep sky blue)
    PLANT(new Color(0x00FFFF), "textures/plant.png"), // #00FFFF - azurová (cyan)
    BOX(new Color(0x8B4513), "textures/box.png"), // #8B4513 - sedlově hnědá
    UNKNOWN(new Color(0xFF00FF), "textures/sky.png"), // #FF00FF - purpurová (magenta)
    SPRING(new Color(0xFF0000), "textures/spring.png"), // #FF0000 - červená
    TELEPORT_BLUE(new Color(0x0000FF), "textures/teleport_blue.png"), // #0000FF - modrá
    TELEPORT_PURPLE(new Color(0xC800FF), "textures/teleport_purple.png"), // #C800FF - fialová
    TELEPORT_RED(new Color(0xFF0033), "textures/teleport_red.png"); // #FF0033 - růžovočervená


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