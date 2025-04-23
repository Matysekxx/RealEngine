package org.example.realengine.physics;

import org.example.realengine.entity.Entity;
import org.example.realengine.map.RMap;
import org.example.realengine.object.EObject;
import org.jetbrains.annotations.NotNull;

/**
 * Poskytuje statické metody pro detekci a řešení kolizí
 * mezi entitami a herní mapou.
 */
public final class CollisionDetector {

    /**
     * Výchozí velikost dlaždice mapy v pixelech.
     */
    private static final int TILE_SIZE = 16;

    public static CollisionResult checkEntityMapCollision(Entity entity, float vx, float vy, RMap map) {
        if (entity == null || map == null) {
            assert entity != null;
            return new CollisionResult(entity.getX(), entity.getY());
        }
        return checkMapCollision(
                entity.getX(), entity.getY(), vx, vy,
                entity.getWidth(), entity.getHeight(),
                map.getCollisionMap()
        );
    }

    public static boolean checkEntityCollision(Entity entity1, Entity entity2) {
        if (entity1 == null || entity2 == null) return false;

        float aLeft = entity1.getX();
        float aRight = entity1.getX() + entity1.getWidth();
        float aTop = entity1.getY();
        float aBottom = entity1.getY() + entity1.getHeight();

        float bLeft = entity2.getX();
        float bRight = entity2.getX() + entity2.getWidth();
        float bTop = entity2.getY();
        float bBottom = entity2.getY() + entity2.getHeight();

        return !(aRight < bLeft || aLeft > bRight || aBottom < bTop || aTop > bBottom);
    }

    /**
     * Zkontroluje a vyřeší kolize obdélníkového objektu (entity) s kolizní mapou
     * při jeho zamýšleném pohybu.
     * Vrací upravené souřadnice a informace o tom, na kterých stranách došlo ke kolizi.
     * Implementuje základní sweep test (oddělená kontrola os).
     * Zohledňuje speciální chování pro platformy.
     *
     * @param currentX Aktuální X souřadnice levého horního rohu objektu.
     * @param currentY Aktuální Y souřadnice levého horního rohu objektu.
     * @param vx       Zamýšlený horizontální posun (rychlost * deltaTime).
     * @param vy       Zamýšlený vertikální posun (rychlost * deltaTime).
     * @param width    Šířka objektu.
     * @param height   Výška objektu.
     * @param map      Dvourozměrné pole reprezentující kolizní mapu světa (`EObject[][]`).
     *                 `null` nebo prázdná mapa znamená žádné kolize.
     * @return {@link CollisionResult} Objekt obsahující upravené souřadnice a příznaky kolizí.
     */
    public static CollisionResult checkMapCollision(float currentX, float currentY, float vx, float vy, int width, int height, EObject[][] map) {
        CollisionResult result = new CollisionResult(currentX, currentY);
        if (map == null || map.length == 0 || map[0].length == 0) {
            result.adjustedX += vx;
            result.adjustedY += vy;
            return result;
        }
        float potentialY = currentY + vy;
        float currentBottom = currentY + height;
        int leftTileX = (int) (currentX / TILE_SIZE);
        int rightTileX = (int) ((currentX + width - 1) / TILE_SIZE);

        if (vy > 0) {
            int bottomTile = (int) ((potentialY + height - 1) / TILE_SIZE);
            for (int tileX = leftTileX; tileX <= rightTileX; tileX++) {
                boolean solidBelow = isSolidOrPlatform(tileX, bottomTile, map);

                if (solidBelow && currentBottom <= bottomTile * TILE_SIZE) {

                    potentialY = bottomTile * TILE_SIZE - height;
                    result.collidedBottom = true;
                    break;
                }
            }
        } else if (vy < 0) {
            int topTile = (int) (potentialY / TILE_SIZE);
            for (int tileX = leftTileX; tileX <= rightTileX; tileX++) {
                if (isSolid(tileX, topTile, map)) {
                    potentialY = (topTile + 1) * TILE_SIZE;
                    result.collidedTop = true;
                    break;
                }
            }
        }
        result.adjustedY = potentialY;
        float potentialX = currentX + vx;
        int topTileY = (int) (result.adjustedY / TILE_SIZE);
        int bottomTileY = (int) ((result.adjustedY + height - 1) / TILE_SIZE);

        if (vx > 0) {
            int rightTile = (int) ((potentialX + width - 1) / TILE_SIZE);
            for (int tileY = topTileY; tileY <= bottomTileY; tileY++) {
                if (isSolid(rightTile, tileY, map)) {
                    potentialX = rightTile * TILE_SIZE - width;
                    result.collidedRight = true;
                    break;
                }
            }
        } else if (vx < 0) {
            int leftTile = (int) (potentialX / TILE_SIZE);
            for (int tileY = topTileY; tileY <= bottomTileY; tileY++) {
                if (isSolid(leftTile, tileY, map)) {
                    potentialX = (leftTile + 1) * TILE_SIZE;
                    result.collidedLeft = true;
                    break;
                }
            }
        }
        result.adjustedX = potentialX;

        return result;
    }

    /**
     * Získá objekt na daných souřadnicích dlaždice. Vrací BORDER pro neplatné souřadnice.
     *
     * @param x   Index sloupce dlaždice.
     * @param y   Index řádku dlaždice.
     * @param map Kolizní mapa.
     * @return {@link EObject} na dané pozici nebo {@link EObject#BORDER}.
     */
    private static EObject getObjectAtTile(int x, int y, @NotNull EObject[][] map) {
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) {
            return EObject.BORDER;
        }
        EObject object = map[x][y];
        return object != null ? object : EObject.EMPTY;
    }

    private static boolean isSolid(int x, int y, EObject[][] map) {
        EObject object = getObjectAtTile(x, y, map);
        return !object.isWalkable();
    }

    /**
     * Zjistí, zda je dlaždice pevná NEBO platforma. Používá se pro detekci kolize zdola.
     *
     * @param x   Souřadnice X dlaždice.
     * @param y   Souřadnice Y dlaždice.
     * @param map Kolizní mapa světa.
     * @return `true`, pokud je dlaždice pevná nebo platforma, jinak `false`.
     */
    private static boolean isSolidOrPlatform(int x, int y, EObject[][] map) {
        EObject object = getObjectAtTile(x, y, map);
        return !object.isWalkable();
    }

    @Deprecated
    public static EObject rayCast(@NotNull Entity entity, float dirX, float dirY, float maxDistance, @NotNull RMap map) {
        float startX = entity.getX() + (float) entity.getWidth() / 2;
        float startY = entity.getY() + (float) entity.getHeight() / 2;

        for (float distance = 0; distance < maxDistance; distance += 0.5f) {
            float checkX = startX + dirX * distance;
            float checkY = startY + dirY * distance;

            int tileX = (int) (checkX / TILE_SIZE);
            int tileY = (int) (checkY / TILE_SIZE);

            if (tileX < 0 || tileX >= map.getWidth() || tileY < 0 || tileY >= map.getHeight()) {
                break;
            }

            EObject object = map.getCollisionMap()[tileX][tileY];
            if (object != null && !object.isWalkable()) {
                return object;
            }
        }
        return null;
    }

    /**
     * Vnitřní třída uchovávající výsledek detekce kolize s mapou.
     * Obsahuje upravené souřadnice po kolizi a příznaky označující,
     * na kterých stranách objektu došlo ke kolizi.
     */
    public static final class CollisionResult {
        /**
         * Upravená X souřadnice po zohlednění kolizí.
         */
        public float adjustedX;
        /**
         * Upravená Y souřadnice po zohlednění kolizí.
         */
        public float adjustedY;
        /**
         * `true`, pokud došlo ke kolizi s horní stranou objektu.
         */
        public boolean collidedTop = false;
        /**
         * `true`, pokud došlo ke kolizi s dolní stranou objektu (objekt stojí na zemi/platformě).
         */
        public boolean collidedBottom = false;
        /**
         * `true`, pokud došlo ke kolizi s levou stranou objektu.
         */
        public boolean collidedLeft = false;
        /**
         * `true`, pokud došlo ke kolizi s pravou stranou objektu.
         */
        public boolean collidedRight = false;

        /**
         * Vytvoří nový výsledek kolize s počátečními souřadnicemi.
         *
         * @param startX Počáteční X souřadnice.
         * @param startY Počáteční Y souřadnice.
         */
        public CollisionResult(float startX, float startY) {
            this.adjustedX = startX;
            this.adjustedY = startY;
        }
    }
}