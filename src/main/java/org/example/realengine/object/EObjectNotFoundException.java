package org.example.realengine.object;

public class EObjectNotFoundException extends RuntimeException {
    public EObjectNotFoundException(String message) {
        super("EObjectNotFoundException: " + message);
    }

    public EObjectNotFoundException() {
        super();
    }
}
