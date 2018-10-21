package pt.edx.p43;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

class Node {
	int value;
	long distance;
	long minDistance;
	Node predecessor;
	ArrayList<Edge> edges;

	Node(int v) {
		value = v;
		edges = new ArrayList<>();
		predecessor = null;
		minDistance = Long.MAX_VALUE;
	}
	
	public String toString() {
		return "{ value : " + value + " , distance : " + distance + "}";
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
		for (int i = 0; i < n; i++) {
			nodes[i] = new Node(i + 1);
		}
		nrEdges = e;
	}
}

public class OptimalExchange {

	static void hasNegativeWeight(Graph g, int source) {
		// Initialize the graph
		for (Node n : g.nodes) {
			n.predecessor = null;
			n.distance = Long.MAX_VALUE;
		}

		g.nodes[source].distance = 0;
		LinkedList<Node> q = new LinkedList<>();
		q.add(g.nodes[source]);
		
		Node t;
		while(false == q.isEmpty()) {
			t = q.pop();
			for(Edge e : t.edges) {
				if (e.source.distance != Long.MAX_VALUE && e.source.distance + e.weight < e.destination.distance) {
					if(e.destination.distance != Long.MAX_VALUE && true == isPredecessor(t, e.destination)) {
						//System.out.println("Found negative loop from " + e.source + " to " + e.destination);
						setNegativeLoop(e.destination, e.source);
					} else {
						e.destination.distance = e.source.distance + e.weight;
						e.destination.predecessor = e.source;
						q.add(e.destination);
					}
				}
			}
		}
	}

	public static boolean isPredecessor(Node n, Node p) {
		while(n != null) {
			if(n.predecessor == p) {
				return true;
			}
			
			n = n.predecessor;
		}
		return false;
	}
	public static void setNegativeLoop(final Node negFirst, Node negDetect) {
		negFirst.minDistance = Long.MIN_VALUE;
		while (negDetect != null && negDetect.value != negFirst.value) {
			negDetect.minDistance = Long.MIN_VALUE;
			negDetect = negDetect.predecessor;
		}
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
		final int s = scanner.nextInt();
		scanner.close();
		hasNegativeWeight(g, s - 1);
		for (int i = 0; i < n; i++) {
			if (g.nodes[i].minDistance == Long.MIN_VALUE) {
				System.out.println("-");
			} else if (g.nodes[i].distance == Long.MAX_VALUE) {
				System.out.println("*");
			} else {
				System.out.println(g.nodes[i].distance);
			}
			// System.out.println("" + g.nodes[i].value + " " + g.nodes[i].distance + " " +
			// g.nodes[i].minDistance);
		}
	}
}
