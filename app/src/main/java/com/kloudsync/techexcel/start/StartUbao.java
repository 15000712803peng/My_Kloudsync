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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.AppName;
import com.kloudsync.techexcel.bean.CompanyAccountInfo;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.bean.LoginResult;
import com.kloudsync.techexcel.bean.RongCloudData;
import com.kloudsync.techexcel.bean.UserPreferenceData;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.KloudPerssionManger;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.tool.SystemUtil;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kloudsync.techexcel.config.AppConfig.ClassRoomID;
import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_PHONE_STATE;

public class StartUbao extends Activity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isFirst;
    private boolean isLogIn;
    private String telephone;
    private String password;
    private int countrycode;
    private ImageView welcomeImage;
    private TextView requestPermissionText;

    public static StartUbao instance;
    String wechatFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        instance = this;
        LoginGet.wechatFilePaht = "";
        welcomeImage = (ImageView) findViewById(R.id.image_welcome);
        requestPermissionText = (TextView) findViewById(R.id.txt_request_permission);
        requestPermissionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(StartUbao.this, new String[]{
                        Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_PHONE_STATE);
            }
        });
        wechatFilePath = getIntent().getStringExtra("wechat_data_path");
        doWithPermissionChecked();
    }

    private void initIfPermissionGranted() {
        startWBService();
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
        if (countrycode == 0) {
            countrycode = 86;
        }
        sharedPreferences.edit().putInt("countrycode", countrycode).commit();
        AppConfig.COUNTRY_CODE = countrycode = sharedPreferences.getInt("countrycode", 86);
        AppConfig.LANGUAGEID = getLocaleLanguage();
        if (AppConfig.LANGUAGEID == 1) {
            //English
            welcomeImage.setImageResource(R.drawable.welcome_english);
        } else if (AppConfig.LANGUAGEID == 2) {
            welcomeImage.setImageResource(R.drawable.welcome);
        }
        AppConfig.deviceType = DeviceManager.getDeviceType(this);
        Log.e("deviceType", "type:" + AppConfig.deviceType);
        if (isFirst) {
            Log.e("StartUbao", "step three");
            editor.putBoolean("isFirst", false);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(),
                    StartKloudActivity.class);
            /*Intent intent = new Intent(getApplicationContext(),
                    LoginActivity.class);*/
            startActivity(intent);
            finish();
        } else {
            if (isLogIn) {
                String name = sharedPreferences.getString("name", null);
                String pwd = LoginGet.DecodeBase64Password(sharedPreferences.getString("password", null));
                String telephone = sharedPreferences.getString("telephone", null);
                Log.e("autoLogin", "name:" + name + ",pwd:" + pwd + ",telephone:" + telephone);
                Log.e("StartUbao", "step four");
                processLogin(name, pwd, telephone);
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

    Disposable loginDisposable;
    String rongCloudUrl = "";

    private void processLogin(final String name, final String password, final String phoneNumber) {
        loginDisposable = Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String o) throws Exception {

                try {
                    Response<NetworkResponse<LoginData>> response = ServiceInterfaceTools.getinstance().login(name, password).execute();
                    Log.e("processLogin", "LoginData,success:" + response.isSuccessful() + ",body:" + response.body());
                    if (response == null || !response.isSuccessful() || response.body() == null || response.body().getRetCode() != 0) {
                        Observable.just("go_to_login").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Intent intent = new Intent(getApplicationContext(),
                                        LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    } else {
                        if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                            saveLoginData(response.body().getRetData());
                            rongCloudUrl = AppConfig.URL_PUBLIC + "RongCloudUserToken";
                            editor.putString("name", name);
                            editor.putString("telephone", phoneNumber);
                            editor.putString("password", LoginGet.getBase64Password(password));
                            editor.putInt("countrycode", AppConfig.COUNTRY_CODE);
                            editor.commit();
                        } else {

                        }
                    }
                } catch (UnknownHostException e) {

                } catch (SocketTimeoutException exception) {

                }
                return rongCloudUrl;
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                if (loginDisposable == null || loginDisposable.isDisposed()) {
                    return s;
                }
                if (!TextUtils.isEmpty(s)) {
                    try {
                        Response<NetworkResponse<RongCloudData>> response = ServiceInterfaceTools.getinstance().getRongCloudInfo().execute();
                        Log.e("processLogin", "RongCloudData:" + response.isSuccessful() + ",body:" + response.body());
                        if (response != null && response.isSuccessful() && response.body() != null) {
                            if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                                RongCloudData data = response.body().getRetData();
                                AppConfig.RongUserToken = data.getUserToken();
                                AppConfig.RongUserID = data.getRongCloudUserID();
                                getAppNames();

                            }
                        }
                    } catch (UnknownHostException e) {

                    } catch (SocketTimeoutException exception) {

                    }
                }
                return s;
            }
        }).subscribe();

    }


    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void saveLoginData(LoginData data) {
        AppConfig.UserToken = data.getUserToken();
        AppConfig.UserID = data.getUserID() + "";
        AppConfig.UserName = data.getName();
        AppConfig.SchoolID = Integer.parseInt(data.getSchoolID() + "");
        AppConfig.Role = data.getRole();
        AppConfig.UserExpirationDate = data.getExpirationDate();
        ClassRoomID = data.getClassRoomID();
        AppConfig.Mobile = data.getMobile();
        editor.putString("UserID", AppConfig.UserID);
        editor.putString("UserToken", AppConfig.UserToken);
        editor.putString("Name", AppConfig.UserName);
        editor.putString("MeetingId",ClassRoomID);
        editor.commit();
    }

    private void startWBService() {
        Intent service = new Intent(this, SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        } else {
            startService(service);
        }

    }



    private void doWithPermissionChecked(){
        if(KloudPerssionManger.isPermissionPhoneStateGranted(this)){
            initIfPermissionGranted();
            requestPermissionText.setVisibility(View.GONE);
        }else {
            requestPermissionText.setVisibility(View.VISIBLE);
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_PHONE_STATE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("check_permission","phone_state_granted");
                 initIfPermissionGranted();
                requestPermissionText.setVisibility(View.GONE);
            } else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                Log.e("check_permission","phone_state_denied");
                requestPermissionText.setVisibility(View.VISIBLE);
            }

        }
    }

    private void getAppNames() {
        int mId=sharedPreferences.getInt("SchoolID",0);
        int id=mId==0?AppConfig.SchoolID:mId;
        ServiceInterfaceTools.getinstance().getAppNames(id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getString("msg").equals("success")){
                        Gson gson = new Gson();
                        List<AppName> appCNNameList=new ArrayList<AppName>();
                        List<AppName> appENNameList=new ArrayList<AppName>();
                        //JSONObject jsonObjectData=new JSONObject(jsonObject.getString("data"));
                        List<AppName> appNameList= gson.fromJson(jsonObject.getString("data"), new TypeToken<List<AppName>>(){}.getType());
                        App.appNames=appNameList;
                        for(AppName appName:appNameList){
                            if(appName.getLanguageId()==0){
                                appCNNameList.add(appName);
                            }else {
                                appENNameList.add(appName);
                            }
                        }
                        App.appCNNames=appCNNameList;
                        App.appENNames=appENNameList;
                        System.out.println("App.appNames->"+App.appENNames.size());
                        goToMainActivity();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());
                goToMainActivity();
            }
        });
    }
}
