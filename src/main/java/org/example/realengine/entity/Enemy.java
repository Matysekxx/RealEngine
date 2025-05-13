package org.example.realengine.entity;

import org.example.realengine.object.EObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.example.realengine.demo.GamePanel.WORLD_HEIGHT;
import static org.example.realengine.demo.GamePanel.WORLD_WIDTH;
import static org.example.realengine.game.GameConstants.TILE_SIZE;

public non-sealed class Enemy extends Entity {
    private int direction = -1;
    private float baseSpeed = 100f;
    private final boolean infinityJumping;

    /**
     * Vytvoří novou entitu na zadaných souřadnicích.
     *
     * @param x      Počáteční X souřadnice.
     * @param y      Počáteční Y souřadnice.
     */
    public Enemy(float x, float y, boolean infinityJumping, String type) {
        super(x, y, TILE_SIZE, TILE_SIZE, type, 10);
        this.infinityJumping = infinityJumping;
        this.baseSpeed = baseSpeed * 1.5f;

        try {
            this.texturesFromDirection = Map.of(
                    -1, new BufferedImage[] {ImageIO.read(new File("textures\\spiny1.png")), ImageIO.read(new File("textures\\spiny2.png"))},
                    1, new BufferedImage[] {ImageIO.read(new File("textures\\spiny-1.png")), ImageIO.read(new File("textures\\spiny-2.png"))}
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(float deltaTime, EObject[][] collisionMap) {
        if (infinityJumping) jump();
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
        updateAnimation();
    }

    @Override
    public void onDead() {
        this.isDead = true;
    }

    public int getDirection() {
        return direction;
    }
}





