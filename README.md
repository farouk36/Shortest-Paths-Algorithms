# Shortest Path Algorithms

## Overview

This repository contains implementations of three fundamental shortest path algorithms in a Graph class:

1. **Dijkstra's Algorithm** - For graphs with non-negative edge weights
2. **Bellman-Ford Algorithm** - For graphs that may contain negative edge weights
3. **Floyd-Warshall Algorithm** - For finding shortest paths between all pairs of vertices

## Algorithm Details

### Dijkstra's Algorithm

Solves the single-source shortest path problem for graphs with non-negative edge weights. Our implementation uses a priority queue for optimal performance.

**Key Features:**
- Cannot handle negative edge weights
- Uses a greedy approach to find optimal paths
- Processes vertices in order of increasing distance from source
- Time Complexity: O((|V| + |E|) log |V|)
- Space Complexity: O(|V| + |E|)

### Bellman-Ford Algorithm

Solves the single-source shortest path problem and can handle graphs with negative edge weights.

**Key Features:**
- Can handle negative edge weights
- Can detect negative cycles
- Processes all edges in each iteration
- Time Complexity: O(|V| × |E|)
- Space Complexity: O(|V| + |E|)

### Floyd-Warshall Algorithm

Solves the all-pairs shortest path problem, finding shortest paths between every pair of vertices.

**Key Features:**
- Computes shortest paths between all pairs of vertices
- Can handle negative edge weights (but not negative cycles)
- Uses a dynamic programming approach
- Time Complexity: O(|V|³)
- Space Complexity: O(|V|²)

## Performance Comparison

### Single-Source Shortest Path

| Graph Characteristics | Dijkstra | Bellman-Ford | Floyd-Warshall |
|-----------------------|----------|--------------|----------------|
| Sparse Graph (E ≈ V)  | O(V log V) | O(V²) | O(V³) |
| Dense Graph (E ≈ V²)  | O(V² log V) | O(V³) | O(V³) |
| Negative Weights      | Not applicable | O(V × E) | O(V³) |

### All-Pairs Shortest Path

| Graph Characteristics | Dijkstra | Bellman-Ford | Floyd-Warshall |
|-----------------------|----------|--------------|----------------|
| Sparse Graph (E ≈ V)  | O(V² log V) | O(V³) | O(V³) |
| Dense Graph (E ≈ V²)  | O(V³ log V) | O(V⁴) | O(V³) |
| Negative Weights      | Not applicable | O(V² × E) | O(V³) |

## Usage Guidelines

### When to use each algorithm:

- **Dijkstra's Algorithm**: Best for single-source shortest path in graphs with non-negative weights, especially sparse graphs.
- **Bellman-Ford Algorithm**: Use when negative edge weights are present or when negative cycle detection is needed.
- **Floyd-Warshall Algorithm**: Optimal for all-pairs shortest path problems, especially in dense graphs or when paths need to be queried frequently.

### Recommendations by Graph Size:

#### Small Graphs (|V| < 100):
- Floyd-Warshall is straightforward and efficient for all-pairs problems
- Dijkstra for single-source/pair queries with non-negative weights

#### Medium Graphs (100 ≤ |V| < 1000):
- For sparse graphs, running Dijkstra from each source may be faster than Floyd-Warshall
- For dense graphs, Floyd-Warshall is generally more efficient
- Bellman-Ford only when negative weights are present

#### Large Graphs (|V| ≥ 1000):
- Floyd-Warshall becomes impractical due to O(|V|²) memory requirements
- Dijkstra's algorithm is preferred for most scenarios
- Consider problem-specific optimizations for all-pairs problems

## Implementation Notes

Our implementations use the following optimizations:
- Adjacency lists for efficient graph representation
- PriorityQueue in Dijkstra's algorithm for optimal vertex selection
- Vertex visitation marking to prevent redundant relaxation in Dijkstra's algorithm

## License

[Include your license information here]

## Contributing

[Include contribution guidelines here]

## Contact

[Include contact information here]
