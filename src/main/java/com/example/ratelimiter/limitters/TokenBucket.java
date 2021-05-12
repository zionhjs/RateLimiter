package com.example.ratelimiter.limitters;

import com.google.common.annotations.VisibleForTesting;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucket {
    public class Bucket{
        // capacity of the Bucket
        int capacity;
        // tokenInsert/sec
        int rateCount;
        AtomicInteger curCount = new AtomicInteger(0);

        public Bucket(int capacity, int rateCount){
            this.capacity = capacity;
            this.rateCount = rateCount;
        }

        public void put(){
            if(curCount.get() < capacity){
                System.out.println("current amount ==" + curCount.get() + ", I have tokens in the bucket for use");
                curCount.addAndGet(Math.min(rateCount, capacity-curCount.get()));
            }
        }

        public boolean get(){
            if(curCount.get() >= 1){
                curCount.decrementAndGet();
                return true;
            }
            return false;
        }
    }

    @org.junit.Test
    public void testTokenBucket(){
        Bucket bucket = new Bucket(5, 2);
        //fixed thread, send request in a fixed rate, N/sec
        ScheduledThreadPoolExecutor scheduledCheck = new ScheduledThreadPoolExecutor(1);
        scheduledCheck.scheduleAtFixedRate(() -> {
            bucket.put();
        }, 0, 1, TimeUnit.SECONDS);

        // wait for a moment, wait till more token into the bucket
        try{
            Thread.sleep(6000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        // simulate 10 Threads come in and take the tokens
        for(int i=0; i<10; i++){
            new Thread(() -> {
                if(bucket.get()){
                    System.out.println(Thread.currentThread() + " got the token!");
                }else{
                    System.out.println(Thread.currentThread() + " got Rejected!");
                }
            }).start();
        }

        // wait for a moment, wait till more token into the bucket
        try{
            Thread.sleep(3000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        // simulate 10 Threads come in and take the tokens
        for(int i=0; i<10; i++){
            new Thread(() -> {
                if(bucket.get()){
                    System.out.println(Thread.currentThread() + " got the token!");
                }else{
                    System.out.println(Thread.currentThread() + " got rejected!");
                }
            }).start();
        }
    }
}
