package com.play.concurrent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CompletableFutureSample {
    Runnable task = () -> {
        System.out.println("run task");
    };


    public static void simple() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<String>();
        completableFuture.complete("hello world");
        String result = completableFuture.get();
        System.out.println("finished with result: " + result);
    }

    public static void runTest() throws ExecutionException, InterruptedException {
        // Using Lambda Expression
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // Simulate a long-running Job
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("I'll run in a separate thread than the main thread.");
        });
        future.get();
    }

    /**
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void callback() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Rajeev";
        }).thenApply(name -> "Hello!" + name)
                .thenApply(greeting -> greeting + ", Welcome to the CalliCoder Blog");
        System.out.println(future.get()); // Hello Rajeev

    }

    public static void combine() throws ExecutionException, InterruptedException {
        System.out.println("Retrieving weight.");
        CompletableFuture<Double> weightInKgFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 65.0;
        });

        System.out.println("Retrieving height.");
        CompletableFuture<Double> heightInCmFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return 177.8;
        });

        System.out.println("Calculating BMI.");
        CompletableFuture<Double> combinedFuture = weightInKgFuture
                .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> {
                    Double heightInMeter = heightInCm / 100;
                    return weightInKg / (heightInMeter * heightInMeter);
                });

        System.out.println("Your BMI is - " + combinedFuture.get());

    }

    public static CompletableFuture<String> downloadWebPage(String pageLink) {
        return CompletableFuture.supplyAsync(() -> {
            // Code to download and return the web page's content
            try {
                final long waitTime = new Random().nextInt(10);
                TimeUnit.SECONDS.sleep(waitTime);
                System.out.println("wait: " + waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!pageLink.startsWith("http"))
                throw new IllegalArgumentException("wrong link");
            return "content:" + pageLink;
        });
    }

    public static void allOf() throws ExecutionException, InterruptedException {
        List<String> webPageLinks = Arrays.asList("https://www.baidu.com",
                "https://www.sina.com",
                "http://sina.com",
                "http://sina.com",
                "http://sina.com",
                "http://zol.com.cn");    // A list of 100 web page links

// Download contents of all the web pages asynchronously
        List<CompletableFuture<String>> pageContentFutures = webPageLinks.stream()
                .map(CompletableFutureSample::downloadWebPage)
                .collect(Collectors.toList());


        CompletableFuture<List<String>> result = CompletableFuture.allOf(
                pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()])
        ).thenApply(results -> pageContentFutures.stream()
                .map(pageContentFuture -> pageContentFuture.join())
                .collect(Collectors.toList())
        );

        System.out.println(result.get());


    }

    public static void exception() throws ExecutionException, InterruptedException {
        Integer age = -1;

        CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() -> {
            if(age < 0) {
                throw new IllegalArgumentException("Age can not be negative");
            }
            if(age > 18) {
                return "Adult";
            } else {
                return "Child";
            }
        }).exceptionally(ex -> {
            System.out.println("Oops! We have an exception - " + ex.getMessage());
            return "Unknown!";
        });

        System.out.println("Maturity : " + maturityFuture.get());



        CompletableFuture<String> maturityFuture2 = CompletableFuture.supplyAsync(() -> {
            if(age < 0) {
                throw new IllegalArgumentException("Age can not be negative");
            }
            if(age > 18) {
                return "Adult";
            } else {
                return "Child";
            }
        }).handle((res, ex) -> {
            if(ex != null) {
                System.out.println("Oops! We have an exception - " + ex.getMessage());
                return "Unknown!";
            }
            return res;
        });

        System.out.println("Maturity : " + maturityFuture2.get());

    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
//        simple();
//        runTest();
//        callback();
//        combine();
        allOf();
//        exception();
        System.out.println("take time: " + (System.currentTimeMillis() - start) + "ms");
    }
}
