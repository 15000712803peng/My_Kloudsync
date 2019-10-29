package com.kloudsync.techexcel.help;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.kloudsync.techexcel.bean.SupportDevice;

import java.util.List;

/**
 * Created by tonyan on 2019/10/25.
 */

public class DeviceManager {

    public static int getDeviceType(Context context){
        Intent intent = new Intent();
        intent.setClassName("com.onyx.android.note", "com.onyx.android.note.note.ui.ScribbleActivity");
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent,0);
        Log.e("ResolveInfo","infos:" + infos);
        if(infos == null || infos.size() == 0){
            return SupportDevice.PHONE;
        }else {
            //说明有写笔记的功能
            return SupportDevice.BOOK;
        }
    }
}
