package com.kloudsync.techexcel.help;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.ub.techexcel.tools.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pingfan on 2017/7/12.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;

    private static CrashHandler mInstance;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private Map<String, String> infos = new HashMap<String, String>();


    //用来存储设备信息和异常信息
    private Map<String, String> mInfo = new HashMap<>();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private CrashHandler() {
    }

    /**
     * 单例
     */
    public static CrashHandler getInstance() {
        if (mInstance == null) {
            synchronized (CrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new CrashHandler();
                }
            }

        }
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        FileUtils.createCrashFilesDir(mContext);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //1.收集错误信息
        //2.保存错误信息
        //3.上传到服务器

        if (!handleException(e)) {
            //未处，调用系统默认的处理器处理
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(t, e);
            }
        } else {
            //已经人为处理
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 人为处理异常
     *
     * @param e
     * @return true:已经处理，false：没有处理
     */
    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        Log.e("Throwable e",e.toString() + ";;;");
        //收集错误信息
        collectErrorInfo();
        //保存错误信息
        try {
            saveCrashInfoFile(e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return false;
    }

    private String writeFile(String crashStr) throws Exception {
        String time = formatter.format(new Date());
        String fileName = "crash-" + time + ".log";
        if (FileUtils.createCrashFilesDir(mContext)) {
            String path = FileUtils.getBaseCrashDir();
            File dir = new File(path);
            File txtFile = new File(dir,fileName);
            if (!txtFile.exists())
                txtFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(txtFile, true);
            fos.write(crashStr.getBytes());
            fos.flush();
            fos.close();
        }
        return fileName;
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName + "";
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {

            }
        }
    }

    private String saveCrashInfoFile(Throwable ex) throws Exception {
        StringBuffer sb = new StringBuffer();
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String date = sDateFormat.format(new java.util.Date());
            sb.append("\r\n" + date + "\n");
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            String fileName = writeFile(sb.toString());
            return fileName;
        } catch (Exception e) {
            sb.append("an error occured while writing file...\r\n");
            writeFile(sb.toString());
        }
        return null;
    }


    private void collectErrorInfo() {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = TextUtils.isEmpty(pi.versionName) ? "为设置版本名称" : pi.versionName;
                String versionCode = pi.versionCode + "";
                mInfo.put("versionName", versionName);
                mInfo.put("versionCode", versionCode);
            }

            Field[] fields = Build.class.getFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        mInfo.put(field.getName(), field.get(null).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
