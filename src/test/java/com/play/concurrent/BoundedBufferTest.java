package com.play.concurrent;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoundedBufferTest extends TestCase {

    @Test
    public void isEmpty() {
        BoundedBuffer<String> buffer = new BoundedBuffer<>(10);
        assertTrue(buffer.isEmpty());
        assertFalse(buffer.isFull());
    }


    @Test
    public void testTakeBlocksWhenEmpty() {
        final BoundedBuffer<String> buffer = new BoundedBuffer<>(10);
        Thread taker = new Thread(() -> {
            try {
                String unused = buffer.take();
                fail();
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        });
        try{
            taker.start();
            Thread.sleep(1000);
            taker.interrupt();
            taker.join(2000);
            assertFalse(taker.isAlive());
        }catch (Exception e){
            fail();
        }
    }
}