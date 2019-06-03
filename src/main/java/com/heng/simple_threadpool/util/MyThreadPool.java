package com.heng.simple_threadpool.util;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPool{

    private volatile boolean shutdown = false;                    //关闭线程池

    private volatile boolean status = true;                     //线程池是否启动

    private volatile int coreThreadNumber = 0;                        //任务数

    private volatile int maxThreadNumber = 5;                         //最大work数


    private BlockingQueue<Thread> taskQueue = new LinkedBlockingQueue<Thread>();      //任务队列

    private HashSet<Work> workQueue = new HashSet<Work>();              //管理work

    private Lock lock = new ReentrantLock();

    Condition produce = lock.newCondition();

    Condition consume = lock.newCondition();

    public void run(Thread thread) {
        addWork(thread);
    }

    public void addWork(Thread thread){
        lock.lock();
        status = true;
        if (workQueue.size() < maxThreadNumber){
            addTask(thread);
            Work work = new Work();
            workQueue.add(work);
            new Thread(work).start();
        }else {
            if (coreThreadNumber > maxThreadNumber){
                try {
                    produce.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            addTask(thread);
        }
        lock.unlock();
    }

    private void addTask(Thread thread){
        lock.lock();
        taskQueue.add(thread);
        coreThreadNumber++;
        lock.unlock();
    }

    public void shutDownPool(){
        shutdown = true;
        for (Work work : workQueue){
            System.out.println(work.getName());
            work.interrupt();
        }
    }


    class Work extends Thread {
        @Override
        public void run() {
            runWork();
        }

        public void runWork(){
            while (status && !shutdown){
                if (Thread.currentThread().isInterrupted()){
                    return ;
                }
                Thread thread = getTask();
                if (thread != null){
                    int result = run(thread, 1);
                    lock.lock();
                    if (result > 0){
                        coreThreadNumber--;
                        produce.signalAll();
                    }
                    lock.unlock();
                }
            }
        }

        private int run(Thread thread,int flag){
            thread.run();
            return flag;
        }

        public Thread getTask(){
            try {
                return taskQueue.poll(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
