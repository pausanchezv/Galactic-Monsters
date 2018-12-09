package com.pausanchezv.puzzle.model.puzzle2;

import java.util.ArrayList;
import java.util.HashMap;
import com.pausanchezv.puzzle.model.Graph;
import com.pausanchezv.puzzle.model.Puzzle;
import com.pausanchezv.puzzle.model.State;
import com.pausanchezv.puzzle.model.Util;

/**
 *
 * @author pausanchezv
 */
public final class Heuristic2 {

    // Types of heuristics
    public enum Kind {CLUSTERING, CLUSTERING_NORMALIZED, GRAPHS, GRAPHS_NORMALIZED}


    /**
     * Matches Heuristic
     */
    private static float manhattanDistance(int[] a, int[] b) {
        return (float) (Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]));
    }

    /**
     * Clustering heuristic by using the Manhattan distance normalized
     */
    static float clusteringHeuristicNormalized(State state) {
        return clusteringHeuristic(state, true);
    }

    /**
     * Clustering heuristic by using the Manhattan distance
     */
    static float clusteringHeuristic(State state, boolean normalize) {

        // total distances to will be returned
        float totalDistances = 0.0f;

        try {
            // Objects
            Puzzle puzzle = state.getPuzzle();
            Graph graphByColor = puzzle.getGraphByColor();//puzzle.getGraphByColor();
            char[][] matrixColor = puzzle.getColor().getColor();
            int totalNumClusters = 0;
            int totalClustersSize = 0;

            // Structures
            ArrayList<String> visited = new ArrayList<>();
            HashMap<String, ArrayList<String>> bestClusters = new HashMap<>();

            // Prepare the dictionary with the colors of the puzzle
            for (int c = 0; c < Puzzle.colorArray.size(); c++) {
                bestClusters.put(Puzzle.colorArray.get(c), new ArrayList<String>());
            }

            // Finding the best clusters
            for (int row = 0; row < puzzle.getNumRows(); row++) {
                for (int col = 0; col < puzzle.getNumCols(); col++) {

                    // Color & node from square
                    String color = String.valueOf(matrixColor[row][col]);
                    String node = String.valueOf(row) + String.valueOf(col);

                    // check whether or not the distance has to be computed
                    if (!color.equals("#") && !visited.contains(node)) {

                        ArrayList<String> cluster = SearchAlgorithms2.depthFirstSearch(graphByColor, node);
                        totalNumClusters++;

                        // Visited extend
                        for (String square : cluster) {
                            if (!visited.contains(square)) {
                                visited.add(square);
                            }
                        }

                        // Update the best cluster
                        if (cluster.size() > bestClusters.get(color).size()) {
                            bestClusters.put(color, cluster);
                        }
                    }
                }
            }

            for (HashMap.Entry pair : bestClusters.entrySet()) {

                // get the cluster
                ArrayList<String> cluster = (ArrayList<String>) pair.getValue();
                totalClustersSize += cluster.size();

                // traverse matrix
                for (int row = 0; row < puzzle.getNumRows(); row++) {
                    for (int col = 0; col < puzzle.getNumCols(); col++) {

                        // Color & node from square
                        String color = String.valueOf(matrixColor[row][col]);
                        String node = String.valueOf(row) + String.valueOf(col);

                        if (color.equals(pair.getKey()) && !cluster.contains(node)) {

                            // init best distance
                            float bestDistance = Float.POSITIVE_INFINITY;

                            // traverse the cluster
                            for (String obj : cluster) {

                                int[] A = {row, col};
                                int[] B = {Integer.parseInt(String.valueOf(obj.charAt(0))), Integer.parseInt(String.valueOf(obj.charAt(1)))};

                                // compute distance
                                float distance = manhattanDistance(A, B);

                                // update best distance
                                if (distance < bestDistance) {
                                    bestDistance = distance - 1;
                                }
                            }

                            // update best distances
                            totalDistances += bestDistance;

                        }
                    }
                }
            }

            // normalize the value if it's needed
            if (normalize) {
                totalDistances = (float) (0.5 * totalDistances - 0.3 * totalClustersSize + 0.2 * totalNumClusters);
            }

        } catch (Exception e) {
            totalDistances = Float.POSITIVE_INFINITY;
        }

        return totalDistances;

    }

    /**
     * Clustering heuristic by using the Manhattan distance normalized
     */
    static float clusteringHeuristicGraphNormalized(State state) {
        return clusteringHeuristicGraph(state, true);
    }

    /**
     * Clustering heuristic by using the Manhattan distance
     */
    static float clusteringHeuristicGraph(State state, boolean normalize) {

        // total distances to will be returned
        float totalDistances = 0.0f;

        try {

            // Objects
            Puzzle puzzle = state.getPuzzle();
            Graph graphByColor = puzzle.getGraphByColor();
            Graph graph = puzzle.getGraph();
            char[][] matrixColor = puzzle.getColor().getColor();
            int totalNumClusters = 0;
            int totalClustersSize = 0;

            // Structures
            ArrayList<String> visited = new ArrayList<>();
            HashMap<String, ArrayList<String>> bestClusters = new HashMap<>();

            // Prepare the dictionary with the colors of the puzzle
            for (int c = 0; c < Puzzle.colorArray.size(); c++) {
                bestClusters.put(Puzzle.colorArray.get(c), new ArrayList<String>());
            }

            // Finding the best clusters
            for (int row = 0; row < puzzle.getNumRows(); row++) {
                for (int col = 0; col < puzzle.getNumCols(); col++) {

                    // Color & node from square
                    String color = String.valueOf(matrixColor[row][col]);
                    String node = String.valueOf(row) + String.valueOf(col);

                    // check whether or not the distance has to be computed
                    if (!color.equals("#") && !visited.contains(node)) {

                        ArrayList<String> cluster = SearchAlgorithms2.depthFirstSearch(graphByColor, node);
                        totalNumClusters++;

                        // Visited extend
                        for (String square : cluster) {
                            if (!visited.contains(square)) {
                                visited.add(square);
                            }
                        }

                        // Update the best cluster
                        if (cluster.size() > bestClusters.get(color).size()) {
                            bestClusters.put(color, cluster);
                        }
                    }
                }
            }


            for (HashMap.Entry pair : bestClusters.entrySet()) {

                // get the cluster
                ArrayList<String> cluster = (ArrayList<String>) pair.getValue();
                totalClustersSize += cluster.size();

                // traverse matrix
                for (int row = 0; row < puzzle.getNumRows(); row++) {
                    for (int col = 0; col < puzzle.getNumCols(); col++) {

                        // Color & node from square
                        String color = String.valueOf(matrixColor[row][col]);
                        String node = String.valueOf(row) + String.valueOf(col);

                        if (color.equals(pair.getKey()) && !cluster.contains(node)) {

                            // init best distance
                            float bestDistance = Float.POSITIVE_INFINITY;

                            // traverse the cluster
                            for (String obj : cluster) {

                                ArrayList<String> path = SearchAlgorithms2.breadthFirstSearch(graph, node, obj);
                                float distance = (float) path.size();

                                // update best distance
                                if (distance < bestDistance) {
                                    bestDistance = distance - 1;
                                }
                            }

                            // update best distances
                            totalDistances += bestDistance;

                        }
                    }
                }
            }

            // normalize the value if it's needed
            if (normalize) {
                totalDistances = (float) (0.5 * totalDistances - 0.3 * totalClustersSize + 0.2 * totalNumClusters);
            }

        } catch (Exception e) {
            totalDistances = Float.POSITIVE_INFINITY;
        }

        return totalDistances;
    }
}
