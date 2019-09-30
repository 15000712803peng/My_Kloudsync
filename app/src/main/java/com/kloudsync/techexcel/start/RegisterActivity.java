package com.kloudsync.techexcel.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class RegisterActivity extends Activity implements OnClickListener {
    private TextView tv_cphone, tv_sendcheckcode;
    private EditText phoneEdit, codeEdit;
    private EditText nameEdit, pwdEdit;
    private TextView registerText;
    //	private EditText et_password;
    private LinearLayout lin_bottom;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String telephone;
    private String AccessCode;

    private boolean flag_gp = false;
    private boolean isVisible;
    public static RegisterActivity instance = null;
    private LinearLayout loginLayout;

    public static final int CHANGE_COUNTRY_CODE = 0;


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
                    registerRequest(AccessCode);
//				ChangePassword();
				/*if(!flag_gp){
					GoToPI();
				}*/
//                    GoToPI();
                    break;
                case AppConfig.HasExisted:
                    tv_sendcheckcode.setEnabled(true);
                    Toast.makeText(
                            RegisterActivity.this,
                            getResources().getString(R.string.HasExisted),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            RegisterActivity.this,
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            RegisterActivity.this,
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();

                    break;
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    tv_sendcheckcode.setEnabled(true);
                    break;
                case AppConfig.SUCCESSCHANGE:
                    Toast.makeText(getApplicationContext(), "密码已重置",
                            Toast.LENGTH_LONG).show();
                    finish();

                    break;

                case AppConfig.REGISTER_SUCC:
                    registerSucc((JSONObject) msg.obj);
                    break;
                case AppConfig.REGISTER_FAIL:
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_sign_up_v2);
        instance = this;
        isVisible = getIntent().getBooleanExtra("isVisible", true);
        initView();
    }

    private void initView() {
        tv_cphone = (TextView) findViewById(R.id.tv_cphone);
        tv_sendcheckcode = (TextView) findViewById(R.id.tv_sendcheckcode);
        phoneEdit = (EditText) findViewById(R.id.et_telephone);
        nameEdit = findViewById(R.id.et_name);
        pwdEdit = findViewById(R.id.et_password);
        registerText = findViewById(R.id.tv_sign_up);
//		et_password = (EditText) findViewById(R.id.et_password);
        codeEdit = (EditText) findViewById(R.id.et_checkcode);
        AppConfig.COUNTRY_CODE = 86;
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
        setEditChangeInput();
        tv_sendcheckcode.setOnClickListener(this);
        tv_cphone.setOnClickListener(this);
        loginLayout = findViewById(R.id.layout_login);
        loginLayout.setOnClickListener(this);
        registerText.setOnClickListener(this);

    }


    protected void GoToPI() {
        flag_gp = true;
        Intent intent = new Intent(RegisterActivity.this, PerfectData2Activity.class);
        intent.putExtra("countrycode", tv_cphone.getText().toString().replaceAll("\\+", ""));
        intent.putExtra("telephone", phoneEdit.getText().toString());
//		intent.putExtra("password", et_password.getText().toString());
        intent.putExtra("AccessCode", AccessCode);
        startActivity(intent);

    }

    private void setEditChangeInput() {
        phoneEdit.addTextChangedListener(new myTextWatch());
//		et_password.addTextChangedListener(new myTextWatch());
        codeEdit.addTextChangedListener(new myTextWatch());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_login:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_reset:

                break;
            case R.id.tv_sendcheckcode:
                GetCheckCode();
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_fpass:
                if (LoginActivity.instance != null) {
                    LoginActivity.instance.finish();
                }
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            case R.id.tv_cphone:
                GotoChangeCode();
                break;
            case R.id.tv_sign_up:
                register();
                break;
            default:
                break;
        }
    }

    private void register() {
        getAccessCode();
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
            if (phoneEdit.getText().length() > 0
//					&& et_password.getText().length() > 0
                    && codeEdit.getText().length() > 0) {

            } else {

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

	/*protected void ChangePassword() {
		
		
		final JSONObject jsonObject = format();
		new Thread(new Runnable() {
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
					}else{
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
		}).start();
	}*/

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
//			String Password = et_password.getText().toString();
            jsonObject.put("AccessCode", AccessCode);
            jsonObject.put("Role", "1");
            jsonObject.put("Mobile", tv_cphone.getText().toString()
                    + phoneEdit.getText().toString());
//			jsonObject.put("Password", LoginGet.getBase64Password(Password));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void getAccessCode() {
        final String checkcode = LoginGet.getBase64Password(codeEdit
                .getText().toString());
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
		LoginGet.CheckCodeRequest(RegisterActivity.this, telephone);*/

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
        String pwd = pwdEdit.getText().toString();
        if (TextUtils.isEmpty(pwd) || pwd.length() < 6 || pwd.length() > 20) {
            Toast.makeText(this, "请输入6-20的密码", Toast.LENGTH_SHORT).show();
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Mobile", "+" + tv_cphone.getText().toString().replaceAll("\\+", "") + phone);
            jsonObject.put("Password", LoginGet.getBase64Password(pwd).trim());
//            jsonObject.put("Password", URLEncoder.encode(,"UTF-8"));
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
//                    JSONObject retdata = responsedata
//                            .getJSONObject("RetData");
//                    String UserID = retdata.getString("UserID");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.REGISTER_SUCC;
                        msg.obj = jsonobject;
                    } else {
                        msg.what = AppConfig.REGISTER_FAIL;
                        String ErrorMessage = responsedata
                                .getString("ErrorMessage");
                        msg.obj = ErrorMessage;
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
            String phone = jsonobject.getString("Mobile");
            String pwd = jsonobject.getString("Password");
            editor.putInt("countrycode", countrycode);
            editor.putString("telephone", phone);
            editor.putString("password", pwd);
            editor.commit();
            LoginGet.LoginRequest(this, "+"
                            + countrycode + phone, pwd, 0, sharedPreferences,
                    editor, ((App) getApplication()).getThreadMgr());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
