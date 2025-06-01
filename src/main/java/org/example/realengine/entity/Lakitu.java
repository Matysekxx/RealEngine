package org.example.realengine.entity;

import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.example.realengine.resource.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import static org.example.realengine.game.GameConstants.TILE_SIZE;

/**
 * Represents the Lakitu enemy entity in the game. Lakitu is an enemy that follows the player
 * and periodically throws other enemies (Spinies) onto the map.
 */
public non-sealed class Lakitu extends Entity {
    /**
     * The player entity that Lakitu targets.
     */
    private Player player;
    /**
     * Cooldown timer for throwing enemies.
     */
    private float throwCooldown = 0;
    /**
     * The interval between throwing enemies.
     */
    private float throwInterval = 4f;
    /**
     * The maximum speed at which Lakitu moves.
     */
    private float maxSpeed = 200f;
    /**
     * Constructs a new Lakitu entity.
     *
     * @param x The initial x-coordinate of Lakitu.
     * @param y The initial y-coordinate of Lakitu.
     */
    public Lakitu(float x, float y, boolean angry) {
        super(x, y, 16, 24, "lakitu", 10);
        if (angry) {
            this.throwInterval /= 2f;
            this.maxSpeed *= 2f;
            try {
                this.texturesFromDirection = Map.of(
                        1, new BufferedImage[]{
                                ResourceManager.getTexture("resources/textures/angry_lakitu.png"),
                                ResourceManager.getTexture("resources/textures/angry_lakitu.png"),
                                ResourceManager.getTexture("resources/textures/angry_lakitu.png"),
                                ResourceManager.getTexture("resources/textures/angry_lakitu.png")
                        },
                        -1, new BufferedImage[]{
                                ResourceManager.getTexture("resources/textures/angry_lakitu-.png"),
                                ResourceManager.getTexture("resources/textures/angry_lakitu-.png"),
                                ResourceManager.getTexture("resources/textures/angry_lakitu-.png"),
                                ResourceManager.getTexture("resources/textures/angry_lakitu-.png")
                        }
                );
            } catch (IOException _) {
                System.err.println("error loading textures");
            }
        } else {
            try {
                this.texturesFromDirection = Map.of(
                        1, new BufferedImage[]{
                                ResourceManager.getTexture("resources/textures/lakitu.png"),
                                ResourceManager.getTexture("resources/textures/lakitu.png"),
                                ResourceManager.getTexture("resources/textures/lakitu.png"),
                                ResourceManager.getTexture("resources/textures/lakitu.png")
                        },
                        -1, new BufferedImage[]{
                                ResourceManager.getTexture("resources/textures/lakitu-.png"),
                                ResourceManager.getTexture("resources/textures/lakitu-.png"),
                                ResourceManager.getTexture("resources/textures/lakitu-.png"),
                                ResourceManager.getTexture("resources/textures/lakitu-.png")
                        }
                );
            } catch (IOException _) {
                System.err.println("error loading textures");
            }
        }
    }

    /**
     * Gets the current target player of Lakitu.
     *
     * @return The Player entity that Lakitu is targeting.
     */
    public Player getTarget() {
        return player;
    }

    /**
     * Sets the target player for Lakitu.
     *
     * @param player The Player entity to set as the target.
     */
    public void setTarget(Player player) {
        this.player = player;
    }

    /**
     * Updates Lakitu's state, including movement towards the player and throwing enemies.
     *
     * @param deltaTime The time elapsed since the last frame.
     * @param map       The current game map.
     */
    @Override
    public void update(float deltaTime, RMap map) {
        if (player == null) return;
        final float desiredDistance = 2 * TILE_SIZE;
        final float catchUpSpeed = maxSpeed * 2;
        if (Math.abs(this.x - player.getX()) <= desiredDistance) {
            this.x = player.getX() + (float) Math.sin(gameTime * 2.0f) * TILE_SIZE * 1.5f;
        } else {
            if (this.x - player.getX() > 0) {
                this.x -= catchUpSpeed * deltaTime;
                direction = -1;
                if (this.x < player.getX() + desiredDistance) {
                    this.x = player.getX() + desiredDistance;
                }
            } else {
                this.x += catchUpSpeed * deltaTime;
                direction = 1;
                if (this.x > player.getX() - desiredDistance) {
                    this.x = player.getX() - desiredDistance;
                }
            }
        }
        throwCooldown -= deltaTime;
        if (throwCooldown < 0) {
            throwEnemy(map);
            throwCooldown = throwInterval;
        }
        updateGameTime(deltaTime);
        updateAnimation();
    }

    /**
     * Throws an enemy (Spiny) onto the map.
     *
     * @param map The current game map to add the enemy to.
     */
    private void throwEnemy(RMap map) {
        map.addEntity(new Enemy(this.x, this.y + TILE_SIZE, false, "enemy"));
    }

    /**
     * Handles Lakitu's death event.
     */
    @Override
    public void onDead() {
        this.isDead = true;
    }

    /**
     * Handles interactions with special tiles. Lakitu does not have specific interactions with special tiles.
     *
     * @param collisionMap The map representing tile collisions.
     */
    @Override
    void handleSpecialTiles(EObject[][] collisionMap) {
    }
}