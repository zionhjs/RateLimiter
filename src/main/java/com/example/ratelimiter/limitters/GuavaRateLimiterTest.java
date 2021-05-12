package com.example.ratelimiter.limitters;

import com.google.common.util.concurrent.RateLimiter;

// This RateLimiter is based on BucketToken method
public class GuavaRateLimiterTest {
    // create 10 token/second
    private RateLimiter rateLimiter = RateLimiter.create(10);

    // simulate the running method
    public void exeBiz(){
        if(rateLimiter.tryAcquire(1)){
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("Thread" + Thread.currentThread().getName() + " :Executing !");
        }else{
            System.out.println("Thread" + Thread.currentThread().getName() + " :Being Rate Limited!");
        }
    }

    public static void main(String[] args) throws InterruptedException{
        GuavaRateLimiterTest limiterTest = new GuavaRateLimiterTest();
        Thread.sleep(500);  // wait for 500ms, let RateLimiter to gen some tokens

        // simulate 100 new Thread Requests
        for(int i=0; i<100; i++){
            Thread thread = new Thread(limiterTest::exeBiz);
            thread.start();
        }

    }
}


