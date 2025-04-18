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
    private String generateRandomGraph(int nodes, int edges) {
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

    @Test
    public void testDijkstraCorrectness() throws IOException {
        String graphData = "6 8\n" +
                "0 1 2\n" +
                "0 2 4\n" +
                "1 2 1\n" +
                "1 3 7\n" +
                "2 4 3\n" +
                "3 5 1\n" +
                "4 3 2\n" +
                "4 5 5\n";

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
    public void testBellmanFordWithNegativeEdges() throws IOException {
        String graphData = "5 8\n" +
                "0 1 6\n" +
                "0 3 7\n" +
                "1 2 5\n" +
                "1 3 8\n" +
                "1 4 -4\n" +
                "2 1 -2\n" +
                "3 2 -3\n" +
                "3 4 9\n";

        String filePath = createTestGraphFile(graphData);
        Graph graph = new Graph(filePath);

        int[] costs = new int[graph.size()];
        int[] parents = new int[graph.size()];

        boolean result = graph.bellmanFord(0, costs, parents);

        assertTrue(result, "Bellman-Ford should not detect negative cycle");

        // Expected shortest path costs from node 0
        int[] expectedCosts = {0, 2, 4, 7, -2};
        assertArrayEquals(expectedCosts, costs, "Bellman-Ford shortest path costs are incorrect");

        // Create a graph with a negative cycle
        String graphWithNegativeCycle = "4 4\n" +
                "0 1 1\n" +
                "1 2 2\n" +
                "2 3 3\n" +
                "3 0 -10\n";

        String cycleFilePath = createTestGraphFile(graphWithNegativeCycle);
        Graph graphWithCycle = new Graph(cycleFilePath);

        result = graphWithCycle.bellmanFord(0, costs, parents);

        assertFalse(result, "Bellman-Ford should detect negative cycle");
    }

    @Test
    public void testFloydWarshallAllPairsShortestPath() throws IOException {
        String graphData = "5 10\n" +
                "0 1 6\n" +
                "0 3 7\n" +
                "1 2 5\n" +
                "1 3 8\n" +
                "1 4 -4\n" +
                "2 1 -2\n" +
                "3 0 -1\n" +
                "3 2 -3\n" +
                "3 4 9\n" +
                "4 1 10\n" ;

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

        // Check if costs are calculated correctly
        for (int i = 0; i < graph.size(); i++) {
            assertArrayEquals(expectedCosts[i], costs[i], "Floyd-Warshall shortest path costs are incorrect");
        }

        // Create a graph with a negative cycle
        String graphWithNegativeCycle = "5 10\n" +
                "0 1 6\n" +
                "0 3 7\n" +
                "1 2 5\n" +
                "1 3 8\n" +
                "1 4 -4\n" +
                "2 1 -2\n" +
                "3 0 -1\n" +
                "3 2 -3\n" +
                "3 4 9\n" +
                "4 1 2\n" ;

        String cycleFilePath = createTestGraphFile(graphWithNegativeCycle);
        Graph graphWithCycle = new Graph(cycleFilePath);

        result = graphWithCycle.floydWarshall(costs, predecessors);

        assertFalse(result, "Floyd-Warshall should detect negative cycle");
    }
}