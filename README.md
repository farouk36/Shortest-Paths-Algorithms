# Shortest Path Algorithms: Time and Space Complexity Analysis

## Introduction

This report presents a comprehensive analysis of three fundamental shortest path algorithms implemented in the provided Graph class:

1. **Dijkstra's Algorithm** - For graphs with non-negative edge weights
2. **Bellman-Ford Algorithm** - For graphs that may contain negative edge weights
3. **Floyd-Warshall Algorithm** - For finding shortest paths between all pairs of vertices

Each algorithm has distinct characteristics, advantages, and limitations in terms of time complexity, space requirements, and practical applications. This analysis examines their theoretical performance bounds and provides insights into their relative efficiency under various graph conditions.

## Algorithm Overview

### Dijkstra's Algorithm

Dijkstra's algorithm solves the single-source shortest path problem for graphs with non-negative edge weights. The implementation in the Graph class uses a priority queue to efficiently select the next vertex with the minimum distance, which is a common optimization.

Key features:
- Cannot handle negative edge weights
- Uses a greedy approach to find optimal paths
- Processes vertices in order of increasing distance from the source

### Bellman-Ford Algorithm

The Bellman-Ford algorithm also solves the single-source shortest path problem but can handle graphs with negative edge weights. It can also detect negative cycles, which are cycles in the graph where the total edge weight is negative.

Key features:
- Can handle negative edge weights
- Can detect negative cycles
- Processes all edges in each iteration

### Floyd-Warshall Algorithm

The Floyd-Warshall algorithm solves the all-pairs shortest path problem, finding the shortest paths between every pair of vertices in the graph.

Key features:
- Computes shortest paths between all pairs of vertices
- Can handle negative edge weights (but not negative cycles)
- Uses a dynamic programming approach

## Time Complexity Analysis

### Dijkstra's Algorithm

The time complexity of Dijkstra's algorithm depends on the data structure used for the priority queue:

- **Operations**: Each vertex is added to and removed from the priority queue once
- **Priority Queue Operations**: The implementation uses a binary heap (PriorityQueue in Java)
- **Edge Relaxation**: Each edge is examined at most once

Time complexity analysis:
- Vertex extraction: O(|V| log |V|) - Each extraction takes O(log |V|) time, done |V| times
- Edge relaxation: O(|E| log |V|) - Each relaxation involves a priority queue update which takes O(log |V|) time
- **Overall time complexity**: O((|V| + |E|) log |V|)

In the provided implementation, the use of a PriorityQueue with Edge objects results in this optimal complexity.

### Bellman-Ford Algorithm

Bellman-Ford performs a fixed number of iterations over all edges:

- **Main Loop**: Iterates |V| - 1 times (where |V| is the number of vertices)
- **Inner Loop**: Examines all |E| edges in each iteration
- **Negative Cycle Detection**: One additional pass through all edges

Time complexity analysis:
- Main processing: O(|V| × |E|)
- Negative cycle detection: O(|E|)
- **Overall time complexity**: O(|V| × |E|)

This makes Bellman-Ford slower than Dijkstra's algorithm for graphs without negative weights, but it's more versatile.

### Floyd-Warshall Algorithm

Floyd-Warshall uses a dynamic programming approach with three nested loops:

- **Outer Loop**: Iterates through all |V| vertices as intermediate vertices
- **Middle Loop**: Iterates through all |V| vertices as source vertices
- **Inner Loop**: Iterates through all |V| vertices as destination vertices

Time complexity analysis:
- Three nested loops each iterating |V| times: O(|V|³)
- **Overall time complexity**: O(|V|³)

This cubic time complexity makes Floyd-Warshall suitable only for small to medium-sized graphs, but it computes all shortest paths in a single execution.

## Space Complexity Analysis

### Dijkstra's Algorithm

The space requirements for Dijkstra's algorithm in the implementation are:

- **Distance Array**: O(|V|) - Stores the distance from the source to each vertex
- **Parent Array**: O(|V|) - Stores the parent of each vertex in the shortest path tree
- **Visited Array**: O(|V|) - Tracks visited vertices
- **Priority Queue**: O(|V|) - At most, all vertices could be in the queue
- **Adjacency List**: O(|V| + |E|) - Graph representation

**Overall space complexity**: O(|V| + |E|)

### Bellman-Ford Algorithm

Bellman-Ford's space requirements are simpler:

- **Distance Array**: O(|V|) - Stores the distance from the source to each vertex
- **Parent Array**: O(|V|) - Stores the parent of each vertex in the shortest path tree
- **Edge List**: O(|E|) - Graph representation

**Overall space complexity**: O(|V| + |E|)

### Floyd-Warshall Algorithm

Floyd-Warshall requires matrices to store distances and predecessors:

- **Distance Matrix**: O(|V|²) - Stores distances between all pairs of vertices
- **Predecessor Matrix**: O(|V|²) - Stores the predecessor of each vertex in the shortest path

**Overall space complexity**: O(|V|²)

This quadratic space requirement can be prohibitive for large graphs.

## Performance Comparison

### Single-Source Shortest Path

| Graph Characteristics | Dijkstra | Bellman-Ford | Floyd-Warshall |
|----------------------|----------|--------------|----------------|
| **Sparse Graph** (|E| ≈ |V|) | O(|V| log |V|) | O(|V|²) | O(|V|³) |
| **Dense Graph** (|E| ≈ |V|²) | O(|V|² log |V|) | O(|V|³) | O(|V|³) |
| **Negative Weights** | Not applicable | O(|V| × |E|) | O(|V|³) |

#### Interpretation:

1. **Sparse Graphs**: 
   - Dijkstra's algorithm is clearly superior for single-source shortest path when edge weights are non-negative
   - Bellman-Ford is significantly slower but can handle negative weights
   - Floyd-Warshall is inefficient for single-source problems

2. **Dense Graphs**:
   - Dijkstra's advantage diminishes as graph density increases
   - For very dense graphs, the performance gap between Dijkstra and Bellman-Ford narrows
   - Floyd-Warshall remains the least efficient for single-source problems

### All-Pairs Shortest Path

| Graph Characteristics | Dijkstra | Bellman-Ford | Floyd-Warshall |
|----------------------|----------|--------------|----------------|
| **Sparse Graph** (|E| ≈ |V|) | O(|V|² log |V|) | O(|V|³) | O(|V|³) |
| **Dense Graph** (|E| ≈ |V|²) | O(|V|³ log |V|) | O(|V|⁴) | O(|V|³) |
| **Negative Weights** | Not applicable | O(|V|² × |E|) | O(|V|³) |

#### Interpretation:

1. **Sparse Graphs**: 
   - Running Dijkstra |V| times (once from each source) is more efficient than Floyd-Warshall for sparse graphs
   - Bellman-Ford is less efficient than both alternatives

2. **Dense Graphs**:
   - Floyd-Warshall becomes the most efficient option
   - Dijkstra's algorithm loses its advantage for all-pairs problems in dense graphs
   - Bellman-Ford becomes prohibitively expensive

3. **Negative Weights**:
   - Floyd-Warshall is the most efficient option for graphs with negative weights (but no negative cycles)
   - Bellman-Ford must be run |V| times, making it inefficient for all-pairs problems

## Practical Considerations

### Mean Time to Find Shortest Path Between Two Specific Nodes

1. **Small Graphs** (|V| < 1000):
   - Dijkstra's algorithm is generally the fastest solution for single-pair queries when weights are non-negative
   - If the graph is very dense or has negative weights, pre-computing all shortest paths with Floyd-Warshall may be more efficient

2. **Medium Graphs** (1000 ≤ |V| < 10000):
   - Dijkstra's algorithm remains efficient for on-demand path queries
   - Pre-computing with Floyd-Warshall becomes memory-intensive
   - Bellman-Ford should only be used if negative weights are present

3. **Large Graphs** (|V| ≥ 10000):
   - Floyd-Warshall becomes impractical due to O(|V|²) memory requirements
   - Dijkstra's algorithm is the preferred solution for most scenarios
   - For graphs with negative weights, Bellman-Ford may be the only option despite its inferior performance

### Time to Find Shortest Paths Between All Pairs of Nodes

1. **Small Graphs** (|V| < 100):
   - Floyd-Warshall is the most straightforward and often the most efficient solution
   - The O(|V|³) time complexity is acceptable for small graphs

2. **Medium Graphs** (100 ≤ |V| < 1000):
   - For sparse graphs, running Dijkstra from each source may be faster than Floyd-Warshall
   - For dense graphs, Floyd-Warshall is generally more efficient

3. **Large Graphs** (|V| ≥ 1000):
   - Both approaches become computationally expensive
   - Problem-specific optimizations or approximation methods may be necessary
   - In practice, full all-pairs computation is often avoided for large graphs

### Implementation-Specific Factors

The performance of the provided implementations can be affected by:

1. **Data Structures**:
   - The use of adjacency lists benefits Dijkstra's algorithm
   - The Edge object creation in the priority queue may introduce overhead

2. **Optimizations**:
   - The current Dijkstra implementation marks vertices as visited after processing, preventing redundant relaxation

3. **Memory Access Patterns**:
   - Floyd-Warshall's matrix-based approach has better cache locality than the other algorithms
   - This can make Floyd-Warshall perform better in practice than its theoretical complexity suggests

## Conclusion

Based on the analysis of the three shortest path algorithms:

1. **Dijkstra's Algorithm** is the most efficient choice for single-source shortest path problems in graphs with non-negative edge weights. It performs particularly well in sparse graphs.

2. **Bellman-Ford Algorithm** is necessary when the graph may contain negative edge weights. While slower than Dijkstra's algorithm, it provides the important capability to detect negative cycles.

3. **Floyd-Warshall Algorithm** is the most suitable for all-pairs shortest path problems, especially in dense graphs or when paths need to be queried frequently.

When selecting an algorithm for a specific application:
- Consider the scale of the graph (number of vertices and edges)
- Evaluate whether negative edge weights are present
- Determine if single-source or all-pairs paths are needed
- Assess the frequency of path queries

For scenarios requiring frequent shortest path queries between arbitrary pairs of nodes in moderate-sized graphs, pre-computing all shortest paths with Floyd-Warshall is often the most practical approach despite its higher initial computational cost.

