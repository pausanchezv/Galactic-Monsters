package com.pausanchezv.puzzle.model.puzzle1;

import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.model.Util;
import com.pausanchezv.puzzle.model.Puzzle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import com.pausanchezv.puzzle.model.Action;
import com.pausanchezv.puzzle.model.Distribution;

/**
 *
 * @author pausanchezv
 */
public class LevelGenerator {

    // Difficulty level
    private final GameController.PUZZLE_SIZES size;

    /**
     * LevelGenerator Constructor
     */
    public LevelGenerator(GameController.PUZZLE_SIZES size) {
        this.size = size;
    }

    /**
     * Get puzzle size
     */
    private int [] getPuzzleSize() {
        return Util.getPuzzleSize(this.size);
    }

    /**
     * Get puzzle colors depending on the puzzle size
     */
    private ArrayList<String> getPuzzleColors(int[] size) {

        ArrayList<String> colors = new ArrayList<>();
        char [] genericColors = {'R', 'B', 'Y', 'G', 'O', 'M'};
        int numRows = size[0];
        int numCols = size[1];
        int puzzleSize = numRows * numCols;
        int numColors;

        // Assign the amount of colors depending on the size of the puzzle
        if (puzzleSize >= 25) numColors = (int) Math.round(Math.random() * (6 - 2) + 2);
        else if (puzzleSize >= 20) numColors = (int) Math.round(Math.random() * (5 - 2) + 2);
        else if (puzzleSize > 15) numColors = (int) Math.round(Math.random() * (4 - 2) + 2);
        else if (puzzleSize > 8) numColors = (int) Math.round(Math.random() * (3 - 2) + 2);
        else numColors = 2;

        // Fill the color array
        while (colors.size() < numColors) {

            // Generate a random color
            char color = genericColors[(int) Math.round((Math.random() * (genericColors.length - 1)))];

            // Add to array whether it's not contained
            if (!colors.contains(String.valueOf(color))) {
                colors.add(String.valueOf(color));
            }
        }

        // Error if there are less than two colors
        if (colors.isEmpty()) {
            System.err.println("Error number of colors!");
            System.exit(1);
        }

        return colors;
    }

    /**
     * Get random scope of characters depending on a simple random number
     */
    public String getCharacterScope() {

        // Get a random number between 0-100
        int randNumCharacter = (int) Math.round(Math.random() * (100 - 1));
        int scope;

        switch (this.size) {

            case S22:
            case S32:
                scope = 1;
                break;

            case S33:
                scope = randNumCharacter < 20 ? 2 : 1;
                break;

            case S43:
                scope = randNumCharacter < 30 ? 2 : 1;
                break;

            case S44:
                scope = randNumCharacter < 50 ? 2 : 1;
                break;

            case S54:
                scope = randNumCharacter < 25 ? 3 : randNumCharacter < 65 ? 2 : 1;
                break;

            case S55:
                scope = randNumCharacter < 10 ? 1 : randNumCharacter < 30 ? 2 : 3;
                break;

            default:
                scope = 3;
                break;

        }

        return "6";//String.valueOf(scope);
    }

    /**
     * Add color to puzzle
     */
    private boolean addCharacterColor(ArrayList<String> puzzleArray, ArrayList<String> colors) {

        // Get the number of involved colors
        int numColors = colors.size();


        // Equitable amount of colors
        int colorsPosition = (int) Math.ceil((puzzleArray.size()) / numColors);
        int colorsStatic = colorsPosition;
        int numColor = 0;



        // Assign the color depending on the color position and the number of the current color
        for (int i = 0; i < puzzleArray.size(); i++) {

            // Changing the color whether the index is greater than the color-position
            if (i >= colorsPosition) {
                colorsPosition += colorsStatic;

                if (numColor < numColors -1)
                    numColor++;
            }

            // Add the color to the square
            if (!puzzleArray.get(i).equals(Puzzle.WALL) && !puzzleArray.get(i).equals(Puzzle.BLANK)) {
                puzzleArray.set(i, puzzleArray.get(i).concat(colors.get(numColor)));
            }
        }

        return checkColorsLength(puzzleArray);
    }

    /**
     * Check whether or not the amount of color is OK
     */
    private boolean checkColorsLength(ArrayList<String> array) {

        ArrayList<String> arrayCont = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            if (!array.get(i).equals(Puzzle.WALL) && !array.get(i).equals(Puzzle.BLANK)) {

                if (!arrayCont.contains(String.valueOf(array.get(i).charAt(2)))) {

                    arrayCont.add(String.valueOf(array.get(i).charAt(2)));
                }
            }
        }

        return arrayCont.size() > 1;
    }

    /**
     * Get generated array
     */
    private ArrayList<String> getGeneratedArray(Distribution distribution, ArrayList<String> colors, int numRows, int numCols) {

        // get the size and generate an empty array that's gonna hold the generated puzzle
        ArrayList<String> puzzleArray = new ArrayList<>();
        int size = numRows * numCols;

        // Get the number of blocks
        int numBlocks = Math.round(distribution.getBlockPercentage() * size / 100);

        // Get the rest of characters' amount
        int numQueens = Math.round(distribution.getQueenPercentage() * size / 100);
        int numBishops = Math.round(distribution.getBishopPercentage() * size / 100);
        int numTowers = size - numBlocks - numQueens - numBishops;

        // Add the characters to the puzzle
        for (int i = 0; i < numBlocks; i++) puzzleArray.add(Puzzle.WALL);
        for (int i = 0; i < numQueens; i++) puzzleArray.add("Q" + getCharacterScope());
        for (int i = 0; i < numBishops; i++) puzzleArray.add("B" + getCharacterScope());
        for (int i = 0; i < numTowers; i++) puzzleArray.add("T" + getCharacterScope());

        // Shuffle the puzzle before adding the colors
        Collections.shuffle(puzzleArray);

        // Add colors to the puzzle
        if (!addCharacterColor(puzzleArray, colors)) {

            // TODO: Test
            System.err.println("Recursion Color");
            int [] aSize = {numRows, numCols};
            ArrayList<String> newColors = getPuzzleColors(aSize);
            return getGeneratedArray(distribution, newColors, numRows, numCols);
        }

        // Shuffle the puzzle before adding the colors
        Collections.shuffle(puzzleArray);

        return puzzleArray;
    }

    /**
     * Get 2-dimensional puzzle from array
     */
    private String [][] getPuzzleFromArray(ArrayList<String> array, int numRows, int numCols) {

        // Create the array that's gonna hold the puzzle
        String [][] puzzle = new String[numRows][numCols];
        int offset = 0;

        // Fill the array with sub-arrays depending on the column size
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                puzzle[row][col] = array.get(offset++);
            }
        }

        return puzzle;
    }


    /**
     * Get goal array
     */
    private ArrayList<String> getGoalArray(ArrayList<String> array) {

        // Arrays
        ArrayList<String> arrayColor;
        ArrayList<String> cloneColor;
        ArrayList<String> clone;

        do {

            // Get a clone of start array
            clone = (ArrayList<String>) array.clone();

            // Shuffling the clone but the blocks
            Util.arrayShuffleWithNeedle(clone, Puzzle.WALL);

            // Get the array colors
            arrayColor = Util.getArrayColor(array);
            cloneColor = Util.getArrayColor(clone);

        } while (Util.arrayListEquals(arrayColor, cloneColor));

        // Goal array
        ArrayList<String> goal = new ArrayList<>();

        // Fill the goal array out
        for (int i = 0; i < array.size(); i++) {
            goal.add(String.valueOf(clone.get(i).charAt(2)));
        }

        return goal;
    }

    /**
     * Show console execution
     */
    private void showLevelFeatures(
            int rows,
            int cols,
            ArrayList<String> colors,
            Distribution distribution,
            ArrayList<String> array,
            String[][] puzzle,
            String[][] goal,
            int numRecursiveCalls
    ) {
        System.out.println("");
        System.out.println("Level's kind: Absolutely random");
        System.out.println("Puzzle distribution percentages:");
        System.out.println("Queen percentage: " + distribution.getQueenPercentage() + "%");
        System.out.println("Bishop percentage: " + distribution.getBishopPercentage() + "%");
        System.out.println("Tower percentage: " + distribution.getTowerPercentage() + "%");
        System.out.println("Block percentage: " + distribution.getBlockPercentage() + "%");
        System.out.println("");
        System.out.println("Number of rows: " + rows);
        System.out.println("Number of columns: " + cols);
        System.out.println("");
        System.out.println("Is connected: true");
        System.out.println("Number of recursive calls needed: " + numRecursiveCalls);
        System.out.println("");
        System.out.println("Colors (" + colors.size() + "): "  + colors);
        System.out.println("Puzzle as array: " + array);
        System.out.println("");
        System.out.println("Start state:");

        for (String[] x : puzzle) {
            System.out.println(Arrays.toString(x));
        }
        System.out.println("");
        System.out.println("Goal state:");

        for (String[] x : goal) {
            System.out.println(Arrays.toString(x));
        }
        System.out.println("");
    }


    /**
     * Generate the level
     */
    public ArrayList<String [][]> getGeneratedLevel(boolean showFeatures) {

        // Get a distribution puzzle
        Distribution distribution = getPuzzleDistribution();

        // Get the puzzle size depending on the difficulty
        int [] size = getPuzzleSize();

        // get the colors depending on the size which depends on the difficulty
        ArrayList<String>colors = this.getPuzzleColors(size);

        String [][] puzzle;
        ArrayList<String> puzzleArray;

        // Counting the necessary recursive calls
        int numRecursiveCalls = 0;

        do {

            puzzleArray = getGeneratedArray(distribution, colors, size[0], size[1]);
            puzzle = getPuzzleFromArray(puzzleArray, size[0], size[1]);
            numRecursiveCalls++;

        } while(!SearchAlgorithms.isConnectedPuzzle(puzzle));

        // Get the goal array
        ArrayList<String> goalArray = this.getGoalArray(puzzleArray);

        // Convert the goal array into a matrix
        String [][] goal = getPuzzleFromArray(goalArray, size[0], size[1]);

        // Show the computes
        if (showFeatures) {
            this.showLevelFeatures(size[0], size[1], colors, distribution, puzzleArray, puzzle, goal, numRecursiveCalls);
        }

        // Level
        ArrayList<String[][]> level = new ArrayList<>();
        level.add(puzzle);
        level.add(goal);

        return level;
    }

    /**
     * Get one of the predetermined possible distributions
     */
    private Distribution getPuzzleDistribution() {

        // Array that's gonna hold the distributions
        ArrayList<Distribution> distributions = new ArrayList<>();

        // Distributions
        switch (this.size) {

            case S21:
            case S22:
                distributions.add(new Distribution(0, 0, 0));
                break;

            case S32:

                distributions.add(new Distribution(0, 0, 0));
                distributions.add(new Distribution(25, 0, 0));
                break;

            case S33:

                distributions.add(new Distribution(0, 15, 0));
                distributions.add(new Distribution(15, 25, 0));
                distributions.add(new Distribution(15, 15, 0));
                distributions.add(new Distribution(0, 25, 0));
                distributions.add(new Distribution(25, 15, 0));
                break;

            case S43:

                distributions.add(new Distribution(0, 15, 0));
                distributions.add(new Distribution(10, 10, 0));
                distributions.add(new Distribution(20, 20, 0));
                distributions.add(new Distribution(30, 15, 0));
                break;

            case S44:
            case S54:
                distributions.add(new Distribution(0, 10, 10));
                distributions.add(new Distribution(10, 25, 10));
                distributions.add(new Distribution(20, 10, 15));
                distributions.add(new Distribution(30, 0, 10));
                distributions.add(new Distribution(10, 15, 0));
                distributions.add(new Distribution(25, 0, 35));
                distributions.add(new Distribution(40, 10, 10));
                break;

            default:
                distributions.add(new Distribution(0, 10, 10));
                distributions.add(new Distribution(10, 25, 10));
                distributions.add(new Distribution(20, 10, 15));
                distributions.add(new Distribution(30, 10, 20));
                distributions.add(new Distribution(40, 0, 35));
                distributions.add(new Distribution(60, 10, 20));
                distributions.add(new Distribution(5, 20, 0));
                distributions.add(new Distribution(15, 10, 10));
                distributions.add(new Distribution(5, 0, 75));
                break;
        }

        int randNum = (int) Math.round(Math.random() * ((distributions.size() - 1)));

        return distributions.get(randNum);
    }

    /**
     * Get best solution
     * @param solutions solutions
     * @return array
     */
    public static ArrayList<Action> getBestSolution(ArrayList<ArrayList<Action>> solutions) {

        int bestSize = (int) Double.POSITIVE_INFINITY;
        ArrayList<Action> bestSolution = null;

        for (ArrayList<Action> solution: solutions) {

            if (solution.size() < bestSize && solution.size() > 0) {
                bestSize = solution.size();
                bestSolution = solution;
            }
        }
        return bestSolution;
    }
}
