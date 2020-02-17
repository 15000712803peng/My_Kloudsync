package com.kloudsync.techexcel.help;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by tonyan on 2020/2/17.
 */

public class KloudPerssionManger {

    public static final int REQUEST_PERMISSION_PHONE_STATE = 1;
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_JOIN_MEETING = 3;
    public static final int REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_START_MEETING = 4;
    public static final int REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_UPLOADFILE = 5;

    public static boolean isPermissionPhoneStateGranted(Context context) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean isPermissionExternalStorageGranted(Context context) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    public static boolean isPermissionReadExternalStorageGranted(Context context) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean isPermissionCameraGranted(Context context) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
    }
}
