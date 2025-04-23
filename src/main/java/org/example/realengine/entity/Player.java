package org.example.realengine.entity;

import org.example.realengine.GamePanel;
import org.example.realengine.game.GameConstants;
import org.example.realengine.object.EObject;

import java.awt.*;

public class Player extends Entity {
    private static final int BOX_PUSH_DELAY = 6;
    private final float autoMoveSpeed = 400.0f;
    private final float jumpVelocity = -900.0f;
    private float velocityX = 0;
    private float velocityY = 0;
    private float gravity = 1700.0f;
    private boolean isOnGround = false;
    private boolean isMovingDown = false;
    private Point spawnPoint;
    private boolean isOnLadder = false;
    private boolean isOnTrap = false;
    private boolean isClimbing = false;
    private boolean wantsToClimbUp = false;
    private boolean wantsToClimbDown = false;
    private int boxPushTick = 0;
    private int teleportCooldown = 0;
    private static final int TELEPORT_COOLDOWN_TICKS = 30;

    public Player(float x, float y) {
        super(x, y);
        this.type = "player";
        this.maxHealth = 100;
        this.health = this.maxHealth;
        this.width = 32;
        this.height = 32;
    }

    public Point getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Point spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Override
    public void update(float deltaTime, EObject[][] collisionMap) {
        float potentialNextX = x + velocityX * deltaTime;
        velocityY += gravity * deltaTime;
        float potentialNextY = y + velocityY * deltaTime;
        int TILE_SIZE = GameConstants.TILE_SIZE;
        final float GAP = TILE_SIZE / 30.0f;
        boxPushTick++;

        if (teleportCooldown > 0) {
            teleportCooldown--;
        }

        boolean collisionDetectedX = handleBoxPush(collisionMap, TILE_SIZE, GAP);
        if (!collisionDetectedX) {
            collisionDetectedX = handleXCollision(collisionMap, potentialNextX, TILE_SIZE, GAP);
        }
        if (!collisionDetectedX) {
            x = potentialNextX;
        }

        boolean collisionDetectedY = handleYCollision(collisionMap, potentialNextY, TILE_SIZE);
        if (!collisionDetectedY) {
            y = potentialNextY;
        }

        handleSpecialTiles(collisionMap, TILE_SIZE);

        x = Math.max(0, Math.min(x, GamePanel.WORLD_WIDTH - width));
        y = Math.max(0, Math.min(y, GamePanel.WORLD_HEIGHT - height));
    }

    private void respawn() {
        if (spawnPoint != null) {
            this.x = spawnPoint.x;
            this.y = spawnPoint.y;
        } else {
            this.x = 2 * GameConstants.TILE_SIZE;
            this.y = (GamePanel.MAX_WORLD_ROW - 5) * GameConstants.TILE_SIZE;
        }
        velocityX = 0;
        velocityY = 0;
    }

    public void jump() {
        if (isOnGround) {
            velocityY = jumpVelocity;
            isOnGround = false;
        }
    }

    public void setMovingUp(boolean moving) {
        if (moving) jump();
    }

    public void setMovingLeft(boolean moving) {
        if (moving) velocityX = -autoMoveSpeed;
        else if (velocityX < 0) velocityX = 0;
    }

    public void setMovingRight(boolean moving) {
        if (moving) velocityX = autoMoveSpeed;
        else if (velocityX > 0) velocityX = 0;
    }

    public void setMovingDown(boolean movingDown) {
        this.isMovingDown = movingDown;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public void setClimbingUp(boolean climbingUp) {
        this.wantsToClimbUp = climbingUp;
    }

    public void setClimbingDown(boolean climbingDown) {
        this.wantsToClimbDown = climbingDown;
    }


    private boolean handleBoxPush(EObject[][] collisionMap, int TILE_SIZE, float GAP) {
        boolean collisionDetectedX = false;
        if (velocityX != 0 && boxPushTick >= BOX_PUSH_DELAY) {
            int dir = velocityX > 0 ? 1 : -1;
            int playerTileX = (int) ((x + (dir > 0 ? width : 0)) / TILE_SIZE);
            int playerTileY = (int) ((y + (float) height / 2) / TILE_SIZE);
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


    private boolean handleXCollision(EObject[][] collisionMap, float potentialNextX, int TILE_SIZE, float GAP) {
        boolean collisionDetectedX = false;
        if (velocityX != 0) {
            int topTileY = (int) (y / TILE_SIZE);
            int bottomTileY = (int) ((y + height - 1) / TILE_SIZE);
            int nextTileX;
            if (velocityX > 0) {
                nextTileX = (int) ((potentialNextX + width) / TILE_SIZE);
                for (int tileY = topTileY; tileY <= bottomTileY; tileY++) {
                    if (collisionMap != null &&
                            nextTileX >= 0 && nextTileX < collisionMap.length &&
                            tileY >= 0 && tileY < collisionMap[0].length &&
                            collisionMap[nextTileX][tileY] != null &&
                            !collisionMap[nextTileX][tileY].isWalkable()) {
                        if (collisionMap[nextTileX][tileY] != EObject.BOX) {
                            collisionDetectedX = true;
                            x = nextTileX * TILE_SIZE - width;
                            velocityX = 0;
                            break;
                        }
                    }
                }
            } else {
                nextTileX = (int) (potentialNextX / TILE_SIZE);
                for (int tileY = topTileY; tileY <= bottomTileY; tileY++) {
                    if (collisionMap != null &&
                            nextTileX >= 0 && nextTileX < collisionMap.length &&
                            tileY >= 0 && tileY < collisionMap[0].length &&
                            collisionMap[nextTileX][tileY] != null &&
                            !collisionMap[nextTileX][tileY].isWalkable()) {
                        collisionDetectedX = true;
                        x = (nextTileX + 1) * TILE_SIZE + GAP;
                        velocityX = 0;
                        break;
                    }
                }
            }
        }
        return collisionDetectedX;
    }

    private boolean handleYCollision(EObject[][] collisionMap, float potentialNextY, int TILE_SIZE) {
        boolean collisionDetectedY = false;
        isOnGround = false;
        if (velocityY < 0) {
            int leftHeadTileX = (int) (x / TILE_SIZE);
            int rightHeadTileX = (int) ((x + width - 1) / TILE_SIZE);
            int topTileY = (int) (potentialNextY / TILE_SIZE);

            for (int tileX = leftHeadTileX; tileX <= rightHeadTileX; tileX++) {
                if (collisionMap != null &&
                        tileX >= 0 && tileX < collisionMap.length &&
                        topTileY >= 0 && topTileY < collisionMap[0].length &&
                        collisionMap[tileX][topTileY] != null &&
                        !collisionMap[tileX][topTileY].isWalkable()) {
                    collisionDetectedY = true;
                    velocityY = 0;
                    y = (topTileY + 1) * TILE_SIZE;
                    break;
                }
            }
        }
        if (velocityY >= 0) {
            int leftFootTileX = (int) (x / TILE_SIZE);
            int rightFootTileX = (int) ((x + width - 1) / TILE_SIZE);
            int bottomTileY = (int) ((potentialNextY + height) / TILE_SIZE);
    
            for (int tileX = leftFootTileX; tileX <= rightFootTileX; tileX++) {
                if (collisionMap != null &&
                        tileX >= 0 && tileX < collisionMap.length &&
                        bottomTileY >= 0 && bottomTileY < collisionMap[0].length &&
                        collisionMap[tileX][bottomTileY] != null) {
                    if (collisionMap[tileX][bottomTileY] == EObject.HAZARD_LIQUID || collisionMap[tileX][bottomTileY] == EObject.SPIKES) {
                        respawn();
                        return true;
                    } else if (!collisionMap[tileX][bottomTileY].isWalkable() && !isMovingDown && !isOnTrap) {
                        if (collisionMap[tileX][bottomTileY] == EObject.SPRING) {
                            velocityY = jumpVelocity * 1.25f;
                            isOnGround = false;
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

    private void handleSpecialTiles(EObject[][] collisionMap, int TILE_SIZE) {
        int centerTileX = (int) ((x + (float) width / 2) / TILE_SIZE);
        int centerTileY = (int) ((y + (float) height / 2) / TILE_SIZE);
    
        isOnLadder = false;
        isOnTrap = false;
        isClimbing = false;
        boolean isOnHoney = false;
    
        EObject currentObject = null;
        if (collisionMap != null &&
                centerTileX >= 0 && centerTileX < collisionMap.length &&
                centerTileY >= 0 && centerTileY < collisionMap[0].length) {
            currentObject = collisionMap[centerTileX][centerTileY];
            if (currentObject == EObject.LADDER) {
                isOnLadder = true;
            }
            if (currentObject == EObject.HONEY) {
                isOnHoney = true;
            }
            if (currentObject == EObject.TRAP) {
                isOnTrap = true;
            }
            if (currentObject == EObject.SPRING && isOnGround && velocityY == 0) {
                velocityY = jumpVelocity * 1.5f;
                isOnGround = false;
            }
            if (teleportCooldown == 0 &&
                (currentObject == EObject.TELEPORT_BLUE ||
                 currentObject == EObject.TELEPORT_GREEN ||
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
            if (wantsToClimbUp) {
                velocityY = -autoMoveSpeed;
                isClimbing = true;
            } else if (wantsToClimbDown) {
                velocityY = autoMoveSpeed;
                isClimbing = true;
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
}


