package org.example.realengine.graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reprezentuje texturu, což je v podstatě obrázek {@link BufferedImage} s unikátním ID.
 * Poskytuje metody pro načítání textur ze souborů nebo vytváření jednoduchých barevných textur.
 * Obsahuje také výchozí "placeholder" texturu pro případy, kdy se načtení nezdaří.
 */
public class Texture {
    /**
     * Výchozí textura použitá, pokud se načtení obrázku nepodaří.
     */
    private static final BufferedImage DEFAULT_TEXTURE;

    static {
        DEFAULT_TEXTURE = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = DEFAULT_TEXTURE.createGraphics();
        try {
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, 16, 16);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, 15, 15);
            g.drawLine(0, 0, 15, 15);
            g.drawLine(0, 15, 15, 0);
        } finally {
            g.dispose();
        }
    }

    /**
     * Unikátní identifikátor textury (např. název souboru bez přípony).
     */
    private final String id;
    /**
     * Obrázek textury.
     */
    private BufferedImage image;

    /**
     * Vytvoří novou texturu načtením obrázku ze souboru v resources.
     * Pokud se načtení nepodaří, použije se výchozí textura.
     *
     * @param id           Unikátní ID pro tuto texturu.
     * @param resourcePath Cesta k souboru obrázku v rámci resources (např. "/textures/player.png").
     */
    public Texture(String id, String resourcePath) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Texture ID cannot be null or empty.");
        }
        String cleanResourcePath = (resourcePath != null && resourcePath.startsWith("/"))
                ? resourcePath.substring(1)
                : resourcePath;

        if (cleanResourcePath == null || cleanResourcePath.isEmpty()) {
            System.err.println("WARN: Texture resource path is null or empty for ID '" + id + "'. Using default texture.");
            this.id = id;
            this.image = DEFAULT_TEXTURE;
            return;
        }
        this.id = id;
        loadFromResource(cleanResourcePath);
    }

    /**
     * Vytvoří novou texturu s již existujícím obrázkem {@link BufferedImage}.
     *
     * @param id    Unikátní ID pro tuto texturu.
     * @param image Obrázek, který má textura používat. Pokud je `null`, použije se výchozí textura.
     */
    public Texture(String id, BufferedImage image) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Texture ID cannot be null or empty.");
        }
        this.id = id;
        this.image = (image != null) ? image : DEFAULT_TEXTURE;
        if (image == null) {
            System.err.println("WARN: Provided BufferedImage is null for texture ID '" + id + "'. Using default texture.");
        }
    }

    /**
     * Vytvoří a vrátí novou texturu vyplněnou jednolitou barvou.
     *
     * @param id     Unikátní ID pro novou texturu.
     * @param color  Barva, kterou má být textura vyplněna.
     * @param width  Šířka textury v pixelech.
     * @param height Výška textury v pixelech.
     * @return Nový objekt {@link Texture}.
     * @throws IllegalArgumentException pokud jsou rozměry nekladné.
     */
    public static Texture createColorTexture(String id, Color color, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Texture dimensions must be positive.");
        }
        if (color == null) {
            color = Color.MAGENTA;
            System.err.println("WARN: Null color provided for createColorTexture with ID '" + id + "'. Using MAGENTA.");
        }

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(color);
            g.fillRect(0, 0, width, height);
        } finally {
            g.dispose();
        }
        return new Texture(id, img);
    }

    /**
     * @return Výchozí "placeholder" textura.
     */
    public static BufferedImage getDefaultTextureImage() {
        return DEFAULT_TEXTURE;
    }

    /**
     * Načte obrázek textury ze souboru v resources pomocí getResourceAsStream.
     * Pokud se načtení nepodaří, nastaví `image` na `DEFAULT_TEXTURE`.
     *
     * @param resourcePath Cesta k souboru v resources (bez úvodního lomítka, např. "textures/grass.png").
     */
    private void loadFromResource(String resourcePath) {
        try (InputStream stream = Texture.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream != null) {
                this.image = ImageIO.read(stream);
                if (this.image == null) {
                    System.err.println("ERR: Failed to decode image resource (format not supported?): " + resourcePath + " for ID '" + id + "'. Using default texture.");
                    this.image = DEFAULT_TEXTURE;
                }
            } else {
                System.err.println("ERR: Texture resource not found (stream is null): " + resourcePath + " for ID '" + id + "'. Using default texture.");
                this.image = DEFAULT_TEXTURE;
            }
        } catch (IOException e) {
            System.err.println("ERR: IOException while loading texture resource: " + resourcePath + " for ID '" + id + "': " + e.getMessage());
            this.image = DEFAULT_TEXTURE;
        } catch (IllegalArgumentException e) {
            System.err.println("ERR: IllegalArgumentException (likely null path): " + resourcePath + " for ID '" + id + "': " + e.getMessage());
            this.image = DEFAULT_TEXTURE;
        }
    }

    /**
     * @return Objekt {@link BufferedImage} reprezentující tuto texturu.
     * Nikdy nevrací `null` (v případě chyby vrací výchozí texturu).
     */
    public BufferedImage getImage() {
        return (image != null) ? image : DEFAULT_TEXTURE;
    }

    /**
     * @return Unikátní ID této textury.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Šířka textury v pixelech.
     */
    public int getWidth() {
        return getImage().getWidth();
    }

    /**
     * @return Výška textury v pixelech.
     */
    public int getHeight() {
        return getImage().getHeight();
    }
}