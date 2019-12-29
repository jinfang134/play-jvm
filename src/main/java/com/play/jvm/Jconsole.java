package com.play.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * 科目： visualvm 内存监控实验
 * 目的： 观察曲线和柱状图的变化
 * 内容：
 *  1. 参数： -Xms100m -Xmx100m -XX:+UseSerialGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
 *  2. 程序将会以64k/50ms的速度往java堆中填充数据，一共填充1000次
 *
 *
 *
 */
public class Jconsole {

    static class OOMObject {
        public byte[] placeholder = new byte[64 * 1024];
    }


    /**
     * 当数值过大时，会oom
     * @param num
     * @throws InterruptedException
     */
    public static void fillHeap(int num) throws InterruptedException {
        List<OOMObject> list = new ArrayList<OOMObject>();

        for (int i = 0; i < num; i++) {
            Thread.sleep(50);
            list.add(new OOMObject());
        }
        System.gc();
    }


    public static void main(String[] args) throws InterruptedException {
        fillHeap(10000);
    }

}


