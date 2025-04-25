package org.example.realengine.graphics;

import org.example.realengine.entity.Entity;

public class Camera {
    private final int screenWidth, screenHeight;
    private int worldWidth, worldHeight;
    private float x, y;
    private Entity target;
    private float followOffsetX = 0;
    private float followOffsetY = 0;

    public Camera(int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.x = 0;
        this.y = 0;
    }

    /**
     * Sets the entity for the camera to follow.
     *
     * @param target The entity to follow, or null to stop following.
     */
    public void follow(Entity target) {
        this.target = target;
    }

    /**
     * ADDED: Sets the desired horizontal offset from the camera's center to the target's center.
     * A negative value keeps the target to the left, positive to the right.
     *
     * @param offsetX The desired offset in pixels.
     */
    public void setFollowOffsetX(float offsetX) {
        this.followOffsetX = offsetX;
    }

    /**
     * ADDED: Sets the desired vertical offset from the camera's center to the target's center.
     * A negative value keeps the target above, positive below.
     *
     * @param offsetY The desired offset in pixels.
     */
    public void setFollowOffsetY(float offsetY) {
        this.followOffsetY = offsetY;
    }


    /**
     * Updates the camera's position. If following a target, centers the view
     * on the target, adjusted by the offset. Clamps position to world bounds.
     */
    public void update() {
        if (target != null) {
            float targetCenterX = target.getX() + target.getWidth() / 2.0f;
            float targetCenterY = target.getY() + target.getHeight() / 2.0f;
            float desiredX = targetCenterX - (screenWidth / 2.0f) + followOffsetX;
            float desiredY = targetCenterY - (screenHeight / 2.0f) + followOffsetY;
            this.x = desiredX;
            this.y = desiredY;
        }
        clampCamera();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setPosition(float x, float y) {
        this.target = null;
        this.x = Math.max(0, Math.min(x, worldWidth - screenWidth));
        this.y = Math.max(0, Math.min(y, worldHeight - screenHeight));
    }

    public boolean isRectVisible(float objX, float objY, int objWidth, int objHeight) {
        return objX < x + screenWidth && objX + objWidth > x &&
                objY < y + screenHeight && objY + objHeight > y;
    }

    public boolean isEntityVisible(Entity entity) {
        return isRectVisible(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
    }

    /**
     * Updates the camera's knowledge of the world's dimensions.
     * Ensures the camera doesn't scroll beyond the new boundaries.
     *
     * @param newWorldWidth  The new width of the game world in pixels.
     * @param newWorldHeight The new height of the game world in pixels.
     */
    public void setWorldDimensions(int newWorldWidth, int newWorldHeight) {
        this.worldWidth = newWorldWidth;
        this.worldHeight = newWorldHeight;
        clampCamera();
        //System.out.println("Camera world dimensions updated to: " + newWorldWidth + "x" + newWorldHeight);
    }

    private void clampCamera() {
        if (worldWidth > screenWidth) {
            x = Math.max(0, Math.min(x, worldWidth - screenWidth));
        } else {
            x = (worldWidth - screenWidth) / 2.0f;
        }
        if (worldHeight > screenHeight) {
            y = Math.max(0, Math.min(y, worldHeight - screenHeight));
        } else {
            y = (worldHeight - screenHeight) / 2.0f;
        }
    }
}