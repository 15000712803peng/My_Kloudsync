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
    public static final int REQUEST_PERMISSION_FOR_JOIN_MEETING = 6;
    public static final int REQUEST_PERMISSION_FOR_INSTALL_APK = 7;
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE_FOR_VIWE_DOC_IN_SPACE = 8;
	public static final int REQUEST_PERMISSION_LOCATION_WRITE_READ = 9;


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

    public static boolean isPermissionRecordAudioGranted(Context context) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
    }

    public static boolean isPermissionRequestInstallPackagesGranted(Context context) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.REQUEST_INSTALL_PACKAGES);
    }

    public static boolean isPermissionInstallApkGranted(Context context) {
        return isPermissionReadExternalStorageGranted(context) && isPermissionRequestInstallPackagesGranted(context);
    }

}
