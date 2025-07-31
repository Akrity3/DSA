// A company's offices are connected by secure communication links, where:
// Each office is represented as a node.
// Each communication link is represented as an edge with a signal strength limit.
// Given a company network with 6 offices (nodes) and 5 communication links (edges), your task is to
// verify if a message can be securely transmitted from one office to another without exceeding the
// maximum signal strength.
// Graph Representation from the Image
// Offices (Nodes): {0, 1, 2, 3, 4, 5}
// Communication Links (Edges with Strengths):
// 0 ↔ 2 (Strength: 4)
// 2 ↔ 3 (Strength: 1)
// 2 ↔ 1 (Strength: 3)
// 4 ↔ 5 (Strength: 5)
// 3 ↔ 2 (Strength: 2)
// Class Implementation
// Implement the SecureTransmission class:
// SecureTransmission(int n, int[][] links)
// Initializes the system with n offices and a list of communication links.
// Each link is represented as [a, b, strength], indicating an undirected connection between office a and
// office b with a signal strength of strength.
// boolean canTransmit(int sender, int receiver, int maxStrength)
// Returns true if there exists a path between sender and receiver, where all links on the path have a
// strength strictly less than maxStrength.
// Otherwise, returns false.
// Example

// [figure ]

// Input:
// ["SecureTransmission", "canTransmit", "canTransmit", "canTransmit", "canTransmit"]
// [[6, [[0, 2, 4], [2, 3, 1], [2, 1, 3], [4, 5, 5]]], [2, 3, 2], [1, 3, 3], [2, 0, 3], [0, 5, 6]]
// Output: [null, true, false, true, false]
// Explanation
// SecureTransmission(6, [[0, 2, 4], [2, 3, 1], [2, 1, 3], [4, 5, 5]])
// Initializes a network with 6 offices and 4 communication links.
// canTransmit(2, 3, 2) → true
// A direct link 2 → 3 exists with strength 1, which is less than 2.
// canTransmit(1, 3, 3) → false
// 1 → 2 has strength 3, which is not strictly less than 3, so transmission fails.
// canTransmit(2, 0, 3) → true
// 2 → 3 → 0 is a valid path.
// Links (2 → 3) and (3 → 0) have strengths 1, 2 (both < 3), so successful transmission.
// canTransmit(0, 5, 6) → false
// There is no connection between 0 and 5, so transmission fails


import java.util.*;

/**
 * SecureTransmission models a network of company offices connected with secure communication links.
 * Each link has a maximum allowed signal strength limit.
 * 
 * This class supports querying if a message can be securely transmitted from one office to another,
 * such that all communication links on the path have strengths strictly less than a specified limit.
 */
public class SecureTransmission {
    private int n;  // number of offices
    private List<List<int[]>> graph;  // adjacency list: each edge as {neighbor, strength}

    /**
     * Initializes the network with n offices and the communication links.
     * 
     * @param n number of offices
     * @param links each link represented as [a, b, strength] for an undirected edge
     */
    public SecureTransmission(int n, int[][] links) {
        this.n = n;
        this.graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] link : links) {
            int a = link[0], b = link[1], strength = link[2];
            // Add undirected edge between a and b with given strength
            graph.get(a).add(new int[]{b, strength});
            graph.get(b).add(new int[]{a, strength});
        }
    }

    /**
     * Checks if a message can be transmitted from sender to receiver such that
     * all edges in the path have strength strictly less than maxStrength.
     * 
     * Uses BFS for path search with edge strength constraint.
     *
     * @param sender source office
     * @param receiver target office
     * @param maxStrength maximum allowed edge strength (exclusive)
     * @return true if such a path exists, false otherwise
     */
    public boolean canTransmit(int sender, int receiver, int maxStrength) {
        if (sender == receiver) {
            return true;  // trivial case: same office
        }

        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(sender);
        visited[sender] = true;

        // BFS traversal with edge strength constraint
        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int[] edge : graph.get(current)) {
                int neighbor = edge[0];
                int strength = edge[1];

                if (!visited[neighbor] && strength < maxStrength) {
                    if (neighbor == receiver) {
                        return true;
                    }

                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }

        return false;  // no path found under the constraint
    }

    // Test the implementation with examples and additional test cases
    public static void main(String[] args) {
        // Original example as in the question
        int[][] links = {
            {0, 2, 4},
            {2, 3, 1},
            {2, 1, 3},
            {4, 5, 5}
        };
        SecureTransmission st = new SecureTransmission(6, links);

        System.out.println("Original test cases:");
        System.out.println("canTransmit(2, 3, 2): " + st.canTransmit(2, 3, 2)); // Expected: true (edge 2-3 has strength 1 < 2)
        System.out.println("canTransmit(1, 3, 3): " + st.canTransmit(1, 3, 3)); // Expected: false (edge 1-2 is 3, not < 3)
        System.out.println("canTransmit(2, 0, 3): " + st.canTransmit(2, 0, 3)); // Expected: false (edge 0-2 is 4, no path under 3)
        System.out.println("canTransmit(0, 5, 6): " + st.canTransmit(0, 5, 6)); // Expected: false (no connection between these components)

        System.out.println("\nAdditional test cases:");

        // Test case: direct connection with low strength
        System.out.println("canTransmit(4, 5, 6): " + st.canTransmit(4, 5, 6)); // Expected: true (edge strength 5 < 6)

        // Test case: same node
        System.out.println("canTransmit(0, 0, 1): " + st.canTransmit(0, 0, 1)); // Expected: true

        // Test case: disconnected nodes
        System.out.println("canTransmit(1, 4, 10): " + st.canTransmit(1, 4, 10)); // Expected: false

        // Test case: path involving multiple edges under maxStrength
        // Add additional edges to form a path from 1 -> 3: Add edge 1 - 4 (strength 2), 4 - 3 (strength 2)
        int[][] extendedLinks = {
            {0, 2, 4},
            {2, 3, 1},
            {2, 1, 3},
            {4, 5, 5},
            {1, 4, 2},
            {4, 3, 2}
        };
        SecureTransmission st2 = new SecureTransmission(6, extendedLinks);

        System.out.println("\nExtended graph test cases:");
        System.out.println("canTransmit(1, 3, 3) with extended graph: " + st2.canTransmit(1, 3, 3)); 
        // Expected: true because path 1 -> 4 (2), 4 -> 3 (2) - both < 3

        System.out.println("canTransmit(1, 3, 2) with extended graph: " + st2.canTransmit(1, 3, 2)); 
        // Expected: false because edges have strength 2, which is not < 2

        // Edge case: maxStrength very high -> all paths allowed except non-connected
        System.out.println("canTransmit(0, 5, 10): " + st2.canTransmit(0, 5, 10)); 
        // Expected: true if connected, but 0 and 5 belong to different components with no connecting edges
        // In extendedLinks, 1 is now connected to 4 and 4 to 3, still no link to 0->5, so false
    }
}




/*
 * ----Output---
Original test cases:
canTransmit(2, 3, 2): true
canTransmit(1, 3, 3): false
canTransmit(2, 0, 3): false
canTransmit(0, 5, 6): false

Additional test cases:
canTransmit(4, 5, 6): true
canTransmit(0, 0, 1): true
canTransmit(1, 4, 10): false

Extended graph test cases:
canTransmit(1, 3, 3) with extended graph: true
canTransmit(1, 3, 2) with extended graph: false
canTransmit(0, 5, 10): true
 */