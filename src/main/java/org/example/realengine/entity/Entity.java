package org.example.realengine.entity;

import org.example.realengine.object.EObject;
import org.example.realengine.physics.CollisionDetector;

import static org.example.realengine.game.GameConstants.TILE_SIZE;
import static org.example.realengine.game.GameConstants.GRAVITY;

/**
 * Základní třída pro všechny herní objekty (postavy, předměty atd.),
 * které mají pozici, rychlost, rozměry a mohou interagovat se světem.
 * Poskytuje základní fyzikální chování, jako je gravitace, detekce kolizí s mapou,
 * a základní podporu pro platformy a žebříky.
 */
public sealed abstract class Entity permits Player {
    protected float x, y;
    protected float vx = 0;
    protected float vy = 0;
    protected int width = 16;
    protected int height = 16;
    protected boolean isOnGround = false;
    protected boolean isOnLadder = false;
    protected boolean active = true;
    protected float health = 100;
    protected float maxHealth = 100;
    protected boolean movingLeft = false;
    protected boolean movingRight = false;
    protected boolean movingUp = false;
    protected boolean movingDown = false;
    protected boolean jumping = false;
    protected float moveSpeed = 3.0f;
    protected float climbSpeed = 2.0f;
    protected float jumpStrength = -10.0f;
    protected float gravity = GRAVITY;
    protected float maxFallSpeed = 15.0f;
    protected final String type;

    /**
     * Vytvoří novou entitu na zadaných souřadnicích.
     *
     * @param x Počáteční X souřadnice.
     * @param y Počáteční Y souřadnice.
     */
    public Entity(float x, float y, String type) {
        this.x = x;
        this.y = y;
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

    /**
     * Zkontroluje, zda se entita nachází na dlaždici typu LADDER.
     * Aktualizuje příznak `isOnLadder`.
     *
     * @param collisionMap Kolizní mapa.
     */
    protected void checkLadderStatus(EObject[][] collisionMap) {
        if (collisionMap == null) {
            isOnLadder = false;
            return;
        }
        int midX = (int) ((x + width / 2f) / TILE_SIZE);
        int bottomY = (int) ((y + height - 1) / TILE_SIZE);
        int midY = (int) ((y + height / 2f) / TILE_SIZE);

        EObject objAtFeet = getObjectAtTile(midX, bottomY, collisionMap);
        EObject objAtMid = getObjectAtTile(midX, midY, collisionMap);

        isOnLadder = (objAtFeet == EObject.LADDER || objAtMid == EObject.LADDER);
        if (isOnLadder) {
            isOnGround = true;
            vy = 0;
        }
    }

    /**
     * Pomocná metoda pro získání objektu na dlaždici.
     */
    private EObject getObjectAtTile(int x, int y, EObject[][] map) {
        if (!isValidMapPosition(map, x, y)) {
            return EObject.BORDER;
        }
        EObject object = map[x][y];
        return object != null ? object : EObject.EMPTY;
    }

    /**
     * Vypočítá zamýšlený pohyb entity na základě aktuálního stavu
     * (movingLeft, movingRight, jumping, isOnLadder, movingUp, movingDown).
     * Nastavuje `vx` a `vy`.
     */
    protected void calculateMovement() {
        vx = 0;
        vy = isOnLadder ? 0 : vy;

        if (isOnLadder) {
            if (movingUp) {
                vy = -climbSpeed;
            } else if (movingDown) {
                vy = climbSpeed;
            } else {
                vy = 0;
            }
            if (movingLeft) vx -= moveSpeed;
            if (movingRight) vx += moveSpeed;

            if (jumping) {
                vy = jumpStrength;
                isOnLadder = false;
                isOnGround = false;
                jumping = false;
            }

        } else {
            if (movingLeft) vx -= moveSpeed;
            if (movingRight) vx += moveSpeed;

            if (jumping && isOnGround) {
                vy = jumpStrength;
                isOnGround = false;
                jumping = false;
            }
        }
    }

    /**
     * Zkontroluje a vyřeší kolize entity s kolizní mapou.
     * Upraví pozici (`x`, `y`) a rychlost (`vx`, `vy`) entity tak, aby nepronikla pevnými objekty.
     * Nastaví příznak `isOnGround`.
     *
     * @param deltaTime    Časový krok.
     * @param collisionMap Kolizní mapa světa.
     */
    protected void handleCollisions(float deltaTime, EObject[][] collisionMap) {
        if (collisionMap == null) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            isOnGround = false;
            return;
        }

        CollisionDetector.CollisionResult result = CollisionDetector.checkMapCollision(
                x, y, vx * deltaTime, vy * deltaTime, width, height, collisionMap
        );

        x = result.adjustedX;
        y = result.adjustedY;

        isOnGround = result.collidedBottom || isOnLadder;

        if (result.collidedTop || result.collidedBottom) {
            if (!isOnLadder) {
                vy = 0;
            }
        }
        if (result.collidedLeft || result.collidedRight) {
            vx = 0;
        }
    }

    /**
     * Zkontroluje, zda tato entita koliduje (překrývá se) s jinou entitou.
     * Používá AABB (Axis-Aligned Bounding Box) kolizní test.
     *
     * @param other Druhá entita pro kontrolu kolize.
     * @return `true`, pokud entity kolidují, jinak `false`.
     */
    public boolean collidesWith(Entity other) {
        if (other == null || !active || !other.active) return false;

        return !(x + width <= other.x ||
                x >= other.x + other.width ||
                y + height <= other.y ||
                y >= other.y + other.height);
    }

    /**
     * Aplikuje poškození na entitu.
     * Snižuje `health` a může nastavit entitu jako neaktivní (`active = false`), pokud zdraví klesne na 0.
     *
     * @param amount Množství poškození.
     * @return `true`, pokud entita poškození přežila (`health > 0`), jinak `false`.
     */
    public boolean damage(float amount) {
        if (!active) return false;

        health -= amount;
        if (health <= 0) {
            health = 0;
            active = false;
            return false;
        }
        return true;
    }

    /**
     * Zahájí skok entity, pokud je na zemi nebo na žebříku.
     * Nastaví příznak `jumping`, který se zpracuje v `calculateMovement`.
     */
    public void jump() {
        if (isOnGround || isOnLadder) {
            jumping = true;
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
     * @return Aktuální horizontální rychlost entity.
     */
    public float getVx() {
        return vx;
    }

    /**
     * Nastaví horizontální rychlost entity. @param vx Nová horizontální rychlost.
     */
    public void setVx(float vx) {
        this.vx = vx;
    }

    /**
     * @return Aktuální vertikální rychlost entity.
     */
    public float getVy() {
        return vy;
    }

    /**
     * Nastaví vertikální rychlost entity. @param vy Nová vertikální rychlost.
     */
    public void setVy(float vy) {
        this.vy = vy;
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
     * @return `true`, pokud je entita aktivní (aktualizuje se, vykresluje, koliduje), jinak `false`.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Nastaví, zda je entita aktivní. @param active `true` pro aktivaci, `false` pro deaktivaci.
     */
    public void setActive(boolean active) {
        this.active = active;
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

    /**
     * Nastaví, zda se má entita pohybovat doleva. @param moving `true` pro pohyb doleva.
     */
    public void setMovingLeft(boolean moving) {
        this.movingLeft = moving;
    }

    /**
     * Nastaví, zda se má entita pohybovat doprava. @param moving `true` pro pohyb doprava.
     */
    public void setMovingRight(boolean moving) {
        this.movingRight = moving;
    }

    /**
     * Nastaví, zda se má entita pohybovat nahoru (pro žebřík). @param moving `true` pro pohyb nahoru.
     */
    public void setMovingUp(boolean moving) {
        this.movingUp = moving;
    }

    /**
     * Nastaví, zda se má entita pohybovat dolů (pro žebřík). @param moving `true` pro pohyb dolů.
     */
    public void setMovingDown(boolean moving) {
        this.movingDown = moving;
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

    /**
     * Helper method to check if coordinates are valid within a map
     */
    protected boolean isValidMapPosition(EObject[][] map, int x, int y) {
        return map != null && x >= 0 && y >= 0 && x < map.length && y < map[0].length;
    }

    public String getType() {
        return this.type;
    }
}