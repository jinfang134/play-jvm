package com.play.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class AQSSample {

    private static class Sync extends AbstractQueuedSynchronizer{
        @Override
        protected boolean tryAcquire(int arg) {
            return super.tryAcquire(arg);
        }
    }
}
