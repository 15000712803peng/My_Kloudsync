package com.kloudsync.techexcel.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventRefreshTab;
import com.kloudsync.techexcel.bean.EventWxFilePath;
import com.kloudsync.techexcel.bean.UserPath;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddDocToSpaceDialog;
import com.kloudsync.techexcel.dialog.AddWxDocDialog;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.dialog.message.CustomizeMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.FriendMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.HelloFriendMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.GroupMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.KnowledgeMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.SendFileMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.ShareMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.SystemMessageItemProvider;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.kloudsync.techexcel.frgment.ContactFragment;
import com.kloudsync.techexcel.frgment.TeamDocumentsFragment;
import com.kloudsync.techexcel.frgment.PersonalCenterFragment;
import com.kloudsync.techexcel.frgment.ServiceFragment;
import com.kloudsync.techexcel.frgment.TopicFragment;
import com.kloudsync.techexcel.frgment.TwoToOneFragment;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.ContactHelpInterface;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.personal.PersonalCollectionActivity;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.service.UploadService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.kloudsync.techexcel.view.CustomViewPager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ErrorCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.wuyr.rippleanimation.RippleAnimation;


public class MainActivityV3 extends FragmentActivity implements AddWxDocDialog.OnDocSavedListener, AddDocToSpaceDialog.OnSpaceSelectedListener {

    private List<TextView> tvs = new ArrayList<TextView>();
    private TextView tv_redcontact;
    private CustomViewPager vp;
    private RelativeLayout rl_update;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private TextView tv_community;
    private int tvIDs[] = {R.id.tv_service, R.id.tv_dialogue, R.id.tv_contact,
            R.id.tv_community, R.id.tv_personal_center};
    private int drawIDs[] = {R.drawable.tab1_unselected, R.drawable.tab2_unselected,
            R.drawable.tab3_unselected, R.drawable.tab4_unselected,
            R.drawable.tab5_unselected};
    private int draw_selectIDs[] = {R.drawable.tab1_selected, R.drawable.tab2_selected,
            R.drawable.tab3_selected, R.drawable.tab4_selected, R.drawable.tab5_selected};
    float density;

    public static RongIMClient mRongIMClient;
    public static MainActivityV3 instance = null;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private WebSocketClient mWebSocketClient;

    public static boolean RESUME = false;

    private boolean flag_update;

    private boolean flag_dialog;

    private boolean flag_jinhua;

    Intent service;

    App app;

    private static ContactHelpInterface chi;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.RONGCONNECT_ERROR:
                    sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                            MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("isLogIn", false);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), (String) msg.obj,
                            Toast.LENGTH_LONG).show();
                    RongIM.getInstance().disconnect();
                    final Intent intent = getPackageManager()
                            .getLaunchIntentForPackage(getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case AppConfig.OnlineGunDan:
                    if (1 == vp.getCurrentItem()) {
                        GoTOTab(0);
                    }
                    RongIM.getInstance().disconnect();
                    AppConfig.Online = 1;
                    flag_dialog = false;
                    Toast.makeText(MainActivityV3.this,
                            getResources().getString(R.string.Dialog_GunDan), Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.UPLOADHEAD:
                    rl_update.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.uploadsuccess),
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), PersonalCollectionActivity.class);
                    startActivity(i);
                    break;
                case AppConfig.UPLOADFAILD:
                    rl_update.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.uploadfailure),
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    long systemTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemTime = System.currentTimeMillis();
        instance = this;
        app = (App) getApplication();
//        app.setMainActivityInstance(instance);
        app.CheckLanguage();
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        EventBus.getDefault().register(this);
        editor.putBoolean("isLogIn", true).commit();
        setContentView(R.layout.activity_main_v3);
//        PgyUpdateManager.register(this);
        initView();
        requestRongCloudOnlineStatus();
        GetSchoolInfo();
        initUpdate();
        StartWBServiceHAHA();
        GetMaZhang();
        GetMyPermission();
        String wechatFilePath = getIntent().getStringExtra("wechat_data_path");
        if (!TextUtils.isEmpty(wechatFilePath)) {
            if (addWxDocDialog != null) {
                addWxDocDialog.dismiss();
                addWxDocDialog = null;
            }

            addWxDocDialog = new AddWxDocDialog(this, wechatFilePath);
            addWxDocDialog.setSavedListener(this);
            addWxDocDialog.show();
        }
    }

    private void requestRongCloudOnlineStatus() {
        ServiceInterfaceTools.getinstance().getRongCloudOnlineStatus().enqueue(new Callback<NetworkResponse<Integer>>() {
            @Override
            public void onResponse(Call<NetworkResponse<Integer>> call, Response<NetworkResponse<Integer>> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getRetData() == null) {
                        return;
                    }
                    AppConfig.Online = response.body().getRetData();
                }
            }

            @Override
            public void onFailure(Call<NetworkResponse<Integer>> call, Throwable t) {

            }
        });

        Log.e("user_token", "user_token:" + AppConfig.UserToken);
    }

    private void GetMyPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO}, 0x0010);
        }
    }

    UserPath userPath;

    private void GetSchoolInfo() {

        LoginGet lg = new LoginGet();
        lg.setSchoolTeamGetListener(new LoginGet.SchoolTeamGetListener() {
            @Override
            public void getST(School school) {
                Log.e("GetShoolInfo","school:" + school);
                if (school != null) {
                    TeamSpaceBean teamSpaceBean = school.getTeamSpaceBean();
                    AppConfig.SchoolID = school.getSchoolID();
                    editor.putInt("SchoolID", school.getSchoolID());
                    editor.putString("SchoolName", school.getSchoolName());
                    editor.putString("teamname", teamSpaceBean.getName());
                    editor.putInt("teamid", teamSpaceBean.getItemID());
                    editor.commit();
                } else {
                    editor.putString("SchoolName", "");
                    editor.putString("teamname", "");
                    editor.putInt("teamid", -1);
                    editor.commit();
                }
                initDatas();
            }
        });
        lg.GetUserPreference(this, 10001 + "");

    }

    private IWXAPI api;

    private void GetMaZhang() {
        WeiXinApi.getInstance().init(this);

        api = WXAPIFactory.createWXAPI(MainActivityV3.this,
                AppConfig.WX_APP_ID, true);
        api.registerApp(AppConfig.WX_APP_ID);
        api.handleIntent(getIntent(), WeiXinApi.getInstance());
        WeiXinApi.getInstance().setApi(api);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, WeiXinApi.getInstance());
    }


    private void UpOrNoOutInfo() {
        if (!TextUtils.isEmpty(AppConfig.OUTSIDE_PATH)) {
            Intent service2 = new Intent(MainActivityV3.this, UploadService.class);
            service2.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startService(service2);
        } else {
            return;
        }
    }

    private void StartWBServiceHAHA() {
        service = new Intent(getApplicationContext(), SocketService.class);
        startService(service);
    }

    ProgressDialog progressDialog;
    private void initUpdate() {
        // TODO Auto-generated method stub
        new PgyUpdateManager.Builder()
                .setForced(false)                //设置是否强制更新,非自定义回调更新接口此方法有用
                .setUserCanRetry(false)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
                .setDeleteHistroyApk(false)     // 检查更新前是否删除本地历史 Apk
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //没有更新是回调此方法
                        Log.e("pgyer", "there is no new version");
                    }

                    @Override
                    public void onUpdateAvailable(final AppBean appBean) {
                        //有更新是回调此方法
                        Log.e("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());

                        //调用以下方法，DownloadFileListener 才有效；如果完全使用自己的下载方法，不需要设置DownloadFileListener
//
                        new AlertDialog.Builder(MainActivityV3.this)
                                .setTitle(getResources().getString(R.string.update))
                                .setMessage(getResources().getString(R.string.update_message))
                                .setPositiveButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(
                                        getResources().getString(R.string.update),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                                            }
                                        }).show();
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        Log.e("pgyer", "check update failed ", e);

                    }
                })
                //注意 ：下载方法调用 PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); 此回调才有效
                .setDownloadFileListener(new DownloadFileListener() {   // 使用蒲公英提供的下载方法，这个接口才有效。
                    @Override
                    public void downloadFailed() {
                        //下载失败
                        Log.e("pgyer", "download apk failed");
                        if (progressDialog != null) {
                            progressDialog.cancel();
                            progressDialog = null;
                        }

                    }

                    @Override
                    public void downloadSuccessful(Uri uri) {
                        Log.e("pgyer", "download apk failed");
                        if (progressDialog != null) {
                            progressDialog.cancel();
                            progressDialog = null;
                        }
                        PgyUpdateManager.installApk(uri);  // 使用蒲公英提供的安装方法提示用户 安装apk

                    }

                    @Override
                    public void onProgressUpdate(Integer... integers) {
                        Log.e("pgyer", "update download apk progress : " + integers[0]);
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(MainActivityV3.this);
                            progressDialog.setProgressStyle(1);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage(getResources().getString(R.string.downloading));
                            progressDialog.show();
                        } else {
                            if (integers != null && integers.length > 0) {
                                progressDialog.setProgress(integers[0]);
                            }

                        }
                    }
                }).register();
    }

    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;

        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        vp = (CustomViewPager) findViewById(R.id.vp);
        tv_redcontact = (TextView) findViewById(R.id.tv_redcontact);
        tv_community = (TextView) findViewById(R.id.tv_community);
        GetTvShow();
        GetTabName();

        RongConnect();

    }

    private void GetTabName() {
        String[] tab = getResources().getStringArray(R.array.tabname);
        for (int i = 0; i < tvIDs.length; i++) {
            tvs.get(i).setText(tab[i]);
        }

    }

    private void GetTabName2() {
        String[] tab = getResources().getStringArray(R.array.tabname2);
        for (int i = 0; i < tvIDs.length; i++) {
            tvs.get(i).setText(tab[i]);
        }
    }

    private void GetTvShow() {
        for (int i = 0; i < tvIDs.length; i++) {
            TextView tv = (TextView) findViewById(tvIDs[i]);
            if (i == 0) {
                Drawable d = getResources().getDrawable(draw_selectIDs[i]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 26),
                        DensityUtil.dp2px(getApplicationContext(), 26)); // 必须设置图片大小，否则不显示
                tv.setTextColor(getResources().getColor(R.color.tab_blue));
                tv.setCompoundDrawables(null, d, null, null);
            } else {
                Drawable d = getResources().getDrawable(drawIDs[i]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 26),
                        DensityUtil.dp2px(getApplicationContext(), 26));  // 必须设置图片大小，否则不显示
                tv.setTextColor(getResources().getColor(R.color.tab_gray));
                tv.setCompoundDrawables(null, d, null, null);
            }
            tv.setOnClickListener(new myOnClick());
            tvs.add(tv);
        }
    }

    private void RongConnect() {
        RongIM.connect(AppConfig.RongUserToken, new ConnectCallback() {

            @Override
            public void onError(ErrorCode arg0) {
                Toast.makeText(getApplicationContext(), "connect onError",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String arg0) {
                /*Toast.makeText(getApplicationContext(), "connect onSuccess",
                        Toast.LENGTH_SHORT).show();*/
                AppConfig.RongUserID = arg0;

                RongIM.registerMessageTemplate(new CustomizeMessageItemProvider());
                RongIM.registerMessageTemplate(new KnowledgeMessageItemProvider());
                RongIM.registerMessageTemplate(new SystemMessageItemProvider());
                RongIM.registerMessageTemplate(new FriendMessageItemProvider());
                RongIM.registerMessageTemplate(new GroupMessageItemProvider());
                RongIM.registerMessageTemplate(new SendFileMessageItemProvider());
//				RongIM.registerMessageTemplate(new CourseMessageItemProvider());
                RongIM.registerMessageTemplate(new ShareMessageItemProvider());
                RongIM.registerMessageTemplate(new HelloFriendMessageItemProvider());

//                initDatas();
                flag_dialog = true;
                if (RongIM.getInstance() != null
                        && RongIM.getInstance().getRongIMClient() != null) {
                    /**
                     * 设置连接状态变化的监听器.
                     */
                    RongIM.getInstance()
                            .getRongIMClient()
                            .setConnectionStatusListener(
                                    new MyConnectionStatusListener());
                }
                AppConfig.Online = 0;
            }


            @Override
            public void onTokenIncorrect() {
                Log.e("Token Incorrect", "Token Incorrect");
                Message msg = new Message();
                msg.what = AppConfig.RONGCONNECT_ERROR;
                msg.obj = "连接错误";
                handler.sendMessage(msg);
            }
        });
//		getToken();
    }


    private class MyConnectionStatusListener implements
            RongIMClient.ConnectionStatusListener {

        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {

                case CONNECTED:// 连接成功。

                    break;
                case DISCONNECTED:// 断开连接。

                    break;
                case CONNECTING:// 连接中。

                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。

                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    if (!instance.isFinishing()) {
                        Message msg = new Message();
                        msg.what = AppConfig.OnlineGunDan;
//                        msg.obj = "该账号已在其他设备上登录";
                        handler.sendMessage(msg);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    TopicFragment topicFragment = new TopicFragment();
    private void initDatas() {
        TeamDocumentsFragment documentsFragment = new TeamDocumentsFragment();
        TwoToOneFragment twoToOneFragment = new TwoToOneFragment();
        ServiceFragment serviceFragment = new ServiceFragment();
//        UpgradeFragment upgradeFragment = new UpgradeFragment();
//        CommunityFragment communityFragment = new CommunityFragment();
        PersonalCenterFragment personalCenterFragment = new PersonalCenterFragment();

        mTabs = new ArrayList<Fragment>();
        mTabs.add(documentsFragment);
        mTabs.add(twoToOneFragment);
        mTabs.add(serviceFragment);
        mTabs.add(topicFragment);
//        mTabs.add(upgradeFragment);
        if (sharedPreferences.getBoolean("enable_sync", false)) {
            tv_community.setVisibility(View.VISIBLE);
        } else {
            tv_community.setVisibility(View.GONE);
        }

        mTabs.add(personalCenterFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        vp.setAdapter(mAdapter);
        vp.setOffscreenPageLimit(5);
        GoTOTab(0);
        Log.e("user_info", "user_token:" + AppConfig.UserToken + ",company_id:" + AppConfig.SchoolID);

    }

    private void initDatas2() {
        TeamDocumentsFragment documentsFragment = new TeamDocumentsFragment();
        TwoToOneFragment twoToOneFragment = new TwoToOneFragment();
        ServiceFragment serviceFragment = new ServiceFragment();
        TopicFragment topicFragment = new TopicFragment();
        PersonalCenterFragment personalCenterFragment = new PersonalCenterFragment();

        mTabs = new ArrayList<Fragment>();
        mTabs.add(documentsFragment);
        mTabs.add(twoToOneFragment);
        mTabs.add(serviceFragment);
        mTabs.add(topicFragment);
        mTabs.add(personalCenterFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        vp.setAdapter(mAdapter);
        vp.setOffscreenPageLimit(4);
//        GoTOTab(0);

    }

    protected class myOnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            /*if(AppConfig.SOCKET!=null){
                Log.e("lalala",AppConfig.SOCKET.isClosed() + ";" + AppConfig.SOCKET.isConnected());
			}else{
				Log.e("lalala","Socket null");
			}*/
            switch (v.getId()) {
                case R.id.tv_dialogue:
                    if (!flag_dialog) {
                        RongConnect();
                    }
                    GoTOTab(1);
                    break;
                case R.id.tv_service:
                    GoTOTab(0);
                    break;
                case R.id.tv_contact:
                    GoTOTab(2);
                    break;
                case R.id.tv_community:
                    GoTOTab(3);
                    break;
                case R.id.tv_personal_center:
                    GoTOTab(4);
                    break;
                default:
                    break;
            }

        }

    }

    public void GoTOTab(int s) {
        for (int i = 0; i < tvs.size(); i++) {
            if (i == s) {
                // 必须设置图片大小，否则不显示ad
                Drawable d = getResources().getDrawable(draw_selectIDs[s]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 20),
                        DensityUtil.dp2px(getApplicationContext(), 20)); // 必须设置图片大小，否则不显示
                tvs.get(s).setTextColor(getResources().getColor(R.color.tab_blue));
                tvs.get(s).setCompoundDrawables(null, d, null, null);
            } else {
                Drawable d = getResources().getDrawable(drawIDs[i]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 20),
                        DensityUtil.dp2px(getApplicationContext(), 20));  // 必须设置图片大小，否则不显示
                tvs.get(i).setTextColor(getResources().getColor(R.color.tab_gray));
                tvs.get(i).setCompoundDrawables(null, d, null, null);
            }
        }

        vp.setCurrentItem(s, false);
    }

    private void UpdateOutData() {
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);

        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        // params.setContentType("application/octet-stream");
        File file = new File(AppConfig.OUTSIDE_PATH);
        if (file.exists()) {
            int lastSlash = 0;
            lastSlash = AppConfig.OUTSIDE_PATH.lastIndexOf("/");
            String name = AppConfig.OUTSIDE_PATH.substring(lastSlash + 1, AppConfig.OUTSIDE_PATH.length());
            params.addBodyParameter(name, file);
            String url = null;
            try {
                url = AppConfig.URL_PUBLIC + "EventAttachment/AddNewFavoriteMultipart?FileName="
                        + URLEncoder.encode(LoginGet.getBase64Password(name), "UTF-8")
                        + "&Guid=" + AppConfig.DEVICE_ID + System.currentTimeMillis()
                        + "&Total=1&Index=1";
                Log.e("hahaha", url + ":" + name);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.e("url", url);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.upload),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {

                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.e("hahaha", responseInfo + "");
                            Message message = new Message();
                            message.what = AppConfig.UPLOADHEAD;
                            handler.sendEmptyMessage(message.what);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Log.e("error", msg.toString());
                            Message message = new Message();
                            message.what = AppConfig.UPLOADFAILD;
                            handler.sendEmptyMessage(message.what);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }

        AppConfig.OUTSIDE_PATH = "";
    }

    public void DisplayRed(boolean flag_r) {
        tv_redcontact.setVisibility(flag_r ? View.VISIBLE : View.GONE);
        if (chi != null) {
            chi.RefreshRed(flag_r);
        }
    }


    private void KillFile() {
        FileUtils fileUtils = new FileUtils(getApplicationContext());
        fileUtils.deleteFile();
        fileUtils.deleteFile2();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment.getClass().equals(ContactFragment.class)) {
            chi = (ContactHelpInterface) fragment;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /*if (!flag_update) {
            UpOrNoOutInfo();
            flag_update = true;
        }*/
//        flag_update = !flag_update;
//        if (!TextUtils.isEmpty(AppConfig.OUTSIDE_PATH) && flag_update) {
//            UpOrNoOutInfo();
//        }
    }


    public void onResume() {
        super.onResume();

        if (RESUME) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            for (int i = 0; i < mTabs.size(); i++) {
                Fragment fragment = (Fragment) mTabs.get(i);
                ft.remove(fragment);
            }
//            ft.commit();
            //华为用ft.commit()，会引发异常(Can not perform this action after onSaveInstanceState)，改用这个就不会了，而且效果一样
            ft.commitAllowingStateLoss();
            ft = null;
            fm.executePendingTransactions();

            GetTabName();
            initDatas();
            GoTOTab(4);
        }

        if (AppConfig.isRefreshRed) {
            DisplayRed(false);
        }

        AppConfig.isToPersonalCenter = false;
        AppConfig.isRefreshRed = false;

        MobclickAgent.onPageStart("MainActivity");
        MobclickAgent.onResume(this);
        Log.e("time_interval", "interval:" + (System.currentTimeMillis() - systemTime));//统计时长

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainActivity");
        MobclickAgent.onPause(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toastRequestErrorMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.operate_failure);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshTabs(EventRefreshTab eventRefreshTab) {
        boolean enableSync = sharedPreferences.getBoolean("enable_sync", false);
        Log.e("MainActivity", "enableSync:" + enableSync);
        if (enableSync) {
            tv_community.setVisibility(View.VISIBLE);
        } else {
            tv_community.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        instance = null;
        app.setMainActivityInstance(null);
        AppConfig.isUpdateCustomer = false;
        AppConfig.isUpdateDialogue = false;
        AppConfig.HASUPDATAINFO = false;
        stopService(service);
        EventBus.getDefault().unregister(this);
        app.getThreadMgr().shutDown();
        KillFile();
    }

    AddWxDocDialog addWxDocDialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fromWeChat(EventWxFilePath filePath) {
        Log.e("check_dialog", "show dialog");
        if (addWxDocDialog != null) {
            addWxDocDialog.dismiss();
            addWxDocDialog = null;
        }
        addWxDocDialog = new AddWxDocDialog(this, filePath.getPath());
        addWxDocDialog.setSavedListener(this);
        addWxDocDialog.show();

    }

//    public  String getTopActivity(Activity context) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//
//     List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
//        ComponentName componentName;
//        if (runningTaskInfos != null) {
//            componentName = runningTaskInfos.get(0).topActivity;
//            String result = componentName.getPackageName() + "." + componentName.getClassName();
//            return result;
//        } else {
//            return null;
//        }
//    }


//    boolean isShow = false;
//    WindowManager windowManager;
//    WindowManager.LayoutParams params ;
//    View contentView;
//    private void showWindow() {
//        if (windowManager == null) {
//            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//            params = new WindowManager.LayoutParams();
//            //窗口类型
//            if (Build.VERSION.SDK_INT > 25) {
//                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//            } else {
//                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//            }
//            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//            params.gravity = Gravity.BOTTOM;
//            // 设置图片格式，效果为背景透明
////            lp.format = PixelFormat.RGBA_8888;
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            contentView = LayoutInflater.from(this).inflate(R.layout.dialog_adddf, null);
//            //设置监听
//        }
//        if (!isShow) {
//            windowManager.addView(contentView, params);
//            isShow = true;
//        }
//    }


    private UploadFileDialog uploadFileDialog;

    private AddDocToSpaceDialog addDocToSpaceDialog;

    @Override
    public void onSpaceSelected(int spaceId) {
        AddDocumentTool.addDocumentToSpace(this, filePath, spaceId, new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog == null) {
                            uploadFileDialog = new UploadFileDialog(MainActivityV3.this);
                            uploadFileDialog.setTile("uploading");
                            uploadFileDialog.show();

                        } else {
                            if (!uploadFileDialog.isShowing()) {
                                uploadFileDialog.show();
                            }
                        }
                    }
                });
            }

            @Override
            public void uploadFile(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.setProgress(progress);
                        }
                    }
                });
            }

            @Override
            public void convertFile(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.setTile("Converting");
                            uploadFileDialog.setProgress(progress);
                        }
                    }
                });
            }

            @Override
            public void uploadFinished(Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        addDocSucc();
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage(getResources().getString(R.string.create_success)).create().show();
                        EventBus.getDefault().post(new TeamSpaceBean());
                    }
                });

            }

            @Override
            public void uploadError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "add favorite success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    String filePath;
    @Override
    public void onSaveSpace(String path) {
        this.filePath = path;
        if (addDocToSpaceDialog != null) {
            addDocToSpaceDialog.dismiss();
        }
        addDocToSpaceDialog = new AddDocToSpaceDialog(this);
        addDocToSpaceDialog.setOnSpaceSelectedListener(this);
        addDocToSpaceDialog.show();
    }


    @Override
    public void onSaveFavorite(String path) {
        AddDocumentTool.addDocumentToFavorite(this, path, new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        uploadFileDialog = new UploadFileDialog(MainActivityV3.this);
                        uploadFileDialog.setTile("uploading");
                        uploadFileDialog.show();
                    }
                });
            }

            @Override
            public void uploadFile(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.setProgress(progress);
                        }
                    }
                });

            }

            @Override
            public void convertFile(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.setTile("Converting");
                            uploadFileDialog.setProgress(progress);
                        }
                    }
                });

            }

            @Override
            public void uploadFinished(Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.cancel();
                            Toast.makeText(getApplicationContext(), "add favorite success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivityV3.this, PersonalCollectionActivity.class);
                            startActivity(intent);

                        }
                    }
                });
            }

            @Override
            public void uploadError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.cancel();
                        }
                        String errorMessage = message;
                        if (TextUtils.isEmpty(errorMessage)) {
                            errorMessage = getString(R.string.operate_failure);
                        }
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
