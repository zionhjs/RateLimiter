package com.example.ratelimiter.limitters;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class SemaphoreOne {
    private static Semaphore semaphore = new Semaphore(10);

    public static void bizMethod() throws InterruptedException {
        if(!semaphore.tryAcquire()){
            System.out.println(Thread.currentThread().getName() + " being rejected!");
            return;
        }
        System.out.println(Thread.currentThread().getName() + " executing! ");
        // 直接call Thread 是获得并控制当前Thread
        Thread.sleep(500);
        semaphore.release();
    }

    public static void main(String[] args){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                semaphore.release(10);
                System.out.println("released locks");
            }
        }, 1000, 1000);

        for(int i=0; i<10000; i++){
            // let current Thread sleep
            try{
                Thread.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            Thread thread = new Thread(() -> {
                try{
                    SemaphoreOne.bizMethod();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}


