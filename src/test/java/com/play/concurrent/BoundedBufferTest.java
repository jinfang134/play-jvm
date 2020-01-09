package com.play.concurrent;

import org.junit.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * https://www.vogella.com/tutorials/Hamcrest/article.html
 */
public class BoundedBufferTest {

    @Test
    public void isEmpty() {
        BoundedBuffer<String> buffer = new BoundedBuffer<>(10);
        assertTrue(buffer.isEmpty());
        assertFalse(buffer.isFull());
    }


    @Test
    public void takeBlocksWhenEmpty() {
        final BoundedBuffer<String> buffer = new BoundedBuffer<>(10);
        Thread taker = new Thread(() -> {
            try {
                String unused = buffer.take();
                fail();
            } catch (InterruptedException e) {
//                e.printStackTrace();
                System.out.println("taker interrupted");
            }
        });
        try {
            taker.start();
            Thread.sleep(1000);
            taker.interrupt();
            taker.join(100);
            assertThat(taker.isAlive(), is(false));
        } catch (Exception e) {
            fail();
        }
        System.out.println("main finished");
    }

    @Test
    public void shouldGetRightValue() {
        PutTakeTest test = new PutTakeTest(10, 10, 100_000);
        test.test();
        test.shutdown();

    }


}

class PutTakeTest {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private final AtomicInteger putSum = new AtomicInteger(0);
    private final AtomicInteger takeSum = new AtomicInteger(0);
    private final CyclicBarrier barrier;
    private final BoundedBuffer<Integer> bb;
    private final int nTrials, nPairs;

    PutTakeTest(int capacity, int nPairs, int nTrials) {
        this.barrier = new CyclicBarrier(capacity * 2 + 1);
        this.nPairs = nPairs;
        this.nTrials = nTrials;
        this.bb = new BoundedBuffer<>(capacity);
    }

    void shutdown() {
        pool.shutdown();
    }

    void test() {
        try {
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            barrier.await();//等待所有的线程就绪
            barrier.await(); //等待所有的线程执行完成
            assertThat(putSum.get(), is(takeSum.get()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {

        @Override
        public void run() {
            try {
                int seed = (int) (this.hashCode() ^ System.nanoTime());
                int sum = 0;
                barrier.await();
                for (int i = nTrials; i > 0; --i) {
                    bb.put(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; i--) {
                    sum += bb.take();
                }
                takeSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
