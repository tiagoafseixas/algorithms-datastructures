package pt.edx.a201x.p12;

import java.util.*;
import java.io.*;

public class tree_height {

	private static boolean TEST_MODE_ON = true;

    private static void log(final String s) {
        if (TEST_MODE_ON == true) {
            System.out.println(s);
        }
	}
	
    class FastScanner {
		StringTokenizer tok = new StringTokenizer("");
		BufferedReader in;

		FastScanner() {
			in = new BufferedReader(new InputStreamReader(System.in));
		}

		String next() throws IOException {
			while (!tok.hasMoreElements())
				tok = new StringTokenizer(in.readLine());
			return tok.nextToken();
		}
		int nextInt() throws IOException {
			return Integer.parseInt(next());
		}
	}

	class Node {
		int id;
		ArrayList<Node> children;
		Node parent;

		public Node() { this.children = new ArrayList<Node>(); }
		public Node(final int id) {
			this.id = id;
			this.children = new ArrayList<Node>();
		}
		public Node(final int id, final Node parent) {
			this.id = id;
			this.parent = parent;
			this.children = new ArrayList<Node>();
		}

		public void addChild(final Node n) {
			this.children.add(n);
		}

		public ArrayList<Node> getChildren() {
			return this.children;
		}
	}

	class Tree {

		Node root;
		Node[] nodes;

		public Tree(final int numberOfNodes) {
			this.nodes = new Node[numberOfNodes];

			// Initialize the Node Array
			for(int i = 0; i < numberOfNodes; i++) {
				nodes[i] = new Node(i);
			}
		}

		public void parseTreeFromParentArray(final int[] parents) {

			for(int i = 0; i < parents.length; i++) {
				if(parents[i] == -1) {
					this.root = nodes[i];
				} else {
					nodes[parents[i]].addChild(nodes[i]);
				}
			}
		}

		public int getHeight() {
			return this.getHeightNode(this.root);
		}

		private int getHeightNode(final Node n) {
			if(n == null) { return 0; }
			if(null == n.getChildren() || true == n.getChildren().isEmpty()) {return 1;}

			int height = 1;
			for(Node current: n.getChildren()) {
				height = Math.max(height, getHeightNode(current) + 1);
			}
			return height;
		}
	}

	class TreeHeightTests {
		
		final void test() {
			oneElementTree();
			balancedThreeElementsHeightTwo();
			unbalancedThreeElementsHeightThree();
			balancedTenPowFiveElementsHeightTenPowFive();
		}

		private final void testTreeHeight(final String testName, final int expected, final int[] parent, final int n) {
			final TreeHeight th = new TreeHeight();
			th.n = n;
			th.parent = parent;

			final int result = th.computeHeight();

			if(result == expected) {
				log("#" + testName + " -> TEST SUCCEEDED");
			} else {
				log("#" + testName + " -> TEST FAILED");
			}
		}

		final void oneElementTree() {
			final String testName = "oneElementTree";
			log(">" + testName);

			final int expected = 1;
			final int n = 1;
			final int[] parent = {-1};

			this.testTreeHeight(testName, expected, parent, n);

			log("<" + testName);
		}

		final void balancedThreeElementsHeightTwo() {
			final String testName = "balancedThreeElementsHeightTwo";
			log(">" + testName);

			final int expected = 2;
			final int n = 3;
			final int[] parent = {-1, 0, 0};

			this.testTreeHeight(testName, expected, parent, n);

			log("<" + testName);
		}

		final void unbalancedThreeElementsHeightThree() {
			final String testName = "unbalancedThreeElementsHeightThree";
			log(">" + testName);

			final int expected = 3;
			final int n = 3;
			final int[] parent = {-1, 0, 1};

			this.testTreeHeight(testName, expected, parent, n);

			log("<" + testName);
		}

		final void balancedTenPowFiveElementsHeightTenPowFive() {
			final String testName = "balancedTenPowFiveElementsHeightTenPowFive";
			log(">" + testName);

			final int expected = (int) Math.pow(10, 5);
			final int n = (int) Math.pow(10, 5);
			final int[] parent = new int[n];

			for(int i = 0; i < n; i++) {
				if(i == 0) {
					parent[i] = -1;
				} else {
					parent[i] = i - 1;
				}
			}

			this.testTreeHeight(testName, expected, parent, n);

			log("<" + testName);
		}
	}

	public class TreeHeight {
		int n;
		int parent[];
		
		void read() throws IOException {
			FastScanner in = new FastScanner();
			n = in.nextInt();
			parent = new int[n];
			for (int i = 0; i < n; i++) {
				parent[i] = in.nextInt();
			}
		}

		int computeHeight() {
			final Tree t = new Tree(n);
			t.parseTreeFromParentArray(parent);
			return t.getHeight();
		}
	}

	static public void main(String[] args) throws IOException {

		if(TEST_MODE_ON) {
			new Thread(null, new Runnable() {
				public void run() {
					try {
						new tree_height().test();
					} catch (IOException e) {
					}
				}
			}, "1", 1 << 26).start();
		} else {
			new Thread(null, new Runnable() {
				public void run() {
					try {
						new tree_height().run();
					} catch (IOException e) {
					}
				}
			}, "1", 1 << 26).start();
		}
	}

	public void run() throws IOException {
		TreeHeight tree = new TreeHeight();
		tree.read();
		System.out.println(tree.computeHeight());
	}

	public void test() throws IOException {
		new TreeHeightTests().test();
	}
}
