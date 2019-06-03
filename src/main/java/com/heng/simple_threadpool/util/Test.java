package com.heng.simple_threadpool.util;

import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        MyThreadPool myThreadPool = new MyThreadPool();
//        myThreadPool.addWork(new MyThread());

        for (int i = 0;i < 10;i++){
            myThreadPool.addWork(new MyThread());
        }
        Scanner scanner = new Scanner(System.in);

        myThreadPool.shutDownPool();
//        scanner.nextLine();
//        myThreadPool.shutDownPool();

    }
}
