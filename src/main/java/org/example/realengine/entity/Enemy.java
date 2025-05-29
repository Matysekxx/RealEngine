package org.example.realengine.entity;

import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.example.realengine.resource.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import static org.example.realengine.demo.GamePanel.WORLD_HEIGHT;
import static org.example.realengine.demo.GamePanel.WORLD_WIDTH;
import static org.example.realengine.game.GameConstants.TILE_SIZE;

/**
 * Represents a generic enemy entity in the game. This class extends {@link Entity}
 * and provides basic enemy behavior such as movement, collision handling, and interaction
 * with specific game objects.
 */
public non-sealed class Enemy extends Entity {
    /**
     * Indicates whether the enemy performs continuous jumping.
     */
    private final boolean infinityJumping;
    /**
     * The base movement speed of the enemy.
     */
    private float baseSpeed = 100f;

    /**
     * Constructs a new Enemy entity.
     *
     * @param x The initial x-coordinate of the enemy.
     * @param y The initial y-coordinate of the enemy.
     * @param infinityJumping If true, the enemy will continuously jump.
     * @param type The type identifier for the enemy.
     */
    public Enemy(float x, float y, boolean infinityJumping, String type) {
        super(x, y, TILE_SIZE, TILE_SIZE, type, 10);
        this.infinityJumping = infinityJumping;
        this.baseSpeed = baseSpeed * 1.5f;

        try {
            this.texturesFromDirection = Map.of(
                    -1, new BufferedImage[]{
                            ResourceManager.getTexture("textures/spiny1.png"),
                            ResourceManager.getTexture("textures/spiny1.png"),
                            ResourceManager.getTexture("textures/spiny1.png"),
                            ResourceManager.getTexture("textures/spiny2.png")
                    },
                    1, new BufferedImage[]{
                            ResourceManager.getTexture("textures/spiny-1.png"),
                            ResourceManager.getTexture("textures/spiny-1.png"),
                            ResourceManager.getTexture("textures/spiny-1.png"),
                            ResourceManager.getTexture("textures/spiny-2.png")
                    }
            );
        } catch (IOException _) {
        }
    }

    /**
     * Updates the enemy's state, including movement, collision detection, and animation.
     * If {@code infinityJumping} is true, the enemy will continuously jump.
     *
     * @param deltaTime The time elapsed since the last frame.
     * @param map The current game map.
     */
    @Override
    public void update(float deltaTime, RMap map) {
        final EObject[][] collisionMap = map.getCollisionMap();
        if (infinityJumping) {
            jump();
        }
        this.velocityX = baseSpeed * direction;
        float potentialNextX = x + this.velocityX * deltaTime;
        velocityY += gravity * deltaTime;

        float potentialNextY = y + velocityY * deltaTime;
        boolean collisionDetectedX = handleXCollision(collisionMap, potentialNextX);

        if (collisionDetectedX) {
            direction *= -1;
        } else {
            this.x = potentialNextX;
        }
        boolean collisionDetectedY = handleYCollision(collisionMap, potentialNextY);
        if (!collisionDetectedY) {
            this.y = potentialNextY;
        }
        handleSpecialTiles(collisionMap);
        this.x = Math.max(0, Math.min(this.x, WORLD_WIDTH - width));
        this.y = Math.max(0, Math.min(this.y, WORLD_HEIGHT - height));
        updateGameTime(deltaTime);
        updateAnimation();
    }

    /**
     * Handles the enemy's death event, typically by setting its {@code isDead} flag to true.
     */
    @Override
    public void onDead() {
        this.isDead = true;
    }

    /**
     * Handles interactions with special tiles such as SLIME, HAZARD_LIQUID, SPIKE, and SPRING.
     *
     * @param collisionMap The map representing tile collisions.
     */
    @Override
    void handleSpecialTiles(EObject[][] collisionMap) {
        final var centerTileX = (int) ((x + (float) width / 2) / TILE_SIZE);
        final var centerTileY = (int) ((y + (float) height / 2) / TILE_SIZE);

        EObject currentObject;
        if (collisionMap != null &&
                centerTileX >= 0 && centerTileX < collisionMap.length &&
                centerTileY >= 0 && centerTileY < collisionMap[0].length) {
            currentObject = collisionMap[centerTileX][centerTileY];
            if (currentObject == EObject.HAZARD_LIQUID || currentObject == EObject.SPIKE) {
                onDead();
            }
            if (currentObject == EObject.SPRING && isOnGround && velocityY == 0) {
                velocityY = jumpVelocity*1.5f;

                isOnGround = false;
            }
        }
        gravity = 1700.0f;
    }
}





