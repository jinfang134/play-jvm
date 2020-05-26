package com.play.jvm;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class Base {
    public static void main(String[] args) {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String s = name.split("@")[0];
        //打印当前Pid
        System.out.println("pid:" + s);
//        while (true) {
//            try {
//                Thread.sleep(5000L);
//            } catch (Exception e) {
//                break;
//            }
//            process();
//        }
        Base.primeFactors(2);
        Base.primeFactors(3);
        Base.primeFactors(6);
        Base.primeFactors(8);
        Base.primeFactors(11);
    }

    public static void process() {
        System.out.println("process");
    }

    public static List<Integer> primeFactors(int number) {
        if (number < 2) {
            throw new IllegalArgumentException("number is: " + number + ", need >= 2");
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        int i = 2;
        while (i <= number) {
            if (number % i == 0) {
                result.add(i);
                System.out.println(i);
                number /= i;
//                i = 2;
                continue;
            }
            ++i;
        }
        return result;
    }

}
