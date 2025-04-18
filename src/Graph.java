
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

    public int size() {
        return nodes;
    }


    public void dijkstra(int source, int[] costs, int[] parents) {

    }


    public boolean bellmanFord(int source, int[] costs, int[] parents) {

        return true;
    }


    public boolean floydWarshall(int[][] costs, int[][] predecessors) {
        return true;
    }

    public static void main(String[] args) throws IOException {
        Graph g=new Graph("input");
        System.out.println(g.nodes);
        for(Edge e:g.edges){
            System.out.println(e.from+" "+e.to+" "+e.weight);
        }

    }
}