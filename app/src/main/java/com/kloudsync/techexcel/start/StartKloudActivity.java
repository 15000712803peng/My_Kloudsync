package com.kloudsync.techexcel.start;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.bean.UserPreferenceData;
import com.kloudsync.techexcel.bean.params.EventCloseStartKloud;
import com.kloudsync.techexcel.bean.params.EventRegisterSuccess;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.ModifyMeetingIdDialog;
import com.kloudsync.techexcel.dialog.RegisterPromptDialog;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.ui.MainActivity;
import com.kloudsync.techexcel.ui.WelcomeAndCreateActivity;
import com.ub.techexcel.tools.JoinCompanyPopup;
import com.ub.techexcel.tools.JoinMeetingUnLoginPopup;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class StartKloudActivity extends Activity implements View.OnClickListener {

    private TextView logintv;
    private TextView registertv;
    private TextView joinmeeting;
    private RelativeLayout joincompanyTv;
    private EditText meetingidedit;
    private ImageView nextstep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startkloud);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    private void initView(){
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        logintv=findViewById(R.id.logintv);
        logintv.setOnClickListener(this);
        registertv=findViewById(R.id.registertv);
        registertv.setOnClickListener(this);
        joinmeeting=findViewById(R.id.joinmeeting);
        joinmeeting.setOnClickListener(this);
        joincompanyTv=findViewById(R.id.joincompanyTv);
        joincompanyTv.setOnClickListener(this);
        nextstep=findViewById(R.id.nextstep);
        nextstep.setOnClickListener(this);
        meetingidedit=findViewById(R.id.meetingidedit);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverCloseSuccess(EventCloseStartKloud eventCloseStartKloud){
        finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverRegisterSuccess(EventRegisterSuccess eventRegisterSuccess){
        if(isDirectJoinCompany){
            showJoinCompanyDialog();
        }else{
            goToWelcomeCreateCompany();
        }
    }

    JoinCompanyPopup joinCompanyPopup;

    private void showJoinCompanyDialog() {
        if (joinCompanyPopup == null) {
            joinCompanyPopup = new JoinCompanyPopup();
            joinCompanyPopup.getPopwindow(this);
            joinCompanyPopup.setJoinCompanyClickedListener(new JoinCompanyPopup.OnJoinCompanyClickedListener() {
                @Override
                public void joinCompanyClick(String code) {
                    requestJoinCompanyAndEnter(code);
                }
            });
        }
        joinCompanyPopup.show();
    }


    private void goToWelcomeCreateCompany() {
        Intent intent = new Intent(this, WelcomeAndCreateActivity.class);
//        intent.putExtra("companies", new Gson().toJson(companies));
//        intent.putExtra("from", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    private SharedPreferences sharedPreferences;

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
                                        Log.e("autoLogin", "name:" + name + ",pwd:" + pwd + ",telephone:" + telephone);
                                        processLogin(name, pwd, telephone);

                                    }
                                } else {

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

    public  class EventInviteCodeData {
        public int code = -1;
        public String message;
        public InviteCompanyData companyData;
    }

    public class InviteCompanyData {
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


    private boolean isDirectJoinCompany=false;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logintv:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.registertv:
                isDirectJoinCompany=false;
                Intent reintent = new Intent(getApplicationContext(), RegisterActivityStepOne.class);
                startActivity(reintent);
                break;
            case R.id.joinmeeting:
                Intent joinmeetingintent = new Intent(getApplicationContext(), JoinMeetingActivity.class);
                startActivity(joinmeetingintent);
                break;
            case R.id.joincompanyTv:
                isDirectJoinCompany=true;
                modifyKlassRoomID();
                break;
            case R.id.nextstep:
                String meetingId=meetingidedit.getText().toString();
                if(TextUtils.isEmpty(meetingId)){
                    Toast.makeText(StartKloudActivity.this,getString(R.string.input_startkloud_meeing_id),Toast.LENGTH_LONG).show();
                }else{
                    showJoinmeeting(meetingId);
                }
                break;
        }
    }


    private JoinMeetingUnLoginPopup joinMeetingUnLoginPopup;

    private void showJoinmeeting(String meetingId){
        if (joinMeetingUnLoginPopup != null) {
            if (joinMeetingUnLoginPopup.isShowing()) {
                joinMeetingUnLoginPopup.dismiss();
            }
            joinMeetingUnLoginPopup = null;
        }

        joinMeetingUnLoginPopup = new JoinMeetingUnLoginPopup();
        joinMeetingUnLoginPopup.getPopwindow(this);
        joinMeetingUnLoginPopup.show(meetingId);
    }

    private  RegisterPromptDialog registerPromptDialog;

    private void modifyKlassRoomID() {
        if (registerPromptDialog == null) {
            registerPromptDialog = new RegisterPromptDialog(this);
        }
        registerPromptDialog.show();
    }
}
