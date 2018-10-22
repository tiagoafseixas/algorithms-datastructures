package pt.edx.a202x.p42;

import java.util.ArrayList;
import java.util.Scanner;

class Node {
	int value;
	long distance;
	Node predecessor;
	ArrayList<Edge> edges;
	Node(int v) {
		value = v;
		edges = new ArrayList<>();
		predecessor = null;
	}
}

class Edge {
	Node source;
	Node destination;
	long weight;
	
	Edge(Node s, Node d, int w) {
		source = s;
		destination = d;
		weight = w;
	}
}

class Graph {
	int nrNodes, nrEdges;
	Node[] nodes;
	
	Graph(int n, int e) {
		nodes = new Node[n];
		for(int i = 0; i < n; i++) {
			nodes[i] = new Node(i);
		}
		nrEdges = e;
	}
}

public class BellmanFord {

	static boolean hasNegativeWeight(Graph g, int source) {
		// Initialize the graph
		for(Node n : g.nodes) {
			n.predecessor = null;
			n.distance = Long.MAX_VALUE;
		}
		
		g.nodes[source].distance = 0;
		
		// Relax the edges
		for(int i = 0; i < g.nodes.length; i++) {
			for(Edge e : g.nodes[i].edges) {
				if (e.source.distance != Long.MAX_VALUE && e.source.distance + e.weight < e.destination.distance) {
					e.destination.distance = e.source.distance + e.weight;
					e.destination.predecessor = e.source;
				}
			}
		}
		
		// Check for negative weight cycles
		for(int i = 0; i < g.nodes.length; i++) {
			for(Edge e : g.nodes[i].edges) {
				if (e.source.distance != Long.MAX_VALUE && 
						e.source.distance + e.weight < e.destination.distance
				) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        final Graph g = new Graph(n, m);
        for (int i = 0; i < m; i++) {
            int x, y, w;
            x = scanner.nextInt();
            y = scanner.nextInt();
            w = scanner.nextInt();
            g.nodes[x - 1].edges.add(new Edge(g.nodes[x - 1], g.nodes[y - 1], w));
        }
        scanner.close();
        boolean hasNeg = false;
        for(int i = 0; i < n; i++) {
        	hasNeg = hasNegativeWeight(g, i);
        }
        System.out.println(hasNeg == true ? "1" : "0");
	}
}
