package org.example.realengine.entity;

import org.example.realengine.demo.GamePanel;
import org.example.realengine.object.EObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.example.realengine.game.GameConstants.*;

public non-sealed class Player extends Entity {
    private final GamePanel gamePanel;
    private Point spawnPoint;
    private boolean wantsToClimbUp = false;
    private boolean wantsToClimbDown = false;
    private int boxPushTick = 0;
    private int teleportCooldown = 0;

    public Player(float x, float y, GamePanel gamePanel) {
        super(x, y, 16, 16, "player", 5);
        this.gamePanel = gamePanel;
        this.maxHealth = 3;
        this.health = this.maxHealth;
        this.width = 32;
        this.height = 32;

        try {
            this.texturesFromDirection = Map.of(
                    1, new BufferedImage[]{ImageIO.read(new File("textures\\mario-IDLE.png")),
                            ImageIO.read(new File("textures\\mario-Jump.png")),
                            ImageIO.read(new File("textures\\mario-Walking.png")),},
                    -1, new BufferedImage[]{ImageIO.read(new File("textures\\marioIDLE.png")),
                            ImageIO.read(new File("textures\\marioJump.png")),
                            ImageIO.read(new File("textures\\marioWalking.png"))}
            );
        } catch (IOException _) {
        }
    }

    public void setSpawnPoint(Point spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public void bunnyJump() {
        if (isOnGround) {
            velocityY = jumpVelocity / 1.6f;
            isOnGround = false;
            jumping = false;
        }
    }

    @Override
    public void update(float deltaTime, EObject[][] collisionMap) {
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
        updateAnimation();
    }

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

    public void setClimbingUp(boolean climbingUp) {
        this.wantsToClimbUp = climbingUp;
    }

    public void setClimbingDown(boolean climbingDown) {
        this.wantsToClimbDown = climbingDown;
    }


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
                gamePanel.showMapMenu();
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
    }

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

    public void setMovingLeft(boolean moving) {
        super.setMovingLeft(moving);
        if (moving) direction = -1;
    }

    public void setMovingRight(boolean moving) {
        super.setMovingRight(moving);
        if (moving) direction = 1;
    }
}