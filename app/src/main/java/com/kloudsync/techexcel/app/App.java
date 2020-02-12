package com.kloudsync.techexcel.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.ChangeItemMessage;
import com.kloudsync.techexcel.dialog.message.CourseMessage;
import com.kloudsync.techexcel.dialog.message.CustomizeMessage;
import com.kloudsync.techexcel.dialog.message.DemoContext;
import com.kloudsync.techexcel.dialog.message.FriendMessage;
import com.kloudsync.techexcel.dialog.message.GroupMessage;
import com.kloudsync.techexcel.dialog.message.KnowledgeMessage;
import com.kloudsync.techexcel.dialog.message.SendFileMessage;
import com.kloudsync.techexcel.dialog.message.ShareMessage;
import com.kloudsync.techexcel.dialog.message.SpectatorMessage;
import com.kloudsync.techexcel.dialog.message.SystemMessage;
import com.kloudsync.techexcel.help.CrashHandler;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.start.StartUbao;
import com.kloudsync.techexcel.ui.MainActivity;
import com.pgyersdk.Pgyer;
import com.pgyersdk.PgyerActivityManager;
import com.pgyersdk.crash.PgyCrashManager;
import com.ub.service.activity.SocketService;

import org.xutils.x;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import io.agora.openlive.model.WorkerThread;
import io.rong.imkit.RongIM;

public class App extends Application {
    private CrashHandler mCrashHandler;
    public App instance;
    private ThreadManager threadMgr;
    private Handler mainHandler;
    private MainActivity mainActivityInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        threadMgr = ThreadManager.getManager();
        mainHandler = new Handler(Looper.getMainLooper());
        instance = this;
        startWBService();
        disableAPIDialog();
//        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
        asyncInit();
    }

    public MainActivity getMainActivityInstance() {
        return mainActivityInstance;
    }

    public void setMainActivityInstance(MainActivity mainActivityInstance) {
        this.mainActivityInstance = mainActivityInstance;
    }

    private WorkerThread mWorkerThread;

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();
            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {

        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;

    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        CheckLanguage();
    }

    public void CheckLanguage() {
        if (AppConfig.LANGUAGEID > 0 && getMainActivityInstance() != null) {
            switch (AppConfig.LANGUAGEID) {
                case 1:
                    StartUbao.updateLange(getMainActivityInstance(), Locale.ENGLISH);
                    break;
                case 2:
                    StartUbao.updateLange(getMainActivityInstance(), Locale.SIMPLIFIED_CHINESE);
                    break;
                default:
                    break;
            }
        }
    }

    private void asyncInit() {
        getThreadMgr().execute(new InitRunnable());
    }

    public ThreadManager getThreadMgr() {
        return threadMgr != null ? threadMgr : ThreadManager.getManager();
    }

    private class InitRunnable implements Runnable {

        @Override
        public void run() {
            init();
        }

        private void init() {

            x.Ext.init(App.this);
            Fresco.initialize(App.this);
            mCrashHandler = CrashHandler.getInstance();
            PgyerActivityManager.set(App.this);
            Pgyer.setAppId("c3ae43cb28a2922fd1145252c3138ad4");
            mCrashHandler.init(App.this);
            PgyCrashManager.register(App.this);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    initRongYun();
                }
            });
        }

        private void initRongYun() {
            /**
             * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
             * io.rong.push 为融云 push 进程名称，不可修改。
             */

            if (getApplicationInfo().packageName
                    .equals(getCurProcessName(getApplicationContext()))
                    || "io.rong.push"
                    .equals(getCurProcessName(getApplicationContext()))) {
                /**
                 * IMKit SDK调用第一步 初始化
                 */
                RongIM.init(App.this);
                RongIM.registerMessageType(CustomizeMessage.class);
                RongIM.registerMessageType(KnowledgeMessage.class);
                RongIM.registerMessageType(SystemMessage.class);
                RongIM.registerMessageType(FriendMessage.class);
                RongIM.registerMessageType(GroupMessage.class);
                RongIM.registerMessageType(CourseMessage.class);
                RongIM.registerMessageType(ChangeItemMessage.class);
                RongIM.registerMessageType(SpectatorMessage.class);
                RongIM.registerMessageType(SendFileMessage.class);
                RongIM.registerMessageType(ShareMessage.class);
                //RongIMClient.init(this);
                DemoContext.getInstance().init(App.this);
                //initWorkerThread();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(App.this);
    }

    private void disableAPIDialog(){
        if (Build.VERSION.SDK_INT < 28)return;
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startWBService() {

        Intent service = new Intent(this, SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        }else {
            startService(service);
        }
        startService(service);
    }


}
