package org.example.realengine.map;

import org.example.realengine.resource.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Enum representing the visual tiles based on their RGB color values.
 * Used for rendering the map and as the source for determining collision objects
 * via {@link MapElementManager}.
 */
public enum ETile {
    /**
     * Represents a stone tile. Hex color: #808080.
     */
    STONE(new Color(0x808080), "textures/stone.png"),

    /**
     * Represents a hard block tile. Hex color: #808000.
     */
    HARD_BLOCK(new Color(0x808000), "textures/hard_block.png"),

    /**
     * Represents a lava tile. Hex color: #FF9900.
     */
    LAVA(new Color(0xFF9900), "textures/lava_up.png"),

    /**
     * Represents a wood tile. Hex color: #963204.
     */
    WOOD(new Color(0x963204), "textures/wood.png"),

    /**
     * Represents a brick tile. Hex color: #CD5C5C.
     */
    BRICK(new Color(0xCD5C5C), "textures/brick.png"),

    /**
     * Represents the player spawn point. Hex color: #FFFF00.
     */
    PLAYER_SPAWN(new Color(0xFFFF00), "textures/sky.png"),

    /**
     * Represents an empty, transparent tile. Hex color: #FFFFFF.
     */
    EMPTY(new Color(0xFFFFFF), "textures/sky.png"),

    /**
     * Represents a background grass tile. Hex color: #228B22.
     */
    BACKGROUND_GRASS(new Color(0x228B22), "textures/grass.png"),

    /**
     * Represents a background stone tile. Hex color: #708090.
     */
    BACKGROUND_STONE(new Color(0x708090), "textures/stone.png"),

    /**
     * Represents a background dirt tile. Hex color: #8B451A.
     */
    BACKGROUND_DIRT(new Color(0x8B451A), "textures/dirt.png"),

    /**
     * Represents a spike hazard tile. Hex color: #AFAEFF.
     */
    SPIKE(new Color(0xAFAEFF), "textures/spike.png"),

    /**
     * Represents a cloud tile. Hex color: #E6CBFF.
     */
    CLOUD(new Color(0xE6CBFF), "textures/cloud.png"),

    /**
     * Represents a dirt tile. Hex color: #67201A.
     */
    DIRT(new Color(0x67201a), "textures/dirt.png"),

    /**
     * Represents a grass tile. Hex color: #049625.
     */
    GRASS(new Color(0x049625), "textures/grass.png"),

    /**
     * Represents a slime tile. Hex color: #B8860B.
     */
    SLIME(new Color(0xB8860B), "textures/slime.png"),

    /**
     * Represents a vine tile, typically used for climbing. Hex color: #00BFFF.
     */
    VINE(new Color(0x00BFFF), "textures/vine.png"),

    /**
     * Represents a plant tile, typically used for climbing. Hex color: #00FFFF.
     */
    PLANT(new Color(0x00FFFF), "textures/plant.png"),

    /**
     * Represents a pushable box tile. Hex color: #8B4513.
     */
    BOX(new Color(0x8B4513), "textures/box.png"),

    /**
     * Represents an unknown or unmapped tile. Hex color: #FF00FF.
     */
    UNKNOWN(new Color(0xFF00FF), "textures/sky.png"),

    /**
     * Represents a spring tile, providing a jump boost. Hex color: #FF0000.
     */
    SPRING(new Color(0xFF0000), "textures/spring.png"),

    /**
     * Represents a generic enemy spawn point. Hex color: #FF1050.
     */
    ENEMY_SPAWN(new Color(0xFF1050), null),
    /**
     * Represents a jumping enemy spawn point. Hex color: #FFE969.
     */
    JUMPING_ENEMY_SPAWN(new Color(0xFFE969), null),
    /**
     * Represents a Lakitu enemy spawn point. Hex color: #FF69FF.
     */
    LAKITU_ENEMY_SPAWN(new Color(0xFF69FF), null),

    /**
     * Represents an angry Lakitu enemy spawn point. Hex color: #6FF9A0.
     */
    ANGRY_LAKITU_ENEMY(new Color(0x6FF9A0), null),
    /**
     * Represents a checkpoint tile. Hex color: #9F09FF.
     */
    CHECKPOINT(new Color(0x9F09FF), "textures/checkpoint.png"),

    /**
     * Represents a blue teleport tile. Hex color: #0000FF.
     */
    TELEPORT_BLUE(new Color(0x0000FF), "textures/teleport_blue.png"),

    /**
     * Represents a purple teleport tile. Hex color: #C800FF.
     */
    TELEPORT_PURPLE(new Color(0xC800FF), "textures/teleport_purple.png"),

    /**
     * Represents a red teleport tile. Hex color: #FF0033.
     */
    TELEPORT_RED(new Color(0xFF0033), "textures/teleport_red.png"),
    /**
     * Represents the first part of an end-of-level marker. Hex color: #FC38D8.
     */
    END1(new Color(0xFC38D8), "textures/end1.png"),
    /**
     * Represents the second part of an end-of-level marker. Hex color: #FF009D.
     */
    END2(new Color(0xFF009D), "textures/end2.png"),

    /**
     * Represents a falling platform tile. Hex color: #00FF99.
     */
    FALLING_PLATFORM(new Color(0x00FF99), "textures/falling_platform.png"),
    ;

    private final Color color;
    private final int rgb;
    private BufferedImage texture;

    /**
     * Constructs an ETile enum constant with a specified color and an optional texture path.
     * The texture is loaded and cached using {@link ResourceManager#getTexture(String)}.
     *
     * @param color The {@link Color} associated with this tile.
     * @param path The file path to the tile's texture image, or null if no texture.
     */
    ETile(Color color, String path) {
        this.rgb = color.getRGB();
        this.color = color;
        if (path != null) try {
            this.texture = ResourceManager.getTexture(path);
        } catch (IOException _) {
        }
    }

    /**
     * Returns the {@link Color} associated with this tile.
     *
     * @return The {@link Color} of the tile.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the {@link BufferedImage} texture for this tile.
     *
     * @return The texture of the tile, or null if no texture is assigned.
     */
    public BufferedImage getTexture() {
        return texture;
    }

    /**
     * Returns the RGB integer value of this tile's color.
     *
     * @return The RGB integer value.
     */
    public int getRGB() {
        return rgb;
    }
}