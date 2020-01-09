package com.play.concurrent;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * https://www.vogella.com/tutorials/Hamcrest/article.html
 *
 */
public class BoundedBufferTest  {

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
        try{
            taker.start();
            Thread.sleep(1000);
            taker.interrupt();
            taker.join(100);
            assertThat(taker.isAlive(),is(false));
        }catch (Exception e){
            fail();
        }
        System.out.println("main finished");
    }


}