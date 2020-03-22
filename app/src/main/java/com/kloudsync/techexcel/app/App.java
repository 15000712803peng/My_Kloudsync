package com.kloudsync.techexcel.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.kloudsync.techexcel.bean.AppName;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.ChangeItemMessage;
import com.kloudsync.techexcel.dialog.message.CourseMessage;
import com.kloudsync.techexcel.dialog.message.CustomizeMessage;
import com.kloudsync.techexcel.dialog.message.DemoContext;
import com.kloudsync.techexcel.dialog.message.FriendMessage;
import com.kloudsync.techexcel.dialog.message.GroupMessage;
import com.kloudsync.techexcel.dialog.message.HelloFriendMessage;
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
import com.ub.service.activity.NetWorkChangReceiver;
import com.ub.service.activity.WatchCourseActivity3;

import org.xutils.x;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.agora.openlive.model.WorkerThread;
import io.rong.imkit.RongIM;

public class App extends Application {
    private CrashHandler mCrashHandler;
	public static App mApplication;
    private ThreadManager threadMgr;
    private Handler mainHandler;
    private MainActivity mainActivityInstance;
	public List<Activity> mList = new LinkedList<Activity>();

    public static List<AppName> appNames;
    public static List<AppName> appCNNames;
    public static List<AppName> appENNames;
    @Override
    public void onCreate() {
        super.onCreate();
        threadMgr = ThreadManager.getManager();
        mainHandler = new Handler(Looper.getMainLooper());
	    mApplication = this;
//        startWBService();
        disableAPIDialog();
//        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
        asyncInit();
        initBroadcastReceiver();
    }


    private NetWorkChangReceiver netWorkChangReceiver;

    private void initBroadcastReceiver() {
        if (netWorkChangReceiver == null) {
            netWorkChangReceiver = new NetWorkChangReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangReceiver, filter);

    }



    @Override
    public void onTerminate() {
        unregisterReceiver(netWorkChangReceiver);
        super.onTerminate();

    }

    public static Context getAppContext() {
		return mApplication;
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
                RongIM.registerMessageType(HelloFriendMessage.class);
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

	private static float mNoncompatDensity;
	private static float mNoncompatScaledDensity;

	/**
	 * 设置屏幕自定义密度,需要在每个activity的setContentView之前调用一下
	 *
	 * @param activity
	 * @param type     1表示以高为维度,0表示以宽为维度适配
	 */
	public static void setCustomDensity(Activity activity, int type) {
		final DisplayMetrics appDisplayMetrics = mApplication.getResources().getDisplayMetrics();
		if (mNoncompatDensity == 0) {
			mNoncompatDensity = appDisplayMetrics.density;
			mNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
			mApplication.registerComponentCallbacks(new ComponentCallbacks() {
				@Override
				public void onConfigurationChanged(Configuration newConfig) {
					if (newConfig != null && newConfig.fontScale > 1) {
						mNoncompatScaledDensity = mApplication.getResources().getDisplayMetrics().scaledDensity;
					}
				}

				@Override
				public void onLowMemory() {

				}
			});
		}

		final float targetDensity;
		if (type == 1) {
			targetDensity = appDisplayMetrics.heightPixels / 812.0f;
		} else {
			targetDensity = appDisplayMetrics.widthPixels / 375.0f;
		}
		final float targetScaledDensity = targetDensity * (mNoncompatScaledDensity / mNoncompatDensity);
		final int targetDensityDpi = (int) (160 * targetDensity);

		appDisplayMetrics.density = appDisplayMetrics.scaledDensity = targetDensity;
		appDisplayMetrics.scaledDensity = targetScaledDensity;
		appDisplayMetrics.densityDpi = targetDensityDpi;

		DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
		activityDisplayMetrics.density = activityDisplayMetrics.scaledDensity = targetDensity;
		activityDisplayMetrics.scaledDensity = targetScaledDensity;
		activityDisplayMetrics.densityDpi = targetDensityDpi;
	}

	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void removeActivity(Activity activity) {
		mList.remove(activity);
	}

	public void exitActivity() {
		try {
			for (Activity activity : mList) {
				if (activity != null) {
					activity.finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
