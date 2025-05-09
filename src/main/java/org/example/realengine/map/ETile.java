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
    /**
     * #808080 - šedá
     */
    STONE(new Color(0x808080), "textures/stone.png"),

    /**
     * #808000 - olivově zelená
     */
    HARD_BLOCK(new Color(0x808000), "textures/hard_block.png"),

    /**
     * #FF9900 - oranžová
     */
    LAVA(new Color(0xFF9900), "textures/lava_up.png"),

    /**
     * #963204 - tmavě hnědá
     */
    WOOD(new Color(0x963204), "textures/wood.png"),

    /**
     * #2980B9 - modrá (oceánová)
     */
    WATER(new Color(0x2980b9), "textures/water.png"),

    /**
     * #D7DBDD - světle šedobílá
     */
    SNOW(new Color(0xd7dbdd), "textures/snow.png"),

    /**
     * #F4D03F - pískově žlutá
     */
    SAND(new Color(0xf4d03f), "textures/sand.png"),

    /**
     * #CD5C5C - cihlově červená
     */
    BRICK(new Color(0xCD5C5C), "textures/brick.png"),

    /**
     * #FFFF00 - jasně žlutá
     */
    PLAYER_SPAWN(new Color(0xFFFF00), "textures/sky.png"),

    /**
     * #FFFFFF - bílá
     */
    EMPTY(new Color(0xFFFFFF), "textures/sky.png"),

    /**
     * #87CEEB - nebeská modř
     */
    SKY(new Color(0x87CEEB), "textures/sky.png"),

    /**
     * #228B22 - tmavě zelená pro pozadí trávy
     */
    BACKGROUND_GRASS(new Color(0x228B22), "textures/grass.png"),

    /**
     * #708090 - šedá břidlice pro pozadí kamene
     */
    BACKGROUND_STONE(new Color(0x708090), "textures/stone.png"),

    /**
     * #8B451A - sedlově hnědá pro pozadí hlíny
     */
    BACKGROUND_DIRT(new Color(0x8B451A), "textures/dirt.png"),

    /**
     * #B22222 - ohnivě červená pro pozadí lávy
     */
    BACKGROUND_LAVA(new Color(0xB22222), "textures/lava.png"),

    /**
     * #AFAEFF - světle fialová
     */
    SPIKE(new Color(0xAFAEFF), "textures/spike.png"),

    /**
     * #E6CBFF
     */
    CLOUD(new Color(0xE6CBFF), "textures/cloud.png"),

    /**
     * #67201A - tmavě hnědá (barva hlíny)
     */
    DIRT(new Color(0x67201a), "textures/dirt.png"),

    /**
     * #049625 - sytě zelená (barva trávy)
     */
    GRASS(new Color(0x049625), "textures/grass.png"),

    /**
     * #B8860B - bronzově hnědá
     */
    SLIME(new Color(0xB8860B), "textures/slime.png"),

    /**
     * #00BFFF - jasně modrá (deep sky blue)
     */
    VINE(new Color(0x00BFFF), "textures/vine.png"),

    /**
     * #00FFFF - azurová (cyan)
     */
    PLANT(new Color(0x00FFFF), "textures/plant.png"),

    /**
     * #8B4513 - sedlově hnědá
     */
    BOX(new Color(0x8B4513), "textures/box.png"),

    /**
     * #FF00FF - purpurová (magenta)
     */
    UNKNOWN(new Color(0xFF00FF), "textures/sky.png"),

    /**
     * #FF0000 - červená
     */
    SPRING(new Color(0xFF0000), "textures/spring.png"),

    /**
     * #0000FF - modrá
     */
    TELEPORT_BLUE(new Color(0x0000FF), "textures/teleport_blue.png"),

    /**
     * #C800FF - fialová
     */
    TELEPORT_PURPLE(new Color(0xC800FF), "textures/teleport_purple.png"),

    /**
     * #FF0033 - růžovočervená
     */
    TELEPORT_RED(new Color(0xFF0033), "textures/teleport_red.png"),
    /**
     * #FC38D8
     */
    END1(new Color(0xFC38D8), "textures/end1.png"),
    /**
     * #FF009D
     */
    END2(new Color(0xFF009D), "textures/end2.png");


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

    public Color getColor() {
        return color;
    }

    public int getRgb() {
        return rgb;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    private final Color color;
    private final int rgb;
    public BufferedImage texture;

    ETile(Color color, String path) {
        this.rgb = color.getRGB();
        this.color = color;
        try {
            this.texture = ImageIO.read(new File(path));
        } catch (IOException _) {}
    }

    public int getRGB() {
        return rgb;
    }
}