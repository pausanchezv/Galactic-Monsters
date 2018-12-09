package com.pausanchezv.puzzle.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * Class Puzzle
 */
public final class Puzzle {

    // Declarations
    private final String [][] puzzle;
    private final Color color;
    private final int numRows;
    private final int numCols;
    public static ArrayList<String> colorArray = new ArrayList<>();

    // Constants
    public static final String WALL = "###";
    public static final String BLANK = "...";

    /**
     * Puzzle Constructor
     *
     * @param puzzle puzzle
     */
    public Puzzle(String[][] puzzle) {

        this.puzzle = puzzle;
        this.numRows = this.puzzle.length;
        this.numCols = this.puzzle[0].length;

        this.color = new Color(puzzle);
    }

    /**
     * Check if a coordinate contains either a wall or a blank
     *
     * @param row int
     * @param col int
     * @return bool
     */
    public boolean containsWall(int row, int col) {

        if (row < 0 || col < 0 || row > this.getNumRows() - 1 || col > this.getNumCols() - 1) {

            return true;
        }

        boolean containsWall;

        try {

            containsWall = WALL.equals(this.puzzle[row][col]) || BLANK.equals(this.puzzle[row][col]);
        } catch (Exception e) {
            containsWall = true;
        }

        return containsWall;
    }



    /**
     * Convert a puzzle to String
     *
     * @return string
     */
    String puzzleToString() {

        String response = "";

        for (String[] row : this.puzzle) {

            response += "[ ";

            for (String col : row) {
                response += col + " ";
            }

            response += "]\n";
        }

        return response;
    }

    /**
     * Convert a puzzle to String
     *
     * @return string
     */
    public String colorToString() {

        StringBuilder response = new StringBuilder();

        for (char[] row : color.getColor()) {

            response.append("[ ");

            for (char col : row) {
                response.append(col).append(" ");
            }

            response.append("]\n");
        }

        return response.toString();
    }

    /**
     * Puzzle getter
     * @return string[][]
     */
    public String[][] getPuzzle() {
        return puzzle;
    }

    /**
     * Color getter
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Get number of rows
     * @return int
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Get number of columns
     * @return int
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Puzzle HasCode
     * @return int
     */
    @Override
    public int hashCode() {
        //return 7;
        String response = "";

        for (String[] row : this.puzzle) {

            for (String col : row) {
                response += col;
            }
        }

        return response.hashCode();
    }

    /**
     * Puzzle equals
     *
     * @param obj Object
     * @return bool
     */
    @Override
    public boolean equals(Object obj) {

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Puzzle other = (Puzzle) obj;
        if (this.numRows != other.numRows) {
            return false;
        }

        if (this.numCols != other.numCols) {
            return false;
        }

        return Arrays.deepEquals(this.puzzle, other.puzzle);
    }

    /**
     * Check if a square is a queen
     * @param square string
     * @return bool
     */
    public static boolean isQueen(String square) {
        return square.charAt(0) == 'Q';
    }

    /**
     * Check if a square is a bishop
     * @param square string
     * @return bool
     */
    public static boolean isBishop(String square) {
        return square.charAt(0) == 'B';
    }

    /**
     * Check if a square is a tower
     * @param square string
     * @return bool
     */
    public static boolean isTower(String square) {
        return square.charAt(0) == 'T';
    }

    /**
     * Get scope from square
     * @param square string
     * @return char
     */
    public static char getScope(String square) {
        return square.charAt(1);
    }

    /**
     * Return the number of squares which doesn't contain wall
     * @return int
     */
    public int getNumCharactersNotWall() {

        int num = 0;

        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                if (!this.containsWall(i, j)) {
                    num++;
                }
            }
        }
        return num;

    }

    /**
     * Get the puzzle color amount
     * @return hash
     */
    public HashMap<String, Integer> getColorCounts() {

        HashMap<String, Integer> colorDict = new HashMap<>();

        // Create keys by puzzle's colors
        for (int i = 0; i < Puzzle.colorArray.size(); i++) {
            colorDict.put(Puzzle.colorArray.get(i), 0);
        }

        // Color counting
        for (int j = 0; j < this.numRows; j++) {
            for (int k = 0; k < this.numCols; k++) {
                for (int c = 0; c < Puzzle.colorArray.size(); c++) {
                    if (String.valueOf(this.color.getColor()[j][k]).equals(Puzzle.colorArray.get(c))) {
                        colorDict.put(Puzzle.colorArray.get(c), colorDict.get(Puzzle.colorArray.get(c)) + 1);
                    }
                }
            }
        }

        return colorDict;
    }

    /**
     * Add graph by color
     * @return graph
     */
    public Graph getGraphByColor() {

        Graph graph = new Graph();

        for (int row = 0; row < getNumRows(); row++) {
            for (int col = 0; col < getNumCols(); col++) {

                if (!containsWall(row, col)) {

                    // perpendicular
                    if (!containsWall(row - 1, col) && this.color.getSquareColor(row - 1, col) == this.color.getSquareColor(row, col) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col));

                    if (!containsWall(row + 1, col) && this.color.getSquareColor(row + 1, col) == this.color.getSquareColor(row, col) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col));

                    if (!containsWall(row, col - 1) && this.color.getSquareColor(row, col - 1) == this.color.getSquareColor(row, col) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col - 1)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col -1));

                    if (!containsWall(row, col + 1) && this.color.getSquareColor(row, col + 1) == this.color.getSquareColor(row, col) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col + 1)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col + 1));

                }
            }
        }

        return graph;
    }

    /**
     * Add graph by color
     * @return graph
     */
    public Graph getGraph() {

        Graph graph = new Graph();

        for (int row = 0; row < getNumRows(); row++) {
            for (int col = 0; col < getNumCols(); col++) {

                if (!containsWall(row, col)) {

                    // perpendicular
                    if (!containsWall(row - 1, col) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col));

                    if (!containsWall(row + 1, col) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col));

                    if (!containsWall(row, col - 1) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col - 1)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col -1));

                    if (!containsWall(row, col + 1) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col + 1)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col + 1));
                }
            }
        }

        return graph;
    }

}
