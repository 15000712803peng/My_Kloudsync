package com.kloudsync.techexcel.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventWxFilePath;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.tools.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class ReceiveWeChatDataActivity extends Activity {

    private SharedPreferences sharedPreferences;
    public static ReceiveWeChatDataActivity instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadnull);
        Log.e("WeChatActivity", "on create");
        instance = this;
        getUri();
        finish();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("WeChatActivity", "on new intent");
    }

    private void getUri() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        String filePath = "";
        if (uri == null) {
            Toast.makeText(getApplicationContext(), "data error ,open failed", Toast.LENGTH_SHORT).show();
        }
        if (uri != null) {
            filePath = FileUtils.getPath(this, uri);
            if (TextUtils.isEmpty(filePath) || !(new File(filePath).exists())) {
                Toast.makeText(getApplicationContext(), "data error ,open failed", Toast.LENGTH_SHORT).show();
            }
        }
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        final boolean isLogIn = sharedPreferences.getBoolean("isLogIn", false);
        final Intent lanchIntent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName());
        lanchIntent.putExtra("wechat_data_path", filePath);
        AppConfig.wechatFilePath=filePath;

        if (!isLogIn) {
            Log.e("check_dialog", "one  "+filePath);
            startActivity(lanchIntent);

        } else {
            if (((App) getApplication()).getMainActivityInstance() == null) {
                Log.e("check_dialog", "two  "+filePath);
                startActivity(lanchIntent);
            } else {
                Log.e("check_dialog", "three  "+ filePath);
                EventWxFilePath path = new EventWxFilePath();
                path.setPath(filePath);
                EventBus.getDefault().post(path);

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("WeChatActivity", "on destroy");
//        if(service != null){
//            stopService(service);
//        }
    }
}
