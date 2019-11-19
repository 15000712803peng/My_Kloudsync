package com.kloudsync.techexcel.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.Company;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.bean.LoginResult;
import com.kloudsync.techexcel.bean.RongCloudData;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.LoadingDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.ui.InvitationsActivity;
import com.kloudsync.techexcel.ui.MainActivity;

import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.kloudsync.techexcel.config.AppConfig.ClassRoomID;

public class LoginActivity extends Activity implements OnClickListener {

    private TextView tv_cphone, tv_login, tv_atjoin, tv_fpass;
    private EditText et_telephone, et_password;
    private FrameLayout fl_login;
    private TextView titleText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String telephone;
    private String password;
    public static LoginActivity instance = null;
    private boolean flag;
    ThreadManager threadManager;
    private RelativeLayout backLayout;
    private TextView rightTitleText;
    private View divider;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_v2);

        instance = this;
        threadManager = ((App) getApplication()).getThreadMgr();
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        loadingDialog = new LoadingDialog.Builder(this).build();
        tv_cphone = (TextView) findViewById(R.id.tv_cphone);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_atjoin = (TextView) findViewById(R.id.tv_atjoin);
        tv_fpass = (TextView) findViewById(R.id.txt_forget_pwd);
        et_telephone = (EditText) findViewById(R.id.et_telephone);
        et_password = (EditText) findViewById(R.id.et_password);
        fl_login = (FrameLayout) findViewById(R.id.fl_login);
        tv_login.setEnabled(false);
        setEditChangeInput();
        getSP();
        tv_login.setOnClickListener(this);
        tv_atjoin.setOnClickListener(this);
        tv_fpass.setOnClickListener(this);
        tv_cphone.setOnClickListener(this);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(R.string.Login);
        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        rightTitleText = findViewById(R.id.txt_right_title);
        rightTitleText.setVisibility(View.GONE);
        divider = findViewById(R.id.title_divider);
        divider.setVisibility(View.GONE);
    }

    private void editListener() {
        et_telephone.setOnKeyListener(Key_listener());
        et_password.setOnKeyListener(Key_listener());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginResult(LoginResult result) {
        loadingDialog.cancel();
        if (result != null) {
            if (result.isSuccessful()) {
                Log.e("LoginActivity","show login succ toast");
//                new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage("登录成功").create().show();
                goToMainActivity();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                },1000);

            } else {
                String message = result.getErrorMessage();
                if (TextUtils.isEmpty(message)) {
                    message = getResources().getString(R.string.operate_failure);
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

//                new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage("登录失败").create().show();

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @NonNull
    private View.OnKeyListener Key_listener() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String t1 = et_telephone.getText().toString();
                    String t2 = et_password.getText().toString();
                    Log.e("haha", "KEYCODE_ENTER" + flag);
                    if (!TextUtils.isEmpty(t1) && !TextUtils.isEmpty(t2)) {
                        if (flag) {
                            login();
                        }
                        flag = !flag;
                    }
                }
                return false;
            }
        };
    }

    private void getSP() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        telephone = sharedPreferences.getString("telephone", null);
        password = com.kloudsync.techexcel.start.LoginGet.DecodeBase64Password(sharedPreferences.getString("password", ""));
        AppConfig.COUNTRY_CODE = sharedPreferences.getInt("countrycode", 86);
        et_telephone.setText(telephone);
        et_password.setText(password);
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);

    }

    private void setEditChangeInput() {
        et_telephone.addTextChangedListener(new myTextWatch());
        et_password.addTextChangedListener(new myTextWatch());

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_login:
                login();
                // GoToMain();
                break;
            case R.id.tv_atjoin:
                GoToSign();
                break;
            case R.id.txt_forget_pwd:
                GoToForget();
                break;
            case R.id.tv_cphone:
                GotoChangeCode();
                break;
            case R.id.layout_back:
                finish();
                break;
            default:
                break;
        }

    }

    protected class myTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @SuppressLint("NewApi")
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (et_password.getText().length() > 0
                    && et_telephone.getText().length() > 0) {
                tv_login.setAlpha(1.0f);
                tv_login.setEnabled(true);
            } else {
                tv_login.setAlpha(0.6f);
                tv_login.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    }


    public void GotoChangeCode() {
        Intent intent = new Intent(getApplicationContext(), com.kloudsync.techexcel.start.ChangeCountryCode.class);
        String code = tv_cphone.getText().toString();
        code = code.replaceAll("\\+", "");
        AppConfig.COUNTRY_CODE = Integer.parseInt(code);
        startActivityForResult(intent, com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE);
        overridePendingTransition(R.anim.tran_in4, R.anim.tran_out4);

    }

    private void login() {
        telephone = et_telephone.getText().toString().trim();
        if (TextUtils.isEmpty(telephone)) {
            Toast.makeText(getApplicationContext(), "please input phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "please input password", Toast.LENGTH_SHORT).show();
            return;
        }
//        editor.putString("telephone", telephone);
//        editor.putString("password", com.kloudsync.techexcel.start.LoginGet.getBase64Password(password));
//        editor.putInt("countrycode", AppConfig.COUNTRY_CODE);
//        editor.commit();
        telephone = tv_cphone.getText().toString() + telephone;
//        com.kloudsync.techexcel.start.LoginGet.LoginRequest(LoginActivity.this, telephone, password, 1,
//                sharedPreferences, editor, threadManager);
        processLogin(telephone, password, et_telephone.getText().toString().trim());

    }

    Disposable loginDisposable;

    private void sendEventLoginFail(String errorMsg) {
        if (loginDisposable != null && !loginDisposable.isDisposed()) {
            loginDisposable.dispose();
        }

        LoginResult result = new LoginResult();
        result.setSuccessful(false);
        result.setErrorMessage(errorMsg);
        EventBus.getDefault().post(result);

    }

    private String rongCloudUrl = "";
    private String rongUserToken = "";
    private List<Company> companies = new ArrayList<>();

    private void processLogin(final String name, final String password, final String phoneNumber) {

        loadingDialog.show();
        loginDisposable = Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String o) throws Exception {
                try {
                    Response<NetworkResponse<LoginData>> response = ServiceInterfaceTools.getinstance().login(name, password).execute();
                    if (response == null || !response.isSuccessful() || response.body() == null) {
                        sendEventLoginFail("network error");
                    } else {
                        if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                            saveLoginData(response.body().getRetData());
                            rongCloudUrl = AppConfig.URL_PUBLIC + "RongCloudUserToken";
                        } else {
                            sendEventLoginFail(response.body().getErrorMessage());
                        }
                    }
                } catch (UnknownHostException e) {
                    sendEventLoginFail("network error");
                } catch (SocketTimeoutException exception) {
                    sendEventLoginFail("network error");
                }
                return rongCloudUrl;
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                if (loginDisposable == null || loginDisposable.isDisposed()) {
                    return rongUserToken;
                }
                if (!TextUtils.isEmpty(s)) {
                    try {
                        Response<NetworkResponse<RongCloudData>> response = ServiceInterfaceTools.getinstance().getRongCloudInfo().execute();
                        if (response != null && response.isSuccessful() && response.body() != null) {
                            if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                                RongCloudData data = response.body().getRetData();
                                AppConfig.RongUserToken = data.getUserToken();
                                AppConfig.RongUserID = data.getRongCloudUserID();
                                rongUserToken = AppConfig.RongUserToken;
                            }
                        }
                    } catch (UnknownHostException e) {

                    } catch (SocketTimeoutException exception) {

                    }
                }
                return rongUserToken;
            }
        }).map(new Function<String, List<Company>>() {
            @Override
            public List<Company> apply(String rongToken) throws Exception {
                if (loginDisposable == null || loginDisposable.isDisposed()) {
                    return companies;
                }
                if (TextUtils.isEmpty(rongToken)) {
                    //get rong data failed
                }
//                try {
//                    Response<InvitationsResponse> response = ServiceInterfaceTools.getinstance().getInvitations().execute();
//                    if (response != null && response.isSuccessful() && response.body() != null) {
//                        if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
//                            List<Company> companiesData = response.body().getRetData();
//                            if (companiesData != null && companiesData.size() > 0) {
//                                companies.clear();
//                                companies.addAll(companiesData);
//                            }
//                        }
//                    }
//                } catch (UnknownHostException e) {
//
//                } catch (SocketTimeoutException exception) {
//
//                }

                return companies;
            }
        }).doOnNext(new Consumer<List<Company>>() {
            @Override
            public void accept(List<Company> companies) throws Exception {
                if (loginDisposable == null || loginDisposable.isDisposed()) {
                    return;
                }
                editor.putString("telephone", phoneNumber);
                editor.putString("password", com.kloudsync.techexcel.start.LoginGet.getBase64Password(password));
                editor.putInt("countrycode", AppConfig.COUNTRY_CODE);
                editor.commit();
                if (companies != null && companies.size() > 0) {
                    goToInvitationsActivity(companies);
                } else {
                    LoginResult loginResult = new LoginResult();
                    loginResult.setSuccessful(true);
                    EventBus.getDefault().post(loginResult);
                }
            }
        }).subscribe();

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
        editor.commit();
    }

    public void GoToForget() {
        Intent intent = new Intent(LoginActivity.this,
                com.kloudsync.techexcel.start.ForgetPasswordActivity.class);
        startActivity(intent);
    }

    public void GoToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void GoToSign() {
        Intent intent = new Intent(LoginActivity.this, com.kloudsync.techexcel.start.RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE:
                tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
                break;
            default:
                break;
        }
    }

    public void onResume() {
        super.onResume();
//	    MobclickAgent.onPageStart("LoginActivity");
//	    MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("LoginActivity");
//	    MobclickAgent.onPause(this);
    }

    private void goToInvitationsActivity(List<Company> companies) {
        Intent intent = new Intent(this, InvitationsActivity.class);
        intent.putExtra("companies", new Gson().toJson(companies));
        intent.putExtra("from", 1);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
