package com.pausanchezv.puzzle.model;

/**
 *
 * Class Action
 */
public class Action {

    // Declarations
    private final String direction;
    private int [] position;
    private final int value;

    /**
     * Action Constructor
     *
     * @param direction direction
     * @param value value
     */
    public Action(String direction, int value) {
        this.direction = direction;
        this.value = value;
    }

    /**
     * Action Constructor
     *
     * @param direction direction
     * @param value value
     * @param position position
     */
    public Action(String direction, int value, int [] position) {
        this.direction = direction;
        this.value = value;
        this.position = position;
    }

    /**
     * Action to String
     * @return string
     */
    @Override
    public String toString() {

        if (position != null) {
            return "{" + "'position': " + String.valueOf(position[0])+ String.valueOf(position[1]) + ", 'direction': " + direction + ", 'value': " + value + "}";
        }

        return "{" + "'direction': " + direction + ", 'value': " + value + "}";
    }

    /**
     * Direction getter
     * @return string
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Position getter
     * @return int[]
     */
    public int[] getPosition() {
        return position;
    }

    /**
     * Value getter
     * @return int
     */
    public int getValue() {
        return value;
    }
}
