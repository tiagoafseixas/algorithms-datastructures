package pt.edx.a201x.p13;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

class ProcessorTests {

    private static void log(final String s) {
        System.out.println(s);
    }

    public final void test() {
        twoRequestsNoneDropped();
        twoRequestsOneDropped();
        noRequests();
        twoRequestsSecondArrivesOnEndTime();
        threeRequestsWithNoneDropped();
    }

    private final void twoRequestsNoneDropped() {
        final String testName = "twoRequestsNoneDropped";
        final int n = 2;
        final ArrayList<Request> requests = new ArrayList<Request>();
        requests.add(new Request(0L, 1000L));
        requests.add(new Request(0L, 1500L));
        log(">" + testName);

        final ProcessorService ps = new ProcessorService(n, requests);
        final ArrayList<Response> result = ps.process();

        for(Response r : result) {
            System.out.println(r.getProcessingStartTime());
        }

        log("<" + testName);
    }

    private final void twoRequestsOneDropped() {
        final String testName = "twoRequestsOneDropped";
        final int n = 1;
        final ArrayList<Request> requests = new ArrayList<Request>();
        requests.add(new Request(0L, 1500L));
        requests.add(new Request(0L, 1500L));
        log(">" + testName);

        final ProcessorService ps = new ProcessorService(n, requests);
        final ArrayList<Response> result = ps.process();

        for(Response r : result) {
            System.out.println(r.getProcessingStartTime());
        }

        log("<" + testName);
    }

    private final void noRequests() {
        final String testName = "noRequests";
        final int n = 1;
        final ArrayList<Request> requests = new ArrayList<Request>();
        log(">" + testName);

        final ProcessorService ps = new ProcessorService(n, requests);
        final ArrayList<Response> result = ps.process();

        for(Response r : result) {
            System.out.println(r.getProcessingStartTime());
        }

        log("<" + testName);
    }

    private final void twoRequestsSecondArrivesOnEndTime() {
        final String testName = "twoRequestsSecondArrivesOnEndTime";
        final int n = 1;
        final ArrayList<Request> requests = new ArrayList<Request>();
        requests.add(new Request(0L, 1500L));
        requests.add(new Request(1500L, 1500L));
        log(">" + testName);

        final ProcessorService ps = new ProcessorService(n, requests);
        final ArrayList<Response> result = ps.process();

        for(Response r : result) {
            System.out.println(r.getProcessingStartTime());
        }

        log("<" + testName);
    }

    private final void threeRequestsWithNoneDropped() {
        final String testName = "threeRequestsWithNoneDropped";
        final int n = 2;
        final ArrayList<Request> requests = new ArrayList<Request>();
        requests.add(new Request(0L, 2L));
        requests.add(new Request(1L, 4L));
        requests.add(new Request(5L, 3L));
        log(">" + testName);

        final ProcessorService ps = new ProcessorService(n, requests);
        final ArrayList<Response> result = ps.process();

        for(Response r : result) {
            System.out.println(r.getProcessingStartTime());
        }

        log("<" + testName);
    }
}

class Request {
    private long timeOfArrival;
    private long processingTime;
    private long processingStartTime;

    public Request(final long a, final long p) {
        this.timeOfArrival = a;
        this.processingTime = p;
    }

    public long getTimeOfArrival() {
        return this.timeOfArrival;
    }

    public long getProcessingTime() {
        return this.processingTime;
    }

    public void setProcessingStartTime(final long t) {
        this.processingStartTime = t;
    }

    public long getProcessingFinishTime() {
        return this.processingStartTime + processingTime;
    }

    public long getProcessingStartTime() {
        return this.processingStartTime;
    }
}

class Response {
    Request r;
    boolean dropped;
    long processingStartTime;
    long processingEndTime;

    public Response(final Request r, final boolean dropped, final long processingStartTime) {
        this.r = r;
        this.dropped = dropped;
        this.processingStartTime = processingStartTime;
    }

    public long getProcessingStartTime() {
        return (false == dropped) ? this.processingStartTime : -1;
    }
}

class ProcessorService {
    ArrayList<Request> requests = null;
    ArrayList<Response> responses = null;
    LinkedList<Request> buffer = null;

    Request current = null;
    long lastPacketStartTime = 0L;
    int sizeLimit = 0;

    public ProcessorService(final int sizeLimit) {
        this.requests = new ArrayList<Request>();
        this.responses = new ArrayList<Response>();
        this.buffer = new LinkedList<Request>();
        this.sizeLimit = sizeLimit; 
    }

    public ProcessorService(final int sizeLimit, final ArrayList<Request> requests) {
        this.requests = requests;
        this.responses = new ArrayList<Response>();
        this.buffer = new LinkedList<Request>();
        this.sizeLimit = sizeLimit; 
    }

    public boolean isBusy() { return this.current != null; }
    public boolean isFull() { return this.buffer.size() == sizeLimit; }
    public boolean isEmpty() { return this.buffer.isEmpty(); }

    public ArrayList<Response> process() {
        for(Request r : requests) {
            processBuffer(r.getTimeOfArrival());

            if (false == isFull()) {
                r.setProcessingStartTime(getRequestProcessingStart(r));
                this.responses.add(new Response(r, false, r.getProcessingStartTime()));
                this.buffer.push(r);
            } else {
                this.responses.add(new Response(r, true, 0L));
            }
        }
        return this.responses;
    }

    public void processBuffer(final long currentTime) {
        
        if(this.buffer.isEmpty()) { return; }

        boolean process = true;

        while(process && false == isEmpty()) {
            if (this.buffer.getLast().getProcessingFinishTime() <= currentTime) {
                this.buffer.pollLast();
            } else {
                process = false;
            }
        }
    }

    public long getRequestProcessingStart(final Request r) {
        if ( false == isEmpty()) {
            return this.buffer.getFirst().getProcessingFinishTime();
        } else {
            return r.getTimeOfArrival();
        }
    }
}

public class Processor {
    
    private static boolean TEST_MODE_ON = false;

    public static final void main(final String[] args) {

        if(TEST_MODE_ON) {
            try {
                Processor obj = new Processor ();
                obj.run (args);
            } catch (Exception e) {
                e.printStackTrace ();
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            int buffer_max_size = scanner.nextInt();

            ArrayList<Request> requests = new ArrayList<Request>();

            int requests_count = scanner.nextInt();
            for (int i = 0; i < requests_count; ++i) {
                long arrival_time = scanner.nextLong();
                long process_time = scanner.nextLong();
                requests.add(new Request(arrival_time, process_time));
            }
            final ProcessorService ps = new ProcessorService(buffer_max_size, requests);
            final ArrayList<Response> result = ps.process();

            for(Response r : result) {
                System.out.println(r.getProcessingStartTime());
            }
        }
        
    }

    public void run(final String[] args) {
        final ProcessorTests pt = new ProcessorTests();
        pt.test();
    }
}