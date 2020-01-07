package com.play.concurrent;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockSample {

    static class Account {
        final Lock lock;
        int balance;
        String name;

        public Account(String name, Lock lock, int balance) {
            this.name = name;
            this.lock = lock;
            this.balance = balance;
        }

        public void debit(int amount) {
            this.balance -= amount;
        }

        public void credit(int amount) {
            this.balance += amount;
        }


    }

    public static boolean transferMoney(Account fromAccount, Account toAcct, int amount, long timeout, TimeUnit unit) throws InterruptedException {
        long fixedDelay = new Random().nextInt(1000);
        long stopTime = System.nanoTime() + unit.toNanos(timeout);
        while (true) {
            if (fromAccount.lock.tryLock()) {
                try {
                    if (toAcct.lock.tryLock()) {
                        try {
                            if (fromAccount.balance < amount) {
                                throw new IllegalArgumentException("no enough money.");
                            } else {
                                fromAccount.debit(amount);
                                toAcct.credit(amount);
                                System.out.println("transfer from " + fromAccount.name + " to " + toAcct.name + ", amount: " + amount);
                                return true;
                            }
                        } finally {
                            toAcct.lock.unlock();
                        }
                    }
                } finally {
                    fromAccount.lock.unlock();
                }
            }
            if (System.nanoTime() > stopTime) {
                System.out.println("out time");
                return false;
            }
            System.out.println("didn't get lock, wait for a while."+ fixedDelay +"ms");
            Thread.sleep(fixedDelay);
        }
    }

    public static void main(String[] args) {
        Account accountA = new Account("A", new ReentrantLock(), 100000);
        Account accountB = new Account("B", new ReentrantLock(), 100000);
        AtomicInteger count = new AtomicInteger();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            final Integer index = i;
            new Thread(() -> {
                try {
                    if (count.getAndIncrement() % 2 == 0) {
                        transferMoney(accountA, accountB, 100, 10, TimeUnit.SECONDS);
                    } else {
                        transferMoney(accountB, accountA, 100, 10, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }
        while (Thread.activeCount() > 1) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(accountA.balance);
        System.out.println(accountB.balance);
        System.out.println("take time: " + (System.currentTimeMillis() - start) + "ms");
    }

}
