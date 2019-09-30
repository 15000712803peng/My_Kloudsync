package com.kloudsync.techexcel.tool;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class ContactsTool {
    public static final int REQUEST_CONTACTS_PERMISSION = 1;
    public static final int REQUEST_CONTACTS = 10086;

    private void intentToContact(Activity host) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        host.startActivityForResult(intent, REQUEST_CONTACTS);
    }

    public void getContact(Activity host) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(host, android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(host,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        REQUEST_CONTACTS_PERMISSION);
            } else {
                intentToContact(host);
            }
        } else {
            intentToContact(host);
        }
    }

    public void onRequestPermissionsResult(Activity host, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentToContact(host);
            } else {
                Toast.makeText(host, "授权被禁止,获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String contactResponse(Activity host, Intent data) {
        String phoneNum = "";
        if (data != null) {
            Uri uri = data.getData();
            String contactName = null;
            ContentResolver contentResolver = host.getContentResolver();
            Cursor cursor = null;
            if (uri != null) {
                cursor = contentResolver.query(uri,
                        new String[]{"display_name", "data1"}, null, null, null);
            }
            while (cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            cursor.close();
            if (phoneNum != null) {
                phoneNum = phoneNum.replaceAll("-", " ");
                phoneNum = phoneNum.replaceAll(" ", "");
            }

        }
        return phoneNum;
    }

}
