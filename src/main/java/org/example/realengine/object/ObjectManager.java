package org.example.realengine.object;

import org.example.realengine.map.RMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the behavior and state of various game objects, such as falling platforms and boxes.
 * This class handles the logic for dynamic object interactions within the game world.
 */
public class ObjectManager {
    /**
     * The delay in game ticks before a falling platform starts to fall after being stepped on.
     */
    private static final int FALL_DELAY = 30;
    /**
     * The delay in game ticks before a fallen platform respawns.
     */
    private static final int RESPAWN_DELAY = 100;
    /**
     * A list to keep track of the current state of falling platforms.
     */
    private final List<FallingPlatformState> fallingPlatforms = new ArrayList<>();

    /**
     * Updates the state of all falling platforms in the game.
     * This method checks if the player is on a falling platform to initiate its fall,
     * manages the falling timer, and handles the respawn of fallen platforms.
     *
     * @param rMap    The current game map, used to access and modify the collision map.
     * @param playerX The player's current X-coordinate.
     * @param playerY The player's current Y-coordinate.
     */
    public void updateFallingPlatforms(RMap rMap, int playerX, int playerY) {
        final EObject[][] collisionMap = rMap.getCollisionMap();
        for (int y = 0; y < rMap.getHeight(); y++) {
            for (int x = 0; x < rMap.getWidth(); x++) {
                if (collisionMap[x][y] == EObject.FALLING_PLATFORM) {
                    boolean found = false;
                    for (int i = 0; i < fallingPlatforms.size(); i++) {
                        FallingPlatformState s = fallingPlatforms.get(i);
                        if (s.x == x && s.y == y) {
                            found = true;
                            break;
                        }
                    }
                    if (playerX == x && playerY == y && !found) {
                        fallingPlatforms.add(new FallingPlatformState(x, y));
                    }
                }
            }
        }
        for (int i = 0; i < fallingPlatforms.size(); ) {
            FallingPlatformState state = fallingPlatforms.get(i);
            if (!state.falling) {
                state.timer++;
                if (state.timer >= FALL_DELAY) {
                    collisionMap[state.x][state.y] = EObject.EMPTY;
                    state.falling = true;
                    state.respawnTimer = 0;
                }
                i++;
            } else {
                state.respawnTimer++;
                if (state.respawnTimer >= RESPAWN_DELAY) {
                    collisionMap[state.x][state.y] = EObject.FALLING_PLATFORM;
                    fallingPlatforms.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    /**
     * Applies gravity to all boxes on the map, causing them to fall if there is empty space below them.
     * This method iterates through the map from bottom to top to ensure correct gravity application.
     *
     * @param rMap The current game map, used to access and modify the collision map.
     */
    public void applyBoxGravity(RMap rMap) {
        final EObject[][] collisionMap = rMap.getCollisionMap();
        for (int y = rMap.getHeight() - 2; y >= 0; y--) {
            for (int x = 0; x < rMap.getWidth(); x++) {
                if (collisionMap[x][y] == EObject.BOX) {
                    if (y + 1 < rMap.getHeight() && collisionMap[x][y + 1] == EObject.EMPTY) {
                        collisionMap[x][y + 1] = EObject.BOX;
                        collisionMap[x][y] = EObject.EMPTY;
                    }
                }
            }
        }
    }

    /**
     * A private static nested class representing the state of a single falling platform.
     * It tracks the platform's coordinates, timers for falling and respawning, and its current falling status.
     */
    private static final class FallingPlatformState {
        private final int x;
        private final int y;
        private int timer;
        private boolean falling;
        private int respawnTimer;

        /**
         * Constructs a new FallingPlatformState.
         *
         * @param x The X-coordinate of the falling platform.
         * @param y The Y-coordinate of the falling platform.
         */
        private FallingPlatformState(int x, int y) {
            this.x = x;
            this.y = y;
            this.timer = 0;
            this.falling = false;
            this.respawnTimer = 0;
        }
    }
}
