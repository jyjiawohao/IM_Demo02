package com.sizu.mingteng.im_demo02.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 线程池 创建一个对象随时new
 */
public class ThreadUtils {

    private static Handler sHandler = new Handler(Looper.getMainLooper());//Looper.getMainLooper()是主线程的

    private static Executor sExecutor = Executors.newSingleThreadExecutor();//创建一个单线程
   // private static Executor sExecutor =Executors.newFixedThreadPool(3);//保留三个线程

    /**子线程
     * 线程池
     * @param runnable
     */
    public static void runOnSubThread(Runnable runnable){
        sExecutor.execute(runnable);
    }

    /**
     * 主线程
     * @param runnable
     */
    public static void runOnMainThread(Runnable runnable){
        sHandler.post(runnable); //在handle所在的线程一起执行

    }




}
