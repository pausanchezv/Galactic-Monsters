package com.pausanchezv.puzzle.model.puzzle1;

import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.model.Action;
import com.pausanchezv.puzzle.model.BinaryHeap;
import com.pausanchezv.puzzle.model.Graph;
import com.pausanchezv.puzzle.model.Color;
import com.pausanchezv.puzzle.model.State;
import com.pausanchezv.puzzle.model.Puzzle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author pausanchezv
 */
class SearchAlgorithms {

    // Number of expanded nodes
    public static int numExpandedStates = 0;
    private static final int MAX_EXPANDED_STATES = 600;

    /**
     * Get Actions
     */
    private static HashMap getActions(State state) {

        // Get the state puzzle
        Puzzle puzzle = state.getPuzzle();

        // Get both number of rows and number of columns
        int numRows = puzzle.getNumRows();
        int numCols = puzzle.getNumCols();

        // A dictionary is gonna hold the actions depending on the key
        // In this way the same key will be able to hold different actions
        HashMap<int[], ArrayList<Action>> actions = new HashMap<>();

        // Traverse the puzzle of the state
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {

                // Get the key from coordinates
                int [] key = {row, col};

                //The square is just the string extracted from the coordinates
                String square = puzzle.getPuzzle()[row][col];

                // An array is gonna hold the posible actions for each key
                actions.put(key, new ArrayList<Action>());

                // Straight actions
                if (Puzzle.isTower(square) || Puzzle.isQueen(square)) {

                    // Adding distance 1
                    if (Puzzle.getScope(square) == '1' || Puzzle.getScope(square) == '2' || Puzzle.getScope(square) == '3' || Puzzle.getScope(square) == '4' || Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 0 && !puzzle.containsWall(row - 1, col)) {
                            actions.get(key).add(new Action("up", 1));
                        }

                        if (row < numRows - 1 && !puzzle.containsWall(row + 1, col)) {
                            actions.get(key).add(new Action("down", 1));
                        }

                        if (col > 0 && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("left", 1));
                        }

                        if (col < numCols - 1 && !puzzle.containsWall(row, col + 1)) {
                            actions.get(key).add(new Action("right", 1));
                        }
                    }

                    // Adding distance 2
                    if (Puzzle.getScope(square) == '2' || Puzzle.getScope(square) == '3' || Puzzle.getScope(square) == '4' || Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 1 && !puzzle.containsWall(row - 2, col) && !puzzle.containsWall(row - 1, col)) {
                            actions.get(key).add(new Action("up", 2));
                        }

                        if (row < numRows - 2 && !puzzle.containsWall(row + 2, col) && !puzzle.containsWall(row + 1, col)) {
                            actions.get(key).add(new Action("down", 2));
                        }

                        if (col > 1 && !puzzle.containsWall(row, col - 2) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("left", 2));
                        }

                        if (col < numCols - 2 && !puzzle.containsWall(row, col + 2) && !puzzle.containsWall(row, col + 1)) {
                            actions.get(key).add(new Action("right", 2));
                        }
                    }

                    // Adding distance 3
                    if (Puzzle.getScope(square) == '3' || Puzzle.getScope(square) == '4' || Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 2 && !puzzle.containsWall(row - 3, col) && !puzzle.containsWall(row - 2, col) && !puzzle.containsWall(row - 1, col)) {
                            actions.get(key).add(new Action("up", 3));
                        }

                        if (row < numRows - 3 && !puzzle.containsWall(row + 3, col) && !puzzle.containsWall(row + 2, col) && !puzzle.containsWall(row + 1, col)) {
                            actions.get(key).add(new Action("down", 3));
                        }

                        if (col > 2 && !puzzle.containsWall(row, col - 3) && !puzzle.containsWall(row, col - 2) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("left", 3));
                        }

                        if (col < numCols - 3 && !puzzle.containsWall(row, col + 3) && !puzzle.containsWall(row, col + 2) && !puzzle.containsWall(row, col + 1)) {
                            actions.get(key).add(new Action("right", 3));
                        }
                    }

                    // Adding distance 4
                    if (Puzzle.getScope(square) == '4' || Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 3 && !puzzle.containsWall(row - 4, col) && !puzzle.containsWall(row - 3, col) && !puzzle.containsWall(row - 2, col) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("up", 4));
                        }

                        if (row < numRows - 4 && !puzzle.containsWall(row + 4, col) && !puzzle.containsWall(row + 3, col) && !puzzle.containsWall(row + 2, col) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("down", 4));
                        }

                        if (col > 3 && !puzzle.containsWall(row, col - 4) && !puzzle.containsWall(row, col - 3) && !puzzle.containsWall(row, col - 2) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("left", 4));
                        }

                        if (col < numCols - 4 && !puzzle.containsWall(row, col + 4) && !puzzle.containsWall(row, col + 3) && !puzzle.containsWall(row, col + 2) && !puzzle.containsWall(row, col + 1)) {
                            actions.get(key).add(new Action("right", 4));
                        }
                    }

                    // Adding distance 5
                    if (Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 4 && !puzzle.containsWall(row - 5, col) && !puzzle.containsWall(row - 4, col) && !puzzle.containsWall(row - 3, col) && !puzzle.containsWall(row, col - 2) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("up", 5));
                        }

                        if (row < numRows - 5 && !puzzle.containsWall(row + 5, col) && !puzzle.containsWall(row + 4, col) && !puzzle.containsWall(row + 3, col) && !puzzle.containsWall(row, col - 2) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("down", 5));
                        }

                        if (col > 4 && !puzzle.containsWall(row, col - 5) && !puzzle.containsWall(row, col - 4) && !puzzle.containsWall(row, col - 3) && !puzzle.containsWall(row, col - 2) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("left", 5));
                        }

                        if (col < numCols - 5 && !puzzle.containsWall(row, col + 5) && !puzzle.containsWall(row, col + 4) && !puzzle.containsWall(row, col + 3) && !puzzle.containsWall(row, col + 2) && !puzzle.containsWall(row, col + 1)) {
                            actions.get(key).add(new Action("right", 5));
                        }
                    }

                    // Adding distance 6
                    if (Puzzle.getScope(square) == '6') {

                        if (row > 5 && !puzzle.containsWall(row - 6, col) && !puzzle.containsWall(row - 5, col) && !puzzle.containsWall(row - 4, col) && !puzzle.containsWall(row, col - 3) && !puzzle.containsWall(row, col - 2)  && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("up", 6));
                        }

                        if (row < numRows - 6 && !puzzle.containsWall(row + 6, col) && !puzzle.containsWall(row + 5, col) && !puzzle.containsWall(row + 4, col) && !puzzle.containsWall(row, col - 3) && !puzzle.containsWall(row, col - 2) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("down", 6));
                        }

                        if (col > 5 && !puzzle.containsWall(row, col - 6) && !puzzle.containsWall(row, col - 5) && !puzzle.containsWall(row, col - 4) && !puzzle.containsWall(row, col - 3) && !puzzle.containsWall(row, col - 2) && !puzzle.containsWall(row, col - 1)) {
                            actions.get(key).add(new Action("left", 6));
                        }

                        if (col < numCols - 6 && !puzzle.containsWall(row, col + 6) && !puzzle.containsWall(row, col + 5) && !puzzle.containsWall(row, col + 4) && !puzzle.containsWall(row, col + 3) && !puzzle.containsWall(row, col + 2) && !puzzle.containsWall(row, col + 1)) {
                            actions.get(key).add(new Action("right", 6));
                        }
                    }
                }

                // Diagonal actions
                if (Puzzle.isBishop(square) || Puzzle.isQueen(square)) {

                    // Adding distance 1
                    if (Puzzle.getScope(square) == '1' || Puzzle.getScope(square) == '2' || Puzzle.getScope(square) == '3' || Puzzle.getScope(square) == '4' || Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 0 && col > 0 && !puzzle.containsWall(row - 1, col - 1)) {
                            actions.get(key).add(new Action("up-left", 1));
                        }

                        if (row < numRows - 1 && col > 0 && !puzzle.containsWall(row + 1, col - 1)) {
                            actions.get(key).add(new Action("down-left", 1));
                        }

                        if (col < numCols - 1 && row > 0 && !puzzle.containsWall(row - 1, col + 1)) {
                            actions.get(key).add(new Action("up-right", 1));
                        }

                        if (col < numCols - 1 && row < numRows - 1 && !puzzle.containsWall(row + 1, col + 1)) {
                            actions.get(key).add(new Action("down-right", 1));
                        }
                    }

                    // Adding distance 2
                    if (Puzzle.getScope(square) == '2' || Puzzle.getScope(square) == '3' || Puzzle.getScope(square) == '4' || Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 1 && col > 1 && !puzzle.containsWall(row - 2, col - 2) && !puzzle.containsWall(row - 1, col - 1)) {
                            actions.get(key).add(new Action("up-left", 2));
                        }

                        if (row < numRows - 2 && col > 1 && !puzzle.containsWall(row + 2, col - 2) && !puzzle.containsWall(row + 1, col - 1)) {
                            actions.get(key).add(new Action("down-left", 2));
                        }

                        if (col < numCols - 2 && row > 1 && !puzzle.containsWall(row - 2, col + 2) && !puzzle.containsWall(row - 1, col + 1)) {
                            actions.get(key).add(new Action("up-right", 2));
                        }

                        if (col < numCols - 2 && row < numRows - 2 && !puzzle.containsWall(row + 2, col + 2) && !puzzle.containsWall(row + 1, col + 1)) {
                            actions.get(key).add(new Action("down-right", 2));
                        }
                    }

                    // Adding distance 3
                    if (Puzzle.getScope(square) == '3' || Puzzle.getScope(square) == '4' || Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 2 && col > 2 && !puzzle.containsWall(row - 3, col - 3) && !puzzle.containsWall(row - 2, col - 2) && !puzzle.containsWall(row - 1, col - 1)) {
                            actions.get(key).add(new Action("up-left", 3));
                        }

                        if (row < numRows - 3 && col > 2 && !puzzle.containsWall(row + 3, col - 3) && !puzzle.containsWall(row + 2, col - 2) && !puzzle.containsWall(row + 1, col - 1)) {
                            actions.get(key).add(new Action("down-left", 3));
                        }

                        if (col < numCols - 3 && row > 2 && !puzzle.containsWall(row - 3, col + 3) && !puzzle.containsWall(row - 2, col + 2) && !puzzle.containsWall(row - 1, col + 1)) {
                            actions.get(key).add(new Action("up-right", 3));
                        }

                        if (col < numCols - 3 && row < numRows - 3 && !puzzle.containsWall(row + 3, col + 3) && !puzzle.containsWall(row + 2, col + 2) && !puzzle.containsWall(row + 1, col + 1)) {
                            actions.get(key).add(new Action("down-right", 3));
                        }
                    }

                    // Adding distance 4
                    if (Puzzle.getScope(square) == '4' || Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 3 && col > 3 && !puzzle.containsWall(row - 4, col - 4) && !puzzle.containsWall(row - 3, col - 3) && !puzzle.containsWall(row - 2, col - 2) && !puzzle.containsWall(row - 1, col - 1)) {
                            actions.get(key).add(new Action("up-left", 4));
                        }

                        if (row < numRows - 4 && col > 3 && !puzzle.containsWall(row + 4, col - 4) && !puzzle.containsWall(row + 3, col - 3) && !puzzle.containsWall(row + 2, col - 2) && !puzzle.containsWall(row + 1, col - 1)) {
                            actions.get(key).add(new Action("down-left", 4));
                        }

                        if (col < numCols - 4 && row > 3 && !puzzle.containsWall(row - 4, col + 4) && !puzzle.containsWall(row - 3, col + 3) && !puzzle.containsWall(row - 2, col + 2) && !puzzle.containsWall(row - 1, col + 1)) {
                            actions.get(key).add(new Action("up-right", 4));
                        }

                        if (col < numCols - 4 && row < numRows - 4 && !puzzle.containsWall(row + 4, col + 4) && !puzzle.containsWall(row + 3, col + 3) && !puzzle.containsWall(row + 2, col + 2) && !puzzle.containsWall(row + 1, col + 1)) {
                            actions.get(key).add(new Action("down-right", 4));
                        }
                    }

                    // Adding distance 5
                    if (Puzzle.getScope(square) == '5' || Puzzle.getScope(square) == '6') {

                        if (row > 4 && col > 4 && !puzzle.containsWall(row - 5, col - 5) && !puzzle.containsWall(row - 4, col - 4) && !puzzle.containsWall(row - 3, col - 3) && !puzzle.containsWall(row - 2, col - 2) && !puzzle.containsWall(row - 1, col - 1)) {
                            actions.get(key).add(new Action("up-left", 5));
                        }

                        if (row < numRows - 5 && col > 4 && !puzzle.containsWall(row + 5, col - 5) && !puzzle.containsWall(row + 4, col - 4) && !puzzle.containsWall(row + 3, col - 3) && !puzzle.containsWall(row + 2, col - 2) && !puzzle.containsWall(row + 1, col - 1)) {
                            actions.get(key).add(new Action("down-left", 5));
                        }

                        if (col < numCols - 5 && row > 4 && !puzzle.containsWall(row - 5, col + 5) && !puzzle.containsWall(row - 4, col + 4) && !puzzle.containsWall(row - 3, col + 3) && !puzzle.containsWall(row - 2, col + 2) && !puzzle.containsWall(row - 1, col + 1)) {
                            actions.get(key).add(new Action("up-right", 5));
                        }

                        if (col < numCols - 5 && row < numRows - 5 && !puzzle.containsWall(row + 5, col + 5) && !puzzle.containsWall(row + 4, col + 4) && !puzzle.containsWall(row + 3, col + 3) && !puzzle.containsWall(row + 2, col + 2) && !puzzle.containsWall(row + 1, col + 1)) {
                            actions.get(key).add(new Action("down-right", 5));
                        }
                    }

                    // Adding distance 6
                    if (Puzzle.getScope(square) == '6') {

                        if (row > 5 && col > 5 && !puzzle.containsWall(row - 6, col - 6) && !puzzle.containsWall(row - 5, col - 5) && !puzzle.containsWall(row - 4, col - 4) && !puzzle.containsWall(row - 3, col - 3) && !puzzle.containsWall(row - 2, col - 2) && !puzzle.containsWall(row - 1, col - 1)) {
                            actions.get(key).add(new Action("up-left", 6));
                        }

                        if (row < numRows - 6 && col > 5 && !puzzle.containsWall(row + 6, col - 6) && !puzzle.containsWall(row + 5, col - 5) && !puzzle.containsWall(row + 4, col - 4) && !puzzle.containsWall(row + 3, col - 3) && !puzzle.containsWall(row + 2, col - 2) && !puzzle.containsWall(row + 1, col - 1)) {
                            actions.get(key).add(new Action("down-left", 6));
                        }

                        if (col < numCols - 6 && row > 5 && !puzzle.containsWall(row - 6, col + 6) && !puzzle.containsWall(row - 5, col + 5) && !puzzle.containsWall(row - 4, col + 4) && !puzzle.containsWall(row - 3, col + 3) && !puzzle.containsWall(row - 2, col + 2) && !puzzle.containsWall(row - 1, col + 1)) {
                            actions.get(key).add(new Action("up-right", 6));
                        }

                        if (col < numCols - 6 && row < numRows - 6 && !puzzle.containsWall(row + 6, col + 6) && !puzzle.containsWall(row + 5, col + 5) && !puzzle.containsWall(row + 4, col + 4) && !puzzle.containsWall(row + 3, col + 3) && !puzzle.containsWall(row + 2, col + 2) && !puzzle.containsWall(row + 1, col + 1)) {
                            actions.get(key).add(new Action("down-right", 6));
                        }
                    }

                }
            }
        }

        return actions;
    }

    /**
     * Add a new successor to a state
     */
    private static void addSuccessor(String[][] newPuzzle, ArrayList<State> successors, Action action, int row, int col) {

        // Get the coordinates
        int [] position = {row, col};

        // First of all the Action is created
        Action newAction = new Action(action.getDirection(), action.getValue(), position);

        // The action object allows us to create the successor
        State successor = new State(newPuzzle, (float) Double.POSITIVE_INFINITY, null, newAction);

        // Add the successor if it is not added yet
        if (!successors.contains(successor)) {
            successors.add(successor);
        }
    }

    /**
     * Obtain the state successors
     */
    private static ArrayList<State> getSuccessors(State state, HashMap actions) {

        // Array which will hold the successors of the state
        ArrayList<State> successors = new ArrayList<>();

        // Getting the current puzzle
        String [][] puzzle = state.getPuzzle().getPuzzle();

        // Creating an iterator

        // Traversing the actions
        for (Object obj : actions.entrySet()) {

            HashMap.Entry pair = (HashMap.Entry) obj;

            // Extract the coordinates
            int[] key = (int[]) pair.getKey();
            int row = key[0];
            int col = key[1];

            // Add successors to the current state
            for (Action action : (ArrayList<Action>) pair.getValue()) {

                // Get a new copy of the puzzle for each action
                Puzzle newPuzzleObj = createPuzzleCopy(state.getPuzzle());
                String[][] newPuzzle = newPuzzleObj.getPuzzle();

                if (action.getDirection().equals("down")) {

                    newPuzzle[row][col] = puzzle[row + action.getValue()][col];
                    newPuzzle[row + action.getValue()][col] = puzzle[row][col];

                    if (!Arrays.deepEquals(state.getPuzzle().getPuzzle(), newPuzzle)) {
                        addSuccessor(newPuzzle, successors, action, row, col);
                    }
                }

                if (action.getDirection().equals("up")) {

                    newPuzzle[row][col] = puzzle[row - action.getValue()][col];
                    newPuzzle[row - action.getValue()][col] = puzzle[row][col];

                    if (!Arrays.deepEquals(state.getPuzzle().getPuzzle(), newPuzzle)) {
                        addSuccessor(newPuzzle, successors, action, row, col);
                    }
                }

                if (action.getDirection().equals("left")) {

                    newPuzzle[row][col - action.getValue()] = puzzle[row][col];
                    newPuzzle[row][col] = puzzle[row][col - action.getValue()];

                    if (!Arrays.deepEquals(state.getPuzzle().getPuzzle(), newPuzzle)) {
                        addSuccessor(newPuzzle, successors, action, row, col);
                    }
                }

                if (action.getDirection().equals("right")) {

                    newPuzzle[row][col] = puzzle[row][col + action.getValue()];
                    newPuzzle[row][col + action.getValue()] = puzzle[row][col];

                    if (!Arrays.deepEquals(state.getPuzzle().getPuzzle(), newPuzzle)) {
                        addSuccessor(newPuzzle, successors, action, row, col);
                    }
                }

                if (action.getDirection().equals("down-left")) {

                    newPuzzle[row][col] = puzzle[row + action.getValue()][col - action.getValue()];
                    newPuzzle[row + action.getValue()][col - action.getValue()] = puzzle[row][col];

                    if (!Arrays.deepEquals(state.getPuzzle().getPuzzle(), newPuzzle)) {
                        addSuccessor(newPuzzle, successors, action, row, col);
                    }
                }

                if (action.getDirection().equals("down-right")) {

                    newPuzzle[row][col] = puzzle[row + action.getValue()][col + action.getValue()];
                    newPuzzle[row + action.getValue()][col + action.getValue()] = puzzle[row][col];

                    if (!Arrays.deepEquals(state.getPuzzle().getPuzzle(), newPuzzle)) {
                        addSuccessor(newPuzzle, successors, action, row, col);
                    }

                }

                if (action.getDirection().equals("up-left")) {

                    newPuzzle[row][col] = puzzle[row - action.getValue()][col - action.getValue()];
                    newPuzzle[row - action.getValue()][col - action.getValue()] = puzzle[row][col];

                    if (!Arrays.deepEquals(state.getPuzzle().getPuzzle(), newPuzzle)) {
                        addSuccessor(newPuzzle, successors, action, row, col);
                    }
                }

                if (action.getDirection().equals("up-right")) {

                    newPuzzle[row][col] = puzzle[row - action.getValue()][col + action.getValue()];
                    newPuzzle[row - action.getValue()][col + action.getValue()] = puzzle[row][col];

                    if (!Arrays.deepEquals(state.getPuzzle().getPuzzle(), newPuzzle)) {
                        addSuccessor(newPuzzle, successors, action, row, col);
                    }
                }
            }
        }

        // Shuffle the successors (it helps us when the AStar algorithm explores the successors to expand them)
        Collections.shuffle(successors);

        return successors;
    }


    /**
     * Make a puzzle copy
     */
    private static Puzzle createPuzzleCopy(Puzzle puzzle) {

        // Create a new matrix to hold the new puzzle
        String [][] newPuzzle = new String[puzzle.getNumRows()][puzzle.getNumCols()];

        // Fill the new matrix
        for (int row = 0; row < puzzle.getNumRows(); row++) {
            System.arraycopy(puzzle.getPuzzle()[row], 0, newPuzzle[row], 0, puzzle.getNumCols());

            /*for (int col = 0; col < puzzle.getNumCols(); col++) {
                newPuzzle[row][col] = puzzle.getPuzzle()[row][col];
            }*/
        }

        // Create and return the new puzzle
        return new Puzzle(newPuzzle);
    }


    /**
     * Get solution
     */
    private static void getSearchSolution(State node) {

        try {
            do {

                // Push the new action
                SearchProblem.actions.add(node.getAction());
                SearchProblem.puzzles.add(node.getPuzzle());
                node = node.getParent();

                // Traverse through the node whilst it has parent
            } while (node.getParent() != null);

        } catch (Exception e) {
            SearchProblem.actions.clear();
            SearchProblem.puzzles.clear();
            return;
        }

        // Reverse the solution arrays
        Collections.reverse(SearchProblem.actions);
        Collections.reverse(SearchProblem.puzzles);
    }

    /**
     * Get node cost
     */
    private static float getCost(State node, State neighbor) {

        float cost = node.getCost() + 1;

        if (cost < neighbor.getCost()) {
            neighbor.setCost(cost);
            neighbor.setParent(node);
        }

        return cost;
    }

    /**
     * A* Search Algorithm
     */
    static void AStarSearch(State start, char[][] goal, Heuristic.Kind heuristic) {

        // Control the number of expanded states
        numExpandedStates = 0;

        // A couple of arrays are gonna hold both the queue and the visited nodes
        BinaryHeap queue = new BinaryHeap();
        ArrayList<Puzzle> visited = new ArrayList<>();
        queue.push(start, 0);

        // Compute the algorithm until the queue is empty
        while (!queue.isEmpty()) {

            // Stop generator if use leaves from activity
            if (GameController.stopLevelGenerator) {
                return;
            }

            // Get the node and its color
            State node = queue.pop();
            Color color = node.getPuzzle().getColor();

            // Check if the color matrix is found in visited array
            if (!visited.contains(node.getPuzzle())) {

                numExpandedStates++;

                // Check the expanded nodes and break if it's necessary
                if (numExpandedStates > MAX_EXPANDED_STATES) {
                    return;
                }

                // Return the solution
                if (Arrays.deepEquals(color.getColor(), goal)) {
                    getSearchSolution(node);
                    return;
                }

                // Add node to visited
                visited.add(node.getPuzzle());

                // Expand the node's children
                for (State neighbor: getSuccessors(node, getActions(node))) {

                    // Compute the cost of the node
                    float cost = getCost(node, neighbor);

                    // Heuristic value
                    float heuristicValue = 0;

                    // Select heuristic kind
                    switch (heuristic) {

                        case MANHATTAN:
                            heuristicValue = Heuristic.manhattanDistancesHeuristic(neighbor.getPuzzle().getPuzzle(), goal);
                            break;

                        case EUCLIDEAN:
                            heuristicValue = Heuristic.euclideanDistancesHeuristic(neighbor.getPuzzle().getPuzzle(), goal);
                            break;

                        case MANHATTAN_MATCHINGS:
                            heuristicValue = Heuristic.manhattanDistancesHeuristicWithMatches(neighbor.getPuzzle().getPuzzle(), goal);
                            break;

                        case EUCLIDEAN_MATCHINGS:
                            heuristicValue = Heuristic.euclideanDistancesHeuristicWithMatches(neighbor.getPuzzle().getPuzzle(), goal);
                            break;
                    }

                    // Compute neighbor
                    queue.push(neighbor, cost + heuristicValue);
                }
            }
        }

    }


    /**
     * Depth First Search Algorithm. It's a traversal algorithm here. Not search!
     */
    private static ArrayList depthFirstSearch(Graph graph, String start) {


        // A couple of arrays are gonna hold both the queue and the visited nodes
        ArrayList<String> stack = new ArrayList<>();
        ArrayList<String> visited = new ArrayList<>();
        stack.add(start);

        // Compute the algorithm until the queue is empty
        while (!stack.isEmpty()) {

            // Get the node and its color
            String node = stack.remove(stack.size() - 1);

            // Check if the color matrix is found in visited array
            if (!visited.contains(node)) {

                visited.add(node);

                // Expand the node's children
                for (Object neighbor: graph.neighbors(node)) {

                    // Compute the neighbor's parent
                    if (!visited.contains(neighbor)) {
                        stack.add((String) neighbor);
                    }
                }
            }
        }

        return visited;
    }

    /**
     * Verifying whether or not a puzzle is connected
     */
    static boolean isConnectedPuzzle(String[][] puzzle) {

        // Create a test state
        State testState = new State(puzzle, 0, null);

        // Create the graph
        Graph graph = new Graph();

        // Add edges to graph with diagonals
        Graph.addPuzzleEdges(graph, testState, true);

        // Depth first search
        ArrayList dfsPath = depthFirstSearch(graph, graph.getAnyNode());

        // Return connectivity
        return dfsPath.size() == testState.getPuzzle().getNumCharactersNotWall();
    }
}
