package com.play.concurrent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class OneShotLatch {
    private final Sync sync = new Sync();

    private class Sync extends AbstractQueuedSynchronizer {
        /**
         * 当state>0时表示已经获取了锁，当state = 0时表示释放了锁。
         *
         * @param arg
         * @return
         */
        @Override
        protected int tryAcquireShared(int arg) {
            return (getState() == 1) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            setState(1);
            return true;
        }
    }

    public void signal() {
        sync.releaseShared(0);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    public static void main(String[] args) throws IOException {
        OneShotLatch start = new OneShotLatch();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    start.await();
                    System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " " + index);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("program started, please input a word to start.");
        br.readLine();
        start.signal();
    }

}
