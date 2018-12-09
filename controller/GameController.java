package com.pausanchezv.puzzle.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.model.Action;
import java.util.ArrayList;

import com.pausanchezv.puzzle.model.GameData;
import com.pausanchezv.puzzle.model.Level;

import com.pausanchezv.puzzle.model.Util;

import com.pausanchezv.puzzle.model.puzzle1.SearchProblem;
import com.pausanchezv.puzzle.model.puzzle1.Heuristic;
import com.pausanchezv.puzzle.model.puzzle1.LevelGenerator;

import com.pausanchezv.puzzle.model.puzzle2.Heuristic2;
import com.pausanchezv.puzzle.model.puzzle2.LevelGenerator2;
import com.pausanchezv.puzzle.model.puzzle2.SearchProblem2;

/**
 *
 * @author pausanchezv
 */
public final class GameController {

    // Singleton Instance
    private static GameController instance;

    // Game data object
    private GameData gameData;

    // Puzzle sizes
    public enum PUZZLE_SIZES {S21, S22, S32, S33, S43, S44, S54, S55, S65, S66, S76}

    // Denotes whether or not the next level is ready to be used
    private boolean isNextLevelReady;

    // Indicates the current game mode (1, 2 or 3)
    public enum GameModes {MATCHING, CLUSTERING, MIXED};
    private GameModes currentGameMode;

    // Moves flags
    public static int ORIGINAL_MOVES_NUM = 0;
    public static int NEW_MOVES_NUM = 0;
    public static int USED_MOVES_NUM = 0;

    // Stop level generator flag
    public static boolean stopLevelGenerator = false;

    // Is generator generating a level?
    public static boolean isGeneratorGeneratingInWelcome = false;

    /**
     * Constructor
     */
    private GameController() {

        // defaults
        isNextLevelReady = false;
        gameData = new GameData();
    }

    private void resetMovesNumber() {
        ORIGINAL_MOVES_NUM = 0;
        NEW_MOVES_NUM = 0;
        USED_MOVES_NUM = 0;
    }

    /**
     * Get instance
     */
    public static GameController getInstance() {

        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    /**
     * Game data instance
     */
    public GameData getGameData() {
        return gameData;
    }

    /**
     * Get puzzle size
     */
    public PUZZLE_SIZES getCurrentPuzzleSize(Activity context) {

        if (getCurrentLevelNumber(context) == 1 && currentGameMode == GameModes.MATCHING) {
            return PUZZLE_SIZES.S21;

        } else if ((getCurrentLevelNumber(context) <= 3 && (currentGameMode == GameModes.MATCHING || currentGameMode == GameModes.MIXED)) || (getCurrentLevelNumber(context) <= 2 && currentGameMode == GameModes.CLUSTERING)) {
            return PUZZLE_SIZES.S22;

        } else if (getCurrentLevelNumber(context) < 8) {
            return PUZZLE_SIZES.S32;

        } else if (getCurrentLevelNumber(context) < 20){
            return PUZZLE_SIZES.S33;

        } else if (getCurrentLevelNumber(context) < 50){
            return PUZZLE_SIZES.S43;

        } else if (getCurrentLevelNumber(context) < 90){
            return PUZZLE_SIZES.S44;

        } else if (getCurrentLevelNumber(context) < 150){
            return PUZZLE_SIZES.S54;

        } else if (getCurrentLevelNumber(context) < 230){
            return PUZZLE_SIZES.S55;

        } else if (getCurrentLevelNumber(context) < 400){
            return PUZZLE_SIZES.S65;

        } else if (getCurrentLevelNumber(context) < 600){
            return PUZZLE_SIZES.S66;
        }

        return PUZZLE_SIZES.S76;
    }


    /**
     * Get num movements
     */
    public void addMovesLeft(Activity context) {

        // Variables
        int numActions = getCurrentLevel().getNumActions();
        int levelNumber = getCurrentLevelNumber(context);
        int[] interval = new int[2];

        if (levelNumber < 2) { interval[0] = 1; interval[1] = 5; }
        else if (levelNumber < 3) { interval[0] = 0; interval[1] = 0; }
        else if (levelNumber < 6) { interval[0] = 1; interval[1] = 3; } // <= S32
        else if (levelNumber < 8) { interval[0] = 0; interval[1] = 0;}

        else if (levelNumber < 12) { interval[0] = 1; interval[1] = 4; }
        else if (levelNumber < 15) { interval[0] = 1; interval[1] = 2; } // S33
        else if (levelNumber < 20) { interval[0] = 0; interval[1] = 1; }

        else if (levelNumber < 25) { interval[0] = 1; interval[1] = 4; } // S43
        else if (levelNumber < 30) { interval[0] = 1; interval[1] = 3; }
        else if (levelNumber < 40) { interval[0] = 1; interval[1] = 2; }
        else if (levelNumber < 50) { interval[0] = 0; interval[1] = 1; }

        else if (levelNumber < 55) { interval[0] = 1; interval[1] = 5; } // S44
        else if (levelNumber < 60) { interval[0] = 1; interval[1] = 4; }
        else if (levelNumber < 70) { interval[0] = 1; interval[1] = 3; }
        else if (levelNumber < 80) { interval[0] = 1; interval[1] = 2; }
        else if (levelNumber < 90) { interval[0] = 0; interval[1] = 1; }

        else if (levelNumber < 95) { interval[0] = 1; interval[1] = 7; } // S54
        else if (levelNumber < 100) { interval[0] = 1; interval[1] = 6; }
        else if (levelNumber < 110) { interval[0] = 1; interval[1] = 5; }
        else if (levelNumber < 120) { interval[0] = 1; interval[1] = 4; }
        else if (levelNumber < 130) { interval[0] = 1; interval[1] = 3; }
        else if (levelNumber < 140) { interval[0] = 1; interval[1] = 2; }
        else if (levelNumber < 150) { interval[0] = 0; interval[1] = 1; }

        else if (levelNumber < 160) { interval[0] = 1; interval[1] = 8; }
        else if (levelNumber < 170) { interval[0] = 1; interval[1] = 7; }
        else if (levelNumber < 180) { interval[0] = 1; interval[1] = 6; }
        else if (levelNumber < 190) { interval[0] = 1; interval[1] = 5; }
        else if (levelNumber < 200) { interval[0] = 1; interval[1] = 4; } // S55
        else if (levelNumber < 210) { interval[0] = 1; interval[1] = 3; }
        else if (levelNumber < 220) { interval[0] = 1; interval[1] = 2; }
        else if (levelNumber < 230) { interval[0] = 0; interval[1] = 1; }

        else if (levelNumber < 250) { interval[0] = 1; interval[1] = 7; } // S65
        else if (levelNumber < 270) { interval[0] = 1; interval[1] = 6; }
        else if (levelNumber < 300) { interval[0] = 1; interval[1] = 5; }
        else if (levelNumber < 320) { interval[0] = 1; interval[1] = 4; }
        else if (levelNumber < 340) { interval[0] = 1; interval[1] = 3; }
        else if (levelNumber < 380) { interval[0] = 1; interval[1] = 2; }
        else if (levelNumber < 400) { interval[0] = 0; interval[1] = 1; }

        else if (levelNumber < 430) { interval[0] = 1; interval[1] = 6; } // S66
        else if (levelNumber < 450) { interval[0] = 1; interval[1] = 5; }
        else if (levelNumber < 490) { interval[0] = 1; interval[1] = 4; }
        else if (levelNumber < 530) { interval[0] = 1; interval[1] = 3; }
        else if (levelNumber < 580) { interval[0] = 1; interval[1] = 2; }
        else if (levelNumber < 600) { interval[0] = 0; interval[1] = 1; }

        else {
            interval[0] = 1;
            interval[1] = 5;
        }

        ORIGINAL_MOVES_NUM = numActions;
        numActions += (int) Math.round(Math.random() * (interval[1] - interval[0]) + interval[0]);
        setMovesLeft(numActions);
        NEW_MOVES_NUM = numActions;
    }


    /**
     * Get moves left
     */
    public int getMovesLeft() {
        return gameData.getMovesLeft();
    }

    /**
     * Get current level
     */
    public Level getCurrentLevel() {
        return gameData.getCurrentLevel();
    }

    /**
     * Get current level number
     */
    public int getCurrentLevelNumber(Activity context) {
        return gameData.getCurrentLevelNumber(context, currentGameMode);
    }

    /**
     * Get current level number depending on the mode
     */
    public int getCurrentLevelNumberDependingOnMode(Activity context, GameModes mode) {
        return gameData.getCurrentLevelNumber(context, mode);
    }

    /**
     * Set moves left
     */
    public void setMovesLeft(int movesLeft) {
        gameData.setMovesLeft(movesLeft);
    }


    /**
     * Set music enabled
     */
    public void setMusicEnabled(Activity context, boolean isEnabled) {
        gameData.setMusicEnabled(context, isEnabled);
    }

    /**
     * Get music enabled
     */
    public boolean isMusicEnabled(Activity context) {
        return gameData.isMusicEnabled(context);
    }

    /**
     * Set current level number
     */
    public void setCurrentLevel(Level currentLevel) {
        gameData.setCurrentLevel(currentLevel);
    }

    /**
     * Set current level number
     */
    public void setCurrentLevelNumber(Activity context, int currentLevelNumber) {
        gameData.setCurrentLevelNumber(context, currentLevelNumber, currentGameMode);
    }

    /**
     * Is next level ready?
     */
    public boolean isNextLevelReady() {
        return isNextLevelReady;
    }

    /**
     * Set next level ready
     */
    public void setNextLevelReady(boolean nextLevelReady) {
        isNextLevelReady = nextLevelReady;
    }

    /**
     * Get current game mode
     */
    public GameModes getCurrentGameMode() {
        return currentGameMode;
    }

    /**
     * Set current game mode
     */
    public void setCurrentGameMode(GameModes currentGameMode) {
        this.currentGameMode = currentGameMode;
    }

    public boolean isDragActive(Activity context) {
        return gameData.isDragActive(context);
    }

    public void setDragActive(Activity context, boolean isActive) {
        gameData.setDragActive(context, isActive);
    }

    /**
     * Generate level kind 1
     */
    public Level generateLevelType1(PUZZLE_SIZES size) {

        // Future generated level
        ArrayList<String[][]> generatedLevel;
        String [][] start;
        char [][] goal;

        // Array that's gonna hold the four solutions given for the four heuristics
        ArrayList<ArrayList<Action>> solutions = new ArrayList<>();

        // Variables
        boolean canStop = false;
        SearchProblem problem;
        long startTime, endTime, totalTime;

        // Repeat until at least one of heuristics returns a solution
        do {

            // Stop generator if use leaves from activity
            if (stopLevelGenerator) {
                return null;
            }

            // Generator objects
            LevelGenerator generator = new LevelGenerator(size);
            generatedLevel = generator.getGeneratedLevel(false);

            // Get start and goal puzzles
            start = generatedLevel.get(0);
            String [][] g = generatedLevel.get(1);
            goal = Util.toGoalColor(g);

            problem = new SearchProblem(start, goal);
            startTime = System.nanoTime();
            System.out.println("");
            problem.AStarSearchSolver(Heuristic.Kind.EUCLIDEAN);
            //problem.showResult();
            endTime = System.nanoTime();
            totalTime = (endTime - startTime) / 1000000;
            System.out.println("AStarSearch: " + totalTime + "ms");

            if (!problem.getSolution().isEmpty()) {
                solutions.add(problem.getSolution());
                canStop = true;
            }

            if (size != PUZZLE_SIZES.S65 && size != PUZZLE_SIZES.S55 && size != PUZZLE_SIZES.S66  && size != PUZZLE_SIZES.S76) {

                problem = new SearchProblem(start, goal);
                startTime = System.nanoTime();
                System.out.println("");
                problem.AStarSearchSolver(Heuristic.Kind.EUCLIDEAN_MATCHINGS);
                problem.showResult();
                endTime = System.nanoTime();
                totalTime = (endTime - startTime) / 1000000;
                System.out.println("AStarSearch: " + totalTime + "ms");

                if (!problem.getSolution().isEmpty()) {
                    solutions.add(problem.getSolution());
                    canStop = true;
                }

            }

        } while(!canStop);

        ArrayList<Action> bestSolution = LevelGenerator.getBestSolution(solutions);
        System.out.println("\nBest solution >> " + bestSolution.size());

        setNextLevelReady(true);
        resetMovesNumber();
        return new Level(start, goal, 1, bestSolution, size);
    }

    /**
     * Generate level kind 2
     */
    public Level generateLevelType2(GameController.PUZZLE_SIZES size) {

        // Future generated level
        ArrayList<String[][]> generatedLevel;
        String [][] start;

        // Array that's gonna hold the four solutions given for the four heuristics
        ArrayList<ArrayList<Action>> solutions = new ArrayList<>();

        // Variables
        boolean canStop = false;
        SearchProblem2 problem;
        long startTime, endTime, totalTime;

        // Repeat until at least one of heuristics returns a solution
        do {

            // Stop generator if use leaves from activity
            if (stopLevelGenerator) {
                return null;
            }

            // Generator objects
            LevelGenerator2 generator = new LevelGenerator2(size);

            // Generate a random level
            generatedLevel = generator.getGeneratedLevel(false);

            // Get start and goal puzzles
            start = generatedLevel.get(0);

            problem = new SearchProblem2(start);
            startTime = System.nanoTime();
            System.out.println("");
            problem.AStarSearchSolver(Heuristic2.Kind.CLUSTERING_NORMALIZED);
            //problem.showResult();
            endTime = System.nanoTime();
            totalTime = (endTime - startTime) / 1000000;
            System.out.println("AStarSearch Clustering Normalized: " + totalTime + "ms");
            if (!problem.getSolution().isEmpty()) {
                solutions.add(problem.getSolution());
                canStop = true;
            }

            if (!canStop  && size != PUZZLE_SIZES.S66  && size != PUZZLE_SIZES.S76) {

                problem = new SearchProblem2(start);
                startTime = System.nanoTime();
                System.out.println("");
                problem.AStarSearchSolver(Heuristic2.Kind.CLUSTERING);
                problem.showResult();
                endTime = System.nanoTime();
                totalTime = (endTime - startTime) / 1000000;
                System.out.println("AStarSearch Clustering: " + totalTime + "ms");
                if (!problem.getSolution().isEmpty()) {
                    solutions.add(problem.getSolution());
                    canStop = true;
                }

            }

        } while(!canStop);

        ArrayList<Action> bestSolution = LevelGenerator2.getBestSolution(solutions);
        System.out.println("\nBest solution >> " + bestSolution.size());

        setNextLevelReady(true);
        resetMovesNumber();
        return new Level(start, 2, bestSolution, size);
    }
}


