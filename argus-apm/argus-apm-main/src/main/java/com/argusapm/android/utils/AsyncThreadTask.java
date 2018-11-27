package com.argusapm.android.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步线程池
 *
 * @author ArgusAPM Team
 */
public class AsyncThreadTask {
    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors(); //cup内核数
    private final int DEAFULT_THREAD_COUNT = CPU_COUNT + 3; //默认核心线程数
    private final int KEEP_ALIVE = 3; //空线程alive时间
    private ExecutorService mThreadPool; //线程池
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "AsyncThreadTask #" + mCount.getAndIncrement());
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    };
    private static AsyncThreadTask instance;

    public static AsyncThreadTask getInstance() {
        if (instance == null) {
            synchronized (AsyncThreadTask.class) {
                if (instance == null) {
                    instance = new AsyncThreadTask();
                }
            }
        }
        return instance;
    }

    /**
     * 构造函数
     */
    private AsyncThreadTask() {
        // 创建线程池
        mThreadPool = new ThreadPoolExecutor(DEAFULT_THREAD_COUNT, DEAFULT_THREAD_COUNT, KEEP_ALIVE,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10000),
                sThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());

    }

    private void executeRunnable(Runnable runnable) {
        mThreadPool.execute(runnable);
    }

    private void executeRunnableDelayed(final Runnable runnable, long delayedTime) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mThreadPool.execute(runnable);
            }
        }, delayedTime);
    }

    /**
     * 异步线程执行任务
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        AsyncThreadTask.getInstance().executeRunnable(runnable);
    }

    /**
     * 异步线程执行延迟任务
     *
     * @param runnable
     * @param delayedTime
     */
    public static void executeDelayed(Runnable runnable, long delayedTime) {
        AsyncThreadTask.getInstance().executeRunnableDelayed(runnable, delayedTime);
    }

    public static void executeDelayedToUI(Runnable runnable, long delayedTime) {
        AsyncThreadTask.getInstance().executeRunnableDelayedToUI(runnable, delayedTime);
    }

    private void executeRunnableDelayedToUI(final Runnable runnable, long delayedTime) {
        getHandler().postDelayed(runnable, delayedTime);
    }

    private InternalHandler mHandler;

    private Handler getHandler() {
        synchronized (this) {
            if (mHandler == null) {
                mHandler = new InternalHandler();
            }
            return mHandler;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }
}
