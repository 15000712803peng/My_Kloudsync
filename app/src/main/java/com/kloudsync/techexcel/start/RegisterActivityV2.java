package com.kloudsync.techexcel.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class RegisterActivityV2 extends Activity {
    private TextView tv_cphone, tv_reset, tv_sendcheckcode, tv_fpass, tv_nametop;
    private EditText et_telephone, et_checkcode;
    //	private EditText et_password;
    private ImageView img_back;
    private LinearLayout lin_bottom;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String telephone;
    private String password;
    private String AccessCode;

    private boolean flag_gp = false;
    private boolean isVisible;

    public static RegisterActivityV2 instance = null;

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
//				ChangePassword();
				/*if(!flag_gp){
					GoToPI();
				}*/
                    GoToPI();
                    break;
                case AppConfig.HasExisted:
                    Toast.makeText(
                            RegisterActivityV2.this,
                            getResources().getString(R.string.HasExisted),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            RegisterActivityV2.this,
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            RegisterActivityV2.this,
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

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_v2);

        instance = this;
        isVisible = getIntent().getBooleanExtra("isVisible", true);
        initView();
    }

    private void initView() {
        tv_cphone = (TextView) findViewById(R.id.tv_cphone);
        tv_reset = (TextView) findViewById(R.id.tv_reset);
        tv_sendcheckcode = (TextView) findViewById(R.id.tv_sendcheckcode);
        tv_fpass = (TextView) findViewById(R.id.tv_fpass);
        tv_nametop = (TextView) findViewById(R.id.tv_nametop);
        et_telephone = (EditText) findViewById(R.id.et_telephone);
//		et_password = (EditText) findViewById(R.id.et_password);
        et_checkcode = (EditText) findViewById(R.id.et_checkcode);
        img_back = (ImageView) findViewById(R.id.img_back);
        lin_bottom = (LinearLayout) findViewById(R.id.lin_bottom);

        lin_bottom.setVisibility(View.VISIBLE);
        tv_nametop.setText(getResources().getString(R.string.Register));

        AppConfig.COUNTRY_CODE = 86;
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);

        tv_reset.setEnabled(false);
        setEditChangeInput();
        tv_reset.setOnClickListener(new myOnClick());
        tv_sendcheckcode.setOnClickListener(new myOnClick());
        img_back.setOnClickListener(new myOnClick());
        tv_fpass.setOnClickListener(new myOnClick());
        tv_cphone.setOnClickListener(new myOnClick());

    }


    protected void GoToPI() {
        flag_gp = true;
        Intent intent = new Intent(RegisterActivityV2.this, PerfectData2Activity.class);
        intent.putExtra("countrycode", tv_cphone.getText().toString().replaceAll("\\+", ""));
        intent.putExtra("telephone", et_telephone.getText().toString());
//		intent.putExtra("password", et_password.getText().toString());
        intent.putExtra("AccessCode", AccessCode);
        startActivity(intent);

    }

    private void setEditChangeInput() {
        et_telephone.addTextChangedListener(new myTextWatch());
//		et_password.addTextChangedListener(new myTextWatch());
        et_checkcode.addTextChangedListener(new myTextWatch());

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
            if (et_telephone.getText().length() > 0
//					&& et_password.getText().length() > 0
                    && et_checkcode.getText().length() > 0) {
                tv_reset.setAlpha(1.0f);
                tv_reset.setEnabled(true);
            } else {
                tv_reset.setAlpha(0.6f);
                tv_reset.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_reset:
                    GetAccessCode();
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

                default:
                    break;
            }

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
                    + et_telephone.getText().toString());
//			jsonObject.put("Password", LoginGet.getBase64Password(Password));
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
                                            + "User/Verify4Register?mobile="
                                            + URLEncoder.encode(tv_cphone
                                                    .getText().toString()
                                                    + et_telephone.getText()
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

}
