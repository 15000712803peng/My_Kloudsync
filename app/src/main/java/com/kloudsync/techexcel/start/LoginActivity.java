package com.kloudsync.techexcel.start;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.Company;
import com.kloudsync.techexcel.bean.InviteCompany;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.bean.LoginResult;
import com.kloudsync.techexcel.bean.RongCloudData;
import com.kloudsync.techexcel.bean.SimpleCompanyData;
import com.kloudsync.techexcel.bean.UserPreferenceData;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.LoadingDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.InvitationsResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.tool.StringUtils;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.kloudsync.techexcel.ui.InvitationsActivity;
import com.kloudsync.techexcel.ui.MainActivity;

import com.kloudsync.techexcel.ui.WelcomeAndCreateActivity;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.kloudsync.techexcel.config.AppConfig.ClassRoomID;
import static com.kloudsync.techexcel.config.AppConfig.conversationId;

public class LoginActivity extends Activity implements OnClickListener {

    private TextView tv_cphone, tv_login, tv_atjoin, tv_fpass;
    private EditText et_telephone, et_password;

    private TextView titleText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String telephone;
    private String password;
    private ImageView pwdEyeImage;
    public static LoginActivity instance = null;
    private boolean flag;
    ThreadManager threadManager;
    private RelativeLayout backLayout;
    private TextView rightTitleText;
    private View divider;
    LoadingDialog loadingDialog;
    Gson gson;
    Intent service;
    private static final int REQUEST_RETISTER = 1;
    private static final int PASSWORD_HIDE = 1;
    private static final int PASSWORD_NOT_HIDE = 2;


    private void startWBService() {
        service = new Intent(getApplicationContext(), SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        } else {
            startService(service);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_v3);
        instance = this;
        gson = new Gson();
        threadManager = ((App) getApplication()).getThreadMgr();
//        checkPermission();
        initView();
        startWBService();
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
        et_password.setTag(PASSWORD_HIDE);
        pwdEyeImage = findViewById(R.id.image_pwd_eye);
        pwdEyeImage.setOnClickListener(this);
        setEditChangeInput();
        getSP();
        tv_login.setOnClickListener(this);
        tv_atjoin.setOnClickListener(this);
        tv_fpass.setOnClickListener(this);
        tv_cphone.setOnClickListener(this);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(R.string.Login);
        backLayout = findViewById(R.id.layout_back);
        backLayout.setVisibility(View.GONE);
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
                Log.e("LoginActivity", "show login succ toast");
//                new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage("登录成功").create().show();
                goToMainActivity();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 1000);

            } else {
                String message = result.getErrorMessage();
                if (TextUtils.isEmpty(message)) {
                    message = getResources().getString(R.string.operate_failure);
                }
                String msgTitle = getString(R.string.login_failed);
                ToastUtils.showInCenter(LoginActivity.this, msgTitle, message);
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
        password = LoginGet.DecodeBase64Password(sharedPreferences.getString("password", ""));
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
            case R.id.image_pwd_eye:
                toggleHidePwd();
                break;
            default:
                break;
        }
    }

    private void toggleHidePwd() {
        if (et_password.getTag() != null) {
            Integer type = (Integer) et_password.getTag();
            togglePwdByType(type);
        }
    }

    private void togglePwdByType(int type) {
        if (type == PASSWORD_HIDE) {
            et_password.setInputType(InputType.TYPE_CLASS_TEXT);
            pwdEyeImage.setImageResource(R.drawable.pwd_eye_close);
            et_password.setTag(PASSWORD_NOT_HIDE);
        } else if (type == PASSWORD_NOT_HIDE) {
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pwdEyeImage.setImageResource(R.drawable.pwd_eye_open);
            et_password.setTag(PASSWORD_HIDE);
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
            Toast.makeText(getApplicationContext(), getString(R.string.please_input_phone_number), Toast.LENGTH_SHORT).show();
            return;
        }

        password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.please_input_password), Toast.LENGTH_SHORT).show();
            return;
        }
//        editor.putString("telephone", telephone);
//        editor.putString("password", com.kloudsync.techexcel.start.LoginGet.getBase64Password(password));
//        editor.putInt("countrycode", AppConfig.COUNTRY_CODE);
//        editor.commit();
//        telephone = tv_cphone.getText().toString() + telephone;
        if (StringUtils.isPhoneNumber(telephone)) {
            telephone = "+86" + telephone;
        }
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
    private List<SimpleCompanyData> companies = new ArrayList<>();

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
                            editor.putString("name", name);
                            editor.putString("telephone", phoneNumber);
                            editor.putString("password", LoginGet.getBase64Password(password));
                            editor.putInt("countrycode", AppConfig.COUNTRY_CODE);
                            editor.commit();
                        } else {
                            sendEventLoginFail(response.body().getErrorMessage());
                            if (loginDisposable != null && !loginDisposable.isDisposed()) {
                                loginDisposable.dispose();
                            }
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
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (loginDisposable == null || loginDisposable.isDisposed()) {
                    return;
                }
                JSONObject response = ServiceInterfaceTools.getinstance().syncGetUserPreference();

                if (response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        UserPreferenceData userPreferenceData = gson.fromJson(response.toString(), UserPreferenceData.class);
                        if (userPreferenceData.getRetData() == null) {
                            handleNoCompany();
                        } else {
                            JSONObject retData = response.getJSONObject("RetData");
                            LoginResult loginResult = new LoginResult();
                            loginResult.setSuccessful(true);
                            EventBus.getDefault().post(loginResult);
                        }

                    } else {
                        sendEventLoginFail("网络异常");
                    }
                }
            }
        }).subscribe();

    }


    class SimpleCompanyResponse {

        private int RetCode;
        private String ErrorMessage;
        private String DetailMessage;
        private List<SimpleCompanyData> RetData;

        public int getRetCode() {
            return RetCode;
        }

        public void setRetCode(int retCode) {
            RetCode = retCode;
        }

        public String getErrorMessage() {
            return ErrorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            ErrorMessage = errorMessage;
        }

        public String getDetailMessage() {
            return DetailMessage;
        }

        public void setDetailMessage(String detailMessage) {
            DetailMessage = detailMessage;
        }

        public List<SimpleCompanyData> getRetData() {
            return RetData;
        }

        public void setRetData(List<SimpleCompanyData> retData) {
            RetData = retData;
        }
    }

    private void handleNoCompany() {
        companies.clear();
        Observable.just("no_company").observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (loadingDialog != null) {
                    loadingDialog.cancel();
                }
            }
        }).observeOn(Schedulers.io()).map(new Function<String, List<SimpleCompanyData>>() {
            @Override
            public List<SimpleCompanyData> apply(String rongToken) throws Exception {
                try {
                    JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncGetCompanies(AppConfig.UserID);
                    if (jsonObject.has("RetCode")) {
                        if (jsonObject.getInt("RetCode") == 0) {
                            SimpleCompanyResponse response = gson.fromJson(jsonObject.toString(), SimpleCompanyResponse.class);
                            if (response.getRetData() != null && response.getRetData().size() > 0) {
                                companies.addAll(response.getRetData());
                            }
                        }
                    }

                } catch (Exception exception) {

                }
                return companies;

            }
        }).doOnNext(new Consumer<List<SimpleCompanyData>>() {
            @Override
            public void accept(List<SimpleCompanyData> companies) throws Exception {

                if (companies != null && companies.size() > 0) {
                    goToInvitationsActivity(companies);
                } else {
                    goToWelcomeCreateCompany();
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
        startActivityForResult(intent, REQUEST_RETISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE:
                    tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
                    editor.putInt("countrycode", AppConfig.COUNTRY_CODE).commit();
                    break;
                case REQUEST_RETISTER:
                    int contryCode = sharedPreferences.getInt("countrycode", 86);
                    AppConfig.COUNTRY_CODE = contryCode;
                    String phoneNumber = sharedPreferences.getString("telephone", "");
                    String password = data.getStringExtra("password");
                    Log.e("check_register_succ", "country_code:" + contryCode + ",phone_number:" + phoneNumber + ",pwd:" + password);
                    tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
                    et_telephone.setText(phoneNumber);
                    et_password.setText(password);
                    login();
//                     processLogin();
                    break;
                default:
                    break;
            }
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

    private void goToInvitationsActivity(List<SimpleCompanyData> companies) {
        Intent intent = new Intent(this, InvitationsActivity.class);
        intent.putExtra("companies", new Gson().toJson(companies));
        intent.putExtra("from", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void goToWelcomeCreateCompany() {
        Intent intent = new Intent(this, WelcomeAndCreateActivity.class);
        intent.putExtra("companies", new Gson().toJson(companies));
        intent.putExtra("from", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        finish();
    }

    private void checkPermission() {
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

}
