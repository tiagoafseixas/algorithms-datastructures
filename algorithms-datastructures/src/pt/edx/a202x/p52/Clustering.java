package pt.edx.a202x.p52;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;


class Edge {
	Node destination;
	Node source;
	double weight;
	
	Edge(double w, Node d, Node s) {
		destination = d;
		source = s;
		weight = w;
	}
}

class Node {
	int value;
	int coordY;
	int coordX;
	Node predecessor;
	ArrayList<Edge> edges;
	
	Node(int v, int y, int x) {
		value = v;
		coordY = y;
		coordX = x;
		edges = new ArrayList<>();
	}
	
	public void addConnection(Edge n) {
		this.edges.add(n);
    }
	
	public double getDistance(Node toNode) {
		return (double) Math.sqrt(
				Math.pow( (double) coordX - (double) toNode.coordX, 2.0 ) +
				Math.pow( (double) coordY - (double) toNode.coordY, 2.0 )
		);
	}
    
    public String toString() {
        return "{ value = " + value + ", coordX = " + coordX + ", coordY = " + coordY + " }";
    }
}

class SortEdgeByWeight implements Comparator<Edge> {

	@Override
	public int compare(Edge arg0, Edge arg1) {
		if(arg0.weight > arg1.weight) {
			return 1;
		} else if (arg0.weight < arg1.weight) {
			return -1;
		} else {
			return 0;
		}
	}
	
}

class Graph {
	int nrNodes;
	Node[] nodes;
	ArrayList<Edge> edges;
	
	Graph(int n) {
		nrNodes = n;
		nodes = new Node[n];
		edges = new ArrayList<>();
		for(int i = 0; i < n; i++) {
			nodes[i] = new Node(i, 0, 0);
		}
    }
    
    public void addEdge(final int a, final int b) {
        addEdge(a, b, 1);
    }
    
    public void orderEdges() {
    	Collections.sort(edges, new SortEdgeByWeight());
    }

    public void addEdge(final int a, final int b, final double w) {
    	final Edge e = new Edge(w, nodes[a], nodes[b]);
        nodes[a].addConnection(e);
        nodes[b].addConnection(e);
        edges.add(e);
    }
}
class DisjointSet {
	
	int[] parent;
    int[] rank;
    Node[] nodes;
    int clusters;
    int maxRows;

    public DisjointSet(final int size) {
        this.parent = new int[size];
        this.rank = new int[size];
        this.nodes = new Node[size];
        this.clusters = 0;
    }

    public void makeSet(final int i, final Node n) {
        this.parent[i] = i;
        this.rank[i] = 0;
        this.nodes[i] = n;
        this.clusters += 1;
    }

    public int find(final int i) {
        if(i != parent[i]) {
            parent[i] = find(parent[i]);
            nodes[i].predecessor = nodes[parent[i]];
        }

        return parent[i];
    }

    public void union(final int i, final int j) {
        final int iId = find(i);
        final int jId = find(j);

        if (iId == jId) { return; }

        if(rank[iId] > rank[jId]) {
            parent[jId] = iId;
            nodes[jId].predecessor = nodes[iId];
        } else {
            parent[iId] = jId;
            nodes[iId].predecessor = nodes[jId];
            
            if(rank[iId] == rank[jId]) {
                rank[jId] = rank[jId] + 1;
            }
        }
        
        this.clusters -= 1;
    }

    public Node get(final int i) {
        return this.nodes[i];
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows() {
        return maxRows;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.nodes);
    }
}

public class Clustering {

	static double kruskal(final Graph g, final int k) {
		
		DisjointSet ds = new DisjointSet(g.nrNodes);
		for(int i = 0; i < g.nrNodes; i++) {
			ds.makeSet(i, g.nodes[i]);
		}
		
		g.orderEdges();
		
		ArrayList<Edge> usedEdges = new ArrayList<>();
		Edge e;
		for(int i = 0; i < g.edges.size(); i++) {
			e = g.edges.get(i);
			if(ds.find(e.source.value) != ds.find(e.destination.value)) {
				ds.union(e.source.value, e.destination.value);
				usedEdges.add(e);
			}
		}

		return usedEdges.get(usedEdges.size() - ( k - 1 ) ).weight;
	}
	
	public static final void main (String[] args) {
		Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        
        final Graph g = new Graph(n);
        for (int i = 0; i < n; i++) {
            g.nodes[i].coordX = scanner.nextInt();
            g.nodes[i].coordY = scanner.nextInt();
        }
        
        for(int i = 0; i < n; i++) {
        	for(int j = i + 1; j < n; j++) {
        		g.addEdge(i, j, g.nodes[i].getDistance(g.nodes[j]));
        	}
        }
        System.out.println(kruskal(g, scanner.nextInt()));
        scanner.close();
	}
}
