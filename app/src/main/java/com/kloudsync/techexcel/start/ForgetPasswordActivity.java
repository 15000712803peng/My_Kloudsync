package com.kloudsync.techexcel.start;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.ActivityWrapper;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ForgetPasswordActivity extends ActivityWrapper implements OnClickListener {

    private static final int PASSWORD_HIDE = 1;
    private static final int PASSWORD_NOT_HIDE = 2;
    private TextView tv_cphone, tv_sendcheckcode, mTitle;
    private Button mBtnReset, mBtnNext;
    private EditText et_telephone, et_password, et_checkcode, mEtEnterPwd;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String telephone;
    private String password;
    private String AccessCode;
    RelativeLayout backLayout;
    private View divider;
    //    private TextView titleText;
    private TextView rightTitleText;
    private LinearLayout mLlyEnterPwd, mLlyForgetPwdCode;
    private ImageView mIvNewClearPwd, mIvEnterClearPwd, mIvClearCode;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

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
                    mTitle.setText(R.string.reset_login_password);
                    mLlyForgetPwdCode.setVisibility(View.GONE);
                    mBtnNext.setVisibility(View.GONE);
                    mLlyEnterPwd.setVisibility(View.VISIBLE);
                    mBtnReset.setVisibility(View.VISIBLE);
                    break;
                case AppConfig.NotExist:
                    Toast.makeText(
                            ForgetPasswordActivity.this,
                            getResources().getString(R.string.UserNotExist),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            ForgetPasswordActivity.this,
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_LONG).show();

                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            ForgetPasswordActivity.this,
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_LONG).show();

                    break;
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    tv_sendcheckcode.setEnabled(true);
                    break;
                case AppConfig.SUCCESSCHANGE:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.CP_Success),
                            Toast.LENGTH_LONG).show();
                    if (MainActivity.instance != null) {
                        MainActivity.instance.finish();
                    }
                    if (LoginActivity.instance != null) {
                        LoginActivity.instance.finish();
                    }
                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                    finish();

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
        setContentView(R.layout.activity_forgetpassword);
        initView();
    }


    private void initView() {
        mTitle = findViewById(R.id.tv_forgetpassword_title);
        mLlyForgetPwdCode = findViewById(R.id.lly_forgetpassword_code);
        mLlyEnterPwd = findViewById(R.id.lly_forgetpassword_enter_pwd);
        mEtEnterPwd = findViewById(R.id.et_enter_password);
        mBtnNext = findViewById(R.id.btn_forgetpwd_next);
        mIvNewClearPwd = findViewById(R.id.iv_new_clear_pwd);
        mIvEnterClearPwd = findViewById(R.id.iv_enter_clear_pwd);
        mIvClearCode = findViewById(R.id.iv_clear_code);
        tv_cphone = (TextView) findViewById(R.id.tv_cphone);
        mBtnReset = findViewById(R.id.btn_reset);
        tv_sendcheckcode = (TextView) findViewById(R.id.tv_sendcheckcode);
        et_telephone = (EditText) findViewById(R.id.et_telephone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_checkcode = (EditText) findViewById(R.id.et_checkcode);

        AppConfig.COUNTRY_CODE = 86;
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
        mBtnReset.setEnabled(false);
        setEditChangeInput();
        mBtnReset.setOnClickListener(this);
        tv_sendcheckcode.setOnClickListener(this);
        tv_cphone.setOnClickListener(this);
        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mIvNewClearPwd.setOnClickListener(this);
        mIvEnterClearPwd.setOnClickListener(this);
        mIvClearCode.setOnClickListener(this);
//        titleText = findViewById(R.id.tv_title);
        divider = findViewById(R.id.title_divider);
        divider.setVisibility(View.GONE);
        rightTitleText = findViewById(R.id.txt_right_title);
        rightTitleText.setVisibility(View.INVISIBLE);
//        titleText.setText(R.string.reset_pwd);
        mTitle.setText(R.string.recover_password);
        mBtnNext.setEnabled(false);
        mBtnReset.setEnabled(false);
    }


    private void setEditChangeInput() {
        et_telephone.addTextChangedListener(new myTextWatch());
        et_password.addTextChangedListener(new myTextWatch());
        et_checkcode.addTextChangedListener(new myTextWatch());
        mEtEnterPwd.addTextChangedListener(new myTextWatch());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forgetpwd_next:
                GetAccessCode();
                break;
            case R.id.btn_reset:
                ChangePassword();
                break;
            case R.id.tv_sendcheckcode:
                GetCheckCode();
                break;
            case R.id.tv_cphone:
                GotoChangeCode();
                break;
            case R.id.layout_back:
                if (mLlyForgetPwdCode.getVisibility() == View.VISIBLE) {
                    finish();
                } else {
                    mTitle.setText(R.string.recover_password);
                    mLlyForgetPwdCode.setVisibility(View.VISIBLE);
                    mLlyEnterPwd.setVisibility(View.GONE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    mBtnReset.setVisibility(View.GONE);
                    et_password.setText("");
                    mEtEnterPwd.setText("");
                }
                break;
            case R.id.iv_clear_code:
                et_checkcode.setText("");
                break;
            case R.id.iv_new_clear_pwd:
                et_password.setText("");
                break;
            case R.id.iv_enter_clear_pwd:
                mEtEnterPwd.setText("");
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
            if (et_checkcode.getText().toString().trim().length() > 0) {
                mIvClearCode.setVisibility(View.VISIBLE);
            } else {
                mIvClearCode.setVisibility(View.GONE);
            }
            if (et_telephone.getText().length() > 0
                    && et_checkcode.getText().length() > 0) {
                mBtnNext.setEnabled(true);
            } else {
                mBtnNext.setEnabled(false);
                return;
            }

            String pwd = et_password.getText().toString().trim();
            String enterPwd = mEtEnterPwd.getText().toString().trim();
            int pwdLength = pwd.length();
            int enterPwdLength = enterPwd.length();
            if (pwdLength > 0) {
                mIvNewClearPwd.setVisibility(View.VISIBLE);
            } else {
                mIvNewClearPwd.setVisibility(View.GONE);
            }
            if (enterPwdLength > 0) {
                mIvEnterClearPwd.setVisibility(View.VISIBLE);
            } else {
                mIvEnterClearPwd.setVisibility(View.GONE);
            }
            //含有数字
            final String NUMBER = ".*[0-9].*";
            //含有大小写字母
            final String CASE = ".*[a-zA-Z].*";
            //特殊符号
            final String REGSYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"].*";
            int hasNumber = pwd.matches(NUMBER) ? 1 : 0;
            int hasCase = pwd.matches(CASE) ? 1 : 0;
            int hasSymbol = pwd.matches(REGSYMBOL) ? 1 : 0;
            if (pwdLength > 7 && pwdLength < 15 && hasNumber + hasCase + hasSymbol >= 2 && enterPwdLength > 0) {
//                tv_reset.setAlpha(1.0f);
                mBtnReset.setEnabled(true);
            } else {
//                tv_reset.setAlpha(0.6f);
                mBtnReset.setEnabled(false);
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
        startActivityForResult(intent, RegisterActivity.CHANGE_COUNTRY_CODE);
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
//            tv_sendcheckcode.setTextColor(getResources().getColor(R.color.skyblue));
        } else {
            tv_sendcheckcode.setEnabled(false);
            tv_sendcheckcode.setText(time + "s");
//            tv_sendcheckcode.setTextColor(getResources().getColor(R.color.newgrey));
        }
    }

    protected void ChangePassword() {
        String newPwd = et_password.getText().toString().trim();
        String enterPwd = mEtEnterPwd.getText().toString().trim();
        if (!newPwd.equals(enterPwd)) {
            ToastUtils.showInCenter(this, getString(R.string.reset_failed), getString(R.string.passwords_are_inconsistent));
            return;
        }

        final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJsonNoToken(
                            AppConfig.URL_PUBLIC
                                    + "User/ResetPwd", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.SUCCESSCHANGE;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            String Password = et_password.getText().toString();
            jsonObject.put("AccessCode", AccessCode);
            jsonObject.put("Role", "1");
            jsonObject.put("Mobile", tv_cphone.getText().toString()
                    + et_telephone.getText().toString());
            jsonObject.put("Password", LoginGet.getBase64Password(Password)
                    .trim());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void GetAccessCode() {
        final String checkcode = LoginGet.getBase64Password(et_checkcode
                .getText().toString());
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService
                            .submitDataByJsonNoToken(
                                    AppConfig.URL_PUBLIC
                                            + "User/Verify4ResetPwd?mobile="
                                            + URLEncoder.encode(tv_cphone
                                                    .getText().toString()
                                                    + et_telephone.getText()
                                                    .toString(),
                                            "UTF-8")
                                            + "&checkcode=" + checkcode + "&role=1",
                                    null);
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
        telephone = et_telephone.getText().toString();
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
                                            + "&type=2"
                                            + "&productID=1", null);
                    Log.e("CheckCode", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    String RetData = responsedata.getString("RetData");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.GETCHECKCODE;
//						msg.obj = RetData;
                    } else if (retcode.equals(AppConfig.UserNotExist)) {
                        msg.what = AppConfig.NotExist;
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
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_sendcheckcode.setEnabled(true);
                        }
                    });
                }
            }
        }).start(ThreadManager.getManager());
		
		/*LoginGet loginget = new LoginGet();
		loginget.setLoginGetListener(new LoginGetListener() {
			
			@Override
			public void getCheckCode(String code) {
				et_checkcode.setText(code);
				new Thread(new CheckCodeEnable()).start();
				
			}

			@Override
			public void getCheckFalse() {
				tv_sendcheckcode.setEnabled(true);
				
			}
		});
		LoginGet.CheckCodeRequest(ForgetPasswordActivity.this, telephone);*/

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
            case RegisterActivity.CHANGE_COUNTRY_CODE:
                tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mLlyForgetPwdCode.getVisibility() == View.VISIBLE) {
                finish();
            } else {
                mTitle.setText(R.string.recover_password);
                mLlyForgetPwdCode.setVisibility(View.VISIBLE);
                mLlyEnterPwd.setVisibility(View.GONE);
                mBtnNext.setVisibility(View.VISIBLE);
                mBtnReset.setVisibility(View.GONE);
                et_password.setText("");
                mEtEnterPwd.setText("");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ForgetPasswordActivity");
        MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ForgetPasswordActivity");
        MobclickAgent.onPause(this);
    }

}
