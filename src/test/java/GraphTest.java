import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    // Helper method to create test graph files
    private String createTestGraphFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("graph_test_", ".txt");
        Files.write(tempFile, content.getBytes());
        return tempFile.toString();
    }

    // Helper method to generate a random graph
    private String randomGraphWithNegativeEdges(int nodes, int edges) {
        Random rand = new Random(12345); // Fixed seed for reproducibility
        StringBuilder sb = new StringBuilder();
        sb.append(nodes).append(" ").append(edges).append("\n");

        for (int i = 0; i < edges; i++) {
            int from = rand.nextInt(nodes);
            int to = rand.nextInt(nodes);
            // Use mostly positive weights, but occasionally add negative weights for Bellman-Ford testing
            int weight = rand.nextInt(100) - (rand.nextInt(10) == 0 ? 5 : 0);
            sb.append(from).append(" ").append(to).append(" ").append(weight).append("\n");
        }

        return sb.toString();
    }

    private String randomGraphWithPositiveEdgesOnly(int nodes, int edges) {
        Random rand = new Random(12345); // Fixed seed for reproducibility
        StringBuilder sb = new StringBuilder();
        sb.append(nodes).append(" ").append(edges).append("\n");

        for (int i = 0; i < edges; i++) {
            int from = rand.nextInt(nodes);
            int to = rand.nextInt(nodes);
            int weight = rand.nextInt(100);
            sb.append(from).append(" ").append(to).append(" ").append(weight).append("\n");
        }

        return sb.toString();
    }

    private long measureExecutionTime(Runnable function) {
        long startTime = System.nanoTime();
        function.run();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    @Test
    public void testDijkstraCorrectness() throws IOException {
        String graphData = """
                6 8
                0 1 2
                0 2 4
                1 2 1
                1 3 7
                2 4 3
                3 5 1
                4 3 2
                4 5 5
                """;

        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[] costs = new int[graph.size()];
        int[] parents = new int[graph.size()];

        graph.dijkstra(0, costs, parents);

        // Expected shortest path costs from node 0
        int[] expectedCosts = {0, 2, 3, 8, 6, 9};
        assertArrayEquals(expectedCosts, costs, "Dijkstra shortest path costs are incorrect");

        // Validate the path from source to node 5
        int [] expectedParents = {-1,0,1,4,2,3};
        assertArrayEquals(parents, expectedParents, "parents array is incorrect");
    }

    @Test
    public void testDijkstraFailureInNegativeEdges() throws IOException {
        String graphData = """
                5 8
                0 1 6
                0 3 7
                1 2 5
                1 3 8
                1 4 -4
                2 1 -2
                3 2 -3
                3 4 9
                """;

        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[] costs = new int[graph.size()];
        int[] parents = new int[graph.size()];

        graph.dijkstra(0, costs, parents);

        // Expected shortest path costs from node 0
        int[] expectedCosts = {0, 2, 4, 7, -2};
        assertFalse(Arrays.equals(expectedCosts, costs), "Dijkstra shortest path costs are incorrect for negative edges");
    }

    @Test
    public void testBellmanFordCorrectness() throws IOException {
        String graphData = """
                5 8
                0 1 6
                0 3 7
                1 2 5
                1 3 8
                1 4 -4
                2 1 -2
                3 2 -3
                3 4 9
                """;

        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[] costs = new int[graph.size()];
        int[] parents = new int[graph.size()];

        boolean result = graph.bellmanFord(0, costs, parents);

        assertTrue(result, "Bellman-Ford should not detect negative cycle");

        // Expected shortest path costs from node 0
        int[] expectedCosts = {0, 2, 4, 7, -2};
        int[] expectedParents = {-1, 2, 3, 0, 1};
        assertArrayEquals(expectedCosts, costs, "Bellman-Ford shortest path costs are incorrect");
        assertArrayEquals(expectedParents, parents, "Bellman-Ford parents array is incorrect");
        // Create a graph with a negative cycle
        String graphWithNegativeCycle = """
                4 4
                0 1 1
                1 2 2
                2 3 3
                3 0 -10
                """;

        String cycleFilePath = createTestGraphFile(graphWithNegativeCycle);
        Graph graphWithCycle = new Graph(cycleFilePath);

        result = graphWithCycle.bellmanFord(0, costs, parents);

        assertFalse(result, "Bellman-Ford should detect negative cycle");
    }

    @Test
    public void testFloydWarshallCorrectness() throws IOException {
        String graphData = """
                5 10
                0 1 6
                0 3 7
                1 2 5
                1 3 8
                1 4 -4
                2 1 -2
                3 0 -1
                3 2 -3
                3 4 9
                4 1 10
                """;

        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[][] costs = new int[graph.size()][graph.size()];
        int[][] predecessors = new int[graph.size()][graph.size()];

        boolean result = graph.floydWarshall(costs, predecessors);

        assertTrue(result, "Floyd-Warshall should not detect negative cycle");

        // Expected shortest path costs matrix
        int[][] expectedCosts = {
                {0, 2, 4, 7, -2},
                {7, 0, 5, 8, -4},
                {5, -2, 0, 6, -6},
                {-1, -5, -3, 0, -9},
                {17, 10, 15, 18, 0}
        };

        // Expected predecessors matrix
        int[][] expectedPredecessors = {
                {-1, 2, 3, 0, 1},
                {3, -1, 1, 1, 1},
                {3, 2, -1, 1, 1},
                {3, 2, 3, -1, 1},
                {3, 4, 1, 1, -1}
        };

        // Check if costs are calculated correctly
        for (int i = 0; i < graph.size(); i++) {
            assertArrayEquals(expectedCosts[i], costs[i], "Floyd-Warshall shortest path costs are incorrect");
        }

        // Check if predecessors are calculated correctly
        for (int i = 0; i < graph.size(); i++) {
            assertArrayEquals(expectedPredecessors[i], predecessors[i], "Floyd-Warshall predecessors are incorrect");
        }

        // Create a graph with a negative cycle
        String graphWithNegativeCycle = """
                5 10
                0 1 6
                0 3 7
                1 2 5
                1 3 8
                1 4 -4
                2 1 -2
                3 0 -1
                3 2 -3
                3 4 9
                4 1 2
                """;

        String cycleFilePath = createTestGraphFile(graphWithNegativeCycle);
        Graph graphWithCycle = new Graph(cycleFilePath);

        result = graphWithCycle.floydWarshall(costs, predecessors);

        assertFalse(result, "Floyd-Warshall should detect negative cycle");
    }


    @Test
    public void compareDijkstraBellmanFordSmallGraph() throws IOException {
        // Small graph with only positive edges
        String graphData = randomGraphWithPositiveEdgesOnly(100, 500);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[] dijkstraCosts = new int[graph.size()];
        int[] dijkstraParents = new int[graph.size()];
        int[] bellmanFordCosts = new int[graph.size()];
        int[] bellmanFordParents = new int[graph.size()];

        // Measure Dijkstra
        long dijkstraTime = measureExecutionTime(() -> {
            graph.dijkstra(0, dijkstraCosts, dijkstraParents);
        });

        // Measure Bellman-Ford
        long bellmanFordTime = measureExecutionTime(() -> {
            graph.bellmanFord(0, bellmanFordCosts, bellmanFordParents);
        });

        System.out.println("Small graph (100 nodes, 500 edges, positive weights):");
        System.out.println("Dijkstra execution time: " + dijkstraTime / 1_000_000 + " ms");
        System.out.println("Bellman-Ford execution time: " + bellmanFordTime / 1_000_000 + " ms");
        System.out.println("Dijkstra is " + (bellmanFordTime / (double)dijkstraTime) + " times faster than Bellman-Ford");

        // Verify both algorithms produce the same results
        assertArrayEquals(dijkstraCosts, bellmanFordCosts, "Dijkstra and Bellman-Ford should produce the same costs for positive edges");
        assertArrayEquals(dijkstraParents, bellmanFordParents, "Dijkstra and Bellman-Ford should produce the same parents for positive edges");
    }

    @Test
    public void compareDijkstraBellmanFordMediumGraph() throws IOException {
        // Medium graph with only positive edges
        String graphData = randomGraphWithPositiveEdgesOnly(500, 5000);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[] dijkstraCosts = new int[graph.size()];
        int[] dijkstraParents = new int[graph.size()];
        int[] bellmanFordCosts = new int[graph.size()];
        int[] bellmanFordParents = new int[graph.size()];

        // Measure Dijkstra
        long dijkstraTime = measureExecutionTime(() -> {
            graph.dijkstra(0, dijkstraCosts, dijkstraParents);
        });

        // Measure Bellman-Ford
        long bellmanFordTime = measureExecutionTime(() -> {
            graph.bellmanFord(0, bellmanFordCosts, bellmanFordParents);
        });

        System.out.println("Medium graph (500 nodes, 5000 edges, positive weights):");
        System.out.println("Dijkstra execution time: " + dijkstraTime / 1_000_000 + " ms");
        System.out.println("Bellman-Ford execution time: " + bellmanFordTime / 1_000_000 + " ms");
        System.out.println("Dijkstra is " + (bellmanFordTime / (double)dijkstraTime) + " times faster than Bellman-Ford");

        // Verify both algorithms produce the same results
        assertArrayEquals(dijkstraCosts, bellmanFordCosts, "Dijkstra and Bellman-Ford should produce the same costs for positive edges");

        // won't check on the parents equality as there may be equal paths costs and every one picked randomly a different path
    }

    @Test
    public void compareDijkstraBellmanFordLargeGraph() throws IOException {
        // Large graph with only positive edges
        String graphData = randomGraphWithPositiveEdgesOnly(1000, 10000);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[] dijkstraCosts = new int[graph.size()];
        int[] dijkstraParents = new int[graph.size()];
        int[] bellmanFordCosts = new int[graph.size()];
        int[] bellmanFordParents = new int[graph.size()];

        // Measure Dijkstra
        long dijkstraTime = measureExecutionTime(() -> {
            graph.dijkstra(0, dijkstraCosts, dijkstraParents);
        });

        // Measure Bellman-Ford
        long bellmanFordTime = measureExecutionTime(() -> {
            graph.bellmanFord(0, bellmanFordCosts, bellmanFordParents);
        });

        System.out.println("Large graph (1000 nodes, 10000 edges, positive weights):");
        System.out.println("Dijkstra execution time: " + dijkstraTime / 1_000_000 + " ms");
        System.out.println("Bellman-Ford execution time: " + bellmanFordTime / 1_000_000 + " ms");
        System.out.println("Dijkstra is " + (bellmanFordTime / (double)dijkstraTime) + " times faster than Bellman-Ford");

        // Verify both algorithms produce the same results
        assertArrayEquals(dijkstraCosts, bellmanFordCosts, "Dijkstra and Bellman-Ford should produce the same costs for positive edges");

        // won't check on the parents equality as there may be equal paths costs and every one picked randomly a different path
    }

    @Test
    public void testBellmanFordNegativeEdgesPerformance() throws IOException {
        // Graph with negative edges, where Dijkstra would fail
        String graphData = randomGraphWithNegativeEdges(500, 3000);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[] costs = new int[graph.size()];
        int[] parents = new int[graph.size()];

        // Measure Bellman-Ford execution time
        long bellmanFordTime = measureExecutionTime(() -> {
            graph.bellmanFord(0, costs, parents);
        });

        System.out.println("Graph with negative edges (500 nodes, 3000 edges):");
        System.out.println("Bellman-Ford execution time: " + bellmanFordTime / 1_000_000 + " ms");

        // No assertions on correctness since that's covered in other tests
        // This test focuses purely on performance
    }

    @Test
    public void testBellmanFordDenseVsSparseGraphPerformance() throws IOException {
        // Create a sparse graph with the same number of nodes but fewer edges
        String sparseGraphData = randomGraphWithNegativeEdges(300, 600);
        String sparseFilePath = createTestGraphFile(sparseGraphData);
        Graph sparseGraph = new Graph(sparseFilePath);

        // Create a dense graph with the same number of nodes but more edges
        String denseGraphData = randomGraphWithNegativeEdges(300, 20000);
        String denseFilePath = createTestGraphFile(denseGraphData);
        Graph denseGraph = new Graph(denseFilePath);

        int[] sparseCosts = new int[sparseGraph.size()];
        int[] sparseParents = new int[sparseGraph.size()];
        int[] denseCosts = new int[denseGraph.size()];
        int[] denseParents = new int[denseGraph.size()];

        // Measure Bellman-Ford on sparse graph
        long sparseTime = measureExecutionTime(() -> {
            sparseGraph.bellmanFord(0, sparseCosts, sparseParents);
        });

        // Measure Bellman-Ford on dense graph
        long denseTime = measureExecutionTime(() -> {
            denseGraph.bellmanFord(0, denseCosts, denseParents);
        });

        System.out.println("Bellman-Ford on sparse graph (300 nodes, 600 edges): " + sparseTime / 1_000_000 + " ms");
        System.out.println("Bellman-Ford on dense graph (300 nodes, 20000 edges): " + denseTime / 1_000_000 + " ms");
        System.out.println("Dense graph takes " + (denseTime / (double)sparseTime) + " times longer than sparse graph");
    }

    @Test
    public void compareFloydWarshallVsMultipleBellmanFord() throws IOException {
        // Medium size graph with negative edges
        String graphData = randomGraphWithNegativeEdges(100, 1000);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // Measure Floyd-Warshall (solves APSP in one go)
        long floydWarshallTime = measureExecutionTime(() -> {
            graph.floydWarshall(floydCosts, floydPredecessors);
        });

        int[][] costs = new int[graph.size()][graph.size()];
        int[][] parents = new int[graph.size()][graph.size()];
        // Measure multiple Bellman-Ford runs (one for each source vertex)
        long multipleBellmanFordTime = measureExecutionTime(() -> {
            for (int source = 0; source < graph.size(); source++) {
                graph.bellmanFord(source, costs[source], parents[source]);
            }
        });

        System.out.println("APSP on graph with 100 nodes, 1000 edges (with negative weights):");
        System.out.println("Floyd-Warshall execution time: " + floydWarshallTime / 1_000_000 + " ms");
        System.out.println("Multiple Bellman-Ford execution time: " + multipleBellmanFordTime / 1_000_000 + " ms");
        System.out.println("Floyd-Warshall is " + (multipleBellmanFordTime / (double)floydWarshallTime) + " times faster than multiple Bellman-Ford runs");

        for(int i = 0; i < graph.size(); i++) {
            assertArrayEquals(floydCosts[i], costs[i], "Floyd-Warshall and multiple Bellman-Ford should produce the same costs for negative edges");
        }
    }

    @Test
    public void compareFloydWarshallVsMultipleDijkstra() throws IOException {
        // Medium size graph with positive edges only
        String graphData = randomGraphWithPositiveEdgesOnly(200, 3000);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // Measure Floyd-Warshall (solves APSP in one go)
        long floydWarshallTime = measureExecutionTime(() -> {
            graph.floydWarshall(floydCosts, floydPredecessors);
        });

        // Measure multiple Dijkstra runs (one for each source vertex)
        int[][] costs = new int[graph.size()][graph.size()];
        int[][] parents = new int[graph.size()][graph.size()];
        long multipleDijkstraTime = measureExecutionTime(() -> {
            for (int source = 0; source < graph.size(); source++) {
                graph.dijkstra(source, costs[source], parents[source]);
            }
        });

        System.out.println("APSP on graph with 200 nodes, 3000 edges (positive weights only):");
        System.out.println("Floyd-Warshall execution time: " + floydWarshallTime / 1_000_000 + " ms");
        System.out.println("Multiple Dijkstra execution time: " + multipleDijkstraTime / 1_000_000 + " ms");

        if (floydWarshallTime < multipleDijkstraTime) {
            System.out.println("Floyd-Warshall is " + (multipleDijkstraTime / (double)floydWarshallTime) + " times faster than multiple Dijkstra runs");
        } else {
            System.out.println("Multiple Dijkstra runs are " + (floydWarshallTime / (double)multipleDijkstraTime) + " times faster than Floyd-Warshall");
        }
        for (int i = 0; i < graph.size(); i++) {
            assertArrayEquals(floydCosts[i], costs[i], "Floyd-Warshall and multiple Dijkstra should produce the same costs for positive edges");
        }
    }

    @Test
    public void testFloydWarshallScalability() throws IOException {
        // Test Floyd-Warshall on progressively larger graphs to demonstrate O(V^3) complexity
        int[] nodeCounts = {50, 100, 150, 200};
        long[] executionTimes = new long[nodeCounts.length];

        for (int i = 0; i < nodeCounts.length; i++) {
            int nodes = nodeCounts[i];
            int edges = nodes * nodes / 2; // Half-dense graph

            String graphData = randomGraphWithPositiveEdgesOnly(nodes, edges);
            String filePath = createTestGraphFile(graphData);
            Graph graph = new Graph(filePath);

            int[][] floydCosts = new int[graph.size()][graph.size()];
            int[][] floydPredecessors = new int[graph.size()][graph.size()];

            executionTimes[i] = measureExecutionTime(() -> {
                graph.floydWarshall(floydCosts, floydPredecessors);
            });

            System.out.println("Floyd-Warshall on graph with " + nodes + " nodes, " + edges + " edges: " +
                    executionTimes[i] / 1_000_000 + " ms");
        }

        // Print scaling factors to verify O(V^3) complexity
        for (int i = 1; i < nodeCounts.length; i++) {
            double nodeFactor = (double)nodeCounts[i] / nodeCounts[i-1];
            double timeFactor = (double)executionTimes[i] / executionTimes[i-1];
            double expectedFactor = Math.pow(nodeFactor, 3);

            System.out.println("Nodes increased by factor: " + nodeFactor);
            System.out.println("Time increased by factor: " + timeFactor);
            System.out.println("Expected factor (V^3): " + expectedFactor);
            System.out.println("Ratio of actual/expected: " + (timeFactor / expectedFactor));
            System.out.println();
        }
    }

    @Test
    public void testDijkstraPerformanceWithIncreasingEdgeDensity() throws IOException {
        // Test how Dijkstra performs as edge density increases
        int nodes = 500;
        int[] edgeCounts = {500, 2500, 10000, 25000};
        long[] executionTimes = new long[edgeCounts.length];

        for (int i = 0; i < edgeCounts.length; i++) {
            int edges = edgeCounts[i];

            String graphData = randomGraphWithPositiveEdgesOnly(nodes, edges);
            String filePath = createTestGraphFile(graphData);
            Graph graph = new Graph(filePath);

            int[] costs = new int[graph.size()];
            int[] parents = new int[graph.size()];

            executionTimes[i] = measureExecutionTime(() -> {
                graph.dijkstra(0, costs, parents);
            });

            System.out.println("Dijkstra on graph with " + nodes + " nodes, " + edges + " edges: " +
                    executionTimes[i] / 1_000_000 + " ms");
        }

        // Print scaling factors to verify complexity
        for (int i = 1; i < edgeCounts.length; i++) {
            double edgeFactor = (double)edgeCounts[i] / edgeCounts[i-1];
            double timeFactor = (double)executionTimes[i] / executionTimes[i-1];

            System.out.println("Edges increased by factor: " + edgeFactor);
            System.out.println("Time increased by factor: " + timeFactor);
            // Expected factor depends on implementation (O(E log V) for binary heap, O(V^2) for array)
            System.out.println();
        }
    }

    @Test
    public void testFloydWarshallVsBellmanFordOnDenseGraphs() throws IOException {
        // Compare Floyd-Warshall and Bellman-Ford on dense graphs with negative edges
        int nodes = 150;
        int edges = nodes * (nodes - 1); // Almost complete graph

        String graphData = randomGraphWithNegativeEdges(nodes, edges);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // Measure Floyd-Warshall
        long floydWarshallTime = measureExecutionTime(() -> {
            graph.floydWarshall(floydCosts, floydPredecessors);
        });

        // Measure time to run Bellman-Ford from just 10 source vertices
        // (running from all vertices would take too long)
        long bellmanFordTime = measureExecutionTime(() -> {
            for (int source = 0; source < 10; source++) {
                int[] costs = new int[graph.size()];
                int[] parents = new int[graph.size()];
                graph.bellmanFord(source, costs, parents);
            }
        });

        // Extrapolate total time for all sources
        long estimatedTotalBellmanFordTime = bellmanFordTime * (graph.size() / 10);

        System.out.println("Dense graph with " + nodes + " nodes, " + edges + " edges (with negative weights):");
        System.out.println("Floyd-Warshall execution time: " + floydWarshallTime / 1_000_000 + " ms");
        System.out.println("Bellman-Ford for 10 sources: " + bellmanFordTime / 1_000_000 + " ms");
        System.out.println("Estimated Bellman-Ford for all sources: " + estimatedTotalBellmanFordTime / 1_000_000 + " ms");
        System.out.println("Floyd-Warshall is approximately " + (estimatedTotalBellmanFordTime / (double)floydWarshallTime) +
                " times faster than running Bellman-Ford for all sources");
    }

    //  now comparing all three algorithms at once.

    /**
     * SSSP - Small Graph (Positive Edges Only)
     * Compares all three algorithms on a small graph with positive edges only.
     * All three algorithms should produce identical results.
     */
    @Test
    public void testSSSP_SmallGraphPositiveEdges() throws IOException {
        String graphData = randomGraphWithPositiveEdgesOnly(50, 200);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int source = 0; // Source vertex

        // Arrays for Dijkstra results
        int[] dijkstraCosts = new int[graph.size()];
        int[] dijkstraParents = new int[graph.size()];

        // Arrays for Bellman-Ford results
        int[] bellmanFordCosts = new int[graph.size()];
        int[] bellmanFordParents = new int[graph.size()];

        // Arrays for Floyd-Warshall results (we'll extract SSSP data from APSP results)
        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // Run Dijkstra
        long dijkstraTime = measureExecutionTime(() -> {
            graph.dijkstra(source, dijkstraCosts, dijkstraParents);
        });

        // Run Bellman-Ford
        long bellmanFordTime = measureExecutionTime(() -> {
            graph.bellmanFord(source, bellmanFordCosts, bellmanFordParents);
        });

        // Run Floyd-Warshall
        long floydWarshallTime = measureExecutionTime(() -> {
            graph.floydWarshall(floydCosts, floydPredecessors);
        });

        // Extract SSSP data from Floyd-Warshall results
        int[] floydSSSPCosts = new int[graph.size()];
        for (int i = 0; i < graph.size(); i++) {
            floydSSSPCosts[i] = floydCosts[source][i];
        }

        // Verify all algorithms produce the same costs
        assertArrayEquals(dijkstraCosts, bellmanFordCosts,
                "Dijkstra and Bellman-Ford should produce the same costs on positive-edge graphs");
        assertArrayEquals(dijkstraCosts, floydSSSPCosts,
                "Dijkstra and Floyd-Warshall should produce the same costs on positive-edge graphs");

        // Print performance results
        System.out.println("SSSP - Small Graph with Positive Edges (50 nodes, 200 edges):");
        System.out.println("Dijkstra: " + dijkstraTime / 1_000_000 + " ms");
        System.out.println("Bellman-Ford: " + bellmanFordTime / 1_000_000 + " ms");
        System.out.println("Floyd-Warshall (complete APSP): " + floydWarshallTime / 1_000_000 + " ms");
        System.out.println("Dijkstra is " + (bellmanFordTime / (double)dijkstraTime) + " times faster than Bellman-Ford");
        System.out.println("Dijkstra is " + (floydWarshallTime / (double)dijkstraTime) + " times faster than Floyd-Warshall for single source");
        System.out.println();
    }

    /**
     * SSSP - Medium Graph (Positive Edges Only)
     * Compares all three algorithms on a medium-sized graph with positive edges only.
     * Shows how performance differences scale with graph size.
     */
    @Test
    public void testSSSP_MediumGraphPositiveEdges() throws IOException {
        String graphData = randomGraphWithPositiveEdgesOnly(300, 3000);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int source = 0; // Source vertex

        // Arrays for Dijkstra results
        int[] dijkstraCosts = new int[graph.size()];
        int[] dijkstraParents = new int[graph.size()];

        // Arrays for Bellman-Ford results
        int[] bellmanFordCosts = new int[graph.size()];
        int[] bellmanFordParents = new int[graph.size()];

        // Arrays for Floyd-Warshall results
        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // Run Dijkstra
        long dijkstraTime = measureExecutionTime(() -> {
            graph.dijkstra(source, dijkstraCosts, dijkstraParents);
        });

        // Run Bellman-Ford
        long bellmanFordTime = measureExecutionTime(() -> {
            graph.bellmanFord(source, bellmanFordCosts, bellmanFordParents);
        });

        // Run Floyd-Warshall
        long floydWarshallTime = measureExecutionTime(() -> {
            graph.floydWarshall(floydCosts, floydPredecessors);
        });

        // Extract SSSP data from Floyd-Warshall results
        int[] floydSSSPCosts = new int[graph.size()];
        for (int i = 0; i < graph.size(); i++) {
            floydSSSPCosts[i] = floydCosts[source][i];
        }

        // Verify all algorithms produce the same costs
        assertArrayEquals(dijkstraCosts, bellmanFordCosts,
                "Dijkstra and Bellman-Ford should produce the same costs on positive-edge graphs");
        assertArrayEquals(dijkstraCosts, floydSSSPCosts,
                "Dijkstra and Floyd-Warshall should produce the same costs on positive-edge graphs");

        // Print performance results
        System.out.println("SSSP - Medium Graph with Positive Edges (300 nodes, 3000 edges):");
        System.out.println("Dijkstra: " + dijkstraTime / 1_000_000 + " ms");
        System.out.println("Bellman-Ford: " + bellmanFordTime / 1_000_000 + " ms");
        System.out.println("Floyd-Warshall (complete APSP): " + floydWarshallTime / 1_000_000 + " ms");
        System.out.println("Dijkstra is " + (bellmanFordTime / (double)dijkstraTime) + " times faster than Bellman-Ford");
        System.out.println("Dijkstra is " + (floydWarshallTime / (double)dijkstraTime) + " times faster than Floyd-Warshall for single source");
        System.out.println();
    }

    /**
     * SSSP - Medium Graph with Negative Edges
     * Compares Bellman-Ford and Floyd-Warshall on a graph with negative edges.
     * Dijkstra is not applicable here because it doesn't handle negative edges correctly.
     */
    @Test
    public void testSSSP_NegativeEdges() throws IOException {
        String graphData = randomGraphWithNegativeEdges(200, 1500);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int source = 0; // Source vertex

        // Arrays for Bellman-Ford results
        int[] bellmanFordCosts = new int[graph.size()];
        int[] bellmanFordParents = new int[graph.size()];

        // Arrays for Floyd-Warshall results
        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // Run Bellman-Ford
        long bellmanFordTime = measureExecutionTime(() -> {
            boolean noNegativeCycle = graph.bellmanFord(source, bellmanFordCosts, bellmanFordParents);
            assertTrue(noNegativeCycle, "Graph should not contain negative cycles");
        });

        // Run Floyd-Warshall
        long floydWarshallTime = measureExecutionTime(() -> {
            boolean noNegativeCycle = graph.floydWarshall(floydCosts, floydPredecessors);
            assertTrue(noNegativeCycle, "Graph should not contain negative cycles");
        });

        // Extract SSSP data from Floyd-Warshall results
        int[] floydSSSPCosts = new int[graph.size()];
        for (int i = 0; i < graph.size(); i++) {
            floydSSSPCosts[i] = floydCosts[source][i];
        }

        // Verify both algorithms produce the same costs
        assertArrayEquals(bellmanFordCosts, floydSSSPCosts,
                "Bellman-Ford and Floyd-Warshall should produce the same costs on graphs with negative edges");

        // Print performance results
        System.out.println("SSSP - Graph with Negative Edges (200 nodes, 1500 edges):");
        System.out.println("Bellman-Ford: " + bellmanFordTime / 1_000_000 + " ms");
        System.out.println("Floyd-Warshall (complete APSP): " + floydWarshallTime / 1_000_000 + " ms");

        if (bellmanFordTime < floydWarshallTime) {
            System.out.println("Bellman-Ford is " + (floydWarshallTime / (double)bellmanFordTime) +
                    " times faster than Floyd-Warshall for single source");
        } else {
            System.out.println("Floyd-Warshall is " + (bellmanFordTime / (double)floydWarshallTime) +
                    " times slower but computes all pairs, not just single source");
        }
        System.out.println();
    }

    /**
     * APSP - Small Graph (Positive Edges)
     * Compares running multiple Dijkstra/Bellman-Ford vs. single Floyd-Warshall for APSP
     * on a small graph with positive edges.
     */
    @Test
    public void testAPSP_SmallGraph() throws IOException {
        String graphData = randomGraphWithPositiveEdgesOnly(50, 200);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        // Arrays for results
        int[][] dijkstraCosts = new int[graph.size()][graph.size()];
        int[][] bellmanFordCosts = new int[graph.size()][graph.size()];
        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // Run multiple Dijkstra (one per source)
        long multipleDijkstraTime = measureExecutionTime(() -> {
            for (int source = 0; source < graph.size(); source++) {
                int[] costs = new int[graph.size()];
                int[] parents = new int[graph.size()];
                graph.dijkstra(source, costs, parents);
                dijkstraCosts[source] = costs.clone();
            }
        });

        // Run multiple Bellman-Ford (one per source)
        long multipleBellmanFordTime = measureExecutionTime(() -> {
            for (int source = 0; source < graph.size(); source++) {
                int[] costs = new int[graph.size()];
                int[] parents = new int[graph.size()];
                graph.bellmanFord(source, costs, parents);
                bellmanFordCosts[source] = costs.clone();
            }
        });

        // Run Floyd-Warshall (single run for APSP)
        long floydWarshallTime = measureExecutionTime(() -> {
            graph.floydWarshall(floydCosts, floydPredecessors);
        });

        // Verify all algorithms produce the same costs matrix
        for (int source = 0; source < graph.size(); source++) {
            assertArrayEquals(dijkstraCosts[source], bellmanFordCosts[source],
                    "Dijkstra and Bellman-Ford should produce the same costs for source " + source);
            assertArrayEquals(dijkstraCosts[source], floydCosts[source],
                    "Dijkstra and Floyd-Warshall should produce the same costs for source " + source);
        }

        // Print performance results
        System.out.println("APSP - Small Graph with Positive Edges (50 nodes, 200 edges):");
        System.out.println("Multiple Dijkstra runs: " + multipleDijkstraTime / 1_000_000 + " ms");
        System.out.println("Multiple Bellman-Ford runs: " + multipleBellmanFordTime / 1_000_000 + " ms");
        System.out.println("Single Floyd-Warshall run: " + floydWarshallTime / 1_000_000 + " ms");

        System.out.println("Multiple Dijkstra is " + (multipleBellmanFordTime / (double)multipleDijkstraTime) +
                " times faster than multiple Bellman-Ford");

        if (floydWarshallTime < multipleDijkstraTime) {
            System.out.println("Floyd-Warshall is " + (multipleDijkstraTime / (double)floydWarshallTime) +
                    " times faster than multiple Dijkstra runs");
        } else {
            System.out.println("Multiple Dijkstra runs are " + (floydWarshallTime / (double)multipleDijkstraTime) +
                    " times faster than Floyd-Warshall");
        }
        System.out.println();
    }

    /**
     * APSP - Medium Graph (Positive Edges)
     * Compares running multiple Dijkstra/Bellman-Ford vs. single Floyd-Warshall for APSP
     * on a medium graph with positive edges.
     * Shows how the algorithm choice becomes more critical as graph size increases.
     */
    @Test
    public void testAPSP_MediumGraph() throws IOException {
        String graphData = randomGraphWithPositiveEdgesOnly(100, 1000);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        // Arrays for results
        int[][] dijkstraCosts = new int[graph.size()][graph.size()];
        int[][] bellmanFordCosts = new int[graph.size()][graph.size()];
        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // For larger graphs, we'll only test a subset of sources to save time
        int sampleSize = 10;
        int[] sampleSources = new int[sampleSize];
        Random rand = new Random(12345);
        for (int i = 0; i < sampleSize; i++) {
            sampleSources[i] = rand.nextInt(graph.size());
        }

        // Run multiple Dijkstra (one per sample source)
        long multipleDijkstraTime = measureExecutionTime(() -> {
            for (int source : sampleSources) {
                int[] costs = new int[graph.size()];
                int[] parents = new int[graph.size()];
                graph.dijkstra(source, costs, parents);
                dijkstraCosts[source] = costs.clone();
            }
        });
        // Extrapolate to all sources
        long estimatedFullDijkstraTime = multipleDijkstraTime * (graph.size() / sampleSize);

        // Run multiple Bellman-Ford (one per sample source)
        long multipleBellmanFordTime = measureExecutionTime(() -> {
            for (int source : sampleSources) {
                int[] costs = new int[graph.size()];
                int[] parents = new int[graph.size()];
                graph.bellmanFord(source, costs, parents);
                bellmanFordCosts[source] = costs.clone();
            }
        });
        // Extrapolate to all sources
        long estimatedFullBellmanFordTime = multipleBellmanFordTime * (graph.size() / sampleSize);

        // Run Floyd-Warshall (single run for APSP)
        long floydWarshallTime = measureExecutionTime(() -> {
            graph.floydWarshall(floydCosts, floydPredecessors);
        });

        // Verify algorithms produce the same costs for sample sources
        for (int source : sampleSources) {
            assertArrayEquals(dijkstraCosts[source], bellmanFordCosts[source],
                    "Dijkstra and Bellman-Ford should produce the same costs for source " + source);
            assertArrayEquals(dijkstraCosts[source], floydCosts[source],
                    "Dijkstra and Floyd-Warshall should produce the same costs for source " + source);
        }

        // Print performance results
        System.out.println("APSP - Medium Graph with Positive Edges (100 nodes, 1000 edges):");
        System.out.println("Multiple Dijkstra runs (sample of " + sampleSize + "): " + multipleDijkstraTime / 1_000_000 + " ms");
        System.out.println("Estimated time for all sources: " + estimatedFullDijkstraTime / 1_000_000 + " ms");
        System.out.println("Multiple Bellman-Ford runs (sample of " + sampleSize + "): " + multipleBellmanFordTime / 1_000_000 + " ms");
        System.out.println("Estimated time for all sources: " + estimatedFullBellmanFordTime / 1_000_000 + " ms");
        System.out.println("Single Floyd-Warshall run: " + floydWarshallTime / 1_000_000 + " ms");

        System.out.println("Estimated speedup of multiple Dijkstra over multiple Bellman-Ford: " +
                (estimatedFullBellmanFordTime / (double)estimatedFullDijkstraTime) + "x");

        if (floydWarshallTime < estimatedFullDijkstraTime) {
            System.out.println("Floyd-Warshall is " + (estimatedFullDijkstraTime / (double)floydWarshallTime) +
                    " times faster than estimated multiple Dijkstra runs");
        } else {
            System.out.println("Estimated multiple Dijkstra runs are " + (floydWarshallTime / (double)estimatedFullDijkstraTime) +
                    " times faster than Floyd-Warshall");
        }
        System.out.println();
    }

    /**
     * APSP - Medium Graph with Negative Edges
     * Compares running multiple Bellman-Ford vs. single Floyd-Warshall for APSP
     * on a medium graph with negative edges.
     * Dijkstra is not applicable due to negative edges.
     */
    @Test
    public void testAPSP_NegativeEdges() throws IOException {
        String graphData = randomGraphWithNegativeEdges(80, 800);
        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        // Arrays for results
        int[][] bellmanFordCosts = new int[graph.size()][graph.size()];
        int[][] floydCosts = new int[graph.size()][graph.size()];
        int[][] floydPredecessors = new int[graph.size()][graph.size()];

        // For larger graphs, we'll only test a subset of sources to save time
        int sampleSize = 8;
        int[] sampleSources = new int[sampleSize];
        Random rand = new Random(12345);
        for (int i = 0; i < sampleSize; i++) {
            sampleSources[i] = rand.nextInt(graph.size());
        }

        // Run multiple Bellman-Ford (one per sample source)
        long multipleBellmanFordTime = measureExecutionTime(() -> {
            for (int source : sampleSources) {
                int[] costs = new int[graph.size()];
                int[] parents = new int[graph.size()];
                boolean noNegativeCycle = graph.bellmanFord(source, costs, parents);
                assertTrue(noNegativeCycle, "Graph should not contain negative cycles");
                bellmanFordCosts[source] = costs.clone();
            }
        });
        // Extrapolate to all sources
        long estimatedFullBellmanFordTime = multipleBellmanFordTime * (graph.size() / sampleSize);

        // Run Floyd-Warshall (single run for APSP)
        long floydWarshallTime = measureExecutionTime(() -> {
            boolean noNegativeCycle = graph.floydWarshall(floydCosts, floydPredecessors);
            assertTrue(noNegativeCycle, "Graph should not contain negative cycles");
        });

        // Verify algorithms produce the same costs for sample sources
        for (int source : sampleSources) {
            assertArrayEquals(bellmanFordCosts[source], floydCosts[source],
                    "Bellman-Ford and Floyd-Warshall should produce the same costs for source " + source);
        }

        // Print performance results
        System.out.println("APSP - Medium Graph with Negative Edges (80 nodes, 800 edges):");
        System.out.println("Multiple Bellman-Ford runs (sample of " + sampleSize + "): " + multipleBellmanFordTime / 1_000_000 + " ms");
        System.out.println("Estimated time for all sources: " + estimatedFullBellmanFordTime / 1_000_000 + " ms");
        System.out.println("Single Floyd-Warshall run: " + floydWarshallTime / 1_000_000 + " ms");

        if (floydWarshallTime < estimatedFullBellmanFordTime) {
            System.out.println("Floyd-Warshall is " + (estimatedFullBellmanFordTime / (double)floydWarshallTime) +
                    " times faster than estimated multiple Bellman-Ford runs");
        } else {
            System.out.println("Estimated multiple Bellman-Ford runs are " + (floydWarshallTime / (double)estimatedFullBellmanFordTime) +
                    " times faster than Floyd-Warshall");
        }
        System.out.println();
    }
}