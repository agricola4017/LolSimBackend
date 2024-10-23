package testPlayground;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class main {

    final static Set<Integer> setint = new HashSet<>();

    public static void main(String[] args) throws InterruptedException {
        final int abc;
        abc=4;

        System.out.println(abc);

        setint.add(3);

        long startTime = System.currentTimeMillis();
        RunnableImpl.addValues();
        //System.out.println(RunnableImpl.q);

        List<Thread> threads = new ArrayList<>();
        //CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 4; i++) {
            Thread t1 = new Thread(new RunnableImpl());
            threads.add(t1);
        }

        for (Thread thread: threads) {
            thread.start();
            thread.join();
        }
        //latch.await();
        System.out.println(RunnableImpl.a);
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time Threads: " + (endTime - startTime));

        startTime = System.currentTimeMillis();
        int sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum+=i;
        }
        System.out.println(sum);
        endTime = System.currentTimeMillis();
        System.out.println("Total execution time Single: " + (endTime - startTime));
    }
}

 class RunnableImpl implements Runnable {
    static ConcurrentLinkedQueue<Integer> q = new ConcurrentLinkedQueue<>();
    static AtomicInteger a = new AtomicInteger(0);
    public void run() {
        int b = 0;

        while (!q.isEmpty()) {
            //List<Integer> vals = new ArrayList<>();
            //int it = 0;
            //while (!q.isEmpty() && it < 1) {
            //    vals.add(q.poll());
            //}

            //for (int val: vals) {
            //    b+=val;
            //}
            //a.getAndAdd(val);

            a.getAndAdd(q.poll());
        }
        //System.out.println(a.getAndAdd(val));
    }
     static void addValues() {
        for (int i = 0 ; i < 1000000; i ++) {
            q.add(i);
        }
    }
}
