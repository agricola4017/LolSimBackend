package testPlayground;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class testThreads {

    final static Set<Integer> setint = new HashSet<>();

    public static void main(String[] args) throws InterruptedException {
        final int abc = 4;

        System.out.println(abc);
        setint.add(3);

        long startTime = System.currentTimeMillis();
        RunnableImpl.addValues();

        List<Thread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(4); // Assuming 4 threads

        for (int i = 0; i < 4; i++) {
            Thread t1 = new Thread(new RunnableImpl(latch));
            threads.add(t1);
            t1.start(); // Start the thread immediately
        }

        // Wait for all threads to finish
        latch.await();

        System.out.println(RunnableImpl.a);
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time Threads: " + (endTime - startTime));

        startTime = System.currentTimeMillis();
        int sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += i;
        }
        System.out.println(sum);
        endTime = System.currentTimeMillis();
        System.out.println("Total execution time Single: " + (endTime - startTime));
    }
}

class RunnableImpl implements Runnable {
    static ConcurrentLinkedQueue<Integer> q = new ConcurrentLinkedQueue<>();
    static AtomicInteger a = new AtomicInteger(0);
    private CountDownLatch latch;

    public RunnableImpl(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        Integer value;
        while ((value = q.poll()) != null) {
            a.getAndAdd(value);
        }
        latch.countDown(); // Signal that this thread is done
    }

    static void addValues() {
        for (int i = 0; i < 1000000; i++) {
            q.add(i);
        }
    }
}