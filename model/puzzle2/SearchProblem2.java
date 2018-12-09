package com.pausanchezv.puzzle.model.puzzle2;

import com.pausanchezv.puzzle.model.Action;
import java.util.ArrayList;
import com.pausanchezv.puzzle.model.Puzzle;
import com.pausanchezv.puzzle.model.State;

/**
 *
 * @author pausanchezv
 */
public final class SearchProblem2 {

    // States invloved
    private final State startState;


    // problem variables

    public static ArrayList<Action> actions;
    static ArrayList<Puzzle> puzzles;


    /**
     * SearchProblem Constructor
     */
    public SearchProblem2(String [][] start) {

        SearchProblem2.actions = new ArrayList<>();
        SearchProblem2.puzzles = new ArrayList<>();

        this.startState = new State(start, 0, null);

    }

    /**
     * A* Search Solver
     */
    public void AStarSearchSolver(Heuristic2.Kind heuristic) {
        cleanVariables();
        SearchAlgorithms2.AStarSearch(startState, heuristic);
    }

    /**
     * Show solution
     */
    public void showResult() {

        /*for (int i = 0; i < actions.size(); i++) {
            System.out.println("Action >> " + actions.get(i));
            System.out.println("\n" + puzzles.get(i).puzzleToString());
        }*/

        for (int i = 0; i < actions.size(); i++) {
            if (i == actions.size() -1) {
                System.out.println(/*"\n" + */puzzles.get(i).colorToString());
            }
        }

        System.out.println("Num actions >> " + actions.size());
        System.out.println("Num expanded estates >> " + SearchAlgorithms2.numExpandedStates);
    }

    /**
     * Clean problem variables
     */
    private void cleanVariables() {
        SearchProblem2.actions.clear();
        SearchProblem2.puzzles.clear();
        SearchAlgorithms2.numExpandedStates = 0;
    }

    /**
     * Solution
     */
    public ArrayList<Action> getSolution() {
        return actions;
    }

}
