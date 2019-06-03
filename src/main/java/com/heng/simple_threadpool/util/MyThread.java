package com.heng.simple_threadpool.util;

public class MyThread extends Thread {
    public static  int  i = 0;

    @Override
    public void run() {
        i++;
        System.out.println(i);
    }
}
