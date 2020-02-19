package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

public class AboutActivity2 extends Activity implements View.OnClickListener {

    private TextView tv_title;
    private RelativeLayout rl_ps;
    private SharedPreferences sharedPreferences;
    private RelativeLayout backLayout;
    int language;
    private Intent intent;
    private TextView versionText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
       // initSlideTrans();
    }


    private void initView() {
        rl_ps = (RelativeLayout) findViewById(R.id.rl_ps);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.about);
        versionText = findViewById(R.id.txt_version);
        versionText.setText("V" + getAppVersionName(this));
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        language = sharedPreferences.getInt("language",1);
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        rl_ps.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();

                break;
            case R.id.rl_ps:
                String enUrl = "https://kloudsync.peertime.cn/privacy.html";
                String zhUrl = "https://kloudsync.peertime.cn/privacy-cn.html";
                String tag = getString(R.string.privacy_statement);
                intent = new Intent(getApplicationContext(), AboutWebActivity.class);
                intent.putExtra(AboutWebActivity.TAG,tag);
                intent.putExtra(AboutWebActivity.ENURL,enUrl);
                intent.putExtra(AboutWebActivity.ZHURL,zhUrl);
                startActivity(intent);
                // OpenWeb();
                break;
            default:
                break;
        }

    }

    private static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;

            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }



//  调用系统浏览器加载链接
/*    private void OpenWeb() {
        Uri uri;
        if(language == 1){
            uri = Uri.parse("https://kloudsync.peertime.cn/privacy.html");
            //Uri uri = Uri.parse("http://http;//www.peertime.com/privacy-statement");

        }else {
            uri = Uri.parse("https://kloudsync.peertime.cn/privacy-cn.html");
        }

        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }*/
/*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(AboutActivity2.this);
    }*/
}
