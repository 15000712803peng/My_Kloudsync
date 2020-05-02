package com.kloudsync.techexcel.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.params.EventRegisterSuccess;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.personal.AboutWebActivity;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class RegisterActivityStepOne extends Activity implements OnClickListener {
    private TextView tv_cphone, tv_sendcheckcode;
    private EditText phoneEdit, codeEdit;
    private EditText nameEdit, pwdEdit;
    private TextView registerText;
    private LinearLayout lin_bottom;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String telephone;
    private String AccessCode;

    private boolean flag_gp = false;
    private boolean isVisible;
    public static RegisterActivityStepOne instance = null;
    private LinearLayout loginLayout;
    public static final int CHANGE_COUNTRY_CODE = 0;
    private static final int PASSWORD_HIDE = 1;
    private static final int PASSWORD_NOT_HIDE = 2;
    private ImageView mPwdEyeImage;
    private ImageView mBack;
    private TextView mTitleBarTitle;
    private TextView mTvPhoneTips;
    private TextView mTvNameTips;
    private RelativeLayout mRlyRegisterPwd;
    private RelativeLayout mRlyRegisterPhone;
    private TextView tv_sign_up_stepone;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String result;
            switch (msg.what) {
                case AppConfig.GETCHECKCODE:
                /*String code = (String) msg.obj;
                                et_checkcode.setText(code);*/
                    new ApiTask(new CheckCodeEnable()).start(ThreadManager.getManager());
                    break;
                case AppConfig.CHECKCODE:
                    int time = (Integer) msg.obj;
                    ChangeSendEnable(time);
                    break;
                case AppConfig.ACCESSCODE:
                    AccessCode = (String) msg.obj;
                    registerRequest(AccessCode);
//				ChangePassword();
                                /*if(!flag_gp){
                                        GoToPI();
				}*/
//                    GoToPI();
                    break;
                case AppConfig.HasExisted:
                    tv_sendcheckcode.setEnabled(true);
                    phoneEdit.setEnabled(true);
                    mRlyRegisterPhone.setSelected(true);
                    mTvPhoneTips.setText(getResources().getString(R.string.HasExisted));
                    mTvPhoneTips.setVisibility(View.VISIBLE);
                    mTvPhoneTips.setSelected(true);
                    registerText.setEnabled(false);

                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            RegisterActivityStepOne.this,
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            RegisterActivityStepOne.this,
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();

                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    if (result.contains("验证码")) {
                        String msgTitle =getString(R.string.registration_failed);
                        ToastUtils.showInCenter(RegisterActivityStepOne.this, msgTitle, result);

                    } else {
                        Toast.makeText(getApplicationContext(), result,
                                Toast.LENGTH_LONG).show();
                    }
                    tv_sendcheckcode.setEnabled(true);
                    phoneEdit.setEnabled(true);
                    break;
                case AppConfig.SUCCESSCHANGE:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_reset),
                            Toast.LENGTH_LONG).show();
                    finish();

                    break;

                case AppConfig.REGISTER_SUCC:
                    phoneEdit.setEnabled(true);
                    registerSucc((JSONObject) msg.obj);
                    break;
                case AppConfig.REGISTER_FAIL:
                    result = (String) msg.obj;
                    phoneEdit.setEnabled(true);
                    if (result.contains("用户名")) {
                        nameEdit.setSelected(true);
                        mTvNameTips.setText(result);
                        mTvNameTips.setVisibility(View.VISIBLE);
                        mTvNameTips.setSelected(true);
                        registerText.setEnabled(false);
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed) + result, Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_v2_step1);
        instance = this;
        isVisible = getIntent().getBooleanExtra("isVisible", true);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverRegisterSuccess(EventRegisterSuccess eventRegisterSuccess){
         finish();
    }

    private void initView() {
        mBack = findViewById(R.id.iv_register_back);
        mTitleBarTitle = findViewById(R.id.tv_register_title);
        tv_cphone = (TextView) findViewById(R.id.tv_cphone);
        tv_sendcheckcode = (TextView) findViewById(R.id.tv_sendcheckcode);
        mRlyRegisterPhone = findViewById(R.id.rly_register_phone);
        mRlyRegisterPwd = findViewById(R.id.rly_register_pwd);
        phoneEdit = (EditText) findViewById(R.id.et_telephone);
        mTvPhoneTips = findViewById(R.id.tv_register_phone_tips);
        mTvNameTips = findViewById(R.id.tv_register_name_tips);
        nameEdit = findViewById(R.id.et_name);
        pwdEdit = findViewById(R.id.et_password);
        pwdEdit.setTag(PASSWORD_HIDE);
        mPwdEyeImage = findViewById(R.id.iv_show_pwd);
        registerText = findViewById(R.id.tv_sign_up);
        registerText.setEnabled(false);
        tv_sign_up_stepone = findViewById(R.id.tv_sign_up_stepone);
        tv_sign_up_stepone.setEnabled(false);
        codeEdit = (EditText) findViewById(R.id.et_checkcode);
        AppConfig.COUNTRY_CODE = 86;
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
        mTitleBarTitle.setText(getResources().getString(R.string.create_account));
        mTitleBarTitle.setTextColor(getResources().getColor(R.color.colorFont34));
        setEditChangeInput();
        mBack.setOnClickListener(this);
        mPwdEyeImage.setOnClickListener(this);
        tv_sendcheckcode.setOnClickListener(this);
        tv_cphone.setOnClickListener(this);
        loginLayout = findViewById(R.id.ly_register_terms);
        loginLayout.setOnClickListener(this);
        registerText.setOnClickListener(this);
        tv_sign_up_stepone.setOnClickListener(this);

    }


    protected void GoToPI() {
        flag_gp = true;
        Intent intent = new Intent(RegisterActivityStepOne.this, PerfectData2Activity.class);
        intent.putExtra("countrycode", tv_cphone.getText().toString().replaceAll("\\+", ""));
        intent.putExtra("telephone", phoneEdit.getText().toString());
//		intent.putExtra("password", et_password.getText().toString());
        intent.putExtra("AccessCode", AccessCode);
        startActivity(intent);

    }

    private void setEditChangeInput() {
        phoneEdit.addTextChangedListener(new myTextWatch());
        codeEdit.addTextChangedListener(new myTextWatch());
        nameEdit.addTextChangedListener(new myTextWatch());
        pwdEdit.addTextChangedListener(new PwdTextWatch());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_register_back:
                finish();
                break;
            case R.id.ly_register_terms:
                String enUrl = "http://kloud.cn/term.html";
                String zhUrl = "http://kloud.cn/term-cn.html";
                String tag = getString(R.string.user_license_agreement);
                Intent intent = new Intent(getApplicationContext(), AboutWebActivity.class);
                intent.putExtra(AboutWebActivity.TAG,tag);
                intent.putExtra(AboutWebActivity.ENURL,enUrl);
                intent.putExtra(AboutWebActivity.ZHURL,zhUrl);
                startActivity(intent);
                break;
            case R.id.tv_sendcheckcode:
                GetCheckCode();
                break;
            case R.id.img_back:
                finish();
                break;

            case R.id.tv_cphone:
                GotoChangeCode();
                break;
            case R.id.iv_show_pwd:
                toggleHidePwd();
                break;
            case R.id.tv_sign_up:
                register();
                break;
            case R.id.tv_sign_up_stepone:
                intent=new Intent(this,RegisterActivity.class);
                intent.putExtra("registerVertifyCode",codeEdit
                        .getText().toString());
                intent.putExtra("registerPhone",phoneEdit.getText()
                        .toString());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 密码是否可见
     */
    private void toggleHidePwd() {
        if (pwdEdit.getTag() != null) {
            Integer type = (Integer) pwdEdit.getTag();
            togglePwdByType(type);
        }
    }

    private void togglePwdByType(int type) {
        if (type == PASSWORD_HIDE) {
            pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT);
            mPwdEyeImage.setSelected(true);
            pwdEdit.setTag(PASSWORD_NOT_HIDE);
        } else if (type == PASSWORD_NOT_HIDE) {
            pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPwdEyeImage.setSelected(false);
            pwdEdit.setTag(PASSWORD_HIDE);
        }
    }

    private void register() {
        getAccessCode();
    }

    protected class PwdTextWatch implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String pwd = pwdEdit.getText().toString().trim();
            int pwdLength = pwdEdit.getText().length();
            //含有数字
            final String NUMBER = ".*[0-9].*";
            //含有大小写字母
            final String CASE = ".*[a-zA-Z].*";
            //特殊符号
            final String REGSYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"].*";
            int hasNumber = pwd.matches(NUMBER) ? 1 : 0;
            int hasCase = pwd.matches(CASE) ? 1 : 0;
            int hasSymbol = pwd.matches(REGSYMBOL) ? 1 : 0;
            if (pwdLength > 7 && pwdLength < 15 && hasNumber + hasCase + hasSymbol >= 2) {
                mRlyRegisterPwd.setSelected(false);
                registerText.setAlpha(1.0f);
                registerText.setEnabled(true);
            } else {
                mRlyRegisterPwd.setSelected(true);
                registerText.setAlpha(0.6f);
                registerText.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

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
            mRlyRegisterPhone.setSelected(false);
            mTvPhoneTips.setVisibility(View.INVISIBLE);
            nameEdit.setSelected(false);
            mTvNameTips.setVisibility(View.INVISIBLE);
            int phoneLength = phoneEdit.getText().length();
            int codeLength = codeEdit.getText().length();
            int nameLength = nameEdit.getText().length();

            if (phoneLength > 0 && codeLength > 0 ) {
                registerText.setAlpha(1.0f);
                registerText.setEnabled(true);
                tv_sign_up_stepone.setAlpha(1.0f);
                tv_sign_up_stepone.setEnabled(true);
            } else {
                registerText.setAlpha(0.6f);
                registerText.setEnabled(false);
                tv_sign_up_stepone.setAlpha(0.6f);
                tv_sign_up_stepone.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    }


    public void GotoChangeCode() {
        Intent intent = new Intent(getApplicationContext(), ChangeCountryCode.class);
        String code = tv_cphone.getText().toString();
        code = code.replaceAll("\\+", "");
        AppConfig.COUNTRY_CODE = Integer.parseInt(code);
        startActivityForResult(intent, CHANGE_COUNTRY_CODE);
        overridePendingTransition(R.anim.tran_in4, R.anim.tran_out4);

    }





    /**
     * 判断是否可发送验证码
     *
     * @param time
     */
    private void ChangeSendEnable(int time) {
        if (time < 0) {
            tv_sendcheckcode.setEnabled(true);
            tv_sendcheckcode.setText(getResources().getString(R.string.Resend_CheckCode));
            tv_sendcheckcode.setTextColor(getResources().getColor(R.color.skyblue));
        } else {
            tv_sendcheckcode.setEnabled(false);
            tv_sendcheckcode.setText(time + "s");
            tv_sendcheckcode.setTextColor(getResources().getColor(R.color.newgrey));
        }
    }
    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AccessCode", AccessCode);
            jsonObject.put("Role", "1");
            jsonObject.put("Mobile", tv_cphone.getText().toString()
                    + phoneEdit.getText().toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void getAccessCode() {

        final String checkcode = LoginGet.getBase64Password(codeEdit
                .getText().toString());
        phoneEdit.setEnabled(false);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService
                            .submitDataByJsonNoToken(
                                    AppConfig.URL_PUBLIC
                                            + "User/Verify4Register?mobile="
                                            + URLEncoder.encode(tv_cphone
                                                    .getText().toString()
                                                    + phoneEdit.getText()
                                                    .toString(),
                                            "UTF-8")
                                            + "&checkcode="
                                            + URLEncoder.encode(checkcode,
                                            "UTF-8") + "&role=1", null);
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        JSONObject RetData = responsedata.getJSONObject("RetData");
                        String AccessCode = RetData.getString("AccessCode");
                        msg.what = AppConfig.ACCESSCODE;
                        msg.obj = AccessCode;
                    } else {

                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private void GetCheckCode() {
        telephone = phoneEdit.getText().toString();
        if (telephone.length() < 1) {
            Toast.makeText(getApplicationContext(), "手机不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        telephone = tv_cphone.getText().toString() + telephone;
        tv_sendcheckcode.setEnabled(false);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService
                            .submitDataByJsonNoToken(
                                    AppConfig.URL_PUBLIC
                                            + "CheckCode/Send?mobile="
                                            + URLEncoder.encode(telephone,
                                            "UTF-8")
                                            + "&type=1"
                                            + "&productID=1", null);
                    Log.e("CheckCode", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    String RetData = responsedata.getString("RetData");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.GETCHECKCODE;
//						msg.obj = RetData;
                    } else if (retcode.equals(AppConfig.UserHasExisted)) {
                        msg.what = AppConfig.HasExisted;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    tv_sendcheckcode.setEnabled(true);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    public class CheckCodeEnable implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            int time = 59;
            while (time >= -1) {
                try {
                    Message message = new Message();
                    message.what = AppConfig.CHECKCODE;
                    message.obj = time;
                    handler.sendMessage(message);
                    Thread.sleep(1000);
                    time--;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHANGE_COUNTRY_CODE:
                tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
                break;
            default:
                break;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RegisterActivity");
        MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RegisterActivity");
        MobclickAgent.onPause(this);
    }

    private JSONObject checkInput(String codeAssess) {
        String phone = phoneEdit.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return null;
        }
        String code = codeEdit.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return null;
        }
        String name = nameEdit.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入名字", Toast.LENGTH_SHORT).show();
            return null;
        }
        String pwd = pwdEdit.getText().toString().trim();
        if (TextUtils.isEmpty(pwd) || pwd.length() < 8 || pwd.length() > 14) {
            Toast.makeText(this, "请输入8-14位的密码", Toast.LENGTH_SHORT).show();
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Mobile", "+" + tv_cphone.getText().toString().replaceAll("\\+", "") + phone);
            jsonObject.put("Password", LoginGet.getBase64Password(pwd).trim());
            jsonObject.put("VerificationCode", codeAssess);
            jsonObject.put("Name", name.trim());
            jsonObject.put("LoginName", name.trim());
            jsonObject.put("Role", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void registerRequest(String code) {
        final JSONObject jsonobject = checkInput(code);
        if (jsonobject == null) {
            return;
        }
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService
                            .submitDataByJsonNoToken(AppConfig.URL_PUBLIC
                                    + "User/Register4Web", jsonobject);
                    String retcode = responsedata.getString("RetCode");
                    Log.e("User/Register4Web", "parmas：" + jsonobject + ",responsedata:" + responsedata);
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.REGISTER_SUCC;
                        msg.obj = jsonobject;
                    } else {
                        if (retcode.equals(AppConfig.UserHasExisted)) {
                            msg.what = AppConfig.HasExisted;
                        } else {
                            msg.what = AppConfig.REGISTER_FAIL;
                            String ErrorMessage = responsedata
                                    .getString("ErrorMessage");
                            msg.obj = ErrorMessage;
                        }

                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private void registerSucc(JSONObject jsonobject) {
        Toast.makeText(this, getResources().getString(R.string.Register_Success),
                Toast.LENGTH_SHORT).show();
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        try {
            int countrycode = Integer.parseInt(tv_cphone.getText().toString().replaceAll("\\+", ""));
            AppConfig.COUNTRY_CODE = countrycode;
            String phone = jsonobject.getString("Mobile");
            String pwd = jsonobject.getString("Password");
            editor.putInt("countrycode", countrycode);
            editor.putString("telephone", phoneEdit.getText().toString().trim());
            editor.putString("password", pwd);
            editor.commit();
            Intent resultData = new Intent();
            resultData.putExtra("password", pwdEdit.getText().toString().trim());
            setResult(RESULT_OK, resultData);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
