package com.play.concurrent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockingQueueSample {
    private final static BlockingQueue queue = new LinkedBlockingDeque();
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static volatile CountDownLatch start = new CountDownLatch(1);
    AtomicInteger count=new AtomicInteger(0);

    public static void produce() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            final Integer index = i;
            executor.submit(() -> {
                try {
                    start.await();
                    queue.add(index);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void consume() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    System.out.println(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        produce();
        consume();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();

        start.countDown();
        while (Thread.activeCount() > 1) {
            Thread.sleep(1000);
        }

    }

}
