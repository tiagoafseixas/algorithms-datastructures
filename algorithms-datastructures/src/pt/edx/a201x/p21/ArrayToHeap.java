package pt.edx.a201x.p21;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

class PQueue {

    private int maxSize;
    private int size;
    private int[] heap;

    // keeping track of switches during build heap
    private int switches;
    private ArrayList<ArrayList<Integer>> switchOps;

    public PQueue(final int maxSize) {
        this.maxSize = maxSize;
        this.heap = new int[maxSize];
        this.size = 0;
    }

    public PQueue(final int[] array) {
        this.size = array.length;
        this.maxSize = array.length;
        this.heap = array;
        buildHeap();
    }

    public int getSwitches() {
        return this.switches;
    }

    public ArrayList<ArrayList<Integer>> getSwitchOps() {
        return this.switchOps;
    }

    public int[] getHeap() {
        return this.heap;
    }

    private void buildHeap() {
        this.switches = 0;
        this.switchOps = new ArrayList<ArrayList<Integer>>();
        for(int i = this.size / 2; i >= 0; i--) {
            shiftDown(i);
        }
    }

    public int getParent(final int index) {
        return this.heap[ ( index - 1 ) / 2];
    }

    public int getLeftChild(final int index) {
        return (2 * index) + 1;
    }

    public int getRightChild(final int index) {
        return (2 * index ) + 2;
    }

    public void insert(final int value) {
        if( this.size == this.maxSize ) {
            throw new RuntimeException("Maxsize is reached!");
        }

        this.size = this.size + 1;
        this.heap[this.size] = value;
        shiftUp(size);
    }

    public void shiftUp(int index) {
        int temp;
        int parent = getParent(index);
        while(index > 0 && this.heap[parent] < this.heap[index]) {
            temp = this.heap[parent];
            this.heap[parent] = this.heap[index];
            this.heap[index] = temp;
            index = parent;
        }
    }

    public void shiftDown(final int index) {
        int maxIndex = index;

        int leftChild = getLeftChild(index);
        if (leftChild < size && this.heap[leftChild] <= this.heap[maxIndex]) {
            maxIndex = leftChild;
        }

        int rightChild = getRightChild(index);
        if (rightChild < size && this.heap[rightChild] <= this.heap[maxIndex]) {
            maxIndex = rightChild;
        }

        if(index != maxIndex) {
            int temp = this.heap[maxIndex];
            this.heap[maxIndex] = this.heap[index];
            this.heap[index] = temp;
            this.switchOps.add(this.switches, new ArrayList<Integer>(Arrays.asList(index, maxIndex)));
            this.switches++;
            shiftDown(maxIndex);
        }
    }

    public int ExtractMax() {
        final int result = this.heap[0];
        this.heap[0] = this.heap[this.size];
        this.size = this.size - 1;
        shiftDown(0);
        return result;
    }

    public void remove(final int index) {
        this.heap[index] = Integer.MAX_VALUE;
        shiftUp(index);
        ExtractMax();
    }

    final void changePriority(final int index, final int p) {
        final int oldPriority = this.heap[index];
        this.heap[index] = p;

        if(p > oldPriority ) {
            shiftUp(index);
        } else {
            shiftDown(index);
        }
    }

    public final void sort() {
        final int originalSize = this.size;
        int temp;
        for(int i = 0; i < this.heap.length; i++) {
            temp = this.heap[i];
            this.heap[i] = this.heap[this.size];
            this.heap[this.size] = temp;
            this.size = this.size - 1;
            shiftDown(0);
        }
    }
}

class ArrayToHeapTests {

    private static void log(final String s) {
        System.out.println(s);
    }

    public void test() {
        smallArrayWithTwoIntegers();
        smallArrayWithFiveIntegers();
    }

    public void testBuildHeap(final String testName, final int[] expected, final int[] array) {
        final PQueue pq = new PQueue(array);
        if(Arrays.equals(expected, pq.getHeap())) {
            log("#" + testName + " -> test succeeded! ");
            log("#" + testName + " -> total switches: " + pq.getSwitches());
            for(int i = 0; i < pq.getSwitches(); i++) {
                log("#" + testName + " -> switched " + pq.getSwitchOps().get(i).get(0) + " " + pq.getSwitchOps().get(i).get(1));
            } 
        } else {
            log("#" + testName + " -> test failed! ");
            log("#" + testName + " -> " + Arrays.toString(pq.getHeap()));
        }
    }

    public void smallArrayWithTwoIntegers() {
        final String testName = "smallArrayWithTwoIntegers";
        log(">" + testName);

        final int[] array = {9, 2};
        final int[] expected = {2, 9};

        testBuildHeap(testName, expected, array);
        
        log("<" + testName);
    }

    public void smallArrayWithFiveIntegers() {
        final String testName = "smallArrayWithTwoIntegers";
        log(">" + testName);

        final int[] array = {5, 4, 3, 2, 1};
        final int[] expected = {1, 2, 3, 5, 4};
        
        testBuildHeap(testName, expected, array);
        
        log("<" + testName);
    }
}

public class ArrayToHeap {

    private static final boolean TEST_MODE_ON = false;

    static class FastScanner {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        public FastScanner() {
            reader = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        public String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
            return tokenizer.nextToken();
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }
    
    public static void main(String[] args) {
        if(TEST_MODE_ON) {
            final ArrayToHeapTests aht = new ArrayToHeapTests();
            aht.test();
        } else {
            try {
                final FastScanner in = new FastScanner();
                int n = in.nextInt();
                int[] data = new int[n];
                for (int i = 0; i < n; ++i) {
                    data[i] = in.nextInt();
                }

                final PQueue pq = new PQueue(data);
                System.out.println(pq.getSwitches());
                for(int i = 0; i < pq.getSwitches(); i++) {
                    System.out.println("" + pq.getSwitchOps().get(i).get(0) + " " + pq.getSwitchOps().get(i).get(1));
                } 
            } catch (Exception e) {
                System.out.println(e);
            }
            
        }
    }
}