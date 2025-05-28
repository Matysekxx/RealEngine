package org.example.realengine.entity;

/**
 * Enum representing different animation states for entities.
 * Each state has its index for accessing textures in an array.
 */
public enum AnimationState {
    /**
     * Represents the idle animation state.
     */
    IDLE(0),
    /**
     * Represents the jumping animation state.
     */
    JUMPING(1),
    /**
     * Represents the first walking animation state.
     */
    WALKING_1(2),
    /**
     * Represents the second walking animation state.
     */
    WALKING_2(3);

    /**
     * The index associated with the animation state, used for texture access.
     */
    private final int index;

    /**
     * Constructs an AnimationState with the specified index.
     * @param index The index for the animation state.
     */
    AnimationState(int index) {
        this.index = index;
    }

    /**
     * Returns the index of this animation state.
     * @return The index of the animation state.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the appropriate animation state based on entity conditions.
     * @param isOnGround True if the entity is on the ground, false otherwise.
     * @param isMoving True if the entity is moving, false otherwise.
     * @param timeInSeconds The current game time in seconds, used for walking animation timing.
     * @return The corresponding AnimationState.
     */
    public static AnimationState getState(boolean isOnGround, boolean isMoving, float timeInSeconds) {
        if (!isOnGround) {
            return JUMPING;
        }
        if (isMoving) {
            return ((int)(timeInSeconds / 0.25f) % 2 == 0) ? WALKING_1 : WALKING_2;
        }
        return IDLE;
    }
}