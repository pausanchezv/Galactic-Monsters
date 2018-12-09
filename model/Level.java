/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pausanchezv.puzzle.model;

import com.pausanchezv.puzzle.controller.GameController.PUZZLE_SIZES;
import java.util.ArrayList;
import com.pausanchezv.puzzle.model.puzzle2.SearchAlgorithms2;

/**
 *
 * @author pausanchezv
 */
public final class Level {

    // Variables
    private final int kind;
    private final String [][] start;
    private final char [][] goal;
    private final ArrayList<Action> actions;
    private final PUZZLE_SIZES size;
    private final int numRows;
    private final int numCols;
    private String [][] squares;

    private ArrayList<Square> squaresList;
    private ArrayList<String> squaresGoalList;

    private Level clone;

    /**
     * Constructor
     * @param start start
     * @param goal goal
     * @param kind kind
     * @param actions actions
     * @param size size
     */
    public Level(String [][] start, char [][] goal, int kind, ArrayList<Action> actions, PUZZLE_SIZES size) {

        this.start = start;
        this.goal = goal;
        this.kind = kind;
        this.actions = actions;
        this.size = size;
        this.numRows = this.start.length;
        this.numCols = this.start[0].length;

        // level arrays
        squares = Util.createPuzzleCopy(start);
        squaresList = new ArrayList<>();
        squaresGoalList = new ArrayList<>();

        // fill level arrays
        fillSquaresList();
        if (goal != null) {
            fillSquaresGoalList();
        }
    }

    /**
     * Constructor
     */
    public Level(String [][] start, int kind, ArrayList<Action> actions, PUZZLE_SIZES size) {
        this(start, null, kind, actions, size);
    }


    /**
     * Fill color goal array
     */
    private void fillSquaresGoalList() {
        for (char[] colorArray : goal) {
            for (char color : colorArray) {
                squaresGoalList.add(Character.toString(color));
            }
        }
    }

    /**
     * Get color array list
     */
    public ArrayList<String> getSquaresGoalList() {
        return this.goal != null ? squaresGoalList : null;
    }

    /**
     * Fill squares list
     */
    public void fillSquaresList() {
        squaresList.clear();
        for (int row = 0; row < squares.length; row++ ) {
            for (int col = 0; col < squares[0].length; col++ ) {
                String cell = squares[row][col];
                squaresList.add(new Square(cell.charAt(0), cell.charAt(1), cell.charAt(2), row, col));
            }
        }
    }

    /**
     * Get squares list
     */
    public ArrayList<Square> getSquaresList() {
        return squaresList;
    }


    /**
     * Get kind
     * @return int
     */
    public int getKind() {
        return kind;
    }

    /**
     * Get start
     * @return int
     */
    public String[][] getStart() {
        return start;
    }

    /**
     * Get goal
     * @return char[][]
     */
    public char[][] getGoal() {
        return goal;
    }

    /**
     * Get num rows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Get num cols
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Get actions
     * @return array
     */
    public ArrayList<Action> getActions() {
        return actions;
    }

    /**
     * Get number of actions
     * @return int
     */
    public int getNumActions() {
        return actions.size();
    }

    /**
     * Get size
     * @return size
     */
    public PUZZLE_SIZES getSize() {
        return size;
    }


    /**
     * Get square
     * @param row int
     * @param col int
     * @return string
     */
    public String getSquare(int row, int col) {
        return squares[row][col];
    }

    public Square getSquareObj(int row, int col) {

        for (Square square: squaresList) {

            if (square.getRow() == row && square.getCol() == col) {
                return square;
            }

        }
        return null;

    }

    public boolean isWall(int row, int col) {
        return squares[row][col].charAt(0) == '#';
    }

    /**
     * Get squares
     * @return string[][]
     */
    public String [][] getSquares() {
        return squares;
    }

    /**
     * Swap squares
     * @param startRow int
     * @param startCol int
     * @param goalRow int
     * @param goalCol int
     */
    public void swapSquare(int startRow, int startCol, int goalRow, int goalCol) {
        String tempSquare = squares[startRow][startCol];
        squares[startRow][startCol] = squares[goalRow][goalCol];
        squares[goalRow][goalCol] = tempSquare;
    }

    /**
     * Check whether or not the current puzzle is a goal state
     * @return bool
     */
    public boolean isGoal() {

        boolean isGoal = true;

        // If level type is 1, compare the current state with goal state's color
        if (kind == 1) {

            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    isGoal &= squares[row][col].charAt(2) == goal[row][col];
                }
            }
        }

        // Otherwise it uses the function in SearchAlgorithms class
        else {

            Puzzle puzzle = new Puzzle(squares);
            isGoal = SearchAlgorithms2.isGoalState(puzzle);
        }

        return isGoal;
    }
}

