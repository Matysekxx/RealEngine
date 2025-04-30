package org.example.realengine.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public enum EBackground {
    GRASS_LAND("resources/grassland.png"),
    CAVE("resources/cave.png"),
    DEFAULT("resources/default.png");


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
