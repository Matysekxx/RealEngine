package org.example.realengine.graphics;

import org.example.realengine.map.ETile;
import org.example.realengine.object.EObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.example.realengine.game.GameConstants.TILE_SIZE;

public final class TextureManager {
    private static final String DEFAULT_TEXTURE_ID = "default";
    private final Map<String, Texture> textureMap = new HashMap<>();
    private final Map<ETile, String> tileTextureMap = new HashMap<>();
    private final Map<EObject, String> objectTextureMap = new HashMap<>();
    private final String resourceBasePath;


    public TextureManager(String resourceBasePath) {
        this.resourceBasePath = resourceBasePath.endsWith("/") ? resourceBasePath : resourceBasePath + "/";
        addTexture(Texture.createColorTexture(DEFAULT_TEXTURE_ID, Color.MAGENTA, TILE_SIZE, TILE_SIZE));
        loadObjectTexturesFromResources();
        setupDefaultMappings();

        
    }

    private void loadObjectTexturesFromResources() {
        System.out.println("=== Starting object texture auto-load from resources: " + resourceBasePath + " ===");
        int loadedCount = 0;
        int failedCount = 0;

        for (EObject object : EObject.values()) {
            String textureId = object.name().toLowerCase();
            String filename = textureId + ".png";
            String filePath = resourceBasePath + filename;
            File textureFile = new File(filePath);

            try {
                if (textureFile.exists() && textureFile.isFile()) {
                    BufferedImage img = ImageIO.read(textureFile);
                    if (img != null) {
                        Texture texture = new Texture(textureId, img);
                        addTexture(texture);
                        mapObjectToTexture(object, textureId);
                        System.out.println("Successfully loaded texture: " + filePath + " for " + object.name());
                        loadedCount++;
                    } else {
                        System.err.println("WARN: Failed to decode image from file: " + filePath + " for " + object.name());
                        mapObjectToTexture(object, DEFAULT_TEXTURE_ID);
                        failedCount++;
                    }
                } else {
                    System.err.println("WARN: Texture file not found: " + filePath + " for " + object.name() + ". Using default.");
                    mapObjectToTexture(object, DEFAULT_TEXTURE_ID);
                    failedCount++;
                }
            } catch (IOException e) {
                System.err.println("ERROR: IOException while loading texture file: " + filePath + " for " + object.name() + " - " + e.getMessage());
                mapObjectToTexture(object, DEFAULT_TEXTURE_ID);
                failedCount++;
            }
        }
        System.out.println("=== Object texture loading finished. Loaded: " + loadedCount + ", Failed/Default: " + failedCount + " ===");
    }

    private void setupDefaultMappings() {
        if (!objectTextureMap.containsKey(EObject.EMPTY)) {
            String emptyTextureId = "empty_fallback";
            if (!textureMap.containsKey(emptyTextureId)) {
                addTexture(Texture.createColorTexture(emptyTextureId, Color.WHITE, TILE_SIZE, TILE_SIZE));
            }
            mapObjectToTexture(EObject.EMPTY, emptyTextureId);
            System.out.println("Mapped EObject.EMPTY to fallback texture: " + emptyTextureId);
        }
        if (!objectTextureMap.containsKey(EObject.WALL)) mapObjectToTexture(EObject.WALL, DEFAULT_TEXTURE_ID);
        if (!objectTextureMap.containsKey(EObject.PLAYER_SPAWN)) mapObjectToTexture(EObject.PLAYER_SPAWN, DEFAULT_TEXTURE_ID);
        mapTileToTexture(ETile.SKY, "sky_fallback");
        if (!textureMap.containsKey("sky_fallback")) {
             addTexture(Texture.createColorTexture("sky_fallback", new Color(0x87CEEB), TILE_SIZE, TILE_SIZE));
        }
    }

    public void addTexture(Texture texture) {
        if (texture != null) {
            textureMap.put(texture.getId(), texture);
        } else {
            System.err.println("WARN: Attempted to add a null texture.");
        }
    }

    public void mapTileToTexture(ETile tile, String textureId) {
        if (tile == null || textureId == null) {
            System.err.println("WARN: Attempted to map null tile or texture ID.");
            return;
        }
        if (!textureMap.containsKey(textureId)) {
            System.err.println("WARN: Cannot map tile " + tile.name() + " to non-existent texture ID: " + textureId + ". Using default mapping.");
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
            System.err.println("WARN: Cannot map object " + object.name() + " to non-existent texture ID: " + textureId + ". Using default mapping.");
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
        if (object == null) {
             System.err.println("WARN: Requested texture for null EObject. Returning default texture.");
             return getTexture(DEFAULT_TEXTURE_ID).getImage();
        }
        String textureId = objectTextureMap.getOrDefault(object, DEFAULT_TEXTURE_ID);
        Texture texture = getTexture(textureId);
        if (texture == null) {
             System.err.println("CRITICAL WARN: Default texture missing! ID: " + DEFAULT_TEXTURE_ID);
             return Texture.createColorTexture("emergency_default", Color.MAGENTA, TILE_SIZE, TILE_SIZE).getImage();
        }
        return texture.getImage();
    }

    public Texture getTexture(String id) {
        if (id == null) {
            System.err.println("WARN: Requested texture with null ID. Returning default texture.");
            return textureMap.get(DEFAULT_TEXTURE_ID);
        }
        Texture texture = textureMap.get(id);
        if (texture == null) {
             System.err.println("WARN: Texture not found for ID: " + id + ". Returning default texture.");
             return textureMap.get(DEFAULT_TEXTURE_ID);
        }
        return texture;
    }
}