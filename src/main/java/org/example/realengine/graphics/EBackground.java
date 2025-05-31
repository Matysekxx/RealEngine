package org.example.realengine.graphics;

import org.example.realengine.resource.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents different background images used in the game.
 * Each enum constant corresponds to a specific background image file.
 */
public enum EBackground {
    /**
     * Represents a grassland background.
     */
    GRASS_LAND("resources/background/grassland.png"),
    /**
     * Represents a cave background.
     */
    CAVE("resources/background/cave.png"),
    /**
     * Represents a default background.
     */
    DEFAULT("resources/background/default.png"),
    /**
     * Represents a lava cave or castle-themed background.
     */
    LAVA_CAVE("resources/background/castle.png"),
    /**
     * Represents a night background.
     */
    NIGHT("resources/background/night.png");

    /**
     * A static map that associates specific map file paths with their corresponding background types.
     * This allows the game to load the correct background based on the map being played.
     */
    public final static Map<String, EBackground> backgrounds = new HashMap<>(Map.of(
            "resources\\maps\\map_1.png", EBackground.GRASS_LAND,
            "resources\\maps\\map_2.png", EBackground.GRASS_LAND,
            "resources\\maps\\map_3.png", EBackground.CAVE,
            "resources\\maps\\map_4.png", EBackground.CAVE,
            "resources\\maps\\map_5.png", EBackground.GRASS_LAND,
            "resources\\maps\\map_6.png", EBackground.LAVA_CAVE,
            "resources\\maps\\map_7.png", EBackground.LAVA_CAVE,
            "resources\\maps\\map_8.png", EBackground.LAVA_CAVE,
            "resources\\maps\\map_9.png", EBackground.LAVA_CAVE,
            "resources\\maps\\map_last.png", EBackground.NIGHT
    ));

    /**
     * The buffered image representing the background texture.
     */
    private final BufferedImage background;

    /**
     * Constructs an EBackground enum constant by loading the specified background image.
     *
     * @param background The file path to the background image.
     * @throws RuntimeException If an error occurs during background image loading.
     */
    EBackground(String background) {
        try {
            this.background = ResourceManager.getTexture(background);
        } catch (IOException _) {
            throw new RuntimeException("Error loading background: " + background);
        }
    }

    /**
     * Returns the buffered image of the background.
     *
     * @return The {@link BufferedImage} of the background.
     */
    public BufferedImage getBackground() {
        return background;
    }
}
