
import java.util.Scanner;

public class CommandLineInterface {
    //? ANSI escape codes for colors
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";

    public void Main_Func (){ //? Main function to run the program
        System.out.println(CYAN + BOLD + "Welcome to the Graph Algorithm CLI!" + RESET);
        Scanner scanner = new Scanner(System.in);

        Graph graph = null;

        while (true) { 
            try {
                System.out.println(GREEN + "Please enter the path of the graph file:" + RESET);
                String filePath = scanner.nextLine();
                graph = new Graph(filePath);
                System.out.println(GREEN + "Graph loaded successfully!" + RESET);
                break;
            } catch (Exception e) {
                System.out.println(RED + "Error loading graph: " + e.getMessage() + RESET);
                System.out.println("Please try again.");
            }
        }

        while (true){
            System.out.println(YELLOW + BOLD + "\n=== MAIN MENU ===" + RESET + RESET);
            System.out.println("1. Single-source shortest paths");
            System.out.println("2. All-pairs shortest paths");
            System.out.println("3. Check for negative cycles");
            System.out.println("4. Exit");
            System.out.print(YELLOW + "Choose an option: " + RESET);

            int choice = takeChoiceAndCheckValidity(4, 1, scanner);

            switch (choice){
                case 1 -> {
                    System.out.println("Enter Source Node: ");
                    int source = takeChoiceAndCheckValidity(graph.size(), 0, scanner);
                    handle_singleSourceShortestPaths(source, graph, scanner);
                }
                    
                case 2 -> {
                    handle_allPairsShortestPaths(graph, scanner);
                }
                    
                case 3 -> {
                    handle_checkForNegativeCycles(graph, scanner);

                }
                   
                case 4 -> {
                    System.out.println(GREEN + "Exiting the program. Goodbye!" + RESET);
                    scanner.close();
                    return;
                }
            } 
        }
    }

    private static void handle_singleSourceShortestPaths(int source, Graph graph, Scanner scanner) {
        System.out.println(YELLOW + BOLD + "\n=== SINGLE-SOURCE SHORTEST PATHS ===" + RESET);
        System.out.println("Choose algorithm:");
        System.out.println("1. Dijkstra's Algorithm");
        System.out.println("2. Bellman-Ford Algorithm");
        System.out.println("3. Floyd-Warshall Algorithm");
        System.out.print(YELLOW + "Choose an option: " + RESET);
        
        int choice = takeChoiceAndCheckValidity(3, 1, scanner);
        
        int[] costs = new int[graph.size()];
        int[] predecessors = new int[graph.size()];
        int[][] costsMatrix = null;
        int[][] predecessorsMatrix = null;
        boolean hasNegativeCycle = false;
        
        switch (choice) {
            case 1:
                graph.dijkstra(source, costs, predecessors);
                System.out.println(GREEN + "Dijkstra algorithm executed successfully!" + RESET);
                break;
                
            case 2:
                boolean result = graph.bellmanFord(source, costs, predecessors);
                // if (!result) {
                //     System.out.println(RED + "Warning: Negative cycle detected!" + RESET);
                //     hasNegativeCycle = true;
                // } else {
                //     System.out.println(GREEN + "Bellman-Ford algorithm executed successfully!" + RESET);
                // }
                break;
                
            case 3:
                costsMatrix = new int[graph.size()][graph.size()];
                predecessorsMatrix = new int[graph.size()][graph.size()];
                result = graph.floydWarshall(costsMatrix, predecessorsMatrix);
                // if (!result) {
                //     System.out.println(RED + "Warning: Negative cycle detected!" + RESET);
                //     hasNegativeCycle = true;
                // } else {
                //     System.out.println(GREEN + "Floyd-Warshall algorithm executed successfully!" + RESET);
                // }
                break;
        }

        if (!hasNegativeCycle) {
            while (true) {
                System.out.println(YELLOW + BOLD + "\n=== MENU ===" + RESET);
                System.out.println("1. Cost of the path to a specific node");
                System.out.println("2. Path from source to a specific node");
                System.out.println("3. Return to main menu");
                System.out.print(YELLOW + "Choose an option: " + RESET);
                
                int queryChoice = takeChoiceAndCheckValidity(3, 1, scanner);
                
                if (queryChoice == 3) {
                    break;
                }
                
                System.out.print("Enter destination node: ");
                int destination = takeChoiceAndCheckValidity(graph.size(), 0, scanner);
                
                if (choice == 3) { // Floyd-Warshall
                    if (queryChoice == 1) {
                        System.out.println("Cost of shortest path: " + costsMatrix[source][destination]);
                    } else {
                        System.out.println("Shortest path: " + graph.getPathFloyd(source, destination, predecessorsMatrix));
                    }
                } else { // Dijkstra or Bellman-Ford
                    if (queryChoice == 1) {
                        System.out.println("Cost of shortest path: " + costs[destination]);
                    } else {
                        System.out.println("Shortest path: " + graph.getPath_bellman_dijkstra(source, destination, predecessors));
                    }
                }
            }
        }
    }

    private static void handle_allPairsShortestPaths(Graph graph, Scanner scanner) {
        System.out.println(YELLOW + BOLD + "\n=== ALL-PAIRS SHORTEST PATHS ===" + RESET);
        System.out.println("Choose algorithm:");
        System.out.println("1. Dijkstra's Algorithm (run for each source)");
        System.out.println("2. Bellman-Ford Algorithm (run for each source)");
        System.out.println("3. Floyd-Warshall Algorithm");
        System.out.print(YELLOW + "Choose an option: " + RESET);
        
        int choice = takeChoiceAndCheckValidity(3, 1, scanner);
        
        int[][] costsMatrix = new int[graph.size()][graph.size()];
        int[][] predecessorsMatrix = new int[graph.size()][graph.size()];
        boolean hasNegativeCycle = false;
        
        switch (choice) {
            case 1:{
                //? Dijkstra's algorithm for all sources add here
                for (int i = 0; i < graph.size(); i++) {
                    int[] costs = new int[graph.size()];
                    int[] predecessors = new int[graph.size()];
                    graph.dijkstra(i, costs, predecessors);
                    System.arraycopy(costs, 0, costsMatrix[i], 0, graph.size());
                    System.arraycopy(predecessors, 0, predecessorsMatrix[i], 0, graph.size());
                }
                System.out.println(GREEN + "Dijkstra algorithm executed successfully for all sources!" + RESET);
                break;
            }
            case 2:{
               //? Bellman-Ford algorithm for all sources add here
                for (int i = 0; i < graph.size(); i++) {
                    int[] costs = new int[graph.size()];
                    int[] predecessors = new int[graph.size()];
                    boolean result = graph.bellmanFord(i, costs, predecessors);
                    if (!result) {
                        System.out.println(RED + "Warning: Negative cycle detected from source " + i + "!" + RESET);
                        hasNegativeCycle = true;
                        break;
                    }
                    System.arraycopy(costs, 0, costsMatrix[i], 0, graph.size());
                    System.arraycopy(predecessors, 0, predecessorsMatrix[i], 0, graph.size());
                }
                System.out.println(GREEN + "Bellman-Ford algorithm executed successfully for all sources!" + RESET);
                break;
            }
                
            case 3:{
                boolean result = graph.floydWarshall(costsMatrix, predecessorsMatrix);
                // if (!result) {
                //     System.out.println(RED + "Warning: Negative cycle detected!" + RESET);
                //     hasNegativeCycle = true;
                // } else {
                //     System.out.println(GREEN + "Floyd-Warshall algorithm executed successfully!" + RESET);
                // }
                break;
            }
        }

        if (!hasNegativeCycle) {
            while (true) {
                System.out.println(YELLOW + BOLD + "\n=== MENU ===" + RESET);
                System.out.println("1. Cost of the path between two specific nodes");
                System.out.println("2. Path between two specific nodes");
                System.out.println("3. Return to main menu");
                System.out.print(YELLOW + "Choose an option: " + RESET);
                
                int queryChoice = takeChoiceAndCheckValidity(3, 1, scanner);
                
                if (queryChoice == 3) {
                    break;
                }
                
                System.out.print("Enter source node: ");
                int source = takeChoiceAndCheckValidity(graph.size(), 0, scanner);
                
                System.out.print("Enter destination node: ");
                int destination = takeChoiceAndCheckValidity(graph.size(), 0, scanner);
                
                if (queryChoice == 1) {
                    System.out.println("Cost of shortest path: " + costsMatrix[source][destination]);
                } else {
                    if (choice == 3) { // Floyd-Warshall
                        System.out.println("Shortest path: " + graph.getPathFloyd(source, destination, predecessorsMatrix));
                    } else { // Dijkstra or Bellman-Ford
                        System.out.println("Shortest path: " + graph.getPath_bellman_dijkstra(source, destination, predecessorsMatrix[source]));
                    }
                }
            }
        }
    }

    private static void handle_checkForNegativeCycles(Graph graph, Scanner scanner) {
        System.out.println(YELLOW + BOLD + "\n=== CHECK FOR NEGATIVE CYCLES ===" + RESET);
        System.out.println("Choose algorithm:");
        System.out.println("1. Bellman-Ford Algorithm");
        System.out.println("2. Floyd-Warshall Algorithm");
        System.out.print(YELLOW + "Choose an option: " + RESET);
        
        int choice = takeChoiceAndCheckValidity(2, 1, scanner);
        boolean hasNegativeCycle = false;
        
        switch (choice) {
            case 1:
                int[] costs = new int[graph.size()];
                int[] predecessors = new int[graph.size()];
                hasNegativeCycle = !graph.bellmanFord(0, costs, predecessors);
                break;
                
            case 2:
                int[][] costsMatrix = new int[graph.size()][graph.size()];
                int[][] predecessorsMatrix = new int[graph.size()][graph.size()];
                hasNegativeCycle = !graph.floydWarshall(costsMatrix, predecessorsMatrix);
                break;
        }
        
        if (hasNegativeCycle) {
            System.out.println(RED + "The graph contains negative cycles." + RESET);
        } else {
            System.out.println(GREEN + "The graph does not contain negative cycles." + RESET);
        }
    }

    private static int takeChoiceAndCheckValidity(int maxLimit, int minLimit, Scanner scanner) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice < minLimit || choice > maxLimit) {
                    System.out.println(RED + "Invalid choice. Please try again." + RESET);
                } else {
                    return choice;
                }
            } catch (NumberFormatException e) {
                System.out.print(RED + "Invalid input. Please enter a number: " + RESET);
            }
        }
    }

    
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.Main_Func();
    }
}
