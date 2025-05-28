package org.example.realengine.graphics;

import org.example.realengine.entity.Entity;

/**
 * Represents the game camera, responsible for controlling the viewable area of the game world.
 * It can follow a target entity and ensures the view stays within the world boundaries.
 */
public class Camera {
    /**
     * The width of the screen or viewport in pixels.
     */
    private int screenWidth;
    /**
     * The height of the screen or viewport in pixels.
     */
    private int screenHeight;
    /**
     * The total width of the game world in pixels.
     */
    private int worldWidth;
    /**
     * The total height of the game world in pixels.
     */
    private int worldHeight;
    /**
     * The current X-coordinate of the top-left corner of the camera's view in world coordinates.
     */
    private float x;
    /**
     * The current Y-coordinate of the top-left corner of the camera's view in world coordinates.
     */
    private float y;
    /**
     * The entity that the camera is currently following. If null, the camera does not follow any entity.
     */
    private Entity target;
    /**
     * The horizontal offset from the target's center to the camera's center. Used for fine-tuning target positioning.
     */
    private float followOffsetX = 0;
    /**
     * The vertical offset from the target's center to the camera's center. Used for fine-tuning target positioning.
     */
    private float followOffsetY = 0;

    /**
     * Constructs a new Camera instance.
     *
     * @param screenWidth  The initial width of the screen/viewport.
     * @param screenHeight The initial height of the screen/viewport.
     * @param worldWidth   The total width of the game world.
     * @param worldHeight  The total height of the game world.
     */
    public Camera(int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.x = 0;
        this.y = 0;
    }

    /**
     * Updates the screen dimensions that the camera is rendering to.
     *
     * @param screenWidth  The new width of the screen.
     * @param screenHeight The new height of the screen.
     */
    public void updateScreenDimensions(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
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
     * Sets the desired horizontal offset from the camera's center to the target's center.
     * A negative value keeps the target to the left, positive to the right.
     *
     * @param offsetX The desired offset in pixels.
     */
    public void setFollowOffsetX(float offsetX) {
        this.followOffsetX = offsetX;
    }

    /**
     * Sets the desired vertical offset from the camera's center to the target's center.
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

    /**
     * Returns the current X-coordinate of the camera's top-left corner in world coordinates.
     *
     * @return The X-coordinate of the camera.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the current Y-coordinate of the camera's top-left corner in world coordinates.
     *
     * @return The Y-coordinate of the camera.
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the width of the screen or viewport that the camera is rendering to.
     *
     * @return The screen width.
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Returns the height of the screen or viewport that the camera is rendering to.
     *
     * @return The screen height.
     */
    public int getScreenHeight() {
        return screenHeight;
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
        System.out.println("Camera world dimensions updated to: " + newWorldWidth + "x" + newWorldHeight);
    }

    /**
     * Clamps the camera's position within the bounds of the game world.
     * This prevents the camera from showing areas outside the defined world dimensions.
     */
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