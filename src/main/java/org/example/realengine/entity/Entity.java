package org.example.realengine.entity;

import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Map;

import static org.example.realengine.game.GameConstants.GRAVITY;
import static org.example.realengine.game.GameConstants.TILE_SIZE;

/**
 * Base class for all game objects (characters, items, etc.) that have a position, velocity, dimensions,
 * and can interact with the world. Provides basic physical behavior such as gravity, collision detection with the map,
 * and basic support for platforms and ladders.
 */
public sealed abstract class Entity permits Enemy, Lakitu, Player {
    /**
     * The type of the entity, e.g., "player", "enemy", "item".
     */
    protected final String type;
    /**
     * A map storing textures for different directions or states of the entity.
     * The key represents a direction (e.g., 1 for right, -1 for left), and the value is an array of BufferedImages
     * for animation frames.
     */
    protected Map<Integer, BufferedImage[]> texturesFromDirection;
    /**
     * Flag indicating if the entity was walking in the previous frame, used for animation control.
     */
    protected boolean wasWalking = true;
    /**
     * Counter for animation frames, used to control animation speed.
     */
    protected int animationCounter = 0;
    /**
     * Delay between animation frames, determining the speed of animation.
     */
    protected int animationDelay;
    /**
     * The X-coordinate of the entity's position in the game world.
     */
    protected float x;
    /**
     * The Y-coordinate of the entity's position in the game world.
     */
    protected float y;
    /**
     * The width of the entity's bounding box.
     */
    protected int width;
    /**
     * The height of the entity's bounding box.
     */
    protected int height;
    /**
     * Flag indicating if the entity is currently on the ground.
     */
    protected boolean isOnGround = false;
    /**
     * Flag indicating if the entity is currently on a ladder.
     */
    protected boolean isOnLadder = false;
    /**
     * The current health of the entity.
     */
    protected float health = 1;
    /**
     * The maximum health of the entity.
     */
    protected float maxHealth = 5;
    /**
     * Flag indicating if the entity is currently moving left.
     */
    protected boolean movingLeft = false;
    /**
     * Flag indicating if the entity is currently moving right.
     */
    protected boolean movingRight = false;
    /**
     * Flag indicating if the entity is currently moving up (e.g., on a ladder).
     */
    protected boolean movingUp = false;
    /**
     * Flag indicating if the entity is currently moving down (e.g., on a ladder).
     */
    protected boolean isMovingDown = false;
    /**
     * Flag indicating if the entity is currently jumping.
     */
    protected boolean jumping = false;
    /**
     * The gravitational force applied to the entity.
     */
    protected float gravity = GRAVITY;
    /**
     * The current horizontal velocity of the entity.
     */
    protected float velocityX = 0;
    /**
     * The current vertical velocity of the entity.
     */
    protected float velocityY = 0;
    /**
     * The initial velocity applied when the entity jumps.
     */
    protected float jumpVelocity = -900.0f;
    /**
     * The speed at which the entity moves automatically (e.g., for enemies).
     */
    protected float autoMoveSpeed = 400.0f;
    /**
     * Flag indicating if the entity is dead.
     */
    protected boolean isDead = false;
    /**
     * The current direction the entity is facing or moving (-1 for left, 1 for right).
     */
    protected int direction = -1;
    /**
     * Internal game time counter for animations or other time-based effects.
     */
    protected float gameTime = 0f;

    /**
     * Creates a new entity at the specified coordinates with given dimensions, type, and animation delay.
     *
     * @param x              Initial X-coordinate.
     * @param y              Initial Y-coordinate.
     * @param width          The width of the entity.
     * @param height         The height of the entity.
     * @param type           The type identifier of the entity.
     * @param animationDelay The delay between animation frames.
     */
    public Entity(float x, float y, int width, int height, String type, int animationDelay) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.animationDelay = animationDelay;
    }

    /**
     * Sets the direction of the entity.
     *
     * @param direction The new direction (-1 for left, 1 for right).
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Toggles the `wasWalking` flag.
     */
    public void setWasWalking() {
        this.wasWalking = !wasWalking;
    }

    /**
     * Updates the entity's state in a single game frame.
     * This includes checking ladders, calculating movement, applying gravity, and resolving map collisions.
     * This method should be called in the main game loop.
     *
     * @param deltaTime Time elapsed since the last frame (in seconds or other units).
     * @param map       The game map for collision detection.
     */
    public abstract void update(float deltaTime, RMap map);

    /**
     * Updates the animation counter and toggles the `wasWalking` flag based on `animationDelay`.
     */
    protected void updateAnimation() {
        animationCounter++;
        if (animationCounter >= animationDelay) {
            animationCounter = 0;
            setWasWalking();
        }
    }

    /**
     * Retrieves the texture for the entity's current animation state and direction.
     *
     * @param state The animation state.
     * @return The {@link BufferedImage} texture or `null` if not found.
     */
    public BufferedImage getTexture(AnimationState state) {
        BufferedImage[] textures = texturesFromDirection.get(direction);
        if (textures == null || state.getIndex() >= textures.length) {
            textures = texturesFromDirection.get(1);
            if (textures == null || state.getIndex() >= textures.length) {
                return null;
            }
        }
        return textures[state.getIndex()];
    }

    /**
     * Retrieves the current animation state of the entity based on its movement and ground status.
     *
     * @return An {@link AnimationState} corresponding to the current state.
     */
    public AnimationState getCurrentAnimationState() {
        final boolean isMoving = Math.abs(velocityX) > 0.1f;
        return AnimationState.getState(isOnGround, isMoving, gameTime);
    }

    /**
     * Updates the internal game time counter for the entity.
     *
     * @param deltaTime Time elapsed since the last update.
     */
    protected void updateGameTime(float deltaTime) {
        gameTime += deltaTime;
    }

    /**
     * Abstract method to be implemented by subclasses, defining behavior when the entity dies.
     */
    public abstract void onDead();

    /**
     * Checks if the entity is dead.
     *
     * @return `true` if the entity is dead, `false` otherwise.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Sets whether the entity should move left.
     *
     * @param moving `true` for moving left.
     */
    public void setMovingLeft(boolean moving) {
        this.movingLeft = moving;
        if (moving) velocityX = -autoMoveSpeed;
        else if (velocityX < 0) velocityX = 0;
    }

    /**
     * Sets whether the entity should move right.
     *
     * @param moving `true` for moving right.
     */
    public void setMovingRight(boolean moving) {
        this.movingRight = moving;
        if (moving) velocityX = autoMoveSpeed;
        else if (velocityX > 0) velocityX = 0;
    }

    /**
     * Sets whether the entity should move up.
     *
     * @param moving `true` for moving up.
     */
    public void setMovingUp(boolean moving) {
        this.movingUp = moving;
    }

    /**
     * Sets whether the entity should move down. If `movingDown` is true, it also checks if the entity is on a ladder.
     *
     * @param movingDown `true` for moving down.
     */
    public void setMovingDown(boolean movingDown) {
        if (!movingDown) {
            this.isMovingDown = false;
            return;
        }
        this.isMovingDown = this.isOnLadder;
    }

    /**
     * Sets the horizontal velocity of the entity.
     *
     * @param velocityX The new horizontal velocity.
     */
    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    /**
     * Sets the vertical velocity of the entity.
     *
     * @param velocityY The new vertical velocity.
     */
    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Abstract method to handle interactions with special tiles in the collision map.
     * To be implemented by subclasses.
     *
     * @param collisionMap The 2D array representing the collision map of the game world.
     */
    abstract void handleSpecialTiles(EObject[][] collisionMap);

    /**
     * Handles horizontal collision detection and resolution for the entity.
     * If a collision is detected, the entity's position and horizontal velocity are adjusted.
     *
     * @param collisionMap   The 2D array representing the collision map.
     * @param potentialNextX The entity's potential next X-coordinate after movement.
     * @return `true` if a horizontal collision was detected and resolved, `false` otherwise.
     */
    boolean handleXCollision(EObject[][] collisionMap, float potentialNextX) {
        boolean collisionDetectedX = false;
        if (velocityX != 0) {
            final var topTileY = (int) (y / TILE_SIZE);
            final var bottomTileY = (int) ((y + height - 1) / TILE_SIZE);
            int nextTileX;
            if (velocityX > 0) {
                nextTileX = (int) ((potentialNextX + width - 1) / TILE_SIZE);
                for (int tileY = topTileY; tileY <= bottomTileY; tileY++) {
                    if (collisionMap != null &&
                            nextTileX >= 0 && nextTileX < collisionMap.length &&
                            tileY >= 0 && tileY < collisionMap[0].length &&
                            collisionMap[nextTileX][tileY] != null &&
                            collisionMap[nextTileX][tileY].isSolid()) {
                        collisionDetectedX = true;
                        x = nextTileX * TILE_SIZE - width;
                        velocityX = 0;
                        break;
                    }
                }
            } else {
                nextTileX = (int) (potentialNextX / TILE_SIZE);
                for (int tileY = topTileY; tileY <= bottomTileY; tileY++) {
                    if (collisionMap != null &&
                            nextTileX >= 0 && nextTileX < collisionMap.length &&
                            tileY >= 0 && tileY < collisionMap[0].length &&
                            collisionMap[nextTileX][tileY] != null &&
                            collisionMap[nextTileX][tileY].isSolid()) {
                        collisionDetectedX = true;
                        x = (nextTileX + 1) * TILE_SIZE;
                        velocityX = 0;
                        break;
                    }
                }
            }
        }
        return collisionDetectedX;
    }

    /**
     * Handles vertical collision detection and resolution for the entity.
     * This method checks for collisions with solid tiles above and below the entity, and handles special tiles like hazard liquid or springs.
     *
     * @param collisionMap   The 2D array representing the collision map.
     * @param potentialNextY The entity's potential next Y-coordinate after movement.
     * @return `true` if a vertical collision was detected and resolved, `false` otherwise.
     */
    boolean handleYCollision(@NotNull EObject[][] collisionMap, float potentialNextY) {
        boolean collisionDetectedY = false;
        isOnGround = false;
        if (velocityY < 0) {
            final var leftHeadTileX = (int) (x / TILE_SIZE);
            final var rightHeadTileX = (int) ((x + width - 1) / TILE_SIZE);
            final var topTileY = (int) (potentialNextY / TILE_SIZE);

            for (int tileX = leftHeadTileX; tileX <= rightHeadTileX; tileX++) {
                if (tileX >= 0 && tileX < collisionMap.length &&
                        topTileY >= 0 && topTileY < collisionMap[0].length &&
                        collisionMap[tileX][topTileY] != null &&
                        collisionMap[tileX][topTileY].isSolid()) {
                    collisionDetectedY = true;
                    velocityY = 0;
                    y = (topTileY + 1) * TILE_SIZE;
                    break;
                }
            }
        }
        if (velocityY >= 0) {
            final var leftFootTileX = (int) (x / TILE_SIZE);
            final var rightFootTileX = (int) ((x + width - 1) / TILE_SIZE);
            final var bottomTileY = (int) ((potentialNextY + height) / TILE_SIZE);

            for (int tileX = leftFootTileX; tileX <= rightFootTileX; tileX++) {
                if (tileX >= 0 && tileX < collisionMap.length &&
                        bottomTileY >= 0 && bottomTileY < collisionMap[0].length &&
                        collisionMap[tileX][bottomTileY] != null) {
                    if (collisionMap[tileX][bottomTileY] == EObject.HAZARD_LIQUID) {
                        onDead();
                        return true;
                    } else if (collisionMap[tileX][bottomTileY].isSolid() && !isMovingDown) {
                        if (collisionMap[tileX][bottomTileY] == EObject.SPRING) {
                            velocityY = jumpVelocity * 1.25f;
                            collisionDetectedY = true;
                        } else {
                            collisionDetectedY = true;
                            isOnGround = true;
                            velocityY = 0;
                            y = bottomTileY * TILE_SIZE - height;
                        }
                        break;
                    }
                }
            }
        }
        return collisionDetectedY;
    }

    /**
     * Initiates a jump for the entity if it is on the ground or on a ladder.
     * Sets the `jumping` flag, which is processed in `calculateMovement`.
     */
    public void jump() {
        if (isOnGround || isOnLadder) {
            velocityY = jumpVelocity;
            isOnGround = false;
            jumping = false;
        }
    }

    /**
     * Returns the current X-coordinate of the entity.
     *
     * @return The current X-coordinate.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the X-coordinate of the entity.
     *
     * @param x The new X-coordinate.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Returns the current Y-coordinate of the entity.
     *
     * @return The current Y-coordinate.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the Y-coordinate of the entity.
     *
     * @param y The new Y-coordinate.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Returns the width of the entity (for collisions and rendering).
     *
     * @return The width of the entity.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the entity.
     *
     * @param width The new width.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Returns the height of the entity (for collisions and rendering).
     *
     * @return The height of the entity.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the entity.
     *
     * @param height The new height.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Checks if the entity is currently on the ground.
     *
     * @return `true` if the entity is on the ground, `false` otherwise.
     */
    public boolean isOnGround() {
        return isOnGround;
    }

    /**
     * Sets the health of the entity.
     *
     * @param health The new health value.
     */
    public void setHealth(float health) {
        this.health = health;
    }

    /**
     * Returns the maximum health of the entity.
     *
     * @return The maximum health.
     */
    public float getMaxHealth() {
        return maxHealth;
    }

    /**
     * Returns the type of the entity.
     *
     * @return The entity type string.
     */
    public String getType() {
        return type;
    }
}