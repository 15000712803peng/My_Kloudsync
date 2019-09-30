package com.kloudsync.techexcel.start;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.SystemUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.Locale;

public class StartUbao extends Activity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isFirst;
    private boolean isLogIn;
    private String telephone;
    private String password;
    private int countrycode;

    public static StartUbao instance;
    String wechatFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        instance = this;
        LoginGet.wechatFilePaht = "";
        wechatFilePath = getIntent().getStringExtra("wechat_data_path");
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!instance.isFinishing()) {
//                    PgyUpdateManager.register(StartUbao.this);
//                }
//            }
//        }, 2000);

        AppConfig.DEVICE_ID = getDeviceInfo(StartUbao.this);
        Log.e("deviceID", AppConfig.DEVICE_ID + ":");
        showSystemParameter();

        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();

        isFirst = sharedPreferences.getBoolean("isFirst", true);
        isLogIn = sharedPreferences.getBoolean("isLogIn", false);
        telephone = sharedPreferences.getString("telephone", null);
        password = LoginGet.DecodeBase64Password(sharedPreferences.getString("password", ""));
        countrycode = sharedPreferences.getInt("countrycode", 86);

        AppConfig.LANGUAGEID = getLocaleLanguage();

		/*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
		finish();*/


        if (isFirst) {
            Log.e("StartUbao", "step three");
            editor.putBoolean("isFirst", false);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(),
                    LoginActivity
                            .class);
            /*Intent intent = new Intent(getApplicationContext(),
                    LoginActivity.class);*/
            startActivity(intent);
            finish();
        } else {
            if (isLogIn) {
                Log.e("StartUbao", "step four");
                LoginGet.LoginRequest(StartUbao.this, "+" + countrycode
                        + telephone, password, 1, sharedPreferences, editor, wechatFilePath, ((App) getApplication()).getThreadMgr());
            } else {
                Log.e("StartUbao", "step five");
                Intent intent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("StartUbao");
        MobclickAgent.onResume(this);       //统计时长
    }

    public String getDeviceInfo(Context context) {
        try {
            JSONObject json = new JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = "";
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                device_id = tm.getDeviceId();
            }
            Log.e("deviceID", device_id + ":");

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            JSONObject obj = new JSONObject(json.toString());
            String s = obj.getString("device_id");
            Log.e("deviceID", s + ":");

            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showSystemParameter() {
        AppConfig.SystemModel = SystemUtil.getSystemModel();
        Log.e("hahaha", "手机型号：" + SystemUtil.getSystemModel());
        /*String TAG = "系统参数：";
		Log.e(TAG, "手机厂商：" + SystemUtil.getDeviceBrand());
		Log.e(TAG, "手机型号：" + SystemUtil.getSystemModel());
		Log.e(TAG, "手机当前系统语言：" + SystemUtil.getSystemLanguage());
		Log.e(TAG, "Android系统版本号：" + SystemUtil.getSystemVersion());
		Log.e(TAG, "手机IMEI：" + SystemUtil.getIMEI(getApplicationContext()));*/
    }


    private int getLocaleLanguage() {

        int language = sharedPreferences.getInt("language", -1);
        String mlanguage = getResources().getConfiguration().locale
                .getLanguage();
        String mcountry = getResources().getConfiguration().locale.getCountry();
        Log.e("嘿嘿嘿", mlanguage + ":" + mcountry + ":" + language);
        if (language != -1) {
            switch (language) {
                case 1:
                    updateLange(this, Locale.ENGLISH);
                    break;
                case 2:
                    updateLange(this, Locale.SIMPLIFIED_CHINESE);
                    break;
                default:
                    break;
            }
            return language;
        }
        if (mlanguage.equals("en")) {
            return 1;
        } else if (mlanguage.equals("zh")) {
            return 2;
        }/*else if(mlanguage.equals("ja")){
			return 4;
		}else if(mlanguage.equals("fr")){
			return 12;
		}*/
        return 1;

    }

    public static void updateLange(Context context, Locale locale) {
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        DisplayMetrics dm = res.getDisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            config.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, dm);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StartUbao");
        MobclickAgent.onPause(this);
    }
}
