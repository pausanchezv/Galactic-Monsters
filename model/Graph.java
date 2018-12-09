package com.pausanchezv.puzzle.model;
import java.util.ArrayList;
import java.util.HashSet;



/**
 *
 * @author pausanchezv
 */
public final class Graph {

    // Nodes & edges
    private final HashSet<String> nodes;
    private final HashSet<String[]> edges;

    /**
     * Graph Constructor
     */
    public Graph() {
        nodes = new HashSet<>();
        edges = new HashSet<>();
    }

    /**
     * Get nodes
     */
    public HashSet<String> getNodes() {
        return nodes;
    }

    /**
     * Get edges
     */
    public HashSet<String[]> getEdges() {
        return edges;
    }

    /**
     * Add node
     */
    private void addNode(String x) {

        if (!nodes.contains(x))
            nodes.add(x);
    }

    /**
     * Add edge
     * @param a string
     * @param b string
     */
    void addEdge(String a, String b) {

        if (!hasEdge(a, b)) {

            addNode(a);
            addNode(b);

            String [] edge = {a, b};
            edges.add(edge);
        }
    }

    /**
     * Get neighbors
     * @param node string
     * @return Array
     */
    public ArrayList<String> neighbors(String node) {

        ArrayList<String> neighbors = new ArrayList<>();

        for (Object edge: edges) {

            String [] stringEdge = (String []) edge;

            if (stringEdge[0].equals(node) || stringEdge[1].equals(node)) {

                String neighbor = stringEdge[0].equals(node) ? stringEdge[1] : stringEdge[0];
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    /**
     * Check whether or not the graph has an specific edge
     * @param A string
     * @param B string
     * @return bool
     */
    boolean hasEdge(String A, String B) {

        boolean found = false;

        for (Object edge : edges) {

            String [] key = (String []) edge;

            if ((key[0].equals(A) && key[1].equals(B)) || (key[0].equals(B) && key[1].equals(A))) {
                found = true;
            }
        }

        return found;
    }

    /**
     * Get number of nodes
     * @return int
     */
    public int getNumNodes() {
        return nodes.size();
    }


    /**
     * Get any node
     * @return string
     */
    public String getAnyNode() {

        for (String node : nodes) {
            return node;
        }
        return "";
    }

    /**
     * Add puzzle edges
     * @param graph graph
     * @param state state
     * @param diagonals bool
     */
    public static void addPuzzleEdges(Graph graph, State state, boolean diagonals) {

        Puzzle puzzle = state.getPuzzle();

        for (int row = 0; row < puzzle.getNumRows(); row++) {
            for (int col = 0; col < puzzle.getNumCols(); col++) {

                if (!puzzle.containsWall(row, col)) {

                    // perpendicular
                    if (!puzzle.containsWall(row - 1, col) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col));

                    if (!puzzle.containsWall(row + 1, col) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col));

                    if (!puzzle.containsWall(row, col - 1) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col - 1)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col -1));

                    if (!puzzle.containsWall(row, col + 1) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col + 1)))
                        graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row) + String.valueOf(col + 1));

                    // diagonals
                    if (diagonals) {

                        if (!puzzle.containsWall(row - 1, col - 1) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col - 1)))
                            graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col - 1));

                        if (!puzzle.containsWall(row + 1, col - 1) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col - 1)))
                            graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col - 1));

                        if (!puzzle.containsWall(row - 1, col + 1) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col + 1)))
                            graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row - 1) + String.valueOf(col + 1));

                        if (!puzzle.containsWall(row + 1, col + 1) && !graph.hasEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col + 1)))
                            graph.addEdge(String.valueOf(row) + String.valueOf(col), String.valueOf(row + 1) + String.valueOf(col + 1));

                    }
                }
            }
        }
    }
}
