package com.example.ratelimiter.limitters;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SliderWindowRateLimiter implements Runnable {
    // max visit/sec
    private final long maxVisitPerSecond;
    // split sec into N block
    private final int block;
    // the count/block
    private final AtomicLong[] countPerBlock;
    // index of the moving of the window
    private volatile int index;
    // total amount of the current count
    private AtomicLong allCount;

    public SliderWindowRateLimiter(int block, long maxVisitPerSecond){
        this.block = block;
        this.maxVisitPerSecond = maxVisitPerSecond;
        countPerBlock = new AtomicLong[block];
        for(int i=0; i<block; i++){
            countPerBlock[i] = new AtomicLong();
        }
        allCount = new AtomicLong(0);
    }

    public boolean isOverLimit(){
        return currentQPS() > maxVisitPerSecond;
    }

    public Long currentQPS(){
        return allCount.get();
    }

    public void visit(){
        countPerBlock[index].incrementAndGet();
        allCount.incrementAndGet();

        if(isOverLimit()){
            System.out.println(Thread.currentThread().getName() + "RateLimited" + ", currentQPS: " + currentQPS() + " ,index: " + index);
        }else{
            System.out.println(Thread.currentThread().getName() + "Executing " + ", currentQPS:" + currentQPS() + ",index" + index);
        }
    }

    @Override
    public void run(){
        index = (index + 1) % block;
        long val = countPerBlock[index].getAndSet(0);
        allCount.addAndGet(-val);
    }

    public static void main(String[] args){
        SliderWindowRateLimiter sliderWindowRateLimiter = new SliderWindowRateLimiter(10, 100);
        // fixed rate request to the limiter window -> this is just call the rum() / period(100ms now)
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(sliderWindowRateLimiter, 100, 100, TimeUnit.MILLISECONDS);

        // simulate different speed of the request-1
        new Thread(() -> {
            while(true){
                sliderWindowRateLimiter.visit();
                try{
                    Thread.sleep(10);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();

        // simulate different speed of the request-2 -> two threads working on same RateLimiter -> now will exceeds the limit
        new Thread(() -> {
            while(true){
                sliderWindowRateLimiter.visit();
                try{
                    Thread.sleep(50);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
