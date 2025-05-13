package org.example.realengine.entity;

import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Map;

import static org.example.realengine.game.GameConstants.GRAVITY;
import static org.example.realengine.game.GameConstants.TILE_SIZE;

/**
 * Základní třída pro všechny herní objekty (postavy, předměty atd.),
 * které mají pozici, rychlost, rozměry a mohou interagovat se světem.
 * Poskytuje základní fyzikální chování, jako je gravitace, detekce kolizí s mapou,
 * a základní podporu pro platformy a žebříky.
 */
public sealed abstract class Entity permits Enemy, Player {
    protected Map<Integer, BufferedImage[]> texturesFromDirection;
    protected boolean wasWalking = true;
    protected int animationCounter = 0;
    protected int animationDelay = 7;

    public void setWasWalking() {
        this.wasWalking = !wasWalking;
    }

    public boolean wasWalking() {
        return wasWalking;
    }

    protected final String type;
    protected float x, y;
    protected int width, height;
    protected boolean isOnGround = false;
    protected boolean isOnLadder = false;
    protected float health = 1;
    protected float maxHealth = 5;
    protected boolean movingLeft = false;
    protected boolean movingRight = false;
    protected boolean movingUp = false;
    protected boolean isMovingDown = false;
    protected boolean jumping = false;
    protected float moveSpeed = 3.0f;
    protected float climbSpeed = 2.0f;
    protected float jumpStrength = -10.0f;
    protected float gravity = GRAVITY;
    protected float maxFallSpeed = 15.0f;
    protected float velocityX = 0;
    protected float velocityY = 0;
    protected float jumpVelocity = -900.0f;
    protected float autoMoveSpeed = 400.0f;
    protected boolean isDead = false;

    /**
     * Vytvoří novou entitu na zadaných souřadnicích.
     *
     * @param x Počáteční X souřadnice.
     * @param y Počáteční Y souřadnice.
     */
    public Entity(float x, float y, int width, int height, String type, int animationDelay) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.animationDelay = animationDelay;
    }

    public Entity(float x, float y, int width, int height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    /**
     * Aktualizuje stav entity v jednom snímku hry.
     * Zahrnuje kontrolu žebříků, výpočet pohybu, aplikaci gravitace a řešení kolizí s mapou.
     * Tato metoda by měla být volána v hlavní herní smyčce.
     *
     * @param deltaTime    Čas uplynulý od posledního snímku (v sekundách nebo jiné jednotce).
     * @param collisionMap Dvourozměrné pole reprezentující kolizní mapu světa.
     */
    public abstract void update(float deltaTime, EObject[][] collisionMap);

    protected void updateAnimation() {
        animationCounter++;
        if (animationCounter >= animationDelay) {
            animationCounter = 0;
            setWasWalking();
        }
    }

    public Map<Integer, BufferedImage[]> getTexturesFromDirection() {
        return texturesFromDirection;
    }

    public abstract void onDead();

    public boolean isDead() {
        return isDead;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    /**
     * Nastaví, zda se má entita pohybovat doleva. @param moving `true` pro pohyb doleva.
     */
    public void setMovingLeft(boolean moving) {
        if (moving) velocityX = -autoMoveSpeed;
        else if (velocityX < 0) velocityX = 0;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    /**
     * Nastaví, zda se má entita pohybovat doprava. @param moving `true` pro pohyb doprava.
     */
    public void setMovingRight(boolean moving) {
        if (moving) velocityX = autoMoveSpeed;
        else if (velocityX > 0) velocityX = 0;
    }

    public boolean isMovingUp() {
        return movingUp;
    }

    public void setMovingUp(boolean moving) {
        this.movingUp = moving;
    }

    public boolean isMovingDown() {
        return isMovingDown;
    }

    public void setMovingDown(boolean movingDown) {
        if (!movingDown) {
            this.isMovingDown = false;
            return;
        }
        this.isMovingDown = this.isOnLadder;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
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

    public float getJumpVelocity() {
        return jumpVelocity;
    }

    public void setJumpVelocity(float jumpVelocity) {
        this.jumpVelocity = jumpVelocity;
    }

    public float getAutoMoveSpeed() {
        return autoMoveSpeed;
    }

    public void setAutoMoveSpeed(float autoMoveSpeed) {
        this.autoMoveSpeed = autoMoveSpeed;
    }

    public void handleSpecialTiles(EObject[][] collisionMap) {
        final var centerTileX = (int) ((x + (float) width / 2) / TILE_SIZE);
        final var centerTileY = (int) ((y + (float) height / 2) / TILE_SIZE);
        boolean isOnSlime = false;

        EObject currentObject;
        if (collisionMap != null &&
                centerTileX >= 0 && centerTileX < collisionMap.length &&
                centerTileY >= 0 && centerTileY < collisionMap[0].length) {
            currentObject = collisionMap[centerTileX][centerTileY];
            if (currentObject == EObject.SLIME) {
                isOnSlime = true;
            }
            if (currentObject == EObject.SPIKE || currentObject == EObject.HAZARD_LIQUID) {
                onDead();
            }
            if (currentObject == EObject.SPRING && isOnGround && velocityY == 0) {
                velocityY = jumpVelocity * 1.5f;
                isOnGround = false;
            }
        }
        gravity = 1700.0f;
        if (isOnSlime) {
            velocityX = 0;
            velocityY = 0;
        }
    }

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
                        !collisionMap[tileX][topTileY].isWalkable()) {
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
                    } else if (!collisionMap[tileX][bottomTileY].isWalkable() && !isMovingDown) {
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
     * Zahájí skok entity, pokud je na zemi nebo na žebříku.
     * Nastaví příznak `jumping`, který se zpracuje v `calculateMovement`.
     */
    public void jump() {
        if (isOnGround || isOnLadder) {
            velocityY = jumpVelocity;
            isOnGround = false;
            jumping = false;
        }
    }

    /**
     * @return Aktuální X souřadnice entity.
     */
    public float getX() {
        return x;
    }

    /**
     * Nastaví X souřadnici entity. @param x Nová X souřadnice.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return Aktuální Y souřadnice entity.
     */
    public float getY() {
        return y;
    }

    /**
     * Nastaví Y souřadnici entity. @param y Nová Y souřadnice.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return Šířka entity (pro kolize a vykreslování).
     */
    public int getWidth() {
        return width;
    }

    /**
     * Nastaví šířku entity. @param width Nová šířka.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return Výška entity (pro kolize a vykreslování).
     */
    public int getHeight() {
        return height;
    }

    /**
     * Nastaví výšku entity. @param height Nová výška.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return `true`, pokud entita stojí na pevném povrchu nebo je na žebříku, jinak `false`.
     */
    public boolean isOnGround() {
        return isOnGround;
    }

    /**
     * Explicitně nastaví stav `isOnGround`. Používat opatrně. @param onGround Nový stav.
     */
    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }

    /**
     * @return `true`, pokud je entita aktuálně na žebříku.
     */
    public boolean isOnLadder() {
        return isOnLadder;
    }

    public void setOnLadder(boolean onLadder) {
        isOnLadder = onLadder;
    }

    /**
     * @return Základní rychlost pohybu entity.
     */
    public float getMoveSpeed() {
        return moveSpeed;
    }

    /**
     * Nastaví základní rychlost pohybu entity. @param moveSpeed Nová rychlost.
     */
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * @return Rychlost lezení po žebříku.
     */
    public float getClimbSpeed() {
        return climbSpeed;
    }

    /**
     * Nastaví rychlost lezení po žebříku. @param climbSpeed Nová rychlost.
     */
    public void setClimbSpeed(float climbSpeed) {
        this.climbSpeed = climbSpeed;
    }

    /**
     * @return Síla (záporná vertikální rychlost) skoku entity.
     */
    public float getJumpStrength() {
        return jumpStrength;
    }

    /**
     * Nastaví sílu skoku. @param jumpStrength Nová síla skoku (typicky záporná hodnota).
     */
    public void setJumpStrength(float jumpStrength) {
        this.jumpStrength = jumpStrength;
    }

    /**
     * @return Základní hodnota gravitace působící na entitu.
     */
    public float getGravity() {
        return gravity;
    }

    /**
     * Nastaví základní hodnotu gravitace. @param gravity Nová hodnota gravitace.
     */
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    /**
     * @return Maximální rychlost pádu entity.
     */
    public float getMaxFallSpeed() {
        return maxFallSpeed;
    }

    /**
     * Nastaví maximální rychlost pádu. @param maxFallSpeed Nová maximální rychlost.
     */
    public void setMaxFallSpeed(float maxFallSpeed) {
        this.maxFallSpeed = maxFallSpeed;
    }

    /**
     * @return Aktuální zdraví entity.
     */
    public float getHealth() {
        return health;
    }

    /**
     * Nastaví aktuální zdraví entity, s omezením na `maxHealth`. @param health Nové zdraví.
     */
    public void setHealth(float health) {
        this.health = Math.min(Math.max(0, health), maxHealth);
    }

    /**
     * @return Maximální zdraví entity.
     */
    public float getMaxHealth() {
        return maxHealth;
    }

    /**
     * Nastaví maximální zdraví entity. Upraví i aktuální zdraví, pokud přesahuje nové maximum. @param maxHealth Nové maximální zdraví.
     */
    public void setMaxHealth(float maxHealth) {
        this.maxHealth = Math.max(0, maxHealth);
        if (health > this.maxHealth) health = this.maxHealth;
    }

    public String getType() {
        return this.type;
    }
}