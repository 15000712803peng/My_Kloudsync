package com.kloudsync.techexcel.help;

import android.os.Handler;
import android.os.Looper;

public class CrashCatch {

    private CrashHandler mCrashHandler;

    private static CrashCatch mInstance;

    private CrashCatch(){

    }

    private static CrashCatch getInstance(){
        if(mInstance == null){
            synchronized (CrashCatch.class){
                if(mInstance == null){
                    mInstance = new CrashCatch();
                }
            }
        }

        return mInstance;
    }

    public static void init(CrashHandler crashHandler){
        getInstance().setCrashHandler(crashHandler);
    }
    private void setCrashHandler(CrashHandler crashHandler){

        mCrashHandler = crashHandler;
        //主线程异常拦截
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        if (mCrashHandler != null) {
                            //处理异常
                            mCrashHandler.handlerException(Looper.getMainLooper().getThread(), e);
                        }
                    }
                }
            }
        });
        //所有线程异常拦截，由于主线程的异常都被我们catch住了，所以下面的代码拦截到的都是子线程的异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if(mCrashHandler!=null){
                    //处理异常
                    mCrashHandler.handlerException(t,e);
                }
            }
        });


    }

    public interface CrashHandler{
        void handlerException(Thread t, Throwable e);
    }
}