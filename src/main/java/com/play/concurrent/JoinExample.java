package com.play.concurrent;

public class JoinExample {
    /**
     * t.join()方法阻塞调用此方法的线程(calling thread)，直到线程t完成，
     * 此线程再继续；
     * 通常用于在main()主线程内，等待其它线程完成再结束main()主线程。我们来看看下面的例子。
     * @param args
     */
    public static void main(String[] args){
        System.out.println("MainThread run start.");

        //启动一个子线程
        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("threadA run start.");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("threadA run finished.");
            }
        });
        threadA.start();

        System.out.println("MainThread join before");
        try {
            threadA.join();    //调用join()
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("MainThread run finished.");
    }
}
