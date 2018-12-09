
package com.pausanchezv.puzzle.model;

import com.pausanchezv.puzzle.controller.GameController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 *
 * @author pausanchezv
 */
public class Util {

    /**
     * Compare ArrayLists
     * @param A array
     * @param B array
     * @return bool
     */
    public static boolean arrayListEquals(ArrayList<String> A, ArrayList<String> B) {

        if (A.size() != B.size()) {
            return false;
        }

        for (int i = 0; i < A.size(); i++) {
            if (!A.get(i).equals(B.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Shuffling array with needle
     * @param array array
     */
    public static void arrayShuffle(String[] array) {

        int j, i;
        String x;
        for (i = array.length - 1; i > 0; i--) {
            j = (int) Math.floor(Math.random() * (i + 1));
            x = array[i];
            array[i] = array[j];
            array[j] = x;
        }
    }

    /**
     * Shuffling array with needle
     * @param array array
     * @param needle needle
     */
    public static void arrayShuffleWithNeedle(ArrayList<String> array, String needle) {

        int j;
        String x;

        for (int i = array.size() - 1; i > 0; i--) {

            j = (int) Math.floor(Math.random() * (i + 1));

            if (!array.get(i).equals(needle) && !array.get(j).equals(needle)) {

                x = array.get(i);
                array.set(i, array.get(j));
                array.set(j, x);
            }
        }
    }

    /**
     * Get array color
     * @param array array
     * @return array
     */
    public static ArrayList<String> getArrayColor(ArrayList<String> array) {

        ArrayList<String> color = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            color.add(String.valueOf(array.get(i).charAt(2)));
        }
        return color;
    }

    /**
     * To goal color
     * @param colorArrayString string[][]
     * @return char[][]
     */
    public static char [][] toGoalColor(String [][] colorArrayString) {

        char [][] color = new char [colorArrayString.length][colorArrayString[0].length];

        for (int i = 0; i < colorArrayString.length; i++) {
            for (int j = 0; j < colorArrayString[0].length; j++) {
                color[i][j] = colorArrayString[i][j].charAt(0);
            }
        }

        return color;
    }


    /**
     * Create puzzle clone
     * @param puzzle string[][]
     * @return string[][]
     */
    public static String [][] createPuzzleCopy(String [][] puzzle) {

        int rows = puzzle.length;
        int cols = puzzle[0].length;

        String [][] copy = new String[rows][cols];

        for (int i = 0; i < rows; i ++) {
            System.arraycopy(puzzle[i], 0, copy[i], 0, cols);
        }

        return copy;
    }

    /**
     * Get array from matrix
     * @param matrix array
     * @return array
     */
    private static ArrayList<String> arrayFromMatrix(String[][] matrix) {

        ArrayList<String> array = new ArrayList<>();

        for (String[] row : matrix) {
            array.addAll(Arrays.asList(row));
        }

        return array;

    }

    /**
     * Shuffle matrix depends on a needle
     * @param matrix string[][]
     */
    public static void matrixShuffleWithNeedle(String [][] matrix) {

        ArrayList<String> array = arrayFromMatrix(matrix);

        arrayShuffleWithNeedle(array, Puzzle.WALL);

        int cont = 0;

        for (String[] row : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                row[j] = array.get(cont++);
            }
        }
    }

    /**
     * Get puzzle size
     */
    public static int [] getPuzzleSize(GameController.PUZZLE_SIZES size) {

        // Array that's gonna hold the puzzle size
        int [] puzzleSize = new int[2];

        switch (size) {

            case S21:
                puzzleSize[0] = 1; puzzleSize[1] = 2;
                break;

            case S22:
                puzzleSize[0] = 2; puzzleSize[1] = 2;
                break;

            case S32:
                puzzleSize[0] = 3; puzzleSize[1] = 2;
                break;

            case S33:
                puzzleSize[0] = 3; puzzleSize[1] = 3;
                break;

            case S43:
                puzzleSize[0] = 4; puzzleSize[1] = 3;
                break;

            case S44:
                puzzleSize[0] = 4; puzzleSize[1] = 4;
                break;

            case S54:
                puzzleSize[0] = 5; puzzleSize[1] = 4;
                break;

            case S55:
                puzzleSize[0] = 5; puzzleSize[1] = 5;
                break;

            case S65:
                puzzleSize[0] = 6; puzzleSize[1] = 5;
                break;

            case S66:
                puzzleSize[0] = 6; puzzleSize[1] = 6;
                break;

            case S76:
                puzzleSize[0] = 7; puzzleSize[1] = 6;
                break;

        }

        return puzzleSize;
    }
}

