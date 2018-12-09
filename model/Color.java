package com.pausanchezv.puzzle.model;

import java.util.Arrays;

/**
 *
 * @author pausanchezv
 */
public class Color {

    // variables
    private char [][] color;

    /**
     * Color Constructor
     * @param puzzle string[][]
     */
    Color(String[][] puzzle) {
        addColor(puzzle);
    }

    /**
     * Color getter
     * @return char[][]
     */
    public char [][] getColor() {
        return color;
    }

    /**
     * Add color to puzzle
     */
    private void addColor(String [][] puzzle) {

        char [][] colorPuzzle = new char[puzzle.length][puzzle[0].length];

        for (int i = 0; i < puzzle.length; i++) {

            for (int j = 0; j < puzzle[0].length; j++) {

                colorPuzzle[i][j] = puzzle[i][j].charAt(2);
            }
        }

        this.color = colorPuzzle;
    }

    /**
     * Color hash code
     */
    @Override
    public int hashCode() {
        return 7;
    }

    /**
     * Color equals
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Color other = (Color) obj;

        return Arrays.deepEquals(this.color, other.color);
    }

    /**
     * Get square color
     */
    public char getSquareColor(int row, int col) {
        return color[row][col];
    }
}
