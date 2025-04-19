import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Graph {
    private final int nodes;
    private final List<Edge> edges;

    public Graph(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
        }
    }

    public int size() {     //?get the number of nodes */
        return nodes;
    }

    public List<Edge> getEdges() {      //?get the Edges */
        return edges;
    }

    public List<List<Edge>> adjList (List<Edge> edges) {
        List<List<Edge>> adjList = new ArrayList<>(nodes);
        for (int i = 0; i < nodes; i++) {
            adjList.add(new ArrayList<>());
        }
        for (Edge edge : edges) {
            adjList.get(edge.from).add(edge);
        }
        return adjList;
    }

    public void dijkstra(int source, int[] costs, int[] parents) {
        if (source < 0 || source >= nodes) {
            throw new IllegalArgumentException("Invalid source node index " + source);
        }
        List<List<Edge>> adj = adjList(edges);      //adjacency list of the graph <List<List<Edge>>>

        Arrays.fill(costs, Integer.MAX_VALUE);
        Arrays.fill(parents, -1);

        costs[source] = 0;
        Boolean [] visited = new Boolean[nodes];
        Arrays.fill(visited, false);

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        pq.add(new Edge(source, source, 0));

        while(!pq.isEmpty()){
            Edge minEdge = pq.poll();
            int v = minEdge.to;

            if(visited[v]) continue;

            for(Edge edge : adj.get(v)){
                int to = edge.to, weight = edge.weight;
                if(costs[v] + weight < costs[to]){
                    costs[to] = costs[v] + weight;
                    parents[to] = v;
                    pq.add(new Edge(v, to, costs[to]));
                }
            }
            visited[v] = true;
        }
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

    public String getPath_bellman_dijkstra(int from, int to, int[] parents) {
        if (from != to && parents[to] == -1) return "No path exists";
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(to));
        while (parents[to] != -1) {
            sb.append("  >-  ").append(Integer.toString(parents[to]));
            to = parents[to];
        }
        return sb.reverse().toString();
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
    public String  getPathFloyd(int from,int to, int[][] predecessors){
        if(from!=to && predecessors[from][to]==-1)return "No path exists";
        StringBuilder sb=new StringBuilder();
        sb.append(Integer.toString(to));
        while(predecessors[from][to]!=-1){
            sb.append("  >-  ").append(Integer.toString(predecessors[from][to]));
            to=predecessors[from][to];
        }
        return sb.reverse().toString();


    }
//    public static void main(String[] args) throws IOException {
//        Graph g=new Graph("input");
//          int [] costs=new int[g.size()];
//             int [] parents=new int[g.size()];
//             g.dijkstra(0, costs, parents);
//             System.out.println("Dijkstra: ");
//             for (int i = 0; i < g.size(); i++) {
//                 System.out.println("Cost to node " + i + ": " + costs[i]);
//                 System.out.println("Path: " + g.getPath_bellman_dijkstra(0, i, parents));
//             }
//             System.out.println("Bellman-Ford: ");
//             if (g.bellmanFord(0, costs, parents)) {
//                 for (int i = 0; i < g.size(); i++) {
//                     System.out.println("Cost to node " + i + ": " + costs[i]);
//                     System.out.println("Path: " + g.getPath_bellman_dijkstra(0, i, parents));
//                 }
//             } else {
//                 System.out.println("Negative cycle detected");
//             }
//     }

}