package org.example.realengine.resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages game resources, specifically textures, by providing a caching mechanism.
 * This class ensures that textures are loaded only once and reused across the application
 * to optimize performance and memory usage.
 */
public class ResourceManager {
    /**
     * A cache to store loaded {@link BufferedImage} textures, mapped by their file paths.
     * This prevents redundant loading of the same texture from disk.
     */
    private static final Map<String, BufferedImage> textureCache = new HashMap<>();

    /**
     * Retrieves a texture ({@link BufferedImage}) from the specified file path.
     * If the texture has already been loaded, it is returned from the cache.
     * Otherwise, it is loaded from the file system, stored in the cache, and then returned.
     *
     * @param path The absolute or relative path to the texture file.
     * @return The loaded {@link BufferedImage} representing the texture.
     * @throws IOException If an I/O error occurs during the loading of the image.
     */
    public static BufferedImage getTexture(String path) throws IOException {
        if (textureCache.containsKey(path)) {
            return textureCache.get(path);
        }
        BufferedImage img = ImageIO.read(new File(path));
        textureCache.put(path, img);
        return img;
    }
}