package pt.edx.a202x.p41;

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

    Node (int v, long d) {
        value = v;
        distance = d;
    }
    
    public void addConnection(Edge n) {
		this.edges.add(n);
    }
    
    public String toString() {
        return "{ value = " + value + ", distance = " + distance + " }";
    }
}

class Edge {
	Node destination;
	long weight;
	
	Edge(int w, Node d) {
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
    
    public void addEdge(final int a, final int b) {
        addEdge(a, b, 1);
    }

    public void addEdge(final int a, final int b, final int w) {
        nodes[a-1].addConnection(new Edge(w, nodes[b-1]));
    }
}

class MinQueue {

	protected Node[] heap;
	protected int size;
	protected int maxSize;
	
	public MinQueue(final int maxSize) {
		this.size = 0;
		this.maxSize = maxSize;
		this.heap = new Node[maxSize];
	}

	public Node[] getHeap() {
		return this.heap;
	}

	public void insert(Node n) {
		if(size == maxSize) {
			throw new RuntimeException("Maxsize is reached: " + size);
		}
		this.heap[this.size] = n;
		siftUp(this.size);
		this.size += 1;
	}

	public void siftUp(int i) {
		while(i > 0 && greater(heap[getParentIndex(i)], heap[i]) == 1) {
			exchange(getParentIndex(i), i);
            i = getParentIndex(i);
		}
    }
    
    protected void exchange(final int i, final int j) {
        Node nodeI = this.heap[i];
        Node nodeJ = this.heap[j];
        this.heap[i] = nodeJ;
        this.heap[j] = nodeI;
    }

	public void siftDown(int index) {
		int maxIndex = index;

        int leftChild = getLeftChildIndex(index);
        if (leftChild < this.size && greater(heap[maxIndex], heap[leftChild]) == 1) {
            maxIndex = leftChild;
        }

        int rightChild = getRightChildIndex(index);
        if (rightChild < this.size && greater(heap[maxIndex], heap[rightChild]) == 1) {
            maxIndex = rightChild;
        }

        if(index != maxIndex) {
            exchange(index, maxIndex);
            siftDown(maxIndex);
        }

	}

	public Node extract() {
		final Node result = this.heap[0];
        this.heap[0] = this.heap[this.size - 1];
        this.heap[this.size - 1] = null;
        this.size = this.size - 1;
        siftDown(0);
        return result;
	}

	public void remove(int i) {
		this.heap[i].distance = Long.MAX_VALUE;
        siftUp(i);
        extract();
    }
	
	public int getLeftChildIndex(final int index) {
        return (2 * index) + 1;
    }

    public Node getLeftChild(final int index) {
        return this.heap[getLeftChildIndex(index)];
    }

    public int getRightChildIndex(final int index) {
        return (2 * index ) + 2;
    }
    
	public Node peek() {
		return this.heap[0];
	}
	
	public int find(double v) {
		for(int i = 0; i < this.size; i++) {
			if(v == this.heap[i].value) {
				return i;
			}
		}
		return -1;
	}
	
    public Node getRightChild(final int index) {
        return this.heap[getRightChildIndex(index)];
    }
	
	public void buildHeap() {
        for(int i = this.size / 2; i >= 0; i--) {
            siftDown(i);
        }
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public int getSize() {
		return this.size;
    }
    
	public int getParentIndex(int i) {
		return ( i - 1 ) / 2;
    }
    
    public int greater(Node n1, Node n2) {
		if(n1.distance > n2.distance) {
			return 1;
		} else if (n1.distance < n2.distance) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public void setPriority(int index, long v) {
		this.heap[index].distance = v;
	}

	public long getPriority(int index) {
		return this.heap[index].distance;
	}
}

public class Dijkstra {

    static void dijkstra(final Graph g, final int s) {
        for(Node n : g.nodes ) {
            n.predecessor = null;
            n.distance = Long.MAX_VALUE;
        }
        g.nodes[s].distance = 0;

        MinQueue pq = new MinQueue(g.nodes.length);
        for(Node n : g.nodes) {
            pq.insert(n);
        }

        Node t;
		int idx;
		while(pq.getSize()> 0) {
			t = pq.extract();
			for(Edge e : t.edges) {
                if(t.distance != Long.MAX_VALUE && e.destination.distance > t.distance + e.weight ) {
                    e.destination.distance = t.distance + e.weight;
                    e.destination.predecessor = t;
                    idx = pq.find(e.destination.value);
				    if(idx > 0) { pq.siftUp(idx); }
                }
			}
		}
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Graph g = new Graph(n , m);
        
        for (int i = 0; i < m; i++) {
            int x, y, w;
            x = scanner.nextInt();
            y = scanner.nextInt();
            w = scanner.nextInt();
            g.addEdge(x, y, w);
        }
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        scanner.close();
		dijkstra(g, x - 1);
		long d = g.nodes[y - 1].distance;
        System.out.println(d < Integer.MAX_VALUE ? d : -1);
    }
}