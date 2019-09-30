package com.kloudsync.techexcel.help;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    private final String TAG = "ThreadManager";
    ExecutorService threadPool;

    private static class InstanceHolder {
        public static final ThreadManager INSTANCE = new ThreadManager();
    }

    private ThreadManager() {
        threadPool = Executors.newFixedThreadPool(3);
    }

    public static ThreadManager getManager() {
        return InstanceHolder.INSTANCE;
    }

    public void execute(Runnable runnable) {
        if (threadPool != null && !threadPool.isShutdown() && !threadPool.isTerminated()) {
            Log.e(TAG, "thread pool:" + threadPool);
            threadPool.execute(runnable);
        } else {
            synchronized (this) {
                if (threadPool == null || threadPool.isShutdown() || threadPool.isTerminated()) {
                    threadPool = Executors.newFixedThreadPool(3);
                    Log.e(TAG, "new thread pool:" + threadPool);
                    threadPool.execute(runnable);
                }
            }
        }

    }

    public void shutDown() {
        if (threadPool != null) {
            if (!threadPool.isShutdown()) {
                threadPool.shutdown();
            }
        }
    }
}
