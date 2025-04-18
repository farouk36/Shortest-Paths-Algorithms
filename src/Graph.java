
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Edge {
    int  from,to, weight;
    public Edge(int from,int to, int weight) {
        this.from=from;
        this.to = to;
        this.weight = weight;
    }
}

public class Graph {
    private int nodes;
    private List<Edge> edges;



    public Graph(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String[] line = reader.readLine().split(" ");
        this.nodes = Integer.parseInt(line[0]);
        int e = Integer.parseInt(line[1]);

        edges = new ArrayList<>(e);


        for (int i = 0; i < e; i++) {
            line = reader.readLine().split(" ");
            int from = Integer.parseInt(line[0]);
            int to = Integer.parseInt(line[1]);
            int weight = Integer.parseInt(line[2]);
            edges.add(new Edge(from,to, weight));
        }
        reader.close();
    }

    public int size() {     //?get the number of nodes */
        return nodes;
    }

    public List<Edge> getEdges() {      //?get the Edges */
        return edges;
    }


    public void dijkstra(int source, int[] costs, int[] parents) {

    }


    private  boolean hasNegativeCycle(int[] costs) {    //?check if there is a negative cycle */
        for (Edge e : edges) {
            if (costs[e.from] != Integer.MAX_VALUE && costs[e.from] + e.weight < costs[e.to])
                return true;   //! Negative cycle detected    
        }
        return false;        //! No negative cycle
    }
    public boolean bellmanFord(int source, int[] costs, int[] parents) {  //?Bellman-Ford algorithm
        if (source < 0 || source >= nodes) {
            throw new IllegalArgumentException("Invalid source node index " + source);
        }

        Arrays.fill(costs, Integer.MAX_VALUE);
        Arrays.fill(parents, -1);
        costs[source] = 0;

        for (int i = 0; i < nodes - 1; i++) {
            for (Edge e : edges) {
                if (costs[e.from] != Integer.MAX_VALUE && costs[e.from] + e.weight < costs[e.to]) {
                    costs[e.to] = costs[e.from] + e.weight;
                    parents[e.to] = e.from;
                }
            }
        }

        return !hasNegativeCycle(costs);
    }


    private void initializeFloyd(int[][] costs, int[][] predecessors) {
        for (int i = 0; i < nodes; i++) {
            Arrays.fill(costs[i], Integer.MAX_VALUE);
            Arrays.fill(predecessors[i], -1);
            costs[i][i] = 0;
        }
        for (Edge e : edges) {
            if (e.from == e.to) continue;
            costs[e.from][e.to] = Math.min(costs[e.from][e.to], e.weight);
        }
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                if (i != j && costs[i][j] != Integer.MAX_VALUE)
                    predecessors[i][j] = i;
            }
        }
    }

    public boolean floydWarshall(int[][] costs, int[][] predecessors) {

        initializeFloyd(costs,predecessors);

        for (int k = 0; k < nodes ; k++) {
            for (int from = 0; from < nodes; from++) {
                for (int to= 0; to < nodes; to++) {
                    if(costs[from][k]==Integer.MAX_VALUE ||costs[k][to]==Integer.MAX_VALUE)continue;

                    int relax=costs[from][k]+costs[k][to];
                    if (relax < costs[from][to]) {
                        costs[from][to] = relax;
                        predecessors[from][to] = predecessors[k][to];
                    }
                }
            }
        }
        for (int i = 0; i < nodes; i++)
            if(costs[i][i]<0)return false;
        return true;
    }

    public static void main(String[] args) throws IOException {
        Graph g=new Graph("input");

    }
}