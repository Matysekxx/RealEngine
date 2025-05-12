package org.example.realengine.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum EBackground {
    GRASS_LAND("resources/background/grassland.png"),
    CAVE("resources/background/cave.png"),
    DEFAULT("resources/background/default.png");

    public final static Map<String, EBackground> backgrounds = new HashMap<>(Map.of(
            "resources\\maps\\map_1.png", EBackground.GRASS_LAND,
            "resources\\maps\\map_2.png", EBackground.GRASS_LAND,
            "resources\\maps\\map_3.png", EBackground.CAVE,
            "resources\\maps\\map_4.png", EBackground.CAVE
    ));

    private final BufferedImage background;

    EBackground(String background) {
        try {
            this.background = ImageIO.read(new File(background));
        } catch (IOException _) {
            throw new RuntimeException("Error loading background: " + background);
        }
    }

    public BufferedImage getBackground() {
        return background;
    }
}
