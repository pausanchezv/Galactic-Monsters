/*********************************************
 * Search Problem
 *********************************************
 *
 * Developed by: Pau Sanchez V.
 *
 * Website:     pausanchezv.com
 * Github:      github.com/pausanchezv
 * Linkedin:    linkedin.com/in/pausanchezv
 * Twitter:     twitter.com/pausanchezv
 * Facebook:    facebook.com/pausanchezv
 *
 * All rights reserved. - Barcelona 2018 -
 *
 **********************************************/
package com.pausanchezv.puzzle.model.puzzle1;

import com.pausanchezv.puzzle.model.Action;
import java.util.ArrayList;
import com.pausanchezv.puzzle.model.Puzzle;
import com.pausanchezv.puzzle.model.State;

/**
 *
 * @author pausanchezv
 */
public final class SearchProblem {

    // States invloved
    private final State startState;
    private final char [][] goal;

    // problem variables

    public static ArrayList<Action> actions;
    static ArrayList<Puzzle> puzzles;


    /**
     * SearchProblem Constructor
     */
    public SearchProblem(String [][] start, char [][] goal) {

        SearchProblem.actions = new ArrayList<>();
        SearchProblem.puzzles = new ArrayList<>();

        this.startState = new State(start, 0, null);
        this.goal = goal;
    }

    /**
     * A* Search Solver
     */
    public void AStarSearchSolver(Heuristic.Kind heuristic) {
        cleanVariables();
        SearchAlgorithms.AStarSearch(startState, goal, heuristic);
    }

    /**
     * Show solution
     */
    public void showResult() {

         /*for (int i = 0; i < actions.size(); i++) {
            System.out.println("Action >> " + actions.get(i));
            System.out.println("\n" + puzzles.get(i).puzzleToString());
        }*/

        System.out.println("Num actions >> " + actions.size());
        System.out.println("Num expanded estates >> " + SearchAlgorithms.numExpandedStates);
    }

    /**
     * Clean problem variables
     */
    private void cleanVariables() {
        SearchProblem.actions.clear();
        SearchProblem.puzzles.clear();
        SearchAlgorithms.numExpandedStates = 0;
    }

    /**
     * Solution
     */
    public ArrayList<Action> getSolution() {
        return actions;
    }

}
