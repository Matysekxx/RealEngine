package org.example.realengine.graphics;

public class TextureNotFoundException extends RuntimeException {
    public TextureNotFoundException(String message) {
        super("Texture not found: " + message);
    }
}
