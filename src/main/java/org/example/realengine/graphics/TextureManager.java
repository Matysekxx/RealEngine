package org.example.realengine.graphics;

import org.example.realengine.game.GameConstants;
import org.example.realengine.map.ETile;
import org.example.realengine.object.EObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Spravuje načítání, ukládání a poskytování textur {@link Texture}.
 * Udržuje mapu textur podle jejich ID a umožňuje mapovat typy dlaždic {@link ETile}
 * a objektů {@link EObject} na specifické textury.
 */
public final class TextureManager {
    private static final String DEFAULT_TEXTURE_ID = "default";
    private static final int TILE_SIZE = GameConstants.TILE_SIZE;
    private final Map<String, Texture> textureMap = new HashMap<>();
    private final Map<ETile, String> tileTextureMap = new HashMap<>();
    private final Map<EObject, String> objectTextureMap = new HashMap<>();
    private final String basePath;

    public TextureManager(String basePath) {
        this.basePath = basePath;
        addTexture(new Texture("wall", Texture.createColorTexture("wall", Color.BLACK, TILE_SIZE, TILE_SIZE).getImage()));
        addTexture(new Texture("lava", Texture.createColorTexture("lava", new Color(0xFF9900), TILE_SIZE, TILE_SIZE).getImage()));
        addTexture(new Texture("spawn", Texture.createColorTexture("spawn", Color.YELLOW, TILE_SIZE, TILE_SIZE).getImage()));
        addTexture(new Texture("empty", Texture.createColorTexture("empty", Color.WHITE, TILE_SIZE, TILE_SIZE).getImage()));
        addTexture(new Texture("sky", Texture.createColorTexture("sky", new Color(0x87CEEB), TILE_SIZE, TILE_SIZE).getImage()));
        addTexture(new Texture("ladder", Texture.createColorTexture("ladder", new Color(0xB8860B), TILE_SIZE, TILE_SIZE).getImage()));
        addTexture(new Texture("trap", Texture.createColorTexture("trap", new Color(0x8B0000), TILE_SIZE, TILE_SIZE).getImage()));
        addTexture(new Texture("spikes", Texture.createColorTexture("spikes", Color.GRAY, TILE_SIZE, TILE_SIZE).getImage()));
        addTexture(new Texture("spring",Texture.createColorTexture("spring", new Color(0xFF0000), TILE_SIZE, TILE_SIZE).getImage()));
        mapTileToTexture(ETile.LADDER, "ladder");
        mapTileToTexture(ETile.TRAP, "trap");
        mapTileToTexture(ETile.SPIKES, "spikes");
        mapObjectToTexture(EObject.LADDER, "ladder");
        mapObjectToTexture(EObject.TRAP, "trap");
        mapObjectToTexture(EObject.SPIKES, "spikes");
        mapTileToTexture(ETile.WALL, "wall");
        mapTileToTexture(ETile.LAVA, "lava");
        mapTileToTexture(ETile.PLAYER_SPAWN, "spawn");
        mapTileToTexture(ETile.EMPTY, "empty");
        mapTileToTexture(ETile.SKY, "sky");
        mapTileToTexture(ETile.SPRING, "spring");
        mapTileToTexture(ETile.UNKNOWN, "sky");

        System.out.println("TextureManager initialized. Base path: '" + this.basePath + "'");
    }

    public void addTexture(Texture texture) {
        if (texture == null) {
            throw new NullPointerException("Cannot add a null texture to TextureManager.");
        }
        textureMap.put(texture.getId(), texture);
    }

    public void mapTileToTexture(ETile tile, String textureId) {
        if (tile == null || textureId == null) {
            System.err.println("WARN: Attempted to map null tile or texture ID.");
            return;
        }
        if (!textureMap.containsKey(textureId)) {
            System.err.println("WARN: Cannot map tile " + tile.name() + " to non-existent texture ID: " + textureId + ". Using default mapping if available.");
            tileTextureMap.put(tile, DEFAULT_TEXTURE_ID);
        } else {
            tileTextureMap.put(tile, textureId);
        }
    }

    public void mapObjectToTexture(EObject object, String textureId) {
        if (object == null || textureId == null) {
            System.err.println("WARN: Attempted to map null object or texture ID.");
            return;
        }
        if (!textureMap.containsKey(textureId)) {
            System.err.println("WARN: Cannot map object " + object.name() + " to non-existent texture ID: " + textureId + ". Using default mapping if available.");
            objectTextureMap.put(object, DEFAULT_TEXTURE_ID);
        } else {
            objectTextureMap.put(object, textureId);
        }
    }

    public BufferedImage getTextureForTile(ETile tile) {
        String textureId = tileTextureMap.getOrDefault(tile, DEFAULT_TEXTURE_ID);
        return getTexture(textureId).getImage();
    }

    public BufferedImage getTextureForObject(EObject object) {
        String textureId = objectTextureMap.getOrDefault(object, DEFAULT_TEXTURE_ID);
        return getTexture(textureId).getImage();
    }

    public Texture getTexture(String id) {
        if (id == null) {
            System.err.println("WARN: Requested texture with null ID. Returning default texture.");
            return textureMap.get(DEFAULT_TEXTURE_ID);
        }
        return textureMap.getOrDefault(id, textureMap.get(DEFAULT_TEXTURE_ID));
    }

    public Texture loadTexture(String textureId, String relativePath) {
        if (textureId == null || textureId.isEmpty() || relativePath == null || relativePath.isEmpty()) {
            System.err.println("WARN: Attempted to load texture with null/empty ID or path.");
            return getTexture(DEFAULT_TEXTURE_ID);
        }


        String resourcePath = (basePath.isEmpty() ? "" : basePath + "/") + relativePath;
        System.out.println("Attempting to load texture from resources: " + resourcePath);
        Texture texture = new Texture(textureId, resourcePath);
        if (texture.getImage() == Texture.getDefaultTextureImage()) {
            System.out.println("Resource loading failed, trying filesystem path");
            String fullPath = System.getProperty("user.dir") + "/src/main/resources/" + resourcePath;
            try {
                BufferedImage img = ImageIO.read(new File(fullPath));
                if (img != null) {
                    texture = new Texture(textureId, img);
                    System.out.println("Successfully loaded texture from filesystem: " + fullPath);
                }
            } catch (IOException e) {
                System.err.println("Failed to load texture from filesystem: " + fullPath);
            }
        }

        addTexture(texture);
        return texture;
    }

    public void autoLoadTileTextures() {
        System.out.println("=== Starting texture auto-load ===");
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        System.out.println("Resource base path: '" + basePath + "'");

        int loadedCount = 0;
        int fallbackCount = 0;

        for (ETile tile : ETile.values()) {
            if (tile == ETile.UNKNOWN) continue;

            String textureId = tile.name().toLowerCase();
            String filename = textureId + ".png";

            Texture loadedTexture = loadTexture(textureId, "textures/" + filename);

            if (loadedTexture.getImage() != Texture.getDefaultTextureImage()) {
                mapTileToTexture(tile, textureId);
                loadedCount++;
            } else {
                System.err.println("WARN: Auto-load failed for tile: " + filename + ". Creating color fallback.");
                String fallbackId = textureId + "_fallback_color";

                if (!textureMap.containsKey(fallbackId)) {
                    Color color = tile.getColor();
                    Texture fallbackTexture = Texture.createColorTexture(fallbackId, color, TILE_SIZE, TILE_SIZE);
                    addTexture(fallbackTexture);
                }
                mapTileToTexture(tile, fallbackId);
                fallbackCount++;
            }
        }

        System.out.println("=== Texture loading results ===");
        System.out.println("Successfully loaded: " + loadedCount);
        System.out.println("Fallback textures: " + fallbackCount);
    }

    public void autoLoadObjectTextures(String subDirectory) {
        String objectTexturePath = basePath.isEmpty() ? "" : (basePath.endsWith("/") ? basePath : basePath + "/");
        if (subDirectory != null && !subDirectory.isEmpty()) {
            objectTexturePath += subDirectory.endsWith("/") ? subDirectory : subDirectory + "/";
        }

        System.out.println("Attempting to auto-load object textures from path: " + objectTexturePath);
        int loadedCount = 0;

        for (EObject object : EObject.values()) {
            String textureId = object.name().toLowerCase();
            String filename = textureId + ".png";
            String fullPath = objectTexturePath + filename;

            Texture texture = new Texture(textureId, fullPath);
            addTexture(texture);

            if (texture.getImage() != Texture.getDefaultTextureImage()) {
                mapObjectToTexture(object, textureId);
                loadedCount++;
            } else {
                if (textureMap.containsKey(DEFAULT_TEXTURE_ID)) {
                    mapObjectToTexture(object, DEFAULT_TEXTURE_ID);
                }
                System.err.println("WARN: Auto-load failed for object texture: " + fullPath + ". Using default.");
            }
        }
        System.out.println("Auto-loaded " + loadedCount + " object textures successfully.");
    }
}