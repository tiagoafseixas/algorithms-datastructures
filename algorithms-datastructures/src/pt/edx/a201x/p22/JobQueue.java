package pt.edx.a201x.p22;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

class Worker implements Comparable {
    private long id;
    private long nextAvailableTime;

    public Worker(final long id) {
        this.id = id;
        this.nextAvailableTime = 0L;
    }

    public void addTime(final long v) {
        this.nextAvailableTime += v;
    }

    public long getNextAvailableTime() {
        return this.nextAvailableTime;
    }

    public long getId() {
        return this.id;
    }

    /**
     * @param nextAvailableTime the nextAvailableTime to set
     */
    public void setNextAvailableTime(long nextAvailableTime) {
        this.nextAvailableTime = nextAvailableTime;
    }
    
    @Override
    public int compareTo(Object w2) {
        if(this.getNextAvailableTime() < ((Worker) w2).getNextAvailableTime()) {
            return 1;
        } else if (((Worker) w2).getNextAvailableTime() < this.getNextAvailableTime()) {
            return -1;
        } else if (this.getId() < ((Worker) w2).getId()) {
            return 1;
        } else {
            return -1;
        } 
    }
}

class PQueue {

    private int maxSize;
    private int size;
    private Worker[] heap;

    // keeping track of switches during build heap
    private int switches;
    private ArrayList<ArrayList<Integer>> switchOps;

    public PQueue(final int maxSize) {
        this.maxSize = maxSize;
        this.heap = new Worker[maxSize];
        this.size = 0;
    }

    public PQueue(final Worker[] array) {
        this.size = array.length - 1;
        this.maxSize = array.length - 1;
        this.heap = array;
        buildHeap();
    }

    public int getSwitches() {
        return this.switches;
    }

    public ArrayList<ArrayList<Integer>> getSwitchOps() {
        return this.switchOps;
    }

    public Worker[] getHeap() {
        return this.heap;
    }

    private void buildHeap() {
        this.switches = 0;
        this.switchOps = new ArrayList<ArrayList<Integer>>();
        for(int i = this.size / 2; i >= 0; i--) {
            shiftDown(i);
        }
    }

    public int getParentIndex(final int index) {
        return ( index - 1 ) / 2;
    }

    public Worker getParent(final int index) {
        return this.heap[getParentIndex(index)];
    }

    public int getLeftChildIndex(final int index) {
        return (2 * index) + 1;
    }

    public Worker getLeftChild(final int index) {
        return this.heap[getLeftChildIndex(index)];
    }

    public int getRightChildIndex(final int index) {
        return (2 * index ) + 2;
    }

    public Worker getRightChild(final int index) {
        return this.heap[getRightChildIndex(index)];
    }

    public void insert(final Worker value) {
        if( this.size == this.maxSize ) {
            throw new RuntimeException("Maxsize is reached!");
        }

        this.size = this.size + 1;
        this.heap[this.size] = value;
        shiftUp(size);
    }

    public void shiftUp(int index) {
        Worker temp;
        int parent = getParentIndex(index);
        while(index > 0 && this.heap[index].compareTo(this.heap[parent]) == 1) {
            temp = this.heap[parent];
            this.heap[parent] = this.heap[index];
            this.heap[index] = temp;
            index = parent;
            parent = getParentIndex(index);
        }
    }


    public void shiftDown(final int index) {
        int maxIndex = index;

        int leftChild = getLeftChildIndex(index);
        if (leftChild <= this.size && this.heap[maxIndex].compareTo(this.heap[leftChild]) == -1) {
            maxIndex = leftChild;
        }

        int rightChild = getRightChildIndex(index);
        if (rightChild <= this.size && this.heap[maxIndex].compareTo(this.heap[rightChild]) == -1) {
            maxIndex = rightChild;
        }

        if(index != maxIndex) {
            Worker temp = this.heap[maxIndex];
            this.heap[maxIndex] = this.heap[index];
            this.heap[index] = temp;
            this.switchOps.add(this.switches, new ArrayList<Integer>(Arrays.asList(index, maxIndex)));
            this.switches++;
            shiftDown(maxIndex);
        }
    }

    public Worker ExtractMax() {
        final Worker result = this.heap[0];
        this.heap[0] = this.heap[this.size];
        this.size = this.size - 1;
        shiftDown(0);
        return result;
    }

    public void remove(final int index) {
        this.heap[index].setNextAvailableTime(Long.MAX_VALUE);
        shiftUp(index);
        ExtractMax();
    }

    final void changePriority(final int index, final Worker p) {
        final long oldPriority = this.heap[index].getNextAvailableTime();
        this.heap[index] = p;

        if(p.getNextAvailableTime() > oldPriority ) {
            shiftUp(index);
        } else {
            shiftDown(index);
        }
    }
}

class JobQueueService {

    public static final long[][] assignJobs(final long[] jobs, final int nrWorkers) {

        final Worker[] workers = new Worker[nrWorkers];
        for(int i = 0; i < nrWorkers; i++) { workers[i] = new Worker(i); }
        
        final PQueue pq = new PQueue(workers);
        Worker w;

        long[][] result = new long[jobs.length][2];
        int nrJobs = 0;
        for(int i = 0; i < jobs.length; i++) {
            w = pq.ExtractMax();
            result[nrJobs] = new long[] {w.getId(), w.getNextAvailableTime() };

            // update the finish time and reinsert into the tree
            w.setNextAvailableTime(w.getNextAvailableTime() + jobs[i]);
            pq.insert(w);
            nrJobs++;
        }

        return result;
    }

}

class JobQueueTests {

    private static void log(final String s) {
        System.out.println(s);
    }

    public void test() {
       /* fiveJobsWithTwoThreads();
        twentyJobsWithFourThreads();
        smallJobQueueWithTwoThreads();
        mediumJobQueueAllOneSecondWithTenThreads();
        fourJobsWithLongTimesAndTwoWorkers();
        sixJobsWithLongTimesAndTwoWorkers();
        sixJobsWithLongTimesAndTenWorkers();*/
        hundredJobsWithLongTimesAndTenWorkers();
    }

    public void testAssignJobs(final String testName, final long[] jobs, final int nrWorkers, final long[][] expected) {
        long[][] result = JobQueueService.assignJobs(jobs, nrWorkers);

        if(Arrays.deepEquals(expected, result)) {
            log("#" + testName + " -> TEST PASSED!");
        } else {
            log("#" + testName + " -> TEST Failed!");
        }

        for(long[] res : result) {
            log("#" + testName + " -> result: " + Arrays.toString(res));
        }
    }
    /**
     * Test where the first job takes longer than the rest of the jobs to complete.
     * The first job should be executed by the first thread an the remaining by the second.
     */
    public void smallJobQueueWithTwoThreads() {
        final String testName = "smallJobQueueWithTwoThreads";
        log(">" + testName);
        int nrWorkers = 2;
        long[] jobs = {5, 2, 1, 1};
        long[][] expected = { {0, 0}, {1, 0}, {1, 2}, {1, 3}};
        testAssignJobs(testName, jobs, nrWorkers, expected);
        log("<" + testName);
    }

    public void mediumJobQueueAllOneSecondWithTenThreads() {
        final String testName = "mediumJobQueueAllOneSecondWithTenThreads";
        log(">" + testName);
        int nrWorkers = 10;
        long[] jobs = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        long[][] expected = { 
            {0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0},
            {6, 0}, {7, 0}, {8, 0}, {9, 0}, {0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1},
            {5, 1}, {6, 1}, {7, 1}, {8, 1}};
        testAssignJobs(testName, jobs, nrWorkers, expected);
        log("<" + testName);
    }

    public void fiveJobsWithTwoThreads() {
        final String testName = "smallJobQueueWithTwoThreads";
        log(">" + testName);
        int nrWorkers = 2;
        long[] jobs = {1, 2, 3, 4, 5};
        long[][] expected = {{0 , 0}, {1 , 0}, {0 , 1}, {1 , 2}, {0 , 4}};
        testAssignJobs(testName, jobs, nrWorkers, expected);
        log("<" + testName);
    }

    public void twentyJobsWithFourThreads() {
        final String testName = "twentyJobsWithFourThreads";
        log(">" + testName);
        int nrWorkers = 4;
        long[] jobs = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        long[][] expected = {
            {0 , 0}, {1 , 0}, {2 , 0}, {3 , 0}, 
            {0 , 1}, {1 , 1}, {2 , 1}, {3 , 1}, 
            {0 , 2}, {1 , 2}, {2 , 2}, {3 , 2}, 
            {0 , 3}, {1 , 3}, {2 , 3}, {3 , 3}, 
            {0 , 4}, {1 , 4}, {2 , 4}, {3 , 4}
        };
        testAssignJobs(testName, jobs, nrWorkers, expected);
        log("<" + testName);
    }

    public void fourJobsWithLongTimesAndTwoWorkers() {
        final String testName = "fourJobsWithLongTimesAndTwoWorkers";
        log(">" + testName);
        int nrWorkers = 2;
        long[] jobs = {(long) Math.pow(10, 9), (long) Math.pow(10, 9), (long) Math.pow(10, 9), (long) Math.pow(10, 9)};
        long[][] expected = {
            {0 , 0}, {1 , 0}, 
            {0 , (long) Math.pow(10, 9)}, {1 , (long) Math.pow(10, 9)}
        };
        testAssignJobs(testName, jobs, nrWorkers, expected);
        log("<" + testName);
    }

    public void sixJobsWithLongTimesAndTwoWorkers() {
        final String testName = "sixJobsWithLongTimesAndTwoWorkers";
        log(">" + testName);
        int nrWorkers = 2;
        long[] jobs = {
            (long) Math.pow(10, 9), (long) Math.pow(10, 9),
            (long) Math.pow(10, 9), (long) Math.pow(10, 9),
            (long) Math.pow(10, 9), (long) Math.pow(10, 9)
        };
        long[][] expected = {
            {0 , 0}, {1 , 0}, 
            {0 , (long) Math.pow(10, 9)}, {1 , (long) Math.pow(10, 9)},
            {0 , (long) Math.pow(10, 9) * 2}, {1 , (long) Math.pow(10, 9) * 2}
        };
        testAssignJobs(testName, jobs, nrWorkers, expected);
        log("<" + testName);
    }

    public void sixJobsWithLongTimesAndTenWorkers() {
        final String testName = "sixJobsWithLongTimesAndTenWorkers";
        log(">" + testName);
        int nrWorkers = 10;
        long[] jobs = {
            (long) Math.pow(10, 9), (long) Math.pow(10, 9),
            (long) Math.pow(10, 9), (long) Math.pow(10, 9),
            (long) Math.pow(10, 9), (long) Math.pow(10, 9)
        };
        long[][] expected = {
            {0 , 0}, {1 , 0}, 
            {2 , 0}, {3, 0},
            {4 , 0}, {5 , 0}
        };
        testAssignJobs(testName, jobs, nrWorkers, expected);
        log("<" + testName);
    }

    public void hundredJobsWithLongTimesAndTenWorkers() {
        final String testName = "hundredJobsWithLongTimesAndTenWorkers";
        log(">" + testName);
        int nrWorkers = 10;
        long[] jobs = {
            124860658, 388437511, 753484620, 349021732, 311346104, 235543106,
            665655446, 28787989, 706718118, 409836312, 217716719, 757274700,
            609723717, 880970735, 972393187, 246159983, 318988174, 209495228,
            854708169, 945600937, 773832664, 587887000, 531713892, 734781348,
            603087775, 148283412, 195634719, 968633747, 697254794, 304163856,
            554172907, 197744495, 261204530, 641309055, 773073192, 463418708,
            59676768, 16042361, 210106931, 901997880, 220470855, 647104348,
            163515452, 27308711, 836338869, 505101921, 397086591, 126041010,
            704685424, 48832532, 944295743, 840261083, 407178084, 723373230,
            242749954, 62738878, 445028313, 734727516, 370425459, 607137327,
            541789278, 281002380, 548695538, 651178045, 638430458, 981678371,
            648753077, 417312222, 446493640, 201544143, 293197772, 298610124,
            31821879, 46071794, 509690783, 183827382, 867731980, 524516363,
            376504571, 748818121, 36366377, 404131214, 128632009, 535716196,
            470711551, 19833703, 516847878, 422344417, 453049973, 58419678,
            175133498, 967886806, 49897195, 188342011, 272087192, 798530288,
            210486166, 836411405, 909200386, 561566778
        };
        long[][] expected = {
            {0 ,0 },{1 ,0 },{2 ,0 },{3 ,0 },{4 ,0 },{5 ,0 },{6 ,0 },{7 ,0 },{8 ,0 }, {9 ,0 },
{7 ,28787989 },{0 ,124860658 },{5 ,235543106 },{7 ,246504708 },{4 ,311346104 },
{3 ,349021732 },{1 ,388437511 },{9 ,409836312 },{3 ,595181715 },{9 ,619331540 },
{6 ,665655446 },{8 ,706718118 },{1 ,707425685 },{2 ,753484620 },{5 ,845266823 },
{0 ,882135358 },{0 ,1030418770 },{7 ,1127475443 },{0 ,1226053489 },
{1 ,1239139577L },{4 ,1283739291L },{8 ,1294605118L },{6 ,1439488110L },
{5 ,1448354598L },{3 ,1449889884L },{2 ,1488265968L },{8 ,1492349613L },
{1 ,1543303433L },{8 ,1552026381L },{1 ,1559345794L },{9 ,1564932477L },
{6 ,1700692640L },{8 ,1762133312L },{9 ,1785403332L },{9 ,1812712043L },
{4 ,1837912198L },{0 ,1923308283L },{8 ,1925648764L },{2 ,1951684676L },
{8 ,2051689774L },{5 ,2089663653L },{7 ,2096109190L },{8 ,2100522306L },
{3 ,2222963076L },{0 ,2320394874L },{4 ,2343014119L },{6 ,2347796988L },
{4 ,2405752997L },{1 ,2461343674L },{8 ,2507700390L },{0 ,2563144828L },
{9 ,2649050912L },{2 ,2656370100L },{6 ,2792825301L },{1 ,2831769133L },
{9 ,2930053292L },{7 ,2936370273L },{3 ,2946336306L },{5 ,3033959396L },
{0 ,3104934106L },{8 ,3114837717L },{4 ,3140480513L },{2 ,3205065638L },
{2 ,3236887517L },{2 ,3282959311L },{0 ,3306478249L },{3 ,3363648528L },
{8 ,3408035489L },{4 ,3439090637L },{6 ,3444003346L },{1 ,3470199591L },
{5 ,3480453036L },{0 ,3490305631L },{1 ,3506565968L },{7 ,3585123350L },
{0 ,3618937640L },{0 ,3638771343L },{2 ,3792650094L },{4 ,3815595208L },
{5 ,3884584250L },{9 ,3911731663L },{8 ,3932551852L },{5 ,3943003928L },
{5 ,3992901123L },{1 ,4042282164L },{7 ,4055834901L },{9 ,4086865161L },
{0 ,4155619221L },{5 ,4181243134L },{6, 4192821467L }
        };
        testAssignJobs(testName, jobs, nrWorkers, expected);
        log("<" + testName);
    }
}

public class JobQueue {
    
    private static final boolean TEST_MODE_ON = true;

    public static void main(String [] args) throws Exception {
        if(TEST_MODE_ON) {
            JobQueueTests jqt = new JobQueueTests();
            jqt.test();
        } else {
            FastScanner in = new FastScanner();
            int numWorkers = in.nextInt();
            int m = in.nextInt();
            long[] jobs = new long[m];
            for (int i = 0; i < m; ++i) {
                jobs[i] = in.nextLong();
            }
            long[][] result = JobQueueService.assignJobs(jobs, numWorkers);
            for(int i = 0; i < result.length; i++) {
                System.out.println("" + result[i][0] + " " + result[i][1]);
            }
        }
    }

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

        public long nextLong() throws IOException {
            return Long.parseLong(next());
        }
    }
}