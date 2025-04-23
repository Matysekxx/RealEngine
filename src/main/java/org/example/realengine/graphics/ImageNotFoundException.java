package org.example.realengine.graphics;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String message) {
        super("Image not found: " + message);
    }

    public ImageNotFoundException() {
        super();
    }
}
