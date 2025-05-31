package org.example.realengine.entity;

import org.example.realengine.demo.GamePanel;
import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.example.realengine.resource.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import static org.example.realengine.game.GameConstants.*;

/**
 * Represents the player entity in the game. Handles player-specific logic
 * such as movement, jumping, climbing, teleportation, and interaction with
 * special tiles and objects.
 */
public non-sealed class Player extends Entity {
    /**
     * Reference to the main game panel.
     */
    final GamePanel gamePanel;
    /**
     * The point where the player will respawn after dying.
     */
    private Point spawnPoint;
    /**
     * Flag indicating if the player intends to climb up a ladder.
     */
    private boolean wantsToClimbUp = false;
    /**
     * Flag indicating if the player intends to climb down a ladder.
     */
    private boolean wantsToClimbDown = false;
    /**
     * Tick counter for controlling the rate of box pushing.
     */
    private int boxPushTick = 0;
    /**
     * Cooldown counter for teleportation ability.
     */
    private int teleportCooldown = 0;

    /**
     * Constructs a new Player entity.
     *
     * @param x The initial x-coordinate of the player.
     * @param y The initial y-coordinate of the player.
     * @param gamePanel The game panel instance.
     */
    public Player(float x, float y, GamePanel gamePanel) {
        super(x, y, 16, 16, "player", 3);
        this.gamePanel = gamePanel;
        this.maxHealth = 3;
        this.health = this.maxHealth;
        this.width = 32;
        this.height = 32;

        try {
            texturesFromDirection = Map.of(
                    1, new BufferedImage[]{
                            ResourceManager.getTexture("resources/textures/mario-IDLE.png"),
                            ResourceManager.getTexture("resources/textures/mario-Jump.png"),
                            ResourceManager.getTexture("resources/textures/mario-Walking.png"),
                            ResourceManager.getTexture("resources/textures/mario-IDLE.png")
                    },
                    -1, new BufferedImage[]{
                            ResourceManager.getTexture("resources/textures/marioIDLE.png"),
                            ResourceManager.getTexture("resources/textures/marioJump.png"),
                            ResourceManager.getTexture("resources/textures/marioWalking.png"),
                            ResourceManager.getTexture("resources/textures/marioIDLE.png")
                    }
            );
        } catch (IOException _) {
        }
    }

    /**
     * Sets the spawn point for the player.
     *
     * @param spawnPoint The point to set as the spawn point.
     */
    public void setSpawnPoint(Point spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    /**
     * Initiates a bunny jump if the player is on the ground.
     */
    public void bunnyJump() {
        if (isOnGround) {
            velocityY = jumpVelocity / 1.6f;
            isOnGround = false;
            jumping = false;
        }
    }

    /**
     * Updates the player's state, including movement, collisions, and interactions.
     *
     * @param deltaTime The time elapsed since the last frame.
     * @param map The current game map.
     */
    @Override
    public void update(float deltaTime, RMap map) {
        final EObject[][] collisionMap = map.getCollisionMap();
        if (jumping) {
            velocityY = jumpVelocity;
            isOnGround = false;
            jumping = false;
        }
        final float potentialNextX = x + velocityX * deltaTime;
        velocityY += gravity * deltaTime;
        final float potentialNextY = y + velocityY * deltaTime;
        boxPushTick++;

        if (teleportCooldown > 0) {
            teleportCooldown--;
        }

        boolean collisionDetectedX = handleBoxPush(collisionMap);
        if (!collisionDetectedX) {
            collisionDetectedX = handleXCollision(collisionMap, potentialNextX);
        }
        if (!collisionDetectedX) {
            x = potentialNextX;
        }

        boolean collisionDetectedY = handleYCollision(collisionMap, potentialNextY);
        if (!collisionDetectedY) {
            y = potentialNextY;
        }

        handleSpecialTiles(collisionMap);

        x = Math.max(0, Math.min(x, GamePanel.WORLD_WIDTH - width));
        y = Math.max(0, Math.min(y, GamePanel.WORLD_HEIGHT - height));
        updateGameTime(deltaTime);
        updateAnimation();
    }

    /**
     * Handles the player's death event, typically by moving the player back to the spawn point.
     */
    @Override
    public void onDead() {
        if (spawnPoint != null) {
            this.x = spawnPoint.x;
            this.y = spawnPoint.y;
        } else {
            this.x = 2 * TILE_SIZE;
            this.y = (GamePanel.MAX_WORLD_ROW - 5) * TILE_SIZE;
        }
        velocityX = 0;
        velocityY = 0;
    }

    /**
     * Sets the flag indicating if the player wants to climb up.
     *
     * @param climbingUp True if the player wants to climb up, false otherwise.
     */
    public void setClimbingUp(boolean climbingUp) {
        this.wantsToClimbUp = climbingUp;
    }

    /**
     * Sets the flag indicating if the player wants to climb down.
     *
     * @param climbingDown True if the player wants to climb down, false otherwise.
     */
    public void setClimbingDown(boolean climbingDown) {
        this.wantsToClimbDown = climbingDown;
    }

    /**
     * Handles the logic for pushing boxes.
     *
     * @param collisionMap The map representing tile collisions.
     * @return True if a box collision was detected and handled, false otherwise.
     */
    private boolean handleBoxPush(EObject[][] collisionMap) {
        var collisionDetectedX = false;
        if (velocityX != 0 && boxPushTick >= BOX_PUSH_DELAY && isOnGround) {
            int dir = velocityX > 0 ? 1 : -1;
            final var playerTileX = (int) ((x + (dir > 0 ? width : 0)) / TILE_SIZE);
            final var playerTileY = (int) ((y + (float) height / 2) / TILE_SIZE);
            int nextTileX = playerTileX + dir;
            if (nextTileX >= 0 && nextTileX < collisionMap.length &&
                    playerTileY >= 0 && playerTileY < collisionMap[0].length &&
                    collisionMap[nextTileX][playerTileY] == EObject.BOX) {

                int boxNextX = nextTileX + dir;
                if (boxNextX >= 0 && boxNextX < collisionMap.length &&
                        collisionMap[boxNextX][playerTileY] == EObject.BOX) {
                    velocityX = 0;
                    collisionDetectedX = true;
                    if (dir > 0) {
                        x = nextTileX * TILE_SIZE - width - GAP;
                    } else {
                        x = (nextTileX + 1) * TILE_SIZE + GAP;
                    }
                } else if (boxNextX >= 0 && boxNextX < collisionMap.length &&
                        collisionMap[boxNextX][playerTileY] == EObject.EMPTY) {
                    collisionMap[boxNextX][playerTileY] = EObject.BOX;
                    collisionMap[nextTileX][playerTileY] = EObject.EMPTY;
                    if (dir > 0) x = nextTileX * TILE_SIZE - width;
                    else x = (nextTileX + 1) * TILE_SIZE;
                    velocityX = 0;
                    collisionDetectedX = true;
                } else {
                    velocityX = 0;
                    collisionDetectedX = true;
                    if (dir > 0) {
                        x = nextTileX * TILE_SIZE - width - GAP;
                    } else {
                        x = (nextTileX + 1) * TILE_SIZE + GAP;
                    }
                }
            }
        }
        return collisionDetectedX;
    }

    /**
     * Handles interactions with special tiles like END, LADDER, SLIME, SPIKE, HAZARD_LIQUID, SPRING, TELEPORT, and CHECKPOINT.
     *
     * @param collisionMap The map representing tile collisions.
     */
    @Override
    public void handleSpecialTiles(EObject[][] collisionMap) {
        final var centerTileX = (int) ((x + (float) width / 2) / TILE_SIZE);
        final var centerTileY = (int) ((y + (float) height / 2) / TILE_SIZE);

        boolean isOnLadder = false;
        boolean isOnHoney = false;

        EObject currentObject;
        if (collisionMap != null &&
                centerTileX >= 0 && centerTileX < collisionMap.length &&
                centerTileY >= 0 && centerTileY < collisionMap[0].length) {
            currentObject = collisionMap[centerTileX][centerTileY];
            if (currentObject == EObject.END) {
                gamePanel.endLevel();
            }
            if (currentObject == EObject.LADDER) {
                isOnLadder = true;
            }
            if (currentObject == EObject.SLIME) {
                isOnHoney = true;
            }
            if (currentObject == EObject.SPIKE || currentObject == EObject.HAZARD_LIQUID) {
                onDead();
            }
            if (currentObject == EObject.SPRING && isOnGround && velocityY == 0) {
                velocityY = jumpVelocity * 1.5f;
                isOnGround = false;
            }
            if (teleportCooldown == 0 &&
                    (currentObject == EObject.TELEPORT_BLUE ||
                            currentObject == EObject.TELEPORT_PURPLE ||
                            currentObject == EObject.TELEPORT_RED)) {
                teleportToNext(collisionMap, currentObject, centerTileX, centerTileY, TILE_SIZE);
                teleportCooldown = TELEPORT_COOLDOWN_TICKS;
            }
            if (currentObject == EObject.CHECKPOINT) {
                setSpawnPoint(new Point(centerTileX * TILE_SIZE, centerTileY * TILE_SIZE));
            }
        }
        if (isOnLadder) {
            gravity = 0;
            boolean canClimbDown = canClimbDown(collisionMap);
            if (wantsToClimbUp) {
                velocityY = -autoMoveSpeed;
            } else if (wantsToClimbDown && canClimbDown) {
                velocityY = autoMoveSpeed;
            } else {
                velocityY = 0;
            }
        } else {
            gravity = 1700.0f;
        }
        if (isOnHoney) {
            velocityX = 0;
            velocityY = 0;
        }
        if (collisionMap != null &&
                centerTileX >= 0 && centerTileX < collisionMap.length &&
                centerTileY + 1 >= 0 && centerTileY + 1 < collisionMap[0].length) {
            if (collisionMap[centerTileX][centerTileY + 1] == EObject.FALLING_PLATFORM) {
                gamePanel.getObjectManager().updateFallingPlatforms(
                        gamePanel.getMap(), centerTileX, centerTileY + 1);
            }
        }
    }

    /**
     * Checks if the player can climb down a ladder.
     *
     * @param collisionMap The map representing tile collisions.
     * @return True if the player can climb down, false otherwise.
     */
    private boolean canClimbDown(EObject[][] collisionMap) {
        boolean canClimbDown = false;
        final int belowTileY = (int) ((y + height) / TILE_SIZE);
        final int belowTileX = (int) ((x + (float) width / 2) / TILE_SIZE);
        if (collisionMap != null && belowTileX >= 0 && belowTileX < collisionMap.length && belowTileY >= 0 && belowTileY < collisionMap[0].length) {
            EObject below = collisionMap[belowTileX][belowTileY];
            if (below != null && !below.isSolid()) {
                canClimbDown = true;
            }
        }
        return canClimbDown;
    }

    /**
     * Teleports the player to another tile of the same teleport type.
     *
     * @param collisionMap The map representing tile collisions.
     * @param teleportType The type of teleport tile.
     * @param fromX The x-coordinate of the current teleport tile.
     * @param fromY The y-coordinate of the current teleport tile.
     * @param TILE_SIZE The size of a single tile.
     */
    private void teleportToNext(EObject[][] collisionMap, EObject teleportType, int fromX, int fromY, int TILE_SIZE) {
        for (int x = 0; x < collisionMap.length; x++) {
            for (int y = 0; y < collisionMap[0].length; y++) {
                if ((x != fromX || y != fromY) && collisionMap[x][y] == teleportType) {
                    this.x = x * TILE_SIZE;
                    this.y = y * TILE_SIZE;
                    return;
                }
            }
        }
    }

    /**
     * Sets the player's moving left state and updates direction.
     *
     * @param moving True if the player is moving left, false otherwise.
     */
    public void setMovingLeft(boolean moving) {
        super.setMovingLeft(moving);
        if (moving) direction = -1;
    }

    /**
     * Sets the player's moving right state and updates direction.
     *
     * @param moving True if the player is moving right, false otherwise.
     */
    public void setMovingRight(boolean moving) {
        super.setMovingRight(moving);
        if (moving) direction = 1;
    }
}