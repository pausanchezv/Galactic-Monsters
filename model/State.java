package com.pausanchezv.puzzle.model;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Objects;


/**
 *
 * Class State
 */
public class State implements Comparable{

    // Declarations
    private float cost;
    private State parent;
    private final Puzzle puzzle;
    private Action action;
    private static Comparator<State> comparator = null;

    /**
     * State Constructor
     *
     * @param puzzle puzzle
     * @param cost cost
     * @param parent parent
     */
    public State(String [][] puzzle, float cost, State parent) {

        this.puzzle = new Puzzle(puzzle);
        this.cost = cost;
        this.parent = parent;
    }

    /**
     * State Constructor
     *
     * @param puzzle puzzle
     * @param cost cost
     * @param parent parent
     * @param action action
     */
    public State(String [][] puzzle, float cost, State parent, Action action) {

        this.puzzle = new Puzzle(puzzle);
        this.cost = cost;
        this.parent = parent;
        this.action = action;
    }


    /**
     * State to String
     *
     * @return string
     */
    @Override
    public String toString() {

        String response = "";

        response += "cost = " + this.cost + "\n";
        response += "parent = " + this.parent + "\n";
        response += "action = " + this.action + "\n\n";
        response += "puzzle\n" + this.puzzle.puzzleToString() + "\n";
        response += "color\n" + this.puzzle.colorToString();

        return response;
    }

    /**
     * Cost getter
     * @return float
     */
    public float getCost() {
        return cost;
    }

    /**
     * Cost setter
     * @param cost cost
     */
    public void setCost(float cost) {
        this.cost = cost;
    }

    /**
     * Action getter
     * @return Action
     */
    public Action getAction() {
        return action;
    }

    /**
     * Action setter
     * @param action action
     */
    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * Parent getter
     * @return State
     */
    public State getParent() {
        return parent;
    }

    /**
     * Parent setter
     * @param parent parent
     */
    public void setParent(State parent) {
        this.parent = parent;
    }

    /**
     * Parent setter
     * @return Puzzle
     */
    public Puzzle getPuzzle() {
        return this.puzzle;
    }

    /**
     * State HashCode
     * @return int
     */
    @Override
    public int hashCode() {
        //return 7;
        return puzzle.hashCode();
    }

    /**
     * State equals
     * This method is based on equals method of the class puzzle.
     * A state will be equal from another one if the puzzles of them are equals.
     *
     * @param obj obj
     * @return bool
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof State) {
            final State other = (State) obj;
            return other.getPuzzle().equals(this.puzzle);
        }
        return false;
    }

    /**
     * State compare
     * @param other other
     * @return int
     */
    @Override
    public int compareTo(@NonNull Object other) {

        if (this.getCost() < ((State) other).getCost())
            return -1;
        if (this.getCost() > ((State) other).getCost())
            return 1;

        return 0;

    }

    /**
     * State comparator
     * @return Comparator
     */
    public static Comparator comparatorState() {

        if (comparator == null) {

            comparator = new Comparator<State>() {
                @Override
                public int compare(State t, State t1) {
                    return t.compareTo(t1);
                }
            };
        }

        return comparator;
    }
}


