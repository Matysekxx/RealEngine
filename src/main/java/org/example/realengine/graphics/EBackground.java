package org.example.realengine.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum EBackground {
    DEFAULT("resources/default.png"),
    GRASS_LAND("resources/grassland.png"),
    CAVE("resources/cave.png");


    private BufferedImage background;

    private static Map<String, EBackground> map = new HashMap<>();

    static {
        map.put("maps/map_1.png", GRASS_LAND);
        map.put("resources/defaultmap.png", DEFAULT);
    }

    EBackground(String background) {
        try {
            this.background = ImageIO.read(new File(background));
        } catch (IOException _) {}
    }

    public BufferedImage getBackground() {
        return background;
    }

    public static Map<String, EBackground> getMap() {
        return map;
    }
}
