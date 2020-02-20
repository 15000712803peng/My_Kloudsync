package com.kloudsync.techexcel.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.EventJoinMeeting;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.bean.UserPreferenceData;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.KloudPerssionManger;
import com.kloudsync.techexcel.personal.CreateOrganizationActivityV2;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.tools.JoinMeetingPopup;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_FOR_JOIN_MEETING;

/**
 * Created by tonyan on 2020/1/19.
 */

public class WelcomeAndCreateActivity extends BaseActivity implements View.OnClickListener {
    private TextView createText;
    private TextView joinMeetingText;
    private TextView backText;
    private EditText inviteCodeEdit;
    private ImageView goToInviteImage;
    private SharedPreferences sharedPreferences;

    @Override
    protected int setLayout() {
        return R.layout.activity_welcome_to_create;
    }

    @Override
    protected void initView() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        startWBService();
        EventBus.getDefault().register(this);
        createText = findViewById(R.id.txt_create);
        createText.setOnClickListener(this);
        joinMeetingText = findViewById(R.id.txt_join_meeting);
        joinMeetingText.setOnClickListener(this);
        backText = findViewById(R.id.txt_back);
        backText.setOnClickListener(this);
        inviteCodeEdit = findViewById(R.id.edit_join_by_invite_code);
        goToInviteImage = findViewById(R.id.image_go_to_invite);
        goToInviteImage.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_create:
                goToCreate();
                break;
            case R.id.txt_join_meeting:
                joinMeetingBeforeCheckPession();
                break;
            case R.id.txt_back:
                finish();
                break;
            case R.id.image_go_to_invite:
                String code = inviteCodeEdit.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请输入邀请码", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestJoinCompanyAndEnter(code);
                break;
            default:
                break;
        }
    }

    private void goToCreate() {
        Intent intent = new Intent(this, CreateOrganizationActivityV2.class);
        intent.putExtra("from_app_setting", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void requestJoinCompanyAndEnter(final String code) {
        Observable.just("Request").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                return ServiceInterfaceTools.getinstance().syncJoinCompanyWithInviteCode(code);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                final EventInviteCodeData eventInviteCodeData = new EventInviteCodeData();
                if (jsonObject.has("code")) {
                    int code = jsonObject.getInt("code");
                    eventInviteCodeData.code = code;
                    if (jsonObject.getInt("code") == 0) {
                        // 邀请码正确
                        InviteCompanyData companyData = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), InviteCompanyData.class);
                        eventInviteCodeData.companyData = companyData;
//                        Observable.just("enter").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
//
//                        })}.
                        Observable.just("enter").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
                            @Override
                            public JSONObject apply(String s) throws Exception {
                                JSONObject result = new JSONObject();
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    JSONObject textObject = new JSONObject();

                                    jsonObject.put("FieldID", 10001);
                                    textObject.put("SchoolID", eventInviteCodeData.companyData.getCompanyId());
                                    textObject.put("SchoolName", eventInviteCodeData.companyData.getCompanyName());
                                    textObject.put("SubSystemData", "");
                                    jsonObject.put("PreferenceText", textObject + "");
                                    jsonObject.put("PreferenceMemo", "");
                                    result = ServiceInterfaceTools.getinstance().syncAddOrUpdateUserPreference(jsonObject);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                return result;
                            }
                        }).doOnNext(new Consumer<JSONObject>() {
                            @Override
                            public void accept(JSONObject result) throws Exception {
                                if (result.has("RetCode")) {
                                    if (result.getString("RetCode").equals("0")) {
                                        // autologin
                                        String name = sharedPreferences.getString("name", null);
                                        String pwd = LoginGet.DecodeBase64Password(sharedPreferences.getString("password", null));
                                        String telephone = sharedPreferences.getString("telephone", null);
                                        Log.e("autoLogin","name:" + name + ",pwd:" + pwd + ",telephone:" + telephone);
                                        processLogin(name,pwd,telephone);

                                    }
                                }else {

                                }

                            }
                        }).subscribe();

                    } else {
                        if (jsonObject.has("msg")) {
                            eventInviteCodeData.message = jsonObject.getString("msg");
                        }
                    }
                } else {
                    eventInviteCodeData.code = -1;
                    eventInviteCodeData.message = "网络异常，加入失败";
                }

                if (eventInviteCodeData.code != 0) {
                    // 失败，
                    String message = eventInviteCodeData.message;
                    if (TextUtils.isEmpty(message)) {
                        eventInviteCodeData.message = "网络异常，加入失败";
                    }
                    Toast.makeText(getApplicationContext(), eventInviteCodeData.message, Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        }).subscribe();
    }

    JoinMeetingPopup joinMeetingPopup;

    private void showJoinDialog() {
        if (joinMeetingPopup == null) {
            joinMeetingPopup = new JoinMeetingPopup();
            joinMeetingPopup.getPopwindow(this);
            joinMeetingPopup.setFavoritePoPListener(new JoinMeetingPopup.FavoritePoPListener() {
                @Override
                public void dismiss() {

                }

                @Override
                public void open() {

                }
            });
        }
        joinMeetingPopup.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void joinMeeting(EventJoinMeeting eventJoinMeeting) {
        if (eventJoinMeeting.getLessionId() <= 0) {
            Toast.makeText(this, "加入的meeting不存在或没有开始", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //-----
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", eventJoinMeeting.getLessionId());
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        startActivity(intent);
    }

    private void startWBService() {
        Intent service = new Intent(getApplicationContext(), SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        } else {
            startService(service);
        }
    }

    class EventInviteCodeData {
        public int code = -1;
        public String message;
        public InviteCompanyData companyData;
    }

    class InviteCompanyData {
        private long companyId;
        private String companyName;
        private long ownerId;
        private int isActive;
        private String createDate;
        private String webAddress;
        private String verifyEmailAddress;

        public long getCompanyId() {
            return companyId;
        }

        public void setCompanyId(long companyId) {
            this.companyId = companyId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public long getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(long ownerId) {
            this.ownerId = ownerId;
        }

        public int getIsActive() {
            return isActive;
        }

        public void setIsActive(int isActive) {
            this.isActive = isActive;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getWebAddress() {
            return webAddress;
        }

        public void setWebAddress(String webAddress) {
            this.webAddress = webAddress;
        }

        public String getVerifyEmailAddress() {
            return verifyEmailAddress;
        }

        public void setVerifyEmailAddress(String verifyEmailAddress) {
            this.verifyEmailAddress = verifyEmailAddress;
        }
    }


    Disposable loginDisposable;

    private void processLogin(final String name, final String password, final String phoneNumber) {

        loginDisposable = Observable.just("login").observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String str) throws Exception {
                try {
                    Response<NetworkResponse<LoginData>> response = ServiceInterfaceTools.getinstance().login(name, password).execute();
                    if (response == null || !response.isSuccessful() || response.body() == null) {

                    } else {
                        if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {


                        } else {
                        }
                    }
                } catch (UnknownHostException e) {

                } catch (SocketTimeoutException exception) {

                }
                return str;
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                JSONObject response = ServiceInterfaceTools.getinstance().syncGetUserPreference();
                if (response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        UserPreferenceData userPreferenceData = new Gson().fromJson(response.toString(), UserPreferenceData.class);
                        if (userPreferenceData.getRetData() == null) {

                        } else {
                            Observable.just("login").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    goToMainActivity();
                                }
                            });
                        }

                    } else {

                    }
                }
            }
        }).subscribe();

    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void joinMeetingBeforeCheckPession() {
        if (KloudPerssionManger.isPermissionCameraGranted(this) && KloudPerssionManger.isPermissionExternalStorageGranted(this)) {
            showJoinDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_FOR_JOIN_MEETING);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_FOR_JOIN_MEETING) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Log.e("check_permission", "phone_camera_granted");
                showJoinDialog();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED) {
                Log.e("check_permission", "phone_Rcamera_denied");
                Toast.makeText(this, "加入会议前请先同意必要的权限", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
