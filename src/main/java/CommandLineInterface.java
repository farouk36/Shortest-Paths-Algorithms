
import java.util.Scanner;

public class CommandLineInterface {
    //? ANSI escape codes for colors
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";

    public void Main_Func (){
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
                    handle_singleSourceShortestPaths(graph, scanner);
                    
                    System.out.println(GREEN + "Single-source shortest paths algorithm executed successfully!" + RESET);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine(); //? Wait for user to press Enter
                    break;
                }
                    
                case 2 -> {
                    handle_allPairsShortestPaths(graph, scanner);

                    System.out.println(GREEN + "All-pairs shortest paths algorithm executed successfully!" + RESET);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine(); //? Wait for user to press Enter
                    break;
                }
                    
                case 3 -> {
                    handle_checkForNegativeCycles(graph, scanner);

                    System.out.println(GREEN + "Negative cycle check executed successfully!" + RESET);
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine(); //? Wait for user to press Enter
                    break;
                }
                   
                case 4 -> {
                    System.out.println(GREEN + "Exiting the program. Goodbye!" + RESET);
                    scanner.close();
                    return;
                }
            } 

        }
    }

    private static void handle_singleSourceShortestPaths(Graph graph, Scanner scanner) {

    }

    private static void handle_allPairsShortestPaths(Graph graph, Scanner scanner) {
        
    }
    private static void handle_checkForNegativeCycles(Graph graph, Scanner scanner) {
        
    }

    private  static int takeChoiceAndCheckValidity(int maxLimit, int minLimit, Scanner scanner){
        while (true){
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice < minLimit || choice > maxLimit) {
                    System.out.println(RED + "Invalid choice. Please try again." + RESET);
                } else {
                    return choice;
                }
            } catch (NumberFormatException e) {
                scanner.nextLine(); //? Clear the invalid input
                System.out.print(RED + "Invalid input. Please enter a number: " + RESET);
            }
        }
    }
    

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.Main_Func();
    }
}
