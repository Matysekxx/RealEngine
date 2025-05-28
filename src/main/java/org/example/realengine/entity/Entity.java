package org.example.realengine.entity;

import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.lang.constant.Constable;
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
     * The horizontal movement speed of the entity.
     */
    protected float moveSpeed = 3.0f;
    /**
     * The vertical climbing speed of the entity on ladders.
     */
    protected float climbSpeed = 2.0f;
    /**
     * The initial upward velocity applied during a jump.
     */
    protected float jumpStrength = -10.0f;
    /**
     * The gravitational force applied to the entity.
     */
    protected float gravity = GRAVITY;
    /**
     * The maximum downward velocity the entity can reach due to gravity.
     */
    protected float maxFallSpeed = 15.0f;
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
     * Returns the current direction of the entity.
     *
     * @return The direction (-1 for left, 1 for right).
     */
    public int getDirection() {
        return direction;
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
     * Checks if the entity was in a walking state.
     *
     * @return `true` if the entity was walking, `false` otherwise.
     */
    public boolean isWasWalking() {
        return wasWalking;
    }

    /**
     * Sets whether the entity was in a walking state.
     *
     * @param wasWalking `true` to indicate walking, `false` otherwise.
     */
    public void setWasWalking(boolean wasWalking) {
        this.wasWalking = wasWalking;
    }

    /**
     * Toggles the `wasWalking` flag.
     */
    public void setWasWalking() {
        this.wasWalking = !wasWalking;
    }

    /**
     * Checks if the entity was in a walking state.
     *
     * @return `true` if the entity was walking, `false` otherwise.
     */
    public boolean wasWalking() {
        return wasWalking;
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
     * Returns the map of textures for different directions.
     *
     * @return A map where keys are directions (e.g., -1, 1) and values are arrays of {@link BufferedImage} for animation.
     */
    public Map<Integer, BufferedImage[]> getTexturesFromDirection() {
        return texturesFromDirection;
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
            textures = texturesFromDirection.get(1); // Fallback to default direction (e.g., right)
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
     * Sets the dead status of the entity.
     *
     * @param dead `true` to mark the entity as dead, `false` otherwise.
     */
    public void setDead(boolean dead) {
        isDead = dead;
    }

    /**
     * Checks if the entity is currently attempting to move left.
     *
     * @return `true` if moving left, `false` otherwise.
     */
    public boolean isMovingLeft() {
        return movingLeft;
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
     * Checks if the entity is currently attempting to move right.
     *
     * @return `true` if moving right, `false` otherwise.
     */
    public boolean isMovingRight() {
        return movingRight;
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
     * Returns the current animation frame counter.
     *
     * @return The animation counter.
     */
    public int getAnimationCounter() {
        return animationCounter;
    }

    /**
     * Sets the animation frame counter.
     *
     * @param animationCounter The new animation counter value.
     */
    public void setAnimationCounter(int animationCounter) {
        this.animationCounter = animationCounter;
    }

    /**
     * Returns the delay between animation frames.
     *
     * @return The animation delay.
     */
    public int getAnimationDelay() {
        return animationDelay;
    }

    /**
     * Sets the delay between animation frames.
     *
     * @param animationDelay The new animation delay value.
     */
    public void setAnimationDelay(int animationDelay) {
        this.animationDelay = animationDelay;
    }

    /**
     * Checks if the entity is currently attempting to move up.
     *
     * @return `true` if moving up, `false` otherwise.
     */
    public boolean isMovingUp() {
        return movingUp;
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
     * Checks if the entity is currently attempting to move down.
     *
     * @return `true` if moving down, `false` otherwise.
     */
    public boolean isMovingDown() {
        return isMovingDown;
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
     * Checks if the entity is currently in a jumping state.
     *
     * @return `true` if jumping, `false` otherwise.
     */
    public boolean isJumping() {
        return jumping;
    }

    /**
     * Sets whether the entity is in a jumping state.
     *
     * @param jumping `true` for jumping.
     */
    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    /**
     * Returns the current horizontal velocity of the entity.
     *
     * @return The horizontal velocity.
     */
    public float getVelocityX() {
        return velocityX;
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
     * Returns the current vertical velocity of the entity.
     *
     * @return The vertical velocity.
     */
    public float getVelocityY() {
        return velocityY;
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
     * Returns the initial velocity applied during a jump.
     *
     * @return The jump velocity.
     */
    public float getJumpVelocity() {
        return jumpVelocity;
    }

    /**
     * Sets the initial velocity applied during a jump.
     *
     * @param jumpVelocity The new jump velocity.
     */
    public void setJumpVelocity(float jumpVelocity) {
        this.jumpVelocity = jumpVelocity;
    }

    /**
     * Returns the automatic movement speed of the entity.
     *
     * @return The auto move speed.
     */
    public float getAutoMoveSpeed() {
        return autoMoveSpeed;
    }

    /**
     * Sets the automatic movement speed of the entity.
     *
     * @param autoMoveSpeed The new auto move speed.
     */
    public void setAutoMoveSpeed(float autoMoveSpeed) {
        this.autoMoveSpeed = autoMoveSpeed;
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
                    y = (topTileY + 1) * TILE_SIZE; // Adjust position to be below the collided tile
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
                        onDead(); // Entity dies if it hits hazard liquid
                        return true;
                    } else if (collisionMap[tileX][bottomTileY].isSolid() && !isMovingDown) {
                        if (collisionMap[tileX][bottomTileY] == EObject.SPRING) {
                            velocityY = jumpVelocity * 1.25f; // Apply extra jump force for springs
                            collisionDetectedY = true;
                        } else {
                            collisionDetectedY = true;
                            isOnGround = true;
                            velocityY = 0;
                            y = bottomTileY * TILE_SIZE - height; // Adjust position to be on top of the collided tile
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
     * Sets whether the entity is on the ground.
     *
     * @param onGround `true` if the entity is on the ground, `false` otherwise.
     */
    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }

    /**
     * Checks if the entity is currently on a ladder.
     *
     * @return `true` if the entity is on a ladder, `false` otherwise.
     */
    public boolean isOnLadder() {
        return isOnLadder;
    }

    /**
     * Sets whether the entity is on a ladder.
     *
     * @param onLadder `true` if the entity is on a ladder, `false` otherwise.
     */
    public void setOnLadder(boolean onLadder) {
        isOnLadder = onLadder;
    }

    /**
     * Returns the current health of the entity.
     *
     * @return The current health.
     */
    public float getHealth() {
        return health;
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
     * Sets the maximum health of the entity.
     *
     * @param maxHealth The new maximum health value.
     */
    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * Returns the movement speed of the entity.
     *
     * @return The movement speed.
     */
    public float getMoveSpeed() {
        return moveSpeed;
    }

    /**
     * Sets the movement speed of the entity.
     *
     * @param moveSpeed The new movement speed.
     */
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * Returns the climbing speed of the entity.
     *
     * @return The climbing speed.
     */
    public float getClimbSpeed() {
        return climbSpeed;
    }

    /**
     * Sets the climbing speed of the entity.
     *
     * @param climbSpeed The new climbing speed.
     */
    public void setClimbSpeed(float climbSpeed) {
        this.climbSpeed = climbSpeed;
    }

    /**
     * Returns the jump strength of the entity.
     *
     * @return The jump strength.
     */
    public float getJumpStrength() {
        return jumpStrength;
    }

    /**
     * Sets the jump strength of the entity.
     *
     * @param jumpStrength The new jump strength.
     */
    public void setJumpStrength(float jumpStrength) {
        this.jumpStrength = jumpStrength;
    }

    /**
     * Returns the gravity applied to the entity.
     *
     * @return The gravity value.
     */
    public float getGravity() {
        return gravity;
    }

    /**
     * Sets the gravity applied to the entity.
     *
     * @param gravity The new gravity value.
     */
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    /**
     * Returns the maximum fall speed of the entity.
     *
     * @return The maximum fall speed.
     */
    public float getMaxFallSpeed() {
        return maxFallSpeed;
    }

    /**
     * Sets the maximum fall speed of the entity.
     *
     * @param maxFallSpeed The new maximum fall speed.
     */
    public void setMaxFallSpeed(float maxFallSpeed) {
        this.maxFallSpeed = maxFallSpeed;
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