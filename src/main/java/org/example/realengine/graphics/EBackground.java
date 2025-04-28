package org.example.realengine.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public enum EBackground {
    GRASS_LAND("resources/grassland.png"),
    CAVE("resources/cave.png");


    public BufferedImage background;

    EBackground(String background) {
        try {
            this.background = ImageIO.read(new File(background));
        } catch (IOException _) {
            System.err.println("Error loading background: " + background);
        }
    }
}
